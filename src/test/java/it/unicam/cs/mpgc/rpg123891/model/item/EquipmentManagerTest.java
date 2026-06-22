package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Greatsword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class EquipmentManagerTest {

    private Warrior warrior;
    private EquipmentManager em;
    private Greatsword sword;

    @BeforeEach
    void setUp() {
        warrior = new Warrior("G");
        em      = new EquipmentManager(warrior);
        sword   = new Greatsword();
        warrior.addItem(sword);
    }

    @Test
    void equip_weaponAppliesStatModifiers() {
        int atkBefore = warrior.getAttack();
        em.equip(sword);
        assertTrue(warrior.getAttack() >= atkBefore,
                "Equipaggiare deve applicare i bonus stat");
    }

    @Test
    void equip_weaponAppearsInSlot() {
        em.equip(sword);
        Optional<Weapon> eq = em.getEquipped(sword.getSlot());
        assertTrue(eq.isPresent());
        assertEquals(sword, eq.get());
    }

    @Test
    void unequip_removesWeaponFromSlot() {
        em.equip(sword);
        em.unequip(sword.getSlot());
        assertTrue(em.getEquipped(sword.getSlot()).isEmpty());
    }

    @Test
    void unequip_revertsStatModifiers() {
        int atkBefore = warrior.getAttack();
        em.equip(sword);
        em.unequip(sword.getSlot());
        assertEquals(atkBefore, warrior.getAttack());
    }

    @Test
    void canEquip_returnsFalse_whenSlotOccupied() {
        em.equip(sword);
        Greatsword sword2 = new Greatsword();
        warrior.addItem(sword2);
        EquipmentManager.EquipResult result = em.canEquip(sword2);
        assertFalse(result.success());
    }

    @Test
    void getEquippedSpecials_returnsSpecialsOfEquippedWeapons() {
        em.equip(sword);
        assertFalse(em.getEquippedSpecials().isEmpty(),
                "Lo Spadone deve avere almeno un attacco speciale");
    }
}
