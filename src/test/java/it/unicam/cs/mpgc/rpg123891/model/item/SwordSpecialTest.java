package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Greatsword;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Sword;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica Sword e Greatsword: nome, slot, speciali,
 * Carica! applica +3 DEF, Spazzatutto stordisce il nemico.
 */
public class SwordSpecialTest {

    // ---- Sword ----

    @Test
    void sword_hasName() {
        Sword s = new Sword();
        assertFalse(s.getName().isBlank());
    }

    @Test
    void sword_slot_isMainHand() {
        Sword s = new Sword();
        assertEquals(EquipSlot.MAIN_HAND, s.getSlot());
    }

    @Test
    void sword_hasSpecialAttacks() {
        Sword s = new Sword();
        assertFalse(s.getSpecialAttacks().isEmpty());
    }

    @Test
    void sword_modifier_warrior_hasBonus() {
        Sword s = new Sword();
        StatModifier mod = s.getModifierFor(CharacterClass.WARRIOR);
        assertTrue(mod.attackDelta() > 0 || mod.defenseDelta() > 0);
    }

    // ---- Greatsword ----

    @Test
    void greatsword_hasSpecialAttacks() {
        Greatsword g = new Greatsword();
        assertFalse(g.getSpecialAttacks().isEmpty());
    }

    @Test
    void greatsword_carica_increasesDefenseBy3() {
        Warrior w = new Warrior("G");
        Greatsword g = new Greatsword();
        // Trova l'attacco speciale "Carica!"
        SpecialAttack carica = g.getSpecialAttacks().stream()
                .filter(sa -> sa.getName().equals("Carica!"))
                .findFirst().orElse(null);
        assertNotNull(carica, "Greatsword deve avere Carica!");
        int defBefore = w.getDefense();
        w.consumeStaminaForSpecial(0); // non consumare stamina
        carica.execute(w, new Enemy("D", 100, 5, 0, AttackType.PHYSICAL, 0.0));
        assertEquals(defBefore + 3, w.getDefense());
    }

    @Test
    void greatsword_spazzatutto_stunsenemy() {
        Warrior w = new Warrior("G");
        w.applyPassiveBonus(); // stamina piena
        Greatsword g = new Greatsword();
        SpecialAttack spazza = g.getSpecialAttacks().stream()
                .filter(sa -> sa.getName().equals("Spazzatutto"))
                .findFirst().orElse(null);
        assertNotNull(spazza, "Greatsword deve avere Spazzatutto");
        Enemy e = new Enemy("D", 100, 5, 0, AttackType.PHYSICAL, 0.0);
        spazza.execute(w, e);
        assertTrue(e.isStunned(), "Spazzatutto deve stordire il nemico");
    }

    @Test
    void greatsword_spazzatutto_dealsDamage() {
        Warrior w = new Warrior("G");
        w.applyPassiveBonus();
        Greatsword g = new Greatsword();
        SpecialAttack spazza = g.getSpecialAttacks().stream()
                .filter(sa -> sa.getName().equals("Spazzatutto"))
                .findFirst().orElse(null);
        assertNotNull(spazza);
        Enemy e = new Enemy("D", 200, 5, 0, AttackType.PHYSICAL, 0.0);
        int dmg = spazza.execute(w, e);
        assertTrue(dmg > 0);
    }
}
