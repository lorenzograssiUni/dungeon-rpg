package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica Meat (GAME_SPEC: ripristina 40 HP e 3 stamina).
 */
public class MeatTest {

    @Test
    void use_restores3Stamina() {
        Warrior w = new Warrior("Test");
        // Consuma tutta la stamina
        while (w.getCurrentStamina() > 0) w.consumeStaminaForAttack();
        new Meat().use(w);
        assertEquals(3, w.getCurrentStamina(),
                "La Carne deve ripristinare 3 stamina (GAME_SPEC)");
    }

    @Test
    void use_doesNotExceedMaxStamina() {
        Warrior w = new Warrior("Test"); // stamina piena
        int before = w.getCurrentStamina();
        new Meat().use(w);
        assertEquals(Math.min(w.getMaxStamina(), before + 3), w.getCurrentStamina(),
                "La Carne non deve superare la stamina massima");
    }

    @Test
    void use_restores40HP() {
        Warrior w = new Warrior("Test");
        w.takeDamage(9999); // porta a 0
        new Meat().use(w);
        assertEquals(40, w.getCurrentHp(),
                "La Carne deve ripristinare 40 HP (GAME_SPEC)");
    }

    @Test
    void use_doesNotExceedMaxHp() {
        Warrior w = new Warrior("Test"); // hp piena
        int maxHp = w.getMaxHp();
        new Meat().use(w);
        assertEquals(maxHp, w.getCurrentHp(),
                "La Carne non deve superare gli HP massimi");
    }

    @Test
    void meat_hasCorrectName() {
        assertEquals("Carne", new Meat().getName());
    }
}
