package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Mage.
 */
public class MageTest {

    private static final Random NO_LUCK = new Random(0) {
        @Override public double nextDouble() { return 1.0; }
    };

    @Test
    void mage_baseStats() {
        Mage m = new Mage("Mago");
        assertEquals(90,   m.getMaxHp(),      "HP base deve essere 90");
        assertEquals(15,   m.getAttack(),      "ATK base deve essere 15");
        assertEquals(4,    m.getDefense(),     "DEF base deve essere 4");
        assertEquals(6,    m.getAgility(),     "AGI base deve essere 6");
        assertEquals(15,   m.getMaxStamina(),  "STA base deve essere 15");
        assertEquals(0.05, m.getCritChance(), 0.001, "CRIT base deve essere 5%");
    }

    @Test
    void mage_physicalDamage_isLowerThanRaw() {
        Mage m = new Mage("Mago");
        int hpBefore = m.getCurrentHp();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        Enemy attacker = new Enemy("Test", 100, 30, 0, AttackType.PHYSICAL, 0.0);
        cs.executeAttack(attacker, m, AttackType.PHYSICAL, 0);
        int actualDamage = hpBefore - m.getCurrentHp();
        int rawDamage = Math.max(0, 30 - m.getDefense());
        assertTrue(actualDamage < rawDamage,
                "Il danno fisico con scudo deve essere minore del danno grezzo");
    }

    @Test
    void mage_magicVulnerability_isHigherThanRaw() {
        Mage m = new Mage("Mago");
        int hpBefore = m.getCurrentHp();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        Enemy attacker = new Enemy("Test", 100, 20, 0, AttackType.MAGICAL, 0.0);
        cs.executeAttack(attacker, m, AttackType.MAGICAL, 0);
        int actualDamage = hpBefore - m.getCurrentHp();
        int rawDamage = Math.max(0, 20 - m.getDefense());
        assertTrue(actualDamage > rawDamage,
                "Il danno magico con vulnerabilita' deve essere maggiore del danno grezzo");
    }

    @Test
    void mage_applyPassiveBonus_restoresStamina() {
        Mage m = new Mage("Mago");
        while (m.getCurrentStamina() > 0) m.consumeStaminaForAttack();
        assertEquals(0, m.getCurrentStamina());
        m.applyPassiveBonus();
        assertTrue(m.getCurrentStamina() > 0,
                "applyPassiveBonus deve ripristinare stamina");
    }

    @Test
    void mage_characterClass_isMage() {
        Mage m = new Mage("Mago");
        assertEquals(CharacterClass.MAGE, m.getCharacterClass());
    }
}
