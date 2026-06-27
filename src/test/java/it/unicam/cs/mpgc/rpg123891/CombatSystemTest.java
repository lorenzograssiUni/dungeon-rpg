package it.unicam.cs.mpgc.rpg123891;

import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class CombatSystemTest {

    private static final Random NO_LUCK = new Random(0) {
        @Override public double nextDouble() { return 1.0; }
    };

    private static Enemy fixedEnemy() {
        return new Enemy("TestEnemy", 200, 12, 0, AttackType.PHYSICAL, 0.0);
    }

    @Test
    void testPlayerAttackDealsPositiveDamage() {
        CombatSystem cs = new CombatSystem();
        Warrior warrior = new Warrior("Test");
        Enemy goblin = EnemyFactory.createGoblin();
        int damage = cs.executeAttack(warrior, goblin, AttackType.PHYSICAL, 0);
        assertTrue(damage >= 0);
    }

    @Test
    void testWarriorHasCorrectBaseHp() {
        Warrior warrior = new Warrior("Test");
        assertEquals(120, warrior.getMaxHp());
    }

    @Test
    void testEnemyDiesWhenHpReachesZero() {
        Enemy goblin = EnemyFactory.createGoblin();
        goblin.takeDamage(9999);
        assertFalse(goblin.isAlive());
    }

    @Test
    void warrior_bloccoGarantito_alQuintoAttacco() {
        Warrior w = new Warrior("G");
        Enemy e = fixedEnemy();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        for (int i = 0; i < 4; i++) cs.executeAttack(e, w, AttackType.PHYSICAL, 0);
        int hpBefore = w.getCurrentHp();
        cs.executeAttack(e, w, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, w.getCurrentHp(), "Il 5° attacco fisico deve essere bloccato");
    }

    @Test
    void mage_scudoMagico_riduce30percentDannoFisico() {
        Mage m = new Mage("Ma");
        Enemy e = fixedEnemy();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        int hpBefore = m.getCurrentHp();
        int dmg = cs.executeAttack(e, m, AttackType.PHYSICAL, 0);
        assertEquals(4, dmg);
        assertEquals(hpBefore - 4, m.getCurrentHp());
    }
}
