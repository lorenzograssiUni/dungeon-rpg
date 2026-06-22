package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.combat.BurnEffect;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gestisce il flusso di un turno di combattimento:
 *   1. Il giocatore agisce (attacco normale, speciale, pozione, fuga)
 *   2. I nemici vivi rispondono
 *   3. Si applica la bruciatura se attiva
 *   4. Si controlla se l'ondata e' finita
 *
 * Drop probabilistici (50% Carne) lanciati al momento dell'uccisione:
 *   Cinghiale, Lupo, Cucciolo di Drago -> 50% Carne immediata
 *   Uovo                               -> nessun drop
 */
public class CombatController {

    // -------------------------------------------------------------------------
    // Interfaccia listener (per la UI)
    // -------------------------------------------------------------------------

    public interface CombatListener {
        void onEvent(String msg);
        void onTurnEnd(List<String> log, boolean playerDead, boolean waveCleared);
    }

    // -------------------------------------------------------------------------
    // Record risultato turno
    // isCombatOver() = true quando il turno e' definitivamente concluso
    // (giocatore morto, fuga riuscita, ondata cleared)
    // -------------------------------------------------------------------------

    public record TurnResult(
            List<String> log,
            boolean playerDead,
            boolean waveCleared,
            boolean fleeSuccess
    ) {
        /** True se il combattimento e' terminato per qualsiasi motivo. */
        public boolean isCombatOver() {
            return playerDead || waveCleared || fleeSuccess;
        }
    }

    // -------------------------------------------------------------------------
    // Stato
    // -------------------------------------------------------------------------

    private final GameController gc;
    private final DungeonMap     dungeonMap;
    private CombatListener       listener;
    private BurnEffect           activeBurn = null;

    public CombatController(GameController gc, DungeonMap dungeonMap) {
        this.gc         = gc;
        this.dungeonMap = dungeonMap;
    }

    public void setListener(CombatListener l) { this.listener = l; }
    public boolean isCaricaActive()           { return gc.isCaricaActive(); }
    public BurnEffect getActiveBurn()         { return activeBurn; }

    // =========================================================================
    // Azioni del giocatore
    // =========================================================================

    /** Attacco normale del giocatore su un singolo nemico. */
    public TurnResult playerNormalAttack(Enemy target) {
        List<String> log = new ArrayList<>();
        GameCharacter player = player();

        int dmg = gc.playerAttack(target);
        log.add("[ATK] " + player.getName() + " attacca " + target.getName()
                + " per " + dmg + " danni."
                + (dmg == 0 ? " (parato!)" : ""));

        if (!target.isAlive()) {
            log.add("[\u2020] " + target.getName() + " e' stato sconfitto!");
            rollMeatDrop(target, log);
        }

        boolean waveCleared = handleEnemyTurns(log);
        boolean playerDead  = checkPlayerDead(log);
        return new TurnResult(log, playerDead, waveCleared, false);
    }

    /** Attacco speciale single-target. */
    public TurnResult playerSpecialAttack(SpecialAttack special, Enemy target) {
        List<String> log = new ArrayList<>();
        GameCharacter player = player();

        int dmg = gc.executeSpecial(special, target);
        log.add("[SPECIAL] " + player.getName() + " usa " + special.getName()
                + " su " + target.getName() + " per " + dmg + " danni.");

        if (!target.isAlive()) {
            log.add("[\u2020] " + target.getName() + " e' stato sconfitto!");
            rollMeatDrop(target, log);
        }

        boolean waveCleared = handleEnemyTurns(log);
        boolean playerDead  = checkPlayerDead(log);
        return new TurnResult(log, playerDead, waveCleared, false);
    }

    /** Attacco AOE (Onda Magica, Spazzatutto). */
    public TurnResult playerAoeAttack(SpecialAttack special) {
        List<String> log = new ArrayList<>();
        GameCharacter player = player();

        Map<Enemy, Integer> results = gc.executeAoeSpecial(special);
        log.add("[AOE] " + player.getName() + " usa " + special.getName() + "!");
        for (Map.Entry<Enemy, Integer> e : results.entrySet()) {
            log.add("  -> " + e.getKey().getName() + ": " + e.getValue() + " danni.");
            if (!e.getKey().isAlive()) {
                log.add("  [\u2020] " + e.getKey().getName() + " e' stato sconfitto!");
                rollMeatDrop(e.getKey(), log);
            }
        }

        boolean waveCleared = handleEnemyTurns(log);
        boolean playerDead  = checkPlayerDead(log);
        return new TurnResult(log, playerDead, waveCleared, false);
    }

