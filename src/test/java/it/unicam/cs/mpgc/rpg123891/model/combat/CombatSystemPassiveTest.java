package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.*;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica passive di Warrior/Mage/Thief in CombatSystem.executeAttack().
 * Usa Enemy con ATK fisso per calcoli deterministici.
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
    void warrior_blockChance_preventsAllDamage() {
        // ATK fisso 12, DEF warrior 8 -> 4 danno/attacco
        // 4 attacchi non bloccati con NO_LUCK -> blockStreak=4
        // 5° attacco: blocco garantito -> HP invariato
        Warrior w = new Warrior("G");
        Enemy e = fixedEnemy();
        CombatSystem csNoLuck = new CombatSystem(NO_LUCK);
        for (int i = 0; i < 4; i++) {
            csNoLuck.executeAttack(e, w, AttackType.PHYSICAL, 0);
        }
        int hpBefore = w.getCurrentHp();
        csNoLuck.executeAttack(e, w, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, w.getCurrentHp(), "Il 5° attacco deve essere bloccato");
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

    @Test
    void mage_magicShield_reduces30percent() {
        // ATK fisso 12, scudo -30% -> floor(12*0.70)=8, DEF mago=4 -> 4 danno
        Mage m = new Mage("Ma");
        Enemy e = fixedEnemy();
        int hpBefore = m.getCurrentHp();
        new CombatSystem(NO_LUCK).executeAttack(e, m, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore - 4, m.getCurrentHp(),
                "Il Mage deve subire esattamente 4 danno (ATK12 *0.70 -DEF4)");
    }

    @Test
    void damage_reducedByDefense() {
        Warrior w = new Warrior("G"); // DEF=8
        Enemy e = fixedEnemy(); // ATK=12 fisso
        int dmg = new CombatSystem(NO_LUCK).executeAttack(e, w, AttackType.PHYSICAL, 0);
        assertEquals(Math.max(0, 12 - w.getDefense()), dmg); // 12-8=4
    }
}
