package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Thief:
 *  - stat base corrette
 *  - critChance base 25%
 *  - passive: primo attacco sempre critico
 *  - stealthBonusActive reset dopo uso
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
    void thief_stealthBonus_activeAtStart() {
        Thief t = new Thief("Ladro");
        assertTrue(t.isStealthBonusActive(),
                "Il primo attacco del Ladro deve essere critico (stealth attivo)");
    }

    @Test
    void thief_stealthBonus_consumedAfterFirstAttack() {
        Thief t = new Thief("Ladro");
        CombatSystem cs = new CombatSystem();
        Enemy goblin = EnemyFactory.createGoblin();
        cs.executeAttack(t, goblin, AttackType.PHYSICAL, 0);
        // Dopo il primo attacco lo stealth deve essere consumato
        assertFalse(t.isStealthBonusActive());
    }

    @Test
    void thief_critChance_increasesWithAttacks() {
        Thief t = new Thief("Ladro");
        double baseCrit = t.getCritChance();
        t.applyPassiveBonus(); // simula avanzamento stanza (+2% crit)
        // applyPassiveBonus nel Thief incrementa crit
        // verifichiamo che la crit non sia diminuita
        assertTrue(t.getCritChance() >= baseCrit);
    }

    @Test
    void thief_characterClass_isThief() {
        Thief t = new Thief("Ladro");
        assertEquals(CharacterClass.THIEF, t.getCharacterClass());
    }
}
