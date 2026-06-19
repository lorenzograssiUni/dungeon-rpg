package it.unicam.cs.mpgc.rpg123891.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Mage.
 * Verifica che la classe rispetti il contratto di GameCharacter
 * e che le statistiche siano coerenti con il profilo "bassa difesa, alto attacco".
 */
class MageTest {

    private Mage mage;
    private Warrior warrior;

    @BeforeEach
    void setUp() {
        mage = new Mage("Gandalf");
        warrior = new Warrior("Eroe");
    }

    @Test
    @DisplayName("Il Mage ha la classe MAGE")
    void testCharacterClass() {
        assertEquals(CharacterClass.MAGE, mage.getCharacterClass());
    }

    @Test
    @DisplayName("Il Mage ha attacco maggiore del Warrior")
    void testMageHasHigherAttackThanWarrior() {
        assertTrue(mage.getAttack() > warrior.getAttack());
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
    @DisplayName("Il Mage è vivo all'inizio")
    void testIsAlive() {
        assertTrue(mage.isAlive());
    }
}
