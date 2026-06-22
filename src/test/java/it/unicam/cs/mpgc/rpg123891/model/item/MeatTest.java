package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica Meat:
 *  - use() ripristina 2 Stamina (NON HP)
 *  - non supera maxStamina
 *  - nome = "Carne"
 */
public class MeatTest {

    @Test
    void use_restores2Stamina() {
        Warrior w = new Warrior("Test");
        // Consuma tutta la stamina
        while (w.getCurrentStamina() > 0) w.consumeStaminaForAttack();
        new Meat().use(w);
        assertEquals(2, w.getCurrentStamina());
    }

    @Test
    void use_doesNotExceedMaxStamina() {
        Warrior w = new Warrior("Test"); // stamina piena
        int before = w.getCurrentStamina();
        new Meat().use(w);
        assertEquals(Math.min(w.getMaxStamina(), before + 2), w.getCurrentStamina());
    }

    @Test
    void use_doesNotChangeHP() {
        Warrior w = new Warrior("Test");
        int hpBefore = w.getCurrentHp();
        new Meat().use(w);
        assertEquals(hpBefore, w.getCurrentHp());
    }

    @Test
    void meat_hasCorrectName() {
        assertEquals("Carne", new Meat().getName());
    }
}
