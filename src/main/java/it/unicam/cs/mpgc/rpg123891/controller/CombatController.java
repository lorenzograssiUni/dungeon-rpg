package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.BurnEffect;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyAbility;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gestisce il flusso di un turno di combattimento.
 */
public class CombatController {

    public interface CombatListener {
        void onEvent(String msg);
        void onTurnEnd(List<String> log, boolean playerDead, boolean waveCleared);
    }

    public record TurnResult(
            List<String> log,
            boolean playerDead,
            boolean waveCleared,
            boolean fleeSuccess
    ) {
        public boolean isCombatOver() {
            return playerDead || waveCleared || fleeSuccess;
        }
    }

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

    public TurnResult playerNormalAttack(Enemy target) {
        List<String> log = new ArrayList<>();
        GameCharacter player = player();

        int dmg = gc.playerAttack(target);
        log.add(dmg == 0
            ? "[ATK] " + player.getName() + " attacca " + target.getName() + " — parato!"
            : "[ATK] " + player.getName() + " attacca " + target.getName() + " per " + dmg + " danni.");

        if (!target.isAlive()) {
            log.add("[\u2020] " + target.getName() + " e' stato sconfitto!");
            rollMeatDrop(target, log);
        }

        boolean waveCleared = handleEnemyTurns(log);
        boolean playerDead  = checkPlayerDead(log);
        return new TurnResult(log, playerDead, waveCleared, false);
    }

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

