package it.unicam.cs.mpgc.rpg123891.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Mage.
 * Riflette il nuovo design: Mage e' un caster con ATK base basso (15)
 * ma alta stamina (10) e buona agilita' (6). Il danno viene amplificato
 * dalle armi magiche (Bastone Magico +5 ATK per il Mage).
 */
class MageTest {

    private Mage mage;
    private Warrior warrior;

    @BeforeEach
    void setUp() {
        mage    = new Mage("Gandalf");
        warrior = new Warrior("Eroe");
    }

    @Test
    @DisplayName("Il Mage ha la classe MAGE")
    void testCharacterClass() {
        assertEquals(CharacterClass.MAGE, mage.getCharacterClass());
    }

    @Test
    @DisplayName("Il Mage ha ATK base inferiore al Warrior (caster: potenziato dalle armi)")
    void testMageHasLowerBaseAttackThanWarrior() {
        assertTrue(mage.getAttack() < warrior.getAttack(),
            "Mage ATK base (" + mage.getAttack() + ") deve essere < Warrior ATK base (" + warrior.getAttack() + ")");
    }

    @Test
    @DisplayName("Il Mage ha piu' stamina del Warrior")
    void testMageHasMoreStaminaThanWarrior() {
        assertTrue(mage.getMaxStamina() > warrior.getMaxStamina(),
            "Mage stamina (" + mage.getMaxStamina() + ") deve essere > Warrior stamina (" + warrior.getMaxStamina() + ")");
    }

    @Test
    @DisplayName("Il Mage ha piu' agilita' del Warrior")
    void testMageHasHigherAgilityThanWarrior() {
        assertTrue(mage.getAgility() > warrior.getAgility(),
            "Mage agilita' (" + mage.getAgility() + ") deve essere > Warrior agilita' (" + warrior.getAgility() + ")");
    }

    @Test
    @DisplayName("Il Mage ha meno HP del Warrior")
    void testMageHasFewerHpThanWarrior() {
        assertTrue(mage.getMaxHp() < warrior.getMaxHp());
    }

    @Test
    @DisplayName("Il Mage inizia con HP pieni")
    void testInitialHpFull() {
        assertEquals(mage.getMaxHp(), mage.getCurrentHp());
    }

    @Test
    @DisplayName("Il Mage inizia con stamina piena")
    void testInitialStaminaFull() {
        assertEquals(mage.getMaxStamina(), mage.getCurrentStamina());
    }

    @Test
    @DisplayName("Il Mage e' vivo all'inizio")
    void testIsAlive() {
        assertTrue(mage.isAlive());
    }

    @Test
    @DisplayName("Lo scudo magico e' inattivo all'inizio")
    void testMagicShieldInactiveAtStart() {
        assertFalse(mage.isMagicShieldActive());
    }

    @Test
    @DisplayName("applyPassiveBonus attiva lo scudo magico e ricarica 2 stamina")
    void testApplyPassiveBonusActivatesShield() {
        mage.consumeStaminaForAttack(); // stamina 10 -> 9
        mage.applyPassiveBonus();
        assertTrue(mage.isMagicShieldActive());
        assertEquals(mage.getMaxStamina(), mage.getCurrentStamina()); // 9 + 2 = 10 (capped)
    }
}
