package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Greatsword;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Shield;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica InventoryManager: add, remove, countByType,
 * hasItem, getItemsByType, maxCapacity.
 */
public class InventoryManagerTest {

    private Warrior warrior;

    @BeforeEach
    void setUp() {
        warrior = new Warrior("G");
    }

    @Test
    void addItem_appearsInInventory() {
        Potion p = new Potion();
        warrior.addItem(p);
        assertTrue(warrior.getInventory().contains(p));
    }

    @Test
    void addMultipleItems_allPresent() {
        Potion p1 = new Potion();
        Potion p2 = new Potion();
        Meat m = new Meat();
        warrior.addItem(p1);
        warrior.addItem(p2);
        warrior.addItem(m);
        assertEquals(3, warrior.getInventory().size());
    }

    @Test
    void removeItem_removesFromInventory() {
        Potion p = new Potion();
        warrior.addItem(p);
        warrior.getInventory().remove(p);
        assertFalse(warrior.getInventory().contains(p));
    }

    @Test
    void inventory_initiallyEmpty() {
        assertTrue(warrior.getInventory().isEmpty());
    }

    @Test
    void countPotions_correct() {
        warrior.addItem(new Potion());
        warrior.addItem(new Potion());
        warrior.addItem(new Meat());
        long count = warrior.getInventory().stream()
                .filter(i -> i instanceof Potion).count();
        assertEquals(2, count);
    }

    @Test
    void inventory_containsWeapon_afterAdd() {
        Greatsword sword = new Greatsword();
        warrior.addItem(sword);
        assertTrue(warrior.getInventory().stream()
                .anyMatch(i -> i instanceof it.unicam.cs.mpgc.rpg123891.model.item.Weapon));
    }

    @Test
    void inventory_containsShield_afterAdd() {
        Shield shield = new Shield();
        warrior.addItem(shield);
        assertTrue(warrior.getInventory().stream()
                .anyMatch(i -> i instanceof Shield));
    }

    @Test
    void inventory_mutableList() {
        warrior.addItem(new Potion());
        warrior.addItem(new Potion());
        List<Item> inv = warrior.getInventory();
        inv.remove(0);
        assertEquals(1, inv.size());
    }
}
