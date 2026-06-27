package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PotionTest {

    private Potion potion;
    private Warrior warrior;

    @BeforeEach
    void setUp() {
        // default: heal=30, stamina=5
        potion  = new Potion();
        warrior = new Warrior("Guerriero");
    }

    @Test
    void potion_healAmount_isDefault() {
        assertEquals(30, potion.getHealAmount());
    }

    @Test
    void potion_use_healsCorrectly() {
        warrior.takeDamage(50); // netto: 50-8=42
        int hpBefore = warrior.getCurrentHp();
        potion.use(warrior);
        assertEquals(Math.min(hpBefore + 30, warrior.getMaxHp()), warrior.getCurrentHp());
    }

    @Test
    void potion_use_doesNotExceedMaxHp() {
        potion.use(warrior); // hp gia' piena
        assertEquals(warrior.getMaxHp(), warrior.getCurrentHp());
    }
}
