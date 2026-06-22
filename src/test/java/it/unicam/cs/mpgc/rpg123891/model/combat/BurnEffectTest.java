package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica le meccaniche di BurnEffect:
 *  - applyTo() decrementa turnsRemaining
 *  - il danno ignorala difesa (applyBurnDamage)
 *  - isExpired() dopo l'ultimo turno
 *  - danno 0 se gia' scaduto
 */
public class BurnEffectTest {

    @Test
    void applyTo_decrementsHP_bypassingDefense() {
        Warrior w = new Warrior("Test");
        int hpBefore = w.getCurrentHp();
        BurnEffect burn = new BurnEffect(10, 2);
        burn.applyTo(w);
        // danno diretto ignorando difesa: hp deve scendere esattamente di 10
        assertEquals(hpBefore - 10, w.getCurrentHp());
    }

    @Test
    void applyTo_expires_afterAllTurns() {
        Warrior w = new Warrior("Test");
        BurnEffect burn = new BurnEffect(5, 2);
        assertFalse(burn.isExpired());
        burn.applyTo(w);
        assertFalse(burn.isExpired());
        burn.applyTo(w);
        assertTrue(burn.isExpired());
    }

    @Test
    void applyTo_returns_zero_when_expired() {
        Warrior w = new Warrior("Test");
        BurnEffect burn = new BurnEffect(8, 1);
        burn.applyTo(w); // esaurisce l'effetto
        int dmg = burn.applyTo(w);
        assertEquals(0, dmg);
    }

    @Test
    void getDamagePerTurn_matchesConstructor() {
        BurnEffect burn = new BurnEffect(7, 3);
        assertEquals(7, burn.getDamagePerTurn());
        assertEquals(3, burn.getTurnsRemaining());
    }
}
