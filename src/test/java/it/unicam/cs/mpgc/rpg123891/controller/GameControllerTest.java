package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import it.unicam.cs.mpgc.rpg123891.persistence.PersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test sul GameController: gestione loot, avanzamento, pozioni.
 * Nota: startNewGame() NON chiama più collectEntryLoot() per la prima stanza;
 * il Bastone Magico viene consegnato tramite la wave 0 (Stanza del Bastone).
 */
public class GameControllerTest {

    private GameController gc;

    @BeforeEach
    void setup() {
        gc = new GameController(new PersistenceManager());
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
        // Dopo startNewGame la wave 0 NON è ancora cleared:
        // l'entryLoot della foresta (aggiunto per compatibilità test) viene
        // consumato esplicitamente qui.
        gc.collectEntryLoot();
        assertTrue(gc.getCurrentRoom().getEntryLoot().isEmpty(),
                "entryLoot deve essere vuoto dopo collectEntryLoot()");
    }

    @Test
    void checkWaveCleared_wave0_advancesToWave1() {
        // La wave 0 (Stanza del Bastone) non ha nemici: simuliamo il cleared
        Room forest = gc.getCurrentRoom();
        Wave wave0 = forest.getCurrentWave();
        assertEquals("Stanza del Bastone", wave0.getName());
        wave0.setCleared(true);
        gc.checkWaveCleared();
        Wave wave1 = forest.getCurrentWave();
        assertEquals("Ondata A", wave1.getName());
    }

    @Test
    void advanceRoom_failsIfCurrentRoomNotCleared() {
        // La foresta non è cleared: advanceRoom deve fallire
        assertFalse(gc.advanceRoom());
    }

    @Test
    void canFlee_falseOnWave0_noEnemies() {
        // Wave 0 non ha nemici vivi: canFlee deve restituire false
        assertFalse(gc.canFlee());
    }

    @Test
    void currentRoom_isForest() {
        assertEquals("r1", gc.getCurrentRoom().getId());
    }
}
