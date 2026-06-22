package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica le meccaniche di Wave:
 *  - isCleared() false con nemici vivi
 *  - isCleared() true dopo uccisione di tutti i nemici
 *  - Wave senza nemici e' subito cleared
 *  - getName() alias di getLabel()
 *  - getDescription() restituisce il testo passato
 */
public class WaveTest {

    @Test
    void waveWithEnemies_notClearedUntilAllDead() {
        Wave wave = new Wave("Test", true, "desc");
        var goblin = EnemyFactory.createGoblin();
        wave.addEnemy(goblin);
        assertFalse(wave.isCleared());
        goblin.takeDamage(9999);
        assertTrue(wave.isCleared());
    }

    @Test
    void emptyWave_isImmediatelyCleared() {
        Wave wave = new Wave("Vuota");
        assertTrue(wave.isCleared());
    }

    @Test
    void getName_equalsLabel() {
        Wave wave = new Wave("OnA", false, "testo");
        assertEquals(wave.getLabel(), wave.getName());
    }

    @Test
    void getDescription_returnsCorrectText() {
        Wave wave = new Wave("OnA", true, "Testo narrativo");
        assertEquals("Testo narrativo", wave.getDescription());
    }

    @Test
    void canFlee_respectsConstructorFlag() {
        Wave noFlee = new Wave("Boss", false);
        Wave flee   = new Wave("Normal", true);
        assertFalse(noFlee.canFlee());
        assertTrue(flee.canFlee());
    }

    @Test
    void setCleared_overridesEnemyState() {
        Wave wave = new Wave("Test");
        wave.addEnemy(EnemyFactory.createGoblin());
        assertFalse(wave.isCleared());
        wave.setCleared(true);
        assertTrue(wave.isCleared());
    }
}
