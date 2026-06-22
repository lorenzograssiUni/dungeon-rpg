package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WeaponTest {

    @Test
    void magicStaff_name_and_slot() {
        Weapon w = new MagicStaff();
        assertEquals("Bastone Magico", w.getName());
        assertEquals(EquipSlot.MAIN_HAND, w.getSlot());
    }

    @Test
    void magicStaff_modifier_mage_increasesAttack() {
        Weapon w = new MagicStaff();
        StatModifier mod = w.getModifierFor(CharacterClass.MAGE);
        assertTrue(mod.attackDelta() > 0,
                "Il Bastone Magico deve aumentare l'attacco per il Mago");
    }

    @Test
    void magicStaff_hasSpecialAttacks() {
        Weapon w = new MagicStaff();
        assertFalse(w.getSpecialAttacks().isEmpty(),
                "Il Bastone Magico deve avere attacchi speciali");
    }

    @Test
    void greatsword_name_and_slot() {
        Weapon w = new Greatsword();
        assertEquals("Spadone", w.getName());
        assertEquals(EquipSlot.MAIN_HAND, w.getSlot());
    }

    @Test
    void greatsword_modifier_warrior_increasesAttack() {
        Weapon w = new Greatsword();
        StatModifier mod = w.getModifierFor(CharacterClass.WARRIOR);
        assertTrue(mod.attackDelta() > 0);
    }

    @Test
    void dualDaggers_name_and_slot() {
        Weapon w = new DualDaggers();
        assertEquals("Doppie Daghe", w.getName());
        assertEquals(EquipSlot.MAIN_HAND, w.getSlot());
    }

    @Test
    void dualDaggers_modifier_thief_increasesAgility() {
        Weapon w = new DualDaggers();
        StatModifier mod = w.getModifierFor(CharacterClass.THIEF);
        assertTrue(mod.agilityDelta() > 0 || mod.attackDelta() > 0,
                "Le Doppie Daghe devono potenziare il Ladro");
    }
}
