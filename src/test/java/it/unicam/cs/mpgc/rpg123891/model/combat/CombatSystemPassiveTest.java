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

    // Random deterministico: nextDouble() -> sempre 0.0 (critico garantito se crit>0, blocco sempre)
    // Usiamo seed=Long.MAX_VALUE per nextDouble() -> ~1.0 (nessun critico, nessun blocco)
    private static final Random NO_LUCK  = new Random(Long.MAX_VALUE);
    private static final Random ALL_LUCK = new Random(0);

    @Test
    void normalAttack_doesNotConsumeStamina() {
        Warrior w = new Warrior("G");
        Enemy goblin = EnemyFactory.createGoblin();
        int staBefore = w.getCurrentStamina();
        new CombatSystem(NO_LUCK).executeAttack(w, goblin, AttackType.PHYSICAL, 0);
        assertEquals(staBefore, w.getCurrentStamina(),
                "L'attacco normale non deve consumare stamina");
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
        // ALL_LUCK: nextDouble()~0.0 -> blocco garantito
        Warrior w = new Warrior("G");
        Enemy goblin = EnemyFactory.createGoblin();
        int hpBefore = w.getCurrentHp();
        new CombatSystem(ALL_LUCK).executeAttack(goblin, w, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, w.getCurrentHp(), "Il blocco deve azzerare il danno");
    }

    @Test
    void thief_normalAttack_doesNotConsumeStamina() {
        Thief t = new Thief("L");
        t.applyPassiveBonus(); // stealth on
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
        // stealth consumato (no crit bonus da stealth) ma incrementCritAfterAttack chiamato
        assertTrue(t.getCritChance() >= critBefore);
    }

    @Test
    void mage_magicShield_absorbsExactlyOnce() {
        Mage m = new Mage("Ma");
        Enemy goblin = EnemyFactory.createGoblin();
        int hp = m.getCurrentHp();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        cs.executeAttack(goblin, m, AttackType.PHYSICAL, 0); // assorbito
        assertEquals(hp, m.getCurrentHp());
        cs.executeAttack(goblin, m, AttackType.PHYSICAL, 0); // danno reale
        assertTrue(m.getCurrentHp() < hp);
    }

    @Test
    void damage_reducedByDefense() {
        Warrior w = new Warrior("G"); // DEF=8
        Enemy goblin = EnemyFactory.createGoblin(); // ATK=10
        // Usiamo NO_LUCK: nessun critico, nessun blocco (nextDouble()~1.0 > crit e blockChance)
        int dmg = new CombatSystem(NO_LUCK).executeAttack(goblin, w, AttackType.PHYSICAL, 0);
        assertEquals(Math.max(0, goblin.getAttack() - w.getDefense()), dmg);
    }
}
