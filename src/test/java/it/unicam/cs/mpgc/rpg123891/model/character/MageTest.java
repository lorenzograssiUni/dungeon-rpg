package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MageTest {

    @Test
    void mage_baseStats() {
        Mage m = new Mage("Mago");
        assertEquals(75,   m.getMaxHp());
        assertEquals(15,   m.getAttack());
        assertEquals(4,    m.getDefense());
        assertEquals(6,    m.getAgility());
        assertEquals(10,   m.getMaxStamina());
        assertEquals(0.05, m.getCritChance(), 0.001);
    }

    @Test
    void mage_magicShield_activeAtStart() {
        Mage m = new Mage("Mago");
        assertTrue(m.isMagicShieldActive());
    }

    @Test
    void mage_magicShield_absorbsFirstPhysicalAttack() {
        Mage m = new Mage("Mago");
        int hpBefore = m.getCurrentHp();
        // Attacchiamo con CombatSystem deterministico (crit=0)
        CombatSystem cs = new CombatSystem(new java.util.Random(0));
        Enemy goblin = EnemyFactory.createGoblin();
        cs.executeAttack(goblin, m, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, m.getCurrentHp(), "Lo scudo deve assorbire il primo fisico");
        assertFalse(m.isMagicShieldActive(), "Lo scudo deve disattivarsi dopo l'assorbimento");
    }

    @Test
    void mage_magicShield_secondAttackDealsNormalDamage() {
        Mage m = new Mage("Mago");
        CombatSystem cs = new CombatSystem(new java.util.Random(0));
        Enemy goblin = EnemyFactory.createGoblin();
        cs.executeAttack(goblin, m, AttackType.PHYSICAL, 0); // scudo
        int hpAfterShield = m.getCurrentHp();
        cs.executeAttack(goblin, m, AttackType.PHYSICAL, 0); // danno reale
        assertTrue(m.getCurrentHp() < hpAfterShield);
    }

    @Test
    void mage_magicVulnerability_increasesMagicalDamage() {
        Mage m = new Mage("Mago");
        m.setMagicShieldActive(false); // scudo spento per testare vulnerabilita'
        int hpBefore = m.getCurrentHp();
        CombatSystem cs = new CombatSystem(new java.util.Random(0));
        Enemy goblin = EnemyFactory.createGoblin(); // ATK basso
        cs.executeAttack(goblin, m, AttackType.MAGICAL, 0);
        // danno magico = (atk - def) * 1.30 arrotondato
        int expected = (int)((Math.max(0, goblin.getAttack() - m.getDefense())) * 1.30);
        assertEquals(hpBefore - expected, m.getCurrentHp());
    }

    @Test
    void mage_applyPassiveBonus_reactivatesShield() {
        Mage m = new Mage("Mago");
        m.setMagicShieldActive(false);
        m.applyPassiveBonus();
        assertTrue(m.isMagicShieldActive());
    }

    @Test
    void mage_characterClass_isMage() {
        Mage m = new Mage("Mago");
        assertEquals(CharacterClass.MAGE, m.getCharacterClass());
    }
}
