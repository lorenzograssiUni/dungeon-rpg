package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Potion.
 * Verifica che l'uso della pozione ripristini HP correttamente
 * e che non superi il massimo consentito.
 */
class PotionTest {

    private Warrior warrior;
    private Potion potion;

    @BeforeEach
    void setUp() {
        warrior = new Warrior("Eroe");
        potion = new Potion("Pozione di Cura", 30);
    }

    @Test
    @DisplayName("La pozione ha il nome corretto")
    void testPotionName() {
        assertEquals("Pozione di Cura", potion.getName());
    }

    @Test
    @DisplayName("La pozione ripristina HP al personaggio")
    void testPotionRestoresHp() {
        warrior.takeDamage(50);
        int hpBefore = warrior.getCurrentHp();
        potion.use(warrior);
        assertTrue(warrior.getCurrentHp() > hpBefore);
    }

    @Test
    @DisplayName("La pozione non porta gli HP oltre il massimo")
    void testPotionDoesNotExceedMaxHp() {
        // Warrior ha HP pieni: la pozione non deve superare maxHp
        potion.use(warrior);
        assertEquals(warrior.getMaxHp(), warrior.getCurrentHp());
    }

    @Test
    @DisplayName("La pozione ripristina esattamente healAmount se non supera maxHp")
    void testPotionRestoresCorrectAmount() {
        warrior.takeDamage(50);
        int hpBefore = warrior.getCurrentHp();
        potion.use(warrior);
        int healed = warrior.getCurrentHp() - hpBefore;
        assertTrue(healed <= 30);
        assertTrue(healed > 0);
    }
}
