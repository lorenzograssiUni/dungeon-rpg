package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test sul GameController: gestione loot, avanzamento, pozioni.
 * La foresta ora ha wave0 = "Stanza del Bastone" (no nemici, loot=MagicStaff).
 */
public class GameControllerTest {

    private GameController gc;

    @BeforeEach
    void setup() {
        gc = new GameController(new JsonPersistenceManager());
        gc.startNewGame(new Warrior("TestWarrior"));
    }

    @Test
    void startNewGame_playerHas3Potions() {
        assertEquals(3, gc.countPotions());
    }

    @Test
    void useFirstPotion_reducePotionCount() {
        gc.useFirstPotion();
        assertEquals(2, gc.countPotions());
    }

    @Test
    void useFirstPotion_noPotion_returnsFalse() {
        gc.useFirstPotion();
        gc.useFirstPotion();
        gc.useFirstPotion();
        assertFalse(gc.useFirstPotion());
    }

    @Test
    void collectEntryLoot_emptyAfterCollection() {
        gc.collectEntryLoot();
        assertTrue(gc.getCurrentRoom().getEntryLoot().isEmpty(),
                "entryLoot deve essere vuoto dopo collectEntryLoot()");
    }

    @Test
    void checkWaveCleared_wave0_advancesToWave1() {
        Room forest = gc.getCurrentRoom();
        Wave wave0 = forest.getCurrentWave();
        // wave0 = "Stanza del Bastone" (no nemici -> cleared automaticamente)
        assertEquals("Stanza del Bastone", wave0.getName());
        wave0.setCleared(true);
        gc.checkWaveCleared();
        Wave wave1 = forest.getCurrentWave();
        assertEquals("Ondata A", wave1.getName());
    }

    @Test
    void advanceRoom_failsIfCurrentRoomNotCleared() {
        assertFalse(gc.advanceRoom());
    }

    @Test
    void canFlee_falseOnWave0_noEnemies() {
        // wave0 ha canFlee=false
        assertFalse(gc.canFlee());
    }

    @Test
    void currentRoom_isForest() {
        assertEquals("r1", gc.getCurrentRoom().getId());
    }
}
