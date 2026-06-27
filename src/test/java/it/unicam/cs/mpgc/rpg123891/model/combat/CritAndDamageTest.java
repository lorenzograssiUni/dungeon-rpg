package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.*;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class CritAndDamageTest {

    private static final Random ALWAYS_CRIT = new Random(0) {
        @Override public double nextDouble() { return 0.0; }
    };
    private static final Random NEVER_CRIT = new Random(0) {
        @Override public double nextDouble() { return 1.0; }
    };

    @Test
    void critical_hit_doublesDamage() {
        Warrior attacker = new Warrior("W"); // ATK=22, nessuno scudo da gestire
        Enemy dummy = new Enemy("D", 200, 1, 0, AttackType.PHYSICAL, 0.0);
        CombatSystem cs = new CombatSystem(ALWAYS_CRIT);
        int dmg = cs.executeAttack(attacker, dummy, AttackType.PHYSICAL, 0);
        assertEquals(attacker.getAttack() * 2, dmg);
    }

    @Test
    void no_critical_normalDamage() {
        Warrior attacker = new Warrior("W"); // ATK=22
        Enemy dummy = new Enemy("D", 200, 1, 0, AttackType.PHYSICAL, 0.0);
        CombatSystem cs = new CombatSystem(NEVER_CRIT);
        int dmg = cs.executeAttack(attacker, dummy, AttackType.PHYSICAL, 0);
        assertEquals(attacker.getAttack(), dmg);
    }

    @Test
    void damage_minimumZero_whenDefenseExceedsAttack() {
        Warrior attacker = new Warrior("W");
        Enemy tank = new Enemy("T", 200, 1, 999, AttackType.PHYSICAL, 0.0);
        CombatSystem cs = new CombatSystem(NEVER_CRIT);
        int dmg = cs.executeAttack(attacker, tank, AttackType.PHYSICAL, 0);
        assertEquals(0, dmg);
    }

    @Test
    void thief_stealthCrit_alwaysDealsDoubleDamage() {
        Thief t = new Thief("L");
        t.applyPassiveBonus();
        Enemy dummy = new Enemy("D", 200, 1, 0, AttackType.PHYSICAL, 0.0);
        CombatSystem cs = new CombatSystem(NEVER_CRIT);
        int dmg = cs.executeAttack(t, dummy, AttackType.PHYSICAL, 0);
        assertEquals(t.getAttack() * 2, dmg);
    }

    @Test
    void thief_afterStealthConsumed_normalDamage() {
        Thief t = new Thief("L");
        t.applyPassiveBonus();
        Enemy dummy = new Enemy("D", 200, 1, 0, AttackType.PHYSICAL, 0.0);
        CombatSystem cs = new CombatSystem(NEVER_CRIT);
        cs.executeAttack(t, dummy, AttackType.PHYSICAL, 0);
        int dmg2 = cs.executeAttack(t, dummy, AttackType.PHYSICAL, 0);
        assertTrue(dmg2 <= t.getAttack());
    }
}
