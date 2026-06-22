package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArmorShieldAmuletTest {

    // ---- Armor ----

    @Test
    void armor_hasName() {
        Armor a = new Armor();
        assertNotNull(a.getName());
        assertFalse(a.getName().isBlank());
    }

    @Test
    void armor_slot_isBody() {
        Armor a = new Armor();
        assertEquals(EquipSlot.BODY, a.getSlot());
    }

    @Test
    void armor_modifier_warrior_increasesDefense() {
        Armor a = new Armor();
        StatModifier mod = a.getModifierFor(CharacterClass.WARRIOR);
        assertTrue(mod.defenseDelta() > 0);
    }

    @Test
    void armor_equals_sameInstance() {
        Armor a = new Armor();
        assertEquals(a, a);
    }

    // ---- Shield ----

    @Test
    void shield_hasName() {
        Shield s = new Shield();
        assertNotNull(s.getName());
        assertFalse(s.getName().isBlank());
    }

    @Test
    void shield_slot_isOffHand() {
        Shield s = new Shield();
        assertEquals(EquipSlot.OFF_HAND, s.getSlot());
    }

    @Test
    void shield_modifier_warrior_increasesDefense() {
        Shield s = new Shield();
        StatModifier mod = s.getModifierFor(CharacterClass.WARRIOR);
        assertTrue(mod.defenseDelta() > 0);
    }

    @Test
    void shield_equals_differentInstances() {
        Shield s1 = new Shield();
        Shield s2 = new Shield();
        assertEquals(s1, s2);
    }

    // ---- MagicAmulet ---- (slot BODY in questo progetto)

    @Test
    void amulet_hasName() {
        MagicAmulet a = new MagicAmulet();
        assertNotNull(a.getName());
        assertFalse(a.getName().isBlank());
    }

    @Test
    void amulet_slot_isBody() {
        MagicAmulet a = new MagicAmulet();
        assertEquals(EquipSlot.BODY, a.getSlot());
    }

    @Test
    void amulet_modifier_mage_hasPositiveBonus() {
        MagicAmulet a = new MagicAmulet();
        StatModifier mod = a.getModifierFor(CharacterClass.MAGE);
        assertTrue(mod.attackDelta() > 0 || mod.maxHpDelta() > 0 || mod.defenseDelta() > 0);
    }
}
