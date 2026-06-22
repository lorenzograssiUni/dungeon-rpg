package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarriorTest {

    @Test
    void warrior_baseStats() {
        Warrior w = new Warrior("Guerriero");
        assertEquals(120,  w.getMaxHp());
        assertEquals(22,   w.getAttack());
        assertEquals(8,    w.getDefense());
        assertEquals(4,    w.getAgility());
        assertEquals(8,    w.getMaxStamina());
        assertEquals(0.05, w.getCritChance(), 0.001);
    }

    @Test
    void warrior_blockChance_is20percent() {
        Warrior w = new Warrior("Guerriero");
        assertEquals(0.20, w.getBlockChance(), 0.001);
    }

    @Test
    void warrior_applyPassiveBonus_increaseDefenseAndMaxHp() {
        Warrior w = new Warrior("Guerriero");
        int defBefore = w.getDefense();
        int hpBefore  = w.getMaxHp();
        w.applyPassiveBonus();
        assertEquals(defBefore + 5,  w.getDefense());
        assertEquals(hpBefore  + 20, w.getMaxHp());
    }

    @Test
    void warrior_characterClass_isWarrior() {
        Warrior w = new Warrior("Guerriero");
        assertEquals(CharacterClass.WARRIOR, w.getCharacterClass());
    }

    @Test
    void warrior_heal_doesNotExceedMaxHp() {
        Warrior w = new Warrior("Guerriero");
        w.heal(9999);
        assertEquals(w.getMaxHp(), w.getCurrentHp());
    }

    @Test
    void warrior_takeDamage_reducedByDefense() {
        Warrior w = new Warrior("Guerriero"); // DEF = 8
        w.takeDamage(20);
        assertEquals(w.getMaxHp() - 12, w.getCurrentHp()); // 20-8=12
    }

    @Test
    void warrior_potion_restoresHpAndStamina() {
        Warrior w = new Warrior("Guerriero");
        w.takeDamage(30); // perde 22 HP (30-8)
        while (w.getCurrentStamina() > 0) w.consumeStaminaForAttack();
        int hpBefore  = w.getCurrentHp();
        int staBefore = w.getCurrentStamina();
        new Potion().use(w);
        assertEquals(hpBefore  + 40, w.getCurrentHp());
        assertEquals(staBefore + 5,  w.getCurrentStamina());
    }
}
