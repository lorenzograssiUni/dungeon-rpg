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
 * Stat base (GAME_SPEC): HP 100, ATK 18, DEF 6, AGI 8, STA 10, CRIT 25%
 *
 * Passive:
 *   - stealthBonusActive parte TRUE alla creazione.
 *   - applyPassiveBonus() riattiva stealth, resetta crit a 25%, +2 STA.
 *   - Primo attacco: critico garantito, stealth consumato.
 *   - Dopo ogni attacco normale: +2% crit (cap 50%).
 */
public class ThiefTest {

    @Test
    void thief_baseStats() {
        Thief t = new Thief("Ladro");
        assertEquals(100,  t.getMaxHp(),      "HP base deve essere 100");
        assertEquals(18,   t.getAttack(),      "ATK base deve essere 18");
        assertEquals(6,    t.getDefense(),     "DEF base deve essere 6");
        assertEquals(8,    t.getAgility(),     "AGI base deve essere 8");
        assertEquals(10,   t.getMaxStamina(),  "STA base deve essere 10");
        assertEquals(0.25, t.getCritChance(), 0.001, "CRIT base deve essere 25%");
    }

    @Test
    void thief_stealthBonus_activeAtConstruction() {
        // Lo stealth parte attivo alla creazione
        Thief t = new Thief("Ladro");
        assertTrue(t.isStealthBonusActive(),
                "Lo stealth deve essere attivo alla creazione");
    }

    @Test
    void thief_stealthBonus_activeAfterPassiveBonus() {
        Thief t = new Thief("Ladro");
        t.consumeStealthBonus(); // consuma manualmente
        assertFalse(t.isStealthBonusActive());
        t.applyPassiveBonus();   // deve riattivarlo
        assertTrue(t.isStealthBonusActive(),
                "Lo stealth deve essere riattivato da applyPassiveBonus()");
    }

    @Test
    void thief_stealthBonus_consumedAfterFirstAttack() {
        Thief t = new Thief("Ladro");
        assertTrue(t.isStealthBonusActive());
        CombatSystem cs = new CombatSystem();
        Enemy goblin = EnemyFactory.createGoblin();
        cs.executeAttack(t, goblin, AttackType.PHYSICAL, 0);
        assertFalse(t.isStealthBonusActive(),
                "Lo stealth deve essere consumato dopo il primo attacco");
    }

    @Test
    void thief_critChance_increasesAfterAttack() {
        Thief t = new Thief("Ladro");
        double baseCrit = t.getCritChance();
        CombatSystem cs = new CombatSystem();
        Enemy goblin = EnemyFactory.createGoblin();
        cs.executeAttack(t, goblin, AttackType.PHYSICAL, 0);
        assertTrue(t.getCritChance() >= baseCrit,
                "La crit chance deve aumentare dopo ogni attacco");
    }

    @Test
    void thief_applyPassiveBonus_resetsCrit() {
        Thief t = new Thief("Ladro");
        t.incrementCritAfterAttack();
        t.incrementCritAfterAttack(); // sale oltre 25%
        t.applyPassiveBonus();        // reset a 25%
        assertEquals(0.25, t.getCritChance(), 0.001,
                "applyPassiveBonus deve resettare crit a 25%");
    }

    @Test
    void thief_applyPassiveBonus_restoresStamina() {
        Thief t = new Thief("Ladro");
        while (t.getCurrentStamina() > 0) t.consumeStaminaForAttack();
        t.applyPassiveBonus();
        assertTrue(t.getCurrentStamina() > 0,
                "applyPassiveBonus deve ripristinare 2 stamina");
    }

    @Test
    void thief_characterClass_isThief() {
        Thief t = new Thief("Ladro");
        assertEquals(CharacterClass.THIEF, t.getCharacterClass());
    }
}
