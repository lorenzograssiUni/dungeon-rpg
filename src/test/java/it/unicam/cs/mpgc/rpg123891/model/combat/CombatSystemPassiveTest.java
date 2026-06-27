package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.*;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica passive di Warrior/Mage/Thief in CombatSystem.executeAttack().
 * Usa Enemy con ATK fisso per calcoli deterministici.
 *
 * Allineato a GAME_SPEC.md:
 *   - Warrior: blocco 20% cumulabile, 5o attacco garantito, reset al blocco.
 *   - Thief: primo attacco wave critico garantito, +2% crit dopo ogni attacco.
 */
public class CombatSystemPassiveTest {

    private static final Random NO_LUCK = new Random(0) {
        @Override public double nextDouble() { return 1.0; }
    };

    /** Nemico con ATK fisso a 12. */
    private static Enemy fixedEnemy() {
        return new Enemy("TestEnemy", 200, 12, 0, AttackType.PHYSICAL, 0.0);
    }

    @Test
    void normalAttack_doesNotConsumeStamina() {
        Warrior w = new Warrior("G");
        Enemy goblin = EnemyFactory.createGoblin();
        int staBefore = w.getCurrentStamina();
        new CombatSystem(NO_LUCK).executeAttack(w, goblin, AttackType.PHYSICAL, 0);
        assertEquals(staBefore, w.getCurrentStamina());
    }

    @Test
    void normalAttack_withZeroStamina_stillWorks() {
        Warrior w = new Warrior("G");
        while (w.getCurrentStamina() > 0) w.consumeStaminaForAttack();
        Enemy goblin = EnemyFactory.createGoblin();
        assertDoesNotThrow(() ->
            new CombatSystem(NO_LUCK).executeAttack(w, goblin, AttackType.PHYSICAL, 0));
    }

    @Test
    void warrior_blockStreak_incrementsOnMiss() {
        Warrior w = new Warrior("G");
        Enemy e = fixedEnemy();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        cs.executeAttack(e, w, AttackType.PHYSICAL, 0);
        assertEquals(1, w.getBlockStreak());
        cs.executeAttack(e, w, AttackType.PHYSICAL, 0);
        assertEquals(2, w.getBlockStreak());
    }

    @Test
    void warrior_fifthAttack_isAlwaysBlocked() {
        Warrior w = new Warrior("G");
        Enemy e = fixedEnemy();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        for (int i = 0; i < 4; i++) {
            cs.executeAttack(e, w, AttackType.PHYSICAL, 0);
        }
        int hpBefore = w.getCurrentHp();
        int dmg = cs.executeAttack(e, w, AttackType.PHYSICAL, 0);
        assertEquals(0, dmg, "Il 5o attacco deve essere bloccato (danno 0)");
        assertEquals(hpBefore, w.getCurrentHp(), "Gli HP non devono diminuire al 5o attacco");
        assertEquals(0, w.getBlockStreak(), "Il counter si azzera dopo un blocco");
    }

    @Test
    void thief_normalAttack_doesNotConsumeStamina() {
        Thief t = new Thief("L");
        t.applyPassiveBonus();
        Enemy goblin = EnemyFactory.createGoblin();
        int staBefore = t.getCurrentStamina();
        new CombatSystem(NO_LUCK).executeAttack(t, goblin, AttackType.PHYSICAL, 0);
        assertEquals(staBefore, t.getCurrentStamina());
    }

    @Test
    void thief_critAfterAttack_critChanceIncreases() {
        Thief t = new Thief("L");
        t.applyPassiveBonus();
        Enemy goblin = EnemyFactory.createGoblin();
        double critBefore = t.getCritChance();
        new CombatSystem(NO_LUCK).executeAttack(t, goblin, AttackType.PHYSICAL, 0);
        assertTrue(t.getCritChance() >= critBefore);
    }
}
