package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.MagicStaff;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Greatsword;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.DualDaggers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica gli attacchi speciali di tutte le armi:
 * costo stamina > 0, nome non vuoto, esecuzione infligge danno o produce effetto.
 */
public class SpecialAttackTest {

    private Enemy dummy() {
        return new Enemy("D", 500, 1, 0, AttackType.PHYSICAL, 0.0);
    }

    // ---- MagicStaff specials ----

    @Test
    void magicStaff_allSpecials_havePositiveStaminaCost() {
        MagicStaff staff = new MagicStaff();
        staff.getSpecialAttacks().forEach(sa ->
                assertTrue(sa.getStaminaCost() > 0,
                        sa.getName() + " deve avere costo stamina > 0"));
    }

    @Test
    void magicStaff_allSpecials_haveNonBlankName() {
        MagicStaff staff = new MagicStaff();
        staff.getSpecialAttacks().forEach(sa ->
                assertFalse(sa.getName().isBlank()));
    }

    @Test
    void magicStaff_bolto_dealsDamage() {
        Mage m = new Mage("M");
        m.applyPassiveBonus();
        MagicStaff staff = new MagicStaff();
        SpecialAttack bolto = staff.getSpecialAttacks().stream()
                .filter(sa -> sa.getName().contains("Bolt") || sa.getName().contains("Fulmine") || sa.getName().contains("Folgore"))
                .findFirst().orElse(staff.getSpecialAttacks().get(0));
        Enemy e = dummy();
        int dmg = bolto.execute(m, e);
        assertTrue(dmg > 0, "Lo speciale del Bastone deve infliggere danno");
    }

    // ---- Greatsword specials ----

    @Test
    void greatsword_allSpecials_havePositiveStaminaCost() {
        Greatsword g = new Greatsword();
        g.getSpecialAttacks().forEach(sa ->
                assertTrue(sa.getStaminaCost() > 0));
    }

    @Test
    void greatsword_allSpecials_haveNonBlankName() {
        Greatsword g = new Greatsword();
        g.getSpecialAttacks().forEach(sa ->
                assertFalse(sa.getName().isBlank()));
    }

    // ---- DualDaggers specials ----

    @Test
    void dualDaggers_allSpecials_havePositiveStaminaCost() {
        DualDaggers dd = new DualDaggers();
        dd.getSpecialAttacks().forEach(sa ->
                assertTrue(sa.getStaminaCost() > 0));
    }

    @Test
    void dualDaggers_allSpecials_haveNonBlankName() {
        DualDaggers dd = new DualDaggers();
        dd.getSpecialAttacks().forEach(sa ->
                assertFalse(sa.getName().isBlank()));
    }

    @Test
    void dualDaggers_firstSpecial_dealsDamage() {
        it.unicam.cs.mpgc.rpg123891.model.character.Thief t =
                new it.unicam.cs.mpgc.rpg123891.model.character.Thief("L");
        t.applyPassiveBonus();
        DualDaggers dd = new DualDaggers();
        SpecialAttack sa = dd.getSpecialAttacks().get(0);
        Enemy e = dummy();
        int dmg = sa.execute(t, e);
        assertTrue(dmg > 0);
    }

    // ---- Stamina insufficiente ----

    @Test
    void specialAttack_insufficientStamina_doesNotExecute() {
        Warrior w = new Warrior("G");
        // Svuota tutta la stamina
        while (w.getCurrentStamina() > 0) w.consumeStaminaForAttack();
        Greatsword g = new Greatsword();
        SpecialAttack sa = g.getSpecialAttacks().get(0);
        assertFalse(w.canUseSpecial(sa.getStaminaCost()),
                "Non deve poter usare lo speciale senza stamina");
    }
}
