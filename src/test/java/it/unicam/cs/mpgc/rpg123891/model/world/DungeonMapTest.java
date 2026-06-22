package it.unicam.cs.mpgc.rpg123891.model.world;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica la struttura e la navigazione di DungeonMap:
 *  - 5 stanze presenti
 *  - navigazione sequenziale
 *  - isTreasureRoomCleaned() default false
 *  - advanceToNextRoom() fino alla fine
 */
public class DungeonMapTest {

    @Test
    void dungeonMap_has5Rooms() {
        DungeonMap map = new DungeonMap();
        assertEquals(5, map.getTotalRooms());
    }

    @Test
    void firstRoom_isForest() {
        DungeonMap map = new DungeonMap();
        assertEquals("r1", map.getCurrentRoom().getId());
    }

    @Test
    void advanceRoom_progressesCorrectly() {
        DungeonMap map = new DungeonMap();
        assertTrue(map.hasNextRoom());
        map.advanceToNextRoom();
        assertEquals(1, map.getCurrentRoomIndex());
    }

    @Test
    void lastRoom_hasNoNextRoom() {
        DungeonMap map = new DungeonMap();
        for (int i = 0; i < 4; i++) map.advanceToNextRoom();
        assertFalse(map.hasNextRoom());
    }

    @Test
    void treasureRoomCleaned_defaultFalse() {
        DungeonMap map = new DungeonMap();
        assertFalse(map.isTreasureRoomCleaned());
    }

    @Test
    void setTreasureRoomCleaned_works() {
        DungeonMap map = new DungeonMap();
        map.setTreasureRoomCleaned(true);
        assertTrue(map.isTreasureRoomCleaned());
    }

    @Test
    void forestRoom_hasEntryLoot() {
        DungeonMap map = new DungeonMap();
        Room forest = map.getCurrentRoom();
        assertFalse(forest.getEntryLoot().isEmpty(),
                "La foresta deve avere il Bastone Magico come entry loot");
    }

    @Test
    void forestRoom_hasTwoWaves() {
        DungeonMap map = new DungeonMap();
        Room forest = map.getCurrentRoom();
        assertEquals(2, forest.getTotalWaves());
    }

    @Test
    void catacombs_hasSalaStatuaWaveWithNoEnemies() {
        DungeonMap map = new DungeonMap();
        // Catacombe = r3, indice 2
        for (int i = 0; i < 2; i++) map.advanceToNextRoom();
        Room catacombs = map.getCurrentRoom();
        // Ondata 1 = Sala della Statua (indice 1 in catacombe): nessun nemico
        Wave salaStatua = catacombs.getWaves().get(1);
        assertTrue(salaStatua.getEnemies().isEmpty(),
                "La Sala della Statua non deve avere nemici");
        assertFalse(salaStatua.getLoot().isEmpty(),
                "La Sala della Statua deve contenere lo Spadone");
    }

    @Test
    void goblinVillage_firstWaveLoot_containsDualDaggers() {
        DungeonMap map = new DungeonMap();
        map.advanceToNextRoom(); // r2
        Room goblin = map.getCurrentRoom();
        Wave waveA = goblin.getWaves().get(0);
        boolean hasDualDaggers = waveA.getLoot().stream()
                .anyMatch(i -> i.getName().equals("Doppie Daghe"));
        assertTrue(hasDualDaggers, "Wave A villaggio deve droppare Doppie Daghe");
    }
}
