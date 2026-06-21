package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Sword;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.DualDaggers;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.MagicStaff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per InventoryManager con il nuovo sistema di armi (Weapon astratta).
 * Le armi concrete usate: Sword (MAIN_HAND), DualDaggers (MAIN_HAND 2h), MagicStaff.
 */
class InventoryManagerTest {

    private Warrior warrior;
    private InventoryManager manager;

    @BeforeEach
    void setUp() {
        warrior = new Warrior("TestWarrior");
        warrior.addItem(new Sword());
        warrior.addItem(new DualDaggers());
        warrior.addItem(new Potion());
        manager = new InventoryManager(warrior);
    }

    @Test
    @DisplayName("getWeapons restituisce solo le armi nell'inventario")
    void testGetWeapons() {
        List<Weapon> weapons = manager.getWeapons();
        assertEquals(2, weapons.size());
        assertTrue(weapons.stream().anyMatch(w -> w.getName().equals("Spada Semplice")));
        assertTrue(weapons.stream().anyMatch(w -> w.getName().equals("Doppie Daghe")));
    }

    @Test
    @DisplayName("getPotions restituisce solo le pozioni")
    void testGetPotions() {
        List<Potion> potions = manager.getPotions();
        assertEquals(1, potions.size());
    }

    @Test
    @DisplayName("usePotion usa e rimuove la prima pozione")
    void testUsePotion() {
        int hpBefore = warrior.getCurrentHp();
        warrior.takeDamage(30); // toglie 30 - 8 DEF = 22 HP
        assertTrue(manager.usePotion());
        assertTrue(warrior.getCurrentHp() > warrior.getCurrentHp() - 40);
        assertEquals(0, manager.countPotions(), "La pozione deve essere rimossa dopo l'uso");
    }

    @Test
    @DisplayName("usePotion restituisce false se non ci sono pozioni")
    void testUsePotionEmpty() {
        Warrior w2 = new Warrior("Empty");
        InventoryManager emptyManager = new InventoryManager(w2);
        assertFalse(emptyManager.usePotion());
    }

    @Test
    @DisplayName("getAllSpecials raccoglie gli attacchi speciali di tutte le armi")
    void testGetAllSpecials() {
        // Sword ha 2 speciali, DualDaggers ha 2 speciali -> totale 4
        List<SpecialAttack> specials = manager.getAllSpecials();
        assertEquals(4, specials.size());
        assertTrue(specials.stream().anyMatch(s -> s.getName().equals("Fendente")));
        assertTrue(specials.stream().anyMatch(s -> s.getName().equals("Carica!")));
        assertTrue(specials.stream().anyMatch(s -> s.getName().equals("Sfuriata")));
        assertTrue(specials.stream().anyMatch(s -> s.getName().equals("Ira")));
    }

    @Test
    @DisplayName("hasItem trova un oggetto per nome")
    void testHasItem() {
        assertTrue(manager.hasItem("Spada Semplice"));
        assertTrue(manager.hasItem("Pozione"));
        assertFalse(manager.hasItem("Spadone"));
    }

    @Test
    @DisplayName("getItemNames restituisce i nomi di tutti gli oggetti")
    void testGetItemNames() {
        List<String> names = manager.getItemNames();
        assertEquals(3, names.size());
        assertTrue(names.contains("Spada Semplice"));
        assertTrue(names.contains("Doppie Daghe"));
        assertTrue(names.contains("Pozione"));
    }

    @Test
    @DisplayName("useMeat usa e rimuove la carne ripristinando stamina")
    void testUseMeat() {
        Warrior w2 = new Warrior("MeatTest");
        w2.consumeStaminaForAttack(); // stamina 8 -> 7
        w2.consumeStaminaForAttack(); // stamina 7 -> 6
        w2.addItem(new Meat());
        InventoryManager m2 = new InventoryManager(w2);

        int staBefore = w2.getCurrentStamina(); // 6
        assertTrue(m2.useMeat());
        assertEquals(staBefore + 2, w2.getCurrentStamina()); // 6 + 2 = 8
        assertEquals(0, m2.countMeats(), "La carne deve essere rimossa dopo l'uso");
    }
}
