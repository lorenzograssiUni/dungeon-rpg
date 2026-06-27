package it.unicam.cs.mpgc.rpg123891.model.world;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica la struttura e la navigazione di DungeonMap.
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
    void forestRoom_hasNoEntryLoot() {
        // Il Bastone Magico ora e' nella wave0, non nell'entryLoot
        DungeonMap map = new DungeonMap();
        Room forest = map.getCurrentRoom();
        assertTrue(forest.getEntryLoot().isEmpty(),
                "La foresta non deve avere entryLoot: il Bastone e' nella wave0");
    }

    @Test
    void forestRoom_hasThreeWaves() {
        DungeonMap map = new DungeonMap();
        Room forest = map.getCurrentRoom();
        // wave 0 = Stanza del Bastone, wave 1 = Ondata A, wave 2 = Ondata B
        assertEquals(3, forest.getTotalWaves());
    }

    @Test
    void forestRoom_wave0_isMagicStaffLoot() {
        DungeonMap map = new DungeonMap();
        Room forest = map.getCurrentRoom();
        Wave w0 = forest.getWaves().get(0);
        assertEquals("Stanza del Bastone", w0.getName());
        assertTrue(w0.getEnemies().isEmpty(), "Wave 0 non deve avere nemici");
        assertFalse(w0.getLoot().isEmpty(), "Wave 0 deve contenere il Bastone Magico");
        assertEquals("Bastone Magico", w0.getLoot().get(0).getName());
    }

    @Test
    void forestRoom_wave1_hasEnemies() {
        DungeonMap map = new DungeonMap();
        Room forest = map.getCurrentRoom();
        Wave w1 = forest.getWaves().get(1);
        assertFalse(w1.getEnemies().isEmpty(), "Wave 1 deve avere nemici (Ondata A)");
    }

    @Test
    void catacombs_hasSalaStatuaWaveWithNoEnemies() {
        DungeonMap map = new DungeonMap();
        for (int i = 0; i < 2; i++) map.advanceToNextRoom();
        Room catacombs = map.getCurrentRoom();
        Wave salaStatua = catacombs.getWaves().get(1);
        assertTrue(salaStatua.getEnemies().isEmpty(),
                "La Sala della Statua non deve avere nemici");
        assertFalse(salaStatua.getLoot().isEmpty(),
                "La Sala della Statua deve contenere lo Spadone");
    }

    @Test
    void goblinVillage_firstWaveLoot_containsDualDaggers() {
        DungeonMap map = new DungeonMap();
        map.advanceToNextRoom();
        Room goblin = map.getCurrentRoom();
        Wave waveA = goblin.getWaves().get(0);
        boolean hasDualDaggers = waveA.getLoot().stream()
                .anyMatch(i -> i.getName().equals("Doppie Daghe"));
        assertTrue(hasDualDaggers, "Wave A villaggio deve droppare Doppie Daghe");
    }
}
