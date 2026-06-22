package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
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
        if (dmg == 0) {
            log.add("[ATK] " + player.getName() + " attacca " + target.getName()
                    + " — parato!");
        } else {
            log.add("[ATK] " + player.getName() + " attacca " + target.getName()
                    + " per " + dmg + " danni.");
        }

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

    public TurnResult playerUsePotion() {
        List<String> log = new ArrayList<>();
        boolean used = gc.useFirstPotion();
        log.add(used ? "[ITEM] Hai usato una Pozione! +40 HP, +5 Stamina."
                     : "[!] Nessuna pozione disponibile.");
        return new TurnResult(log, false, false, false);
    }

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
            log.add("[DROP] " + enemy.getName() + " ha lasciato della Carne! (+2 Stamina se usata)");
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

        GameCharacter player = player();

        List<Enemy> alive = wave.getEnemies().stream()
                .filter(Enemy::isAlive)
                .sorted((a, b) -> b.getAgility() - a.getAgility())
                .toList();

        for (Enemy enemy : alive) {
            if (!player.isAlive()) break;
            if (enemy.isStunned()) {
                enemy.clearStun();
                log.add("[STORD] " + enemy.getName() + " e' stordito e salta il turno.");
                continue;
            }
            if (enemy.isImmune()) {
                log.add("[IMM] " + enemy.getName() + " e' immune: non attacca.");
                continue;
            }

            int rawDamage = enemy.getAttack();
            int defense   = player.getDefense();

            // Controlla passive difensive prima di applicare
            if (player instanceof Warrior warrior) {
                // Il blocco e' casuale: lo logghiamo solo se il danno risultante e' 0
                int dmg = gc.enemyAttack(enemy);
                if (dmg == 0) {
                    log.add("[ENEMY] " + enemy.getName() + " attacca (" + rawDamage
                            + " lordo) — BLOCCATO dal Guerriero!");
                } else {
                    log.add("[ENEMY] " + enemy.getName() + " attacca per " + rawDamage
                            + " lordo — " + defense + " difesa = " + dmg + " danni subiti.");
                }
            } else if (player instanceof Mage mage && mage.isMagicShieldActive()) {
                int dmg = gc.enemyAttack(enemy);
                if (dmg == 0) {
                    log.add("[ENEMY] " + enemy.getName() + " attacca (" + rawDamage
                            + " lordo) — ASSORBITO dallo Scudo Magico!");
                } else {
                    log.add("[ENEMY] " + enemy.getName() + " attacca per " + rawDamage
                            + " lordo — " + defense + " difesa = " + dmg + " danni subiti.");
                }
            } else {
                int dmg = gc.enemyAttack(enemy);
                int netExpected = Math.max(0, rawDamage - defense);
                log.add("[ENEMY] " + enemy.getName() + " attacca per " + rawDamage
                        + " lordo — " + defense + " difesa = " + dmg + " danni subiti.");
            }
        }

        if (activeBurn != null) {
            int burnDmg = activeBurn.applyTo(player);
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

    public void applyBurn(BurnEffect burn) { this.activeBurn = burn; }

    private GameCharacter player() {
        return (GameCharacter) gc.getPlayer();
    }
}
