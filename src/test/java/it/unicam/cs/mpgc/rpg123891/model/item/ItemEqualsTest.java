package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per equals() e hashCode() di Potion, Weapon e GameCharacter.
 * Verifica il contratto: oggetti uguali devono avere lo stesso hashCode.
 * Dimostra anche l'uso corretto con HashSet (lezione 14).
 */
class ItemEqualsTest {

    // --- Potion ---

    @Test
    @DisplayName("Due pozioni con stesso nome e healAmount sono uguali")
    void testPotionEquality() {
        Potion p1 = new Potion("Pozione di Cura", 30);
        Potion p2 = new Potion("Pozione di Cura", 30);
        assertEquals(p1, p2);
    }

    @Test
    @DisplayName("Due pozioni uguali hanno lo stesso hashCode")
    void testPotionHashCode() {
        Potion p1 = new Potion("Pozione di Cura", 30);
        Potion p2 = new Potion("Pozione di Cura", 30);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("Due pozioni diverse non sono uguali")
    void testPotionInequality() {
        Potion p1 = new Potion("Piccola", 10);
        Potion p2 = new Potion("Grande", 50);
        assertNotEquals(p1, p2);
    }

    @Test
    @DisplayName("HashSet deduplica pozioni uguali correttamente")
    void testPotionInHashSet() {
        Set<Potion> set = new HashSet<>();
        set.add(new Potion("Pozione di Cura", 30));
        set.add(new Potion("Pozione di Cura", 30)); // duplicato
        assertEquals(1, set.size());
    }

    // --- Weapon ---

    @Test
    @DisplayName("Due armi con stesso nome e attackBonus sono uguali")
    void testWeaponEquality() {
        Weapon w1 = new Weapon("Spada", "Desc", 10, 0.1);
        Weapon w2 = new Weapon("Spada", "Altra desc", 10, 0.1);
        assertEquals(w1, w2);
    }

    @Test
    @DisplayName("Due armi uguali hanno lo stesso hashCode")
    void testWeaponHashCode() {
        Weapon w1 = new Weapon("Spada", "Desc", 10, 0.1);
        Weapon w2 = new Weapon("Spada", "Altra desc", 10, 0.1);
        assertEquals(w1.hashCode(), w2.hashCode());
    }

    @Test
    @DisplayName("HashSet deduplica armi uguali correttamente")
    void testWeaponInHashSet() {
        Set<Weapon> set = new HashSet<>();
        set.add(new Weapon("Spada", "Desc", 10, 0.1));
        set.add(new Weapon("Spada", "Altra", 10, 0.05)); // stesso nome+atk = uguale
        assertEquals(1, set.size());
    }

    // --- GameCharacter ---

    @Test
    @DisplayName("Due Warrior con stesso nome sono uguali")
    void testCharacterEquality() {
        Warrior w1 = new Warrior("Eroe");
        Warrior w2 = new Warrior("Eroe");
        assertEquals(w1, w2);
    }

    @Test
    @DisplayName("Due personaggi uguali hanno lo stesso hashCode")
    void testCharacterHashCode() {
        Warrior w1 = new Warrior("Eroe");
        Warrior w2 = new Warrior("Eroe");
        assertEquals(w1.hashCode(), w2.hashCode());
    }

    @Test
    @DisplayName("Warrior e Mage con stesso nome non sono uguali (classi diverse)")
    void testDifferentClassSameName() {
        Warrior warrior = new Warrior("Eroe");
        Mage mage = new Mage("Eroe");
        assertNotEquals(warrior, mage);
    }

    @Test
    @DisplayName("HashSet deduplica personaggi uguali correttamente")
    void testCharacterInHashSet() {
        Set<Warrior> set = new HashSet<>();
        set.add(new Warrior("Eroe"));
        set.add(new Warrior("Eroe")); // duplicato
        assertEquals(1, set.size());
    }
}
