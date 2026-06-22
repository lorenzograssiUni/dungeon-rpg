package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di integrazione per GameController:
 *  - startNewGame() fornisce 3 Pozioni e il Bastone Magico
 *  - useFirstPotion() usa e rimuove la pozione
 *  - countPotions() aggiornato
 *  - canFlee() false su wave non fuggibile
 *  - advanceRoom() incrementa stanza
 *  - checkPlayerDead() imposta gameOver
 */
public class GameControllerTest {

    private GameController gc;

    @BeforeEach
    void setup() {
        gc = new GameController(new it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager());
        gc.startNewGame(new Warrior("Eroe"));
    }

    @Test
    void startNewGame_gives3Potions() {
        assertEquals(3, gc.countPotions());
    }

    @Test
    void startNewGame_givesEntryLoot_MagicStaff() {
        boolean hasStaff = gc.getPlayer().getInventory().stream()
                .anyMatch(i -> i.getName().equals("Bastone Magico"));
        assertTrue(hasStaff, "Il giocatore deve avere il Bastone Magico all'inizio");
    }

    @Test
    void useFirstPotion_returnsTrueAndRemovesPotion() {
        long before = gc.countPotions();
        boolean used = gc.useFirstPotion();
        assertTrue(used);
        assertEquals(before - 1, gc.countPotions());
    }

    @Test
    void useFirstPotion_returnsFalseWhenNoPotions() {
        // Rimuovi tutte le pozioni
        gc.getPlayer().getInventory().removeIf(i -> i instanceof Potion);
        assertFalse(gc.useFirstPotion());
    }

    @Test
    void canFlee_falseOnBossWave() {
        // Boss room = ultima stanza (r5): wave canFlee=false
        // Avanziamo fino a r5
        for (int i = 0; i < 4; i++) {
            // segna la stanza come cleared manualmente
            Room r = gc.getCurrentRoom();
            r.getWaves().forEach(w -> w.setCleared(true));
            gc.advanceRoom();
        }
        // r5: wave Boss con canFlee=false
        assertFalse(gc.canFlee());
    }

    @Test
    void advanceRoom_movesToNextRoom() {
        Room r = gc.getCurrentRoom();
        r.getWaves().forEach(w -> w.setCleared(true));
        boolean advanced = gc.advanceRoom();
        assertTrue(advanced);
        assertNotEquals("r1", gc.getCurrentRoom().getId());
    }

    @Test
    void checkPlayerDead_setsGameOverWhenDead() {
        var player = (it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter) gc.getPlayer();
        player.takeDamage(9999);
        assertTrue(gc.checkPlayerDead());
        assertTrue(gc.getGameState().isGameOver());
    }

    @Test
    void startNewGame_worksWith_Mage() {
        gc.startNewGame(new Mage("Mago"));
        assertEquals(3, gc.countPotions());
    }

    @Test
    void startNewGame_worksWith_Thief() {
        gc.startNewGame(new Thief("Ladro"));
        assertEquals(3, gc.countPotions());
    }
}
