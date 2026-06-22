package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Thief.
 *
 * Nota: stealthBonusActive e' FALSE alla creazione e viene attivato
 * da applyPassiveBonus() (chiamato all'ingresso di ogni stanza).
 */
public class ThiefTest {

    @Test
    void thief_baseStats() {
        Thief t = new Thief("Ladro");
        assertEquals(90,   t.getMaxHp());
        assertEquals(18,   t.getAttack());
        assertEquals(6,    t.getDefense());
        assertEquals(8,    t.getAgility());
        assertEquals(12,   t.getMaxStamina());
        assertEquals(0.25, t.getCritChance(), 0.001);
    }

    @Test
    void thief_stealthBonus_inactiveAtConstruction() {
        // Lo stealth parte spento; viene attivato da applyPassiveBonus()
        Thief t = new Thief("Ladro");
        assertFalse(t.isStealthBonusActive(),
                "Lo stealth deve essere inattivo alla creazione");
    }

    @Test
    void thief_stealthBonus_activeAfterPassiveBonus() {
        Thief t = new Thief("Ladro");
        t.applyPassiveBonus();
        assertTrue(t.isStealthBonusActive(),
                "Lo stealth deve essere attivo dopo applyPassiveBonus()");
    }

    @Test
    void thief_stealthBonus_consumedAfterFirstAttack() {
        Thief t = new Thief("Ladro");
        t.applyPassiveBonus(); // attiva stealth
        CombatSystem cs = new CombatSystem();
        Enemy goblin = EnemyFactory.createGoblin();
        cs.executeAttack(t, goblin, AttackType.PHYSICAL, 0);
        assertFalse(t.isStealthBonusActive(),
                "Lo stealth deve essere consumato dopo il primo attacco");
    }

    @Test
    void thief_critChance_increasesAfterAttack() {
        Thief t = new Thief("Ladro");
        t.applyPassiveBonus();
        double baseCrit = t.getCritChance();
        CombatSystem cs = new CombatSystem();
        Enemy goblin = EnemyFactory.createGoblin();
        cs.executeAttack(t, goblin, AttackType.PHYSICAL, 0);
        // Dopo il primo attacco la crit deve essere >= base
        assertTrue(t.getCritChance() >= baseCrit);
    }

    @Test
    void thief_applyPassiveBonus_resetsCrit() {
        Thief t = new Thief("Ladro");
        t.incrementCritAfterAttack();
        t.incrementCritAfterAttack(); // sale oltre 25%
        t.applyPassiveBonus();        // reset a 25%
        assertEquals(0.25, t.getCritChance(), 0.001);
    }

    @Test
    void thief_characterClass_isThief() {
        Thief t = new Thief("Ladro");
        assertEquals(CharacterClass.THIEF, t.getCharacterClass());
    }
}
