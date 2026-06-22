package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.*;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica che l'attacco normale NON consumi stamina
 * e che le passive di Warrior/Mage/Thief funzionino
 * in CombatSystem.executeAttack().
 */
public class CombatSystemPassiveTest {

    // nextDouble() = 1.0 → mai critico, mai blocco
    private static final Random NO_LUCK = new Random(0) {
        @Override public double nextDouble() { return 1.0; }
    };
    // nextDouble() = 0.0 → critico garantito E blocco garantito
    private static final Random ALL_LUCK = new Random(0) {
        @Override public double nextDouble() { return 0.0; }
    };

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
        // ALL_LUCK: nextDouble()=0.0 → 0.0 < 0.20 (blockChance) → blocco garantito
        Warrior w = new Warrior("G");
        Enemy goblin = EnemyFactory.createGoblin();
        int hpBefore = w.getCurrentHp();
        new CombatSystem(ALL_LUCK).executeAttack(goblin, w, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, w.getCurrentHp(), "Il blocco deve azzerare il danno");
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
    void mage_magicShield_absorbsExactlyOnce() {
        Mage m = new Mage("Ma");
        m.applyPassiveBonus(); // attiva lo scudo
        Enemy goblin = EnemyFactory.createGoblin();
        int hp = m.getCurrentHp();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        cs.executeAttack(goblin, m, AttackType.PHYSICAL, 0); // assorbito dallo scudo
        assertEquals(hp, m.getCurrentHp());
        cs.executeAttack(goblin, m, AttackType.PHYSICAL, 0); // danno reale
        assertTrue(m.getCurrentHp() < hp);
    }

    @Test
    void damage_reducedByDefense() {
        Warrior w = new Warrior("G"); // DEF=8
        Enemy goblin = EnemyFactory.createGoblin(); // ATK=12
        // NO_LUCK: nessun critico (1.0 > crit), nessun blocco (1.0 > 0.20)
        int dmg = new CombatSystem(NO_LUCK).executeAttack(goblin, w, AttackType.PHYSICAL, 0);
        assertEquals(Math.max(0, goblin.getAttack() - w.getDefense()), dmg);
    }
}
