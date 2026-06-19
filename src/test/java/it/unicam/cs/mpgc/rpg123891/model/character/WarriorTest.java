package it.unicam.cs.mpgc.rpg123891.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Warrior.
 * Verifica le statistiche iniziali, i metodi ereditati da GameCharacter
 * e il comportamento del bonus passivo.
 */
class WarriorTest {

    private Warrior warrior;

    @BeforeEach
    void setUp() {
        warrior = new Warrior("Eroe");
    }

    @Test
    @DisplayName("Il Warrior inizia con HP pieni")
    void testInitialHpFull() {
        assertEquals(warrior.getMaxHp(), warrior.getCurrentHp());
    }

    @Test
    @DisplayName("Il Warrior è vivo all'inizio")
    void testIsAliveAtStart() {
        assertTrue(warrior.isAlive());
    }

    @Test
    @DisplayName("takeDamage riduce gli HP correttamente tenendo conto della difesa")
    void testTakeDamageReducesHp() {
        int hpBefore = warrior.getCurrentHp();
        int damage = 20;
        warrior.takeDamage(damage);
        int expectedHp = hpBefore - Math.max(0, damage - warrior.getDefense());
        assertEquals(expectedHp, warrior.getCurrentHp());
    }

    @Test
    @DisplayName("takeDamage non porta gli HP sotto zero")
    void testHpNeverBelowZero() {
        warrior.takeDamage(99999);
        assertEquals(0, warrior.getCurrentHp());
    }

    @Test
    @DisplayName("heal ripristina HP senza superare il massimo")
    void testHealDoesNotExceedMaxHp() {
        warrior.takeDamage(30);
        warrior.heal(99999);
        assertEquals(warrior.getMaxHp(), warrior.getCurrentHp());
    }

    @Test
    @DisplayName("Il personaggio muore quando gli HP raggiungono zero")
    void testCharacterDiesAtZeroHp() {
        warrior.takeDamage(99999);
        assertFalse(warrior.isAlive());
    }

    @Test
    @DisplayName("Il Warrior ha la classe WARRIOR")
    void testCharacterClass() {
        assertEquals(CharacterClass.WARRIOR, warrior.getCharacterClass());
    }

    @Test
    @DisplayName("addItem aggiunge un oggetto all'inventario")
    void testAddItemToInventory() {
        var potion = new it.unicam.cs.mpgc.rpg123891.model.item.Potion("Pozione", 30);
        warrior.addItem(potion);
        assertEquals(1, warrior.getInventory().size());
    }
}