    /** Esegue Ira: attacca ogni nemico vivo una volta con +25% critico. */
    public TurnResult playerIraAttack(SpecialAttack special) {
        List<String> log = new ArrayList<>();
        GameCharacter player = player();

        Map<Enemy, Integer> results = gc.executeIra(special);
        log.add("[IRA] " + player.getName() + " scatena l'Ira!");
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

    public TurnResult playerUsePotion() {
        List<String> log = new ArrayList<>();
        boolean used = gc.useFirstPotion();
        log.add(used ? "[ITEM] Hai usato una Pozione! Stamina completamente ripristinata."
                     : "[!] Nessuna pozione disponibile.");
        return new TurnResult(log, false, false, false);
    }

    public TurnResult playerFlee() {
        List<String> log = new ArrayList<>();
        if (!gc.canFlee()) {
            log.add("[FUGA] Non puoi fuggire: la tua agilita' non e' abbastanza bassa rispetto ai nemici!");
            return new TurnResult(log, false, false, false);
        }
        gc.flee();
        log.add("[FUGA] Sei fuggito!");
        return new TurnResult(log, false, false, true);
    }

    // =========================================================================
    // Dragon PassiveBuff
    // =========================================================================

    public void checkAndActivateDragonBuff(Enemy dragon) {
        if (!dungeonMap.isTreasureRoomCleaned()) return;
        dragon.applyPassiveBonus();
    }

    // =========================================================================
    // Drop probabilistici Carne (50%)
    // =========================================================================

    private void rollMeatDrop(Enemy enemy, List<String> log) {
        if (!isMeatDropper(enemy.getName())) return;
        if (Math.random() < 0.5) {
            player().addItem(new it.unicam.cs.mpgc.rpg123891.model.item.Meat());
            log.add("[DROP] " + enemy.getName() + " ha lasciato della Carne! (+40 HP +3 Stamina se usata)");
        }
    }

    private boolean isMeatDropper(String name) {
        return switch (name) {
            case "Cinghiale", "Lupo", "Cucciolo di Drago" -> true;
            default -> false;
        };
    }

    // =========================================================================
    // Turni nemici + schiusa Uovo + tick abilita'
    // =========================================================================

    private boolean handleEnemyTurns(List<String> log) {
        Wave wave = dungeonMap.getCurrentRoom().getCurrentWave();
        if (wave == null) return false;

        GameCharacter player = player();

        List<Enemy> alive = wave.getEnemies().stream()
                .filter(Enemy::isAlive)
                .sorted((a, b) -> b.getAgility() - a.getAgility())
                .toList();

        for (Enemy enemy : alive) {
            if (!player.isAlive()) break;

            // --- Tick abilita' nemico ---
            if (enemy.hasAbility()) {
                enemy.getAbility().tick();
            }

            // --- Schiusa Uovo ---
            if (enemy.isEgg()) {
                boolean hatches = enemy.tickHatch();
                log.add("[UOVO] " + enemy.getName() + " pulsa... (turno " + enemy.getHatchCounter() + "/" + enemy.getTurnsToHatch() + ")");
                if (hatches) {
                    enemy.applyBurnDamage(enemy.getMaxHp());
                    Enemy cucciolo = EnemyFactory.createCuccioloDrago();
                    wave.getEnemies().add(cucciolo);
                    log.add("[SCHIUSA] L'Uovo si e' schiuso! E' nato un Cucciolo di Drago!");
                }
                // L'uovo attacca con danno 1 diretto (ignora difesa)
                player.applyBurnDamage(1);
                log.add("[UOVO] L'uovo crepita e ti infligge 1 danno diretto.");
                continue;
            }

            if (enemy.isStunned()) {
                enemy.clearStun();
                log.add("[STORD] " + enemy.getName() + " e' stordito e salta il turno.");
                continue;
            }
            if (enemy.isImmune()) {
                log.add("[IMM] " + enemy.getName() + " e' immune: non attacca.");
                continue;
            }

            // --- Abilita' speciale nemico ---
            if (enemy.hasAbility() && enemy.getAbility().isReady()) {
                EnemyAbility.AbilityResult result = enemy.getAbility().use(enemy, player);
                log.add("[ABILITY] " + result.message());
                if (result.burnEffect() != null) {
                    activeBurn = result.burnEffect();
                }
                if (!result.summonedEnemies().isEmpty()) {
                    wave.getEnemies().addAll(result.summonedEnemies());
                    log.add("[EVOCA] " + result.summonedEnemies().size() + " nemici evocati!");
                }
                continue;
            }

            // --- Attacco normale nemico ---
            int rawDamage = enemy.getAttack();
            int defense   = player.getDefense();
            int dmg       = gc.enemyAttack(enemy);

            if (player instanceof Warrior && dmg == 0 && gc.isCaricaActive()) {
                log.add("[ENEMY] " + enemy.getName() + " attacca (" + rawDamage + " lordo) — CARICA! 0 danni subiti.");
            } else if (player instanceof Warrior warrior) {
                if (dmg == 0) {
                    log.add("[ENEMY] " + enemy.getName() + " attacca (" + rawDamage + " lordo) — BLOCCATO!");
                } else {
                    log.add("[ENEMY] " + enemy.getName() + " attacca per " + rawDamage
                            + " lordo — " + defense + " difesa = " + dmg + " danni subiti.");
                }
            } else {
                if (dmg == 0 && gc.isCaricaActive()) {
                    log.add("[ENEMY] " + enemy.getName() + " attacca — CARICA! 0 danni subiti.");
                } else {
                    log.add("[ENEMY] " + enemy.getName() + " attacca per " + rawDamage
                            + " lordo — " + defense + " difesa = " + dmg + " danni subiti.");
                }
            }
        }

        // --- Bruciatura ---
        if (activeBurn != null) {
            int burnDmg = activeBurn.applyTo(player);
            log.add("[BURN] La bruciatura ti infligge " + burnDmg + " danni!"
                    + (activeBurn.isExpired() ? " (terminata)" : ""));
            if (activeBurn.isExpired()) activeBurn = null;
        }

        // --- Tick Carica! ---
        gc.tickCarica();

        return wave.isCleared();
    }

    private boolean checkPlayerDead(List<String> log) {
        if (!player().isAlive()) {
            log.add("[\u2620] Sei morto. Game Over.");
            return true;
        }
        return false;
    }

    public void applyBurn(BurnEffect burn) { this.activeBurn = burn; }

    private GameCharacter player() {
        return (GameCharacter) gc.getPlayer();
    }
}
