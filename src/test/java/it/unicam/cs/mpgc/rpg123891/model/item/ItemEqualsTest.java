package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Sword;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.DualDaggers;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Greatsword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di equals/hashCode per Weapon e Potion.
 * Weapon usa il nome come chiave di uguaglianza.
 */
class ItemEqualsTest {

    // -----------------------------------------------------------------------
    // Potion equals
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Due Potion con stessa quantita' e nome sono uguali")
    void testPotionEquals() {
        Potion p1 = new Potion("Pozione", 40);
        Potion p2 = new Potion("Pozione", 40);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("Due Potion con quantita' diversa non sono uguali")
    void testPotionNotEquals() {
        Potion p1 = new Potion("Pozione", 40);
        Potion p2 = new Potion("Pozione Grande", 80);
        assertNotEquals(p1, p2);
    }

    @Test
    @DisplayName("Potion in HashSet: duplicati non inseriti")
    void testPotionInSet() {
        Set<Potion> set = new HashSet<>();
        set.add(new Potion("Pozione", 40));
        set.add(new Potion("Pozione", 40)); // duplicato
        assertEquals(1, set.size());
    }

    // -----------------------------------------------------------------------
    // Weapon equals (basato sul nome)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Due Sword sono uguali (stesso tipo = stesso nome)")
    void testWeaponEquals() {
        Weapon w1 = new Sword();
        Weapon w2 = new Sword();
        assertEquals(w1, w2);
        assertEquals(w1.hashCode(), w2.hashCode());
    }

    @Test
    @DisplayName("Sword e DualDaggers non sono uguali")
    void testWeaponNotEquals() {
        Weapon w1 = new Sword();
        Weapon w2 = new DualDaggers();
        assertNotEquals(w1, w2);
    }

    @Test
    @DisplayName("Weapon in HashSet: duplicati non inseriti")
    void testWeaponInSet() {
        Set<Weapon> set = new HashSet<>();
        set.add(new Sword());
        set.add(new Sword());      // duplicato, non inserito
        set.add(new Greatsword()); // diverso, inserito
        assertEquals(2, set.size());
    }

    // -----------------------------------------------------------------------
    // Meat equals
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Due Meat sono sempre uguali")
    void testMeatEquals() {
        Meat m1 = new Meat();
        Meat m2 = new Meat();
        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }
}
