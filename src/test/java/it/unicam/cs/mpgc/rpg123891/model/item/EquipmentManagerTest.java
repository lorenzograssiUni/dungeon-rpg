package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Greatsword;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Shield;
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
        assertTrue(warrior.getAttack() >= atkBefore);
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
        // Lo Spadone e' 2 mani (MAIN_HAND) → blocca OFF_HAND (Shield)
        em.equip(sword);
        Shield shield = new Shield();
        warrior.addItem(shield);
        EquipmentManager.EquipResult result = em.canEquip(shield);
        assertFalse(result.success(),
                "canEquip Shield deve fallire se MAIN_HAND ha arma a 2 mani");
    }

    @Test
    void getEquippedSpecials_returnsSpecialsOfEquippedWeapons() {
        em.equip(sword);
        assertFalse(em.getEquippedSpecials().isEmpty());
    }
}
