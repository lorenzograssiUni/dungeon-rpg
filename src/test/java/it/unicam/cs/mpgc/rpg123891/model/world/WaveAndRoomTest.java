package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WaveAndRoomTest {

    private Enemy aliveEnemy() {
        return new Enemy("G", 10, 5, 0, AttackType.PHYSICAL, 0.0);
    }

    // ---- Wave ----

    @Test
    void wave_notCleared_whenEnemiesAlive() {
        Wave w = new Wave("W", true);
        w.addEnemy(aliveEnemy());
        assertFalse(w.isCleared());
    }

    @Test
    void wave_cleared_whenAllDead() {
        Enemy e = aliveEnemy();
        Wave w = new Wave("W", true);
        w.addEnemy(e);
        e.takeDamage(999);
        assertTrue(w.isCleared());
    }

    @Test
    void wave_canFlee_flag() {
        Wave yes = new Wave("W", true);
        Wave no  = new Wave("W", false);
        assertTrue(yes.canFlee());
        assertFalse(no.canFlee());
    }

    @Test
    void wave_addLoot_appearsInGetLoot() {
        Wave w = new Wave("W", true);
        Potion p = new Potion();
        w.addLoot(p);
        assertTrue(w.getLoot().contains(p));
    }

    @Test
    void wave_getName() {
        Wave w = new Wave("Ondata A");
        assertEquals("Ondata A", w.getName());
    }

    @Test
    void wave_noEnemies_isCleared() {
        Wave w = new Wave("Vuota", true);
        assertTrue(w.isCleared());
    }

    // ---- Room ----

    @Test
    void room_notCleared_whenWaveActive() {
        Wave w = new Wave("W", true);
        w.addEnemy(aliveEnemy());
        Room r = new Room("r1", "Test", "desc");
        r.addWave(w);
        assertFalse(r.isCleared());
    }

    @Test
    void room_cleared_whenNoWaves() {
        Room r = new Room("r1", "Test", "desc");
        assertTrue(r.isCleared());
    }

    @Test
    void room_cleared_afterAllWavesCleared() {
        Enemy e = aliveEnemy();
        Wave w = new Wave("W", true);
        w.addEnemy(e);
        Room r = new Room("r1", "Test", "desc");
        r.addWave(w);
        e.takeDamage(999);
        assertTrue(r.isCleared());
    }

    @Test
    void room_hasMoreWaves_trueWhenMultiple() {
        Wave w1 = new Wave("A", true);
        w1.addEnemy(aliveEnemy());
        Wave w2 = new Wave("B", true);
        w2.addEnemy(aliveEnemy());
        Room r = new Room("r1", "Test", "desc");
        r.addWave(w1);
        r.addWave(w2);
        assertTrue(r.hasMoreWaves());
    }

    @Test
    void room_advanceWave_movesToNext() {
        Wave w1 = new Wave("A", true);
        Wave w2 = new Wave("B", true);
        Room r = new Room("r1", "Test", "desc");
        r.addWave(w1);
        r.addWave(w2);
        assertEquals("A", r.getCurrentWave().getName());
        r.advanceWave();
        assertEquals("B", r.getCurrentWave().getName());
    }

    @Test
    void room_getAllEnemies_returnsAll() {
        Enemy e1 = aliveEnemy();
        Enemy e2 = aliveEnemy();
        Wave w = new Wave("W", true);
        w.addEnemy(e1);
        w.addEnemy(e2);
        Room r = new Room("r1", "Test", "desc");
        r.addWave(w);
        assertTrue(r.getAllEnemies().containsAll(java.util.List.of(e1, e2)));
    }

    @Test
    void room_setVisited() {
        Room r = new Room("r1", "Test", "desc");
        assertFalse(r.isVisited());
        r.setVisited(true);
        assertTrue(r.isVisited());
    }

    @Test
    void room_noCurrentWave_whenEmpty() {
        Room r = new Room("r1", "Test", "desc");
        assertNull(r.getCurrentWave());
    }
}
