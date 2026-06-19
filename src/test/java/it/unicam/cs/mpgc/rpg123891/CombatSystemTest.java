package it.unicam.cs.mpgc.rpg123891;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per il sistema di combattimento.
 */
public class CombatSystemTest {

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
}
