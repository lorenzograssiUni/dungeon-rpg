package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica Wave e Room: isCleared, canFlee, addLoot,
 * avanzamento wave nella Room, getAllEnemies.
 */
public class WaveAndRoomTest {

    private Enemy aliveEnemy() {
        return new Enemy("G", 10, 5, 0, AttackType.PHYSICAL, 0.0);
    }

    private Enemy deadEnemy() {
        Enemy e = new Enemy("G", 10, 5, 0, AttackType.PHYSICAL, 0.0);
        e.takeDamage(999);
        return e;
    }

    // ---- Wave ----

    @Test
    void wave_notCleared_whenEnemiesAlive() {
        Wave w = new Wave("W", List.of(aliveEnemy()), List.of(), true);
        assertFalse(w.isCleared());
    }

    @Test
    void wave_cleared_whenAllDead() {
        Enemy e = aliveEnemy();
        Wave w = new Wave("W", List.of(e), List.of(), true);
        e.takeDamage(999);
        assertTrue(w.isCleared());
    }

    @Test
    void wave_canFlee_flag() {
        Wave yes = new Wave("W", List.of(aliveEnemy()), List.of(), true);
        Wave no  = new Wave("W", List.of(aliveEnemy()), List.of(), false);
        assertTrue(yes.canFlee());
        assertFalse(no.canFlee());
    }

    @Test
    void wave_addLoot_appearsInGetLoot() {
        Wave w = new Wave("W", List.of(aliveEnemy()), List.of(), true);
        Potion p = new Potion();
        w.addLoot(p);
        assertTrue(w.getLoot().contains(p));
    }

    @Test
    void wave_getName() {
        Wave w = new Wave("Ondata A", List.of(), List.of(), true);
        assertEquals("Ondata A", w.getName());
    }

    // ---- Room ----

    @Test
    void room_notCleared_whenWaveActive() {
        Wave w = new Wave("W", List.of(aliveEnemy()), List.of(), true);
        Room r = new Room("r1", "Test", List.of(w), List.of());
        assertFalse(r.isCleared());
    }

    @Test
    void room_cleared_whenNoMoreWaves() {
        Enemy e = aliveEnemy();
        Wave w = new Wave("W", List.of(e), List.of(), true);
        Room r = new Room("r1", "Test", List.of(w), List.of());
        e.takeDamage(999);
        r.advanceWave(); // avanza: non ci sono altre wave
        assertTrue(r.isCleared());
    }

    @Test
    void room_hasMoreWaves_trueWhenMultiple() {
        Wave w1 = new Wave("A", List.of(aliveEnemy()), List.of(), true);
        Wave w2 = new Wave("B", List.of(aliveEnemy()), List.of(), true);
        Room r = new Room("r1", "Test", List.of(w1, w2), List.of());
        assertTrue(r.hasMoreWaves());
    }

    @Test
    void room_advanceWave_movesToNext() {
        Wave w1 = new Wave("A", List.of(aliveEnemy()), List.of(), true);
        Wave w2 = new Wave("B", List.of(aliveEnemy()), List.of(), true);
        Room r = new Room("r1", "Test", List.of(w1, w2), List.of());
        assertEquals("A", r.getCurrentWave().getName());
        aliveEnemy().takeDamage(999); // dummy
        r.getCurrentWave().getEnemies().forEach(en -> en.takeDamage(999));
        r.advanceWave();
        assertEquals("B", r.getCurrentWave().getName());
    }

    @Test
    void room_getAllEnemies_returnsAll() {
        Enemy e1 = aliveEnemy();
        Enemy e2 = aliveEnemy();
        Wave w = new Wave("W", List.of(e1, e2), List.of(), true);
        Room r = new Room("r1", "Test", List.of(w), List.of());
        assertTrue(r.getAllEnemies().containsAll(List.of(e1, e2)));
    }

    @Test
    void room_setVisited() {
        Room r = new Room("r1", "Test", List.of(), List.of());
        assertFalse(r.isVisited());
        r.setVisited(true);
        assertTrue(r.isVisited());
    }
}
