package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.item.InventoryManager;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per InventoryManager.
 * Verifica i metodi basati su Stream API (lezione 15):
 * filter, map, collect, count, max, findFirst, anyMatch.
 */
class InventoryManagerTest {

    private Warrior warrior;
    private InventoryManager manager;

    @BeforeEach
    void setUp() {
        warrior = new Warrior("Eroe");
        warrior.addItem(new Potion("Pozione Piccola", 20));
        warrior.addItem(new Potion("Pozione Grande", 50));
        warrior.addItem(new Weapon("Spada", "Desc", 10, 0.1));
        warrior.addItem(new Weapon("Pugnale", "Desc", 5, 0.15));
        manager = new InventoryManager(warrior);
    }

    @Test
    @DisplayName("getPotions() restituisce solo le pozioni (filter + map)")
    void testGetPotions() {
        List<Potion> potions = manager.getPotions();
        assertEquals(2, potions.size());
        assertTrue(potions.stream().allMatch(p -> p instanceof Potion));
    }

    @Test
    @DisplayName("getWeapons() restituisce solo le armi (filter + map)")
    void testGetWeapons() {
        List<Weapon> weapons = manager.getWeapons();
        assertEquals(2, weapons.size());
    }

    @Test
    @DisplayName("countPotions() conta correttamente le pozioni (filter + count)")
    void testCountPotions() {
        assertEquals(2, manager.countPotions());
    }

    @Test
    @DisplayName("getTotalAttackBonus() somma i bonus ATK di tutte le armi (mapToInt + sum)")
    void testTotalAttackBonus() {
        assertEquals(15, manager.getTotalAttackBonus()); // 10 + 5
    }

    @Test
    @DisplayName("getBestWeapon() restituisce l'arma con ATK più alto (max)")
    void testGetBestWeapon() {
        assertTrue(manager.getBestWeapon().isPresent());
        assertEquals("Spada", manager.getBestWeapon().get().getName());
    }

    @Test
    @DisplayName("getFirstPotion() restituisce la prima pozione (findFirst)")
    void testGetFirstPotion() {
        assertTrue(manager.getFirstPotion().isPresent());
        assertEquals("Pozione Piccola", manager.getFirstPotion().get().getName());
    }

    @Test
    @DisplayName("getItemNames() restituisce i nomi di tutti gli oggetti (map + collect)")
    void testGetItemNames() {
        List<String> names = manager.getItemNames();
        assertEquals(4, names.size());
        assertTrue(names.contains("Spada"));
        assertTrue(names.contains("Pozione Piccola"));
    }

    @Test
    @DisplayName("hasItem() trova un oggetto per nome (anyMatch)")
    void testHasItem() {
        assertTrue(manager.hasItem("Spada"));
        assertFalse(manager.hasItem("OggettoInesistente"));
    }

    @Test
    @DisplayName("getBestWeapon() restituisce empty su inventario senza armi")
    void testBestWeaponEmptyInventory() {
        Warrior nudo = new Warrior("Senzarmi");
        InventoryManager emptyManager = new InventoryManager(nudo);
        assertTrue(emptyManager.getBestWeapon().isEmpty());
    }
}
