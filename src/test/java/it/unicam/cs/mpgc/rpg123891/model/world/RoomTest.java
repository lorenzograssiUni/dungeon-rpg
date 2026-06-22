package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica le meccaniche di Room:
 *  - isCleared() senza ondate
 *  - isCleared() con ondate non completate
 *  - advanceWave() e hasMoreWaves()
 *  - entryLoot aggiunto e recuperato
 *  - visited flag
 */
public class RoomTest {

    @Test
    void emptyRoom_isCleared() {
        Room r = new Room("t1", "Test", "Desc");
        assertTrue(r.isCleared());
    }

    @Test
    void roomWithUnfinishedWave_isNotCleared() {
        Room r = new Room("t1", "Test", "Desc");
        Wave w = new Wave("W");
        w.addEnemy(EnemyFactory.createGoblin());
        r.addWave(w);
        assertFalse(r.isCleared());
    }

    @Test
    void roomCleared_afterAllWavesCleared() {
        Room r = new Room("t1", "Test", "Desc");
        Wave w = new Wave("W");
        var goblin = EnemyFactory.createGoblin();
        w.addEnemy(goblin);
        r.addWave(w);
        goblin.takeDamage(9999);
        assertTrue(r.isCleared());
    }

    @Test
    void advanceWave_returnsTrueWhenMoreWaves() {
        Room r = new Room("t1", "Test", "Desc");
        r.addWave(new Wave("W1"));
        r.addWave(new Wave("W2"));
        assertTrue(r.advanceWave());
    }

    @Test
    void advanceWave_returnsFalseAtLastWave() {
        Room r = new Room("t1", "Test", "Desc");
        r.addWave(new Wave("W1"));
        assertFalse(r.advanceWave());
    }

    @Test
    void entryLoot_addAndRetrieve() {
        Room r = new Room("t1", "Test", "Desc");
        r.addEntryLoot(new Potion());
        assertEquals(1, r.getEntryLoot().size());
    }

    @Test
    void visited_defaultFalse() {
        Room r = new Room("t1", "Test", "Desc");
        assertFalse(r.isVisited());
        r.setVisited(true);
        assertTrue(r.isVisited());
    }

    @Test
    void getAllEnemies_returnsEnemiesFromAllWaves() {
        Room r = new Room("t1", "Test", "Desc");
        Wave w1 = new Wave("W1"); w1.addEnemy(EnemyFactory.createGoblin());
        Wave w2 = new Wave("W2"); w2.addEnemy(EnemyFactory.createLupo());
        r.addWave(w1); r.addWave(w2);
        assertEquals(2, r.getAllEnemies().size());
    }
}
