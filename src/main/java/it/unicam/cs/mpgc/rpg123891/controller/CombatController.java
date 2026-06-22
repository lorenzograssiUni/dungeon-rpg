package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg123891.model.combat.*;
import it.unicam.cs.mpgc.rpg123891.model.item.Meat;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Gestisce il loop di un singolo combattimento (una Wave).
 *
 * Responsabilita':
 *   - Determinare l'iniziativa (chi attacca prima)
 *   - Eseguire il turno del giocatore (attacco normale, speciale, pozione, fuga)
 *   - Eseguire il turno di ogni nemico vivo (normale o abilita' speciale)
 *   - Gestire BurnEffect (tick a fine turno nemico)
 *   - Gestire la trasformazione Uovo -> Cucciolo dopo 3 turni
 *   - Aggiungere i nemici evocati dalla Strega all'ondata corrente
 *   - Attivare il buff passivo del Drago se la Sala del Tesoro e' stata liberata
 *   - Rollback del buff Carica! (+3 DEF) dopo l'attacco nemico
 *   - Comunicare gli eventi alla UI tramite CombatListener
 *
 * NON gestisce:
 *   - La raccolta del loot (GameController.checkWaveCleared)
 *   - L'avanzamento di stanza (GameController.advanceRoom)
 *   - La persistenza (GameController.saveGame)
 */
public class CombatController implements Serializable {

    private static final long serialVersionUID = 1L;

    private final GameController gameController;
    private final CombatSystem   combatSystem;
    private final Random         random;
    private final DungeonMap     dungeonMap;

    /** BurnEffect correntemente attivo sul giocatore. null se non bruciato. */
    private BurnEffect activeBurn = null;

    /** Contatore turni per ogni Uovo (nome -> turni rimasti prima di schiudersi). */
    private final java.util.Map<Enemy, Integer> eggTurnCounter = new java.util.LinkedHashMap<>();

    /** Flag: il buff Carica! e' attivo questo round. */
    private boolean caricaActive = false;

    /** Listener per gli eventi di combattimento (UI). */
    private CombatListener listener = CombatListener.NOOP;

    // -------------------------------------------------------------------------
    // Costruttore
    // -------------------------------------------------------------------------

    public CombatController(GameController gameController, DungeonMap dungeonMap) {
        this.gameController = gameController;
        this.combatSystem   = new CombatSystem();
        this.random         = new Random();
        this.dungeonMap     = dungeonMap;
    }

    public CombatController(GameController gameController, DungeonMap dungeonMap,
                            Random random) {
        this.gameController = gameController;
        this.combatSystem   = new CombatSystem(random);
        this.random         = random;
        this.dungeonMap     = dungeonMap;
    }

    public void setListener(CombatListener listener) {
        this.listener = listener != null ? listener : CombatListener.NOOP;
    }

    // =========================================================================
    // API pubblica: azioni del giocatore in un turno
    // =========================================================================

    /**
     * Esegue un turno completo: attacco normale del giocatore su un nemico,
     * poi tutti i nemici attaccano il giocatore.
     *
     * @param target il nemico scelto dalla UI
     * @return TurnResult con l'esito del turno
     */
    public TurnResult playerNormalAttack(Enemy target) {
        if (!target.isAlive() || target.isImmune()) {
            return TurnResult.invalid("Bersaglio non valido o immune.");
        }

        List<String> log = new ArrayList<>();
        GameCharacter player = asGC(gameController.getPlayer());

        // --- Turno giocatore ---
        int dmg = combatSystem.executeAttack(player, target,
                AttackType.PHYSICAL, target.getCritModifierOnPlayer());
        log.add(String.format("%s attacca %s: %d danno.",
                player.getName(), target.getName(), dmg));

        checkUovoDeath(target, log);
        checkCaricaOnAttack();

        // --- Turno nemici ---
        enemyTurns(log);

        // --- Tick bruciatura ---
        burnTick(player, log);

        // --- Rollback Carica! ---
        if (caricaActive) {
            player.increaseDefense(-3);
            caricaActive = false;
            log.add("Il bonus difensivo di Carica! e' terminato.");
        }

        return buildResult(log);
    }

    /**
     * Esegue un turno con attacco speciale SINGLE-TARGET del giocatore.
     */
    public TurnResult playerSpecialAttack(SpecialAttack special, Enemy target) {
        if (!asGC(gameController.getPlayer()).canUseSpecial(special.getStaminaCost())) {
            return TurnResult.invalid("Stamina insufficiente per " + special.getName() + ".");
        }
        if (!target.isAlive() || target.isImmune()) {
            return TurnResult.invalid("Bersaglio non valido o immune.");
        }

        List<String> log = new ArrayList<>();
        GameCharacter player = asGC(gameController.getPlayer());

        // Attacco speciale
        int dmg = combatSystem.executeSpecialAttack(player, target, special);
        log.add(String.format("%s usa %s su %s: %d danno.",
                player.getName(), special.getName(), target.getName(), dmg));

        // Carica! attiva il buff difensivo
        if (special.getName().equals("Carica!")) {
            caricaActive = true;
            log.add("+3 DEF attivo per questo turno (Carica!).");
        }

        checkUovoDeath(target, log);

        // --- Turno nemici ---
        enemyTurns(log);
        burnTick(player, log);

        if (caricaActive) {
            player.increaseDefense(-3);
            caricaActive = false;
            log.add("Il bonus difensivo di Carica! e' terminato.");
        }

        return buildResult(log);
    }

    /**
     * Esegue un turno con attacco speciale AOE (Onda Magica / Spazzatutto).
     * Colpisce tutti i nemici vivi dell'ondata corrente.
     */
    public TurnResult playerAoeAttack(SpecialAttack special) {
        GameCharacter player = asGC(gameController.getPlayer());
        if (!player.canUseSpecial(special.getStaminaCost())) {
            return TurnResult.invalid("Stamina insufficiente per " + special.getName() + ".");
        }

        List<String> log = new ArrayList<>();
        Wave wave = gameController.getCurrentRoom().getCurrentWave();
        List<Enemy> targets = wave == null ? List.of() :
                wave.getEnemies().stream().filter(e -> e.isAlive() && !e.isImmune()).toList();

        player.consumeStaminaForSpecial(special.getStaminaCost());
        log.add(String.format("%s usa %s!", player.getName(), special.getName()));

        for (Enemy enemy : targets) {
            int dmg = special.execute(player, enemy);
            log.add(String.format("  -> %s: %d danno.", enemy.getName(), dmg));
            checkUovoDeath(enemy, log);
        }

        enemyTurns(log);
        burnTick(player, log);

        if (caricaActive) {
            player.increaseDefense(-3);
            caricaActive = false;
            log.add("Il bonus difensivo di Carica! e' terminato.");
        }

        return buildResult(log);
    }

    /**
     * Il giocatore usa una pozione nel suo turno.
     * I nemici attaccano comunque dopo.
     */
    public TurnResult playerUsePotion() {
        List<String> log = new ArrayList<>();
        GameCharacter player = asGC(gameController.getPlayer());

        if (!gameController.useFirstPotion()) {
            return TurnResult.invalid("Nessuna pozione nell'inventario!");
        }
        log.add(String.format("%s usa una Pozione! HP: %d/%d.",
                player.getName(), player.getCurrentHp(), player.getMaxHp()));

        enemyTurns(log);
        burnTick(player, log);

        if (caricaActive) {
            player.increaseDefense(-3);
            caricaActive = false;
        }

        return buildResult(log);
    }

    /**
     * Il giocatore tenta la fuga.
     * @return TurnResult con fleeSuccess=true se la fuga riesce.
     */
    public TurnResult playerFlee() {
        if (!gameController.canFlee()) {
            return TurnResult.invalid("Non puoi fuggire da questo combattimento!");
        }
        activeBurn = null;
        eggTurnCounter.clear();
        caricaActive = false;
        List<String> log = List.of(asGC(gameController.getPlayer()).getName() +
                " fugge dal combattimento!");
        return new TurnResult(log, false, false, true);
    }

    // =========================================================================
    // Turno dei nemici
    // =========================================================================

    /**
     * Esegue il turno di tutti i nemici vivi dell'ondata corrente.
     * Ogni nemico:
     *   1. Se stordito: rimuove lo stordimento e salta il turno
     *   2. Se ha un'abilita' speciale: la usa con probabilita' 30%
     *      (sempre al primo turno per il Re Goblin, gestito da ReGoblinThrowAbility)
     *   3. Altrimenti: attacco normale
     */
    private void enemyTurns(List<String> log) {
        Wave wave = gameController.getCurrentRoom().getCurrentWave();
        if (wave == null) return;

        GameCharacter player = asGC(gameController.getPlayer());

        // Copia la lista per gestire eventuali aggiunte (evocazioni Strega)
        List<Enemy> enemies = new ArrayList<>(wave.getEnemies());

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            if (!player.isAlive()) break;

            // 1. Stordimento
            if (enemy.isStunned()) {
                enemy.clearStun();
                log.add(enemy.getName() + " e' stordito e salta il turno.");
                continue;
            }

            // 2. Abilita' speciale (30% probabilita')
            if (enemy.hasAbility() && random.nextDouble() < 0.30) {
                EnemyAbility.AbilityResult result = enemy.getAbility().use(enemy, player);
                log.add(result.message());

                // Gestisci evocazioni (Strega)
                if (!result.summonedEnemies().isEmpty()) {
                    handleSummons(result.summonedEnemies(), wave, log);
                }

                // Gestisci bruciatura (Drago)
                if (result.burnEffect() != null) {
                    activeBurn = result.burnEffect();
                }

                // La WitchSummonAbility imposta immune sulla Strega
                if (enemy.getName().equals("Strega") && !result.summonedEnemies().isEmpty()) {
                    enemy.setImmune(true);
                    log.add("La Strega e' immune finche' gli scheletri sono in vita!");
                }

            } else {
                // 3. Attacco normale
                // L'Uovo ha ATK 1 che ignora la difesa
                int dmg;
                if (enemy.getName().equals("Uovo")) {
                    player.applyBurnDamage(1);
                    dmg = 1;
                } else {
                    dmg = combatSystem.executeAttack(enemy, player,
                            enemy.getAttackType(), 0);
                }
                log.add(String.format("%s attacca %s: %d danno.",
                        enemy.getName(), player.getName(), dmg));
            }

            // Aggiorna immunità Strega (rimuovi se tutti gli scheletri evocati morti)
            checkWitchImmunity(wave, log);
        }
    }

    // =========================================================================
    // Meccaniche speciali
    // =========================================================================

    /**
     * Aggiunge i nemici evocati all'ondata corrente.
     * Attiva il buff passivo del Drago se la Sala del Tesoro era stata liberata.
     */
    private void handleSummons(List<Enemy> summoned, Wave wave, List<String> log) {
        for (Enemy s : summoned) {
            wave.getEnemies().add(s);
            log.add("  -> " + s.getName() + " evocato! (HP: " + s.getCurrentHp() + ")");
        }
    }

    /**
     * Rimuove l'immunità della Strega se tutti i nemici evocati sono morti.
     * Considera "evocati" tutti i nemici nell'ondata tranne la Strega stessa.
     */
    private void checkWitchImmunity(Wave wave, List<String> log) {
        Enemy witch = wave.getEnemies().stream()
                .filter(e -> e.getName().equals("Strega") && e.isImmune())
                .findFirst().orElse(null);
        if (witch == null) return;

        boolean anyScheletrAlive = wave.getEnemies().stream()
                .filter(e -> !e.getName().equals("Strega"))
                .anyMatch(Enemy::isAlive);

        if (!anyScheletrAlive) {
            witch.setImmune(false);
            log.add("Tutti gli scheletri sono caduti! La Strega non e' piu' immune.");
        }
    }

    /**
     * Gestisce la morte di un Uovo:
     *   - Se l'Uovo muore prima di 3 turni: scompare (nessuna trasformazione)
     *   - Se sopravvive 3 turni: si trasforma in Cucciolo di Drago
     *
     * Chiamato dopo ogni attacco su un nemico.
     */
    private void checkUovoDeath(Enemy enemy, List<String> log) {
        if (!enemy.getName().equals("Uovo")) return;
        if (!enemy.isAlive()) {
            eggTurnCounter.remove(enemy);
            log.add("L'Uovo e' stato distrutto prima di schiudersi!");
            // 50% drop Carne (per i cuccioli, spec dice drop solo dai cuccioli;
            // le uova non droppano — nessun drop qui)
        }
    }

    /**
     * Avanza il contatore di turni per ogni Uovo vivo.
     * Se un Uovo raggiunge 3 turni, si trasforma in Cucciolo di Drago.
     * Chiamato a fine di ogni round completo.
     */
    private void tickEggCounters(Wave wave, List<String> log) {
        List<Enemy> eggs = wave.getEnemies().stream()
                .filter(e -> e.getName().equals("Uovo") && e.isAlive())
                .toList();

        for (Enemy egg : eggs) {
            int turns = eggTurnCounter.getOrDefault(egg, 0) + 1;
            if (turns >= 3) {
                // Trasformazione in Cucciolo
                eggTurnCounter.remove(egg);
                Enemy cucciolo = EnemyFactory.createCuccioloDrago();
                int idx = wave.getEnemies().indexOf(egg);
                // Segniamo l'uovo come morto (HP a 0 tramite danno diretto)
                egg.applyBurnDamage(egg.getCurrentHp());
                wave.getEnemies().add(idx >= 0 ? idx : wave.getEnemies().size(), cucciolo);
                log.add("Un Uovo si e' schiuso! Nasce un Cucciolo di Drago!");
            } else {
                eggTurnCounter.put(egg, turns);
                log.add(String.format("L'Uovo si sta schiudendo... (%d/3 turni)", turns));
            }
        }
    }

    /** Tick del BurnEffect attivo. Applicato a fine turno nemico. */
    private void burnTick(GameCharacter player, List<String> log) {
        if (activeBurn == null || activeBurn.isExpired()) {
            activeBurn = null;
            return;
        }
        int dmg = activeBurn.applyTo(player);
        log.add(String.format("%s subisce %d danno da bruciatura! (%d turni rimasti)",
                player.getName(), dmg, activeBurn.getTurnsRemaining()));
        if (activeBurn.isExpired()) {
            activeBurn = null;
            log.add("La bruciatura e' terminata.");
        }
    }

    // =========================================================================
    // Buff passivo Drago
    // =========================================================================

    /**
     * Controlla e attiva il buff passivo del Drago prima del combattimento.
     * Chiamare PRIMA di iniziare il loop di combattimento con il Boss.
     * Se la Sala del Tesoro (r4) e' stata liberata, il Drago ottiene +20% ATK.
     */
    public void checkAndActivateDragonBuff(Enemy dragon) {
        if (dungeonMap.isTreasureRoomCleaned() && dragon.getPassiveBuff() != null) {
            dragon.applyPassiveBonus();
            listener.onEvent("L'Ultimo Drago e' infuriato per la morte dei suoi piccoli! +20% ATK!");
        }
    }

    // =========================================================================
    // Costruzione risultato turno
    // =========================================================================

    private TurnResult buildResult(List<String> log) {
        // Tick uova a fine round
        Wave wave = gameController.getCurrentRoom().getCurrentWave();
        if (wave != null) tickEggCounters(wave, log);

        boolean playerDead  = !asGC(gameController.getPlayer()).isAlive();
        boolean waveCleared = wave != null && wave.isCleared();

        if (playerDead)  gameController.checkPlayerDead();
        if (waveCleared) gameController.checkWaveCleared();

        listener.onTurnEnd(log, playerDead, waveCleared);
        return new TurnResult(log, playerDead, waveCleared, false);
    }

    // =========================================================================
    // TurnResult
    // =========================================================================

    /**
     * Risultato di un turno di combattimento.
     *
     * @param log         lista di messaggi da mostrare nella UI
     * @param playerDead  true se il giocatore e' morto
     * @param waveCleared true se l'ondata e' stata completata
     * @param fleeSuccess true se il giocatore e' fuggito con successo
     */
    public record TurnResult(
            List<String> log,
            boolean playerDead,
            boolean waveCleared,
            boolean fleeSuccess
    ) {
        public static TurnResult invalid(String reason) {
            return new TurnResult(List.of(reason), false, false, false);
        }

        public boolean isCombatOver() {
            return playerDead || waveCleared || fleeSuccess;
        }
    }

    // =========================================================================
    // CombatListener
    // =========================================================================

    /**
     * Interfaccia per notificare la UI degli eventi di combattimento.
     * Implementazione di default NOOP per chi non vuole registrarsi.
     */
    public interface CombatListener {
        void onEvent(String message);
        void onTurnEnd(List<String> log, boolean playerDead, boolean waveCleared);

        CombatListener NOOP = new CombatListener() {
            public void onEvent(String message) {}
            public void onTurnEnd(List<String> log, boolean playerDead, boolean waveCleared) {}
        };
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private GameCharacter asGC(PlayerCharacter p) { return (GameCharacter) p; }

    public BurnEffect getActiveBurn()  { return activeBurn; }
    public boolean isCaricaActive()    { return caricaActive; }
}
