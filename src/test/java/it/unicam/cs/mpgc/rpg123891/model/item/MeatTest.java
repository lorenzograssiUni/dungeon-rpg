package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica Meat:
 *  - use() aumenta HP di 40 (fino al massimo)
 *  - non supera il maxHp
 */
public class MeatTest {

    @Test
    void use_heals40Hp() {
        Warrior w = new Warrior("Test");
        w.takeDamage(60); // scende di 60 HP
        int hpBefore = w.getCurrentHp();
        new Meat().use(w);
        assertEquals(Math.min(w.getMaxHp(), hpBefore + 40), w.getCurrentHp());
    }

    @Test
    void use_doesNotExceedMaxHp() {
        Warrior w = new Warrior("Test"); // HP pieno
        new Meat().use(w);
        assertEquals(w.getMaxHp(), w.getCurrentHp());
    }

    @Test
    void meat_hasCorrectName() {
        assertEquals("Carne", new Meat().getName());
    }
}