    /** Usa la prima pozione disponibile nell'inventario. */
    public TurnResult playerUsePotion() {
        List<String> log = new ArrayList<>();
        boolean used = gc.useFirstPotion();
        if (used) {
            log.add("[ITEM] Hai usato una Pozione! Stamina +3.");
        } else {
            log.add("[!] Nessuna pozione disponibile.");
        }
        // Usare una pozione non fa rispondere i nemici
        return new TurnResult(log, false, false, false);
    }

    /** Tentativo di fuga. */
    public TurnResult playerFlee() {
        List<String> log = new ArrayList<>();
        if (!gc.canFlee()) {
            log.add("[FUGA] Non puoi fuggire da qui!");
            return new TurnResult(log, false, false, false);
        }
        gc.flee();
        log.add("[FUGA] Sei fuggito!");
        return new TurnResult(log, false, false, true);
    }

    // =========================================================================
    // Dragon PassiveBuff
    // =========================================================================

    /**
     * Controlla se la Sala del Tesoro e' stata ripulita e, in caso affermativo,
     * attiva il buff passivo del Drago (+20% danno).
     * Chiamato da GameUI prima di iniziare il combattimento con il boss.
     */
    public void checkAndActivateDragonBuff(Enemy dragon) {
        if (!dungeonMap.isTreasureRoomCleaned()) return;
        dragon.activatePassiveBuff();
    }

    // =========================================================================
    // Drop probabilistici Carne (50%)
    // =========================================================================

    private void rollMeatDrop(Enemy enemy, List<String> log) {
        if (!isMeatDropper(enemy.getName())) return;
        if (Math.random() < 0.5) {
            player().addItem(new it.unicam.cs.mpgc.rpg123891.model.item.Meat());
            log.add("[DROP] " + enemy.getName() + " ha lasciato della Carne! (+40 HP se usata)");
        }
    }

    private boolean isMeatDropper(String name) {
        return switch (name) {
            case "Cinghiale", "Lupo", "Cucciolo di Drago" -> true;
            default -> false;
        };
    }

    // =========================================================================
    // Turni nemici
    // =========================================================================

    private boolean handleEnemyTurns(List<String> log) {
        Wave wave = dungeonMap.getCurrentRoom().getCurrentWave();
        if (wave == null) return false;

        List<Enemy> alive = wave.getEnemies().stream()
                .filter(Enemy::isAlive)
                .sorted((a, b) -> b.getAgility() - a.getAgility())
                .toList();

        for (Enemy enemy : alive) {
            if (!player().isAlive()) break;
            if (enemy.isStunned()) {
                enemy.clearStun();
                log.add("[STORD] " + enemy.getName() + " e' stordito e salta il turno.");
                continue;
            }
            if (enemy.isImmune()) {
                log.add("[IMM] " + enemy.getName() + " e' immune: non attacca.");
                continue;
            }
            int dmg = gc.enemyAttack(enemy);
            log.add("[ENEMY] " + enemy.getName() + " attacca per " + dmg + " danni.");
        }

        // Bruciatura a fine turno: usa applyTo() che chiama applyBurnDamage()
        if (activeBurn != null) {
            int burnDmg = activeBurn.applyTo(player());
            log.add("[BURN] La bruciatura ti infligge " + burnDmg + " danni!"
                    + (activeBurn.isExpired() ? " (terminata)" : ""));
            if (activeBurn.isExpired()) activeBurn = null;
        }

        gc.rollbackCarica();
        return wave.isCleared();
    }

    private boolean checkPlayerDead(List<String> log) {
        if (!player().isAlive()) {
            log.add("[\u2620] Sei morto. Game Over.");
            return true;
        }
        return false;
    }

    /** Imposta una bruciatura attiva (da Soffio del Drago). */
    public void applyBurn(BurnEffect burn) { this.activeBurn = burn; }

    private GameCharacter player() {
        return (GameCharacter) gc.getPlayer();
    }
}
