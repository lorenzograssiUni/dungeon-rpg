package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica tutti i costruttori di Potion e che use() sia consistente.
 */
public class PotionConstructorTest {

    @Test
    void potion_defaultConstructor_40hp_5stamina() {
        Potion p = new Potion();
        assertEquals(40, p.getHealAmount());
        assertEquals(5,  p.getStaminaAmount());
    }

    @Test
    void potion_nameInt_constructor_5staminaDefault() {
        Potion p = new Potion("Pozione", 30);
        assertEquals(30, p.getHealAmount());
        assertEquals(5,  p.getStaminaAmount());
        assertEquals("Pozione", p.getName());
    }

    @Test
    void potion_nameIntInt_constructor() {
        Potion p = new Potion("P", 20, 10);
        assertEquals(20, p.getHealAmount());
        assertEquals(10, p.getStaminaAmount());
    }

    @Test
    void potion_fullConstructor() {
        Potion p = new Potion("Grande", "Cura totale", 80, 15);
        assertEquals(80, p.getHealAmount());
        assertEquals(15, p.getStaminaAmount());
        assertEquals("Grande", p.getName());
        assertEquals("Cura totale", p.getDescription());
    }

    @Test
    void potion_use_healsHp() {
        Warrior w = new Warrior("G");
        w.takeDamage(50); // perde 42 HP (50-8)
        int hpBefore = w.getCurrentHp();
        new Potion().use(w);
        assertEquals(hpBefore + 40, w.getCurrentHp());
    }

    @Test
    void potion_use_restoresStamina() {
        Warrior w = new Warrior("G");
        while (w.getCurrentStamina() > 0) w.consumeStaminaForAttack();
        new Potion().use(w);
        assertEquals(5, w.getCurrentStamina());
    }

    @Test
    void potion_use_doesNotExceedMaxHp() {
        Warrior w = new Warrior("G"); // hp piena
        int maxHp = w.getMaxHp();
        new Potion().use(w);
        assertEquals(maxHp, w.getCurrentHp());
    }

    @Test
    void potion_equals_sameNameAndAmount() {
        Potion p1 = new Potion("P", 40, 5);
        Potion p2 = new Potion("P", 40, 5);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void potion_notEquals_differentAmount() {
        Potion p1 = new Potion("P", 40, 5);
        Potion p2 = new Potion("P", 20, 5);
        assertNotEquals(p1, p2);
    }
}
