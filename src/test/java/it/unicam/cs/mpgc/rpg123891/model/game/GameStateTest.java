package it.unicam.cs.mpgc.rpg123891.model.game;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica GameState: player, mappa, flag gameOver/victory,
 * stati iniziali e transizioni.
 */
public class GameStateTest {

    private GameState state;
    private Warrior player;

    @BeforeEach
    void setUp() {
        player = new Warrior("G");
        state = new GameState(player);
    }

    @Test
    void gameState_playerNotNull() {
        assertNotNull(state.getPlayer());
        assertEquals(player, state.getPlayer());
    }

    @Test
    void gameState_dungeonMapNotNull() {
        assertNotNull(state.getDungeonMap());
    }

    @Test
    void gameState_notGameOverAtStart() {
        assertFalse(state.isGameOver());
    }

    @Test
    void gameState_notVictoryAtStart() {
        assertFalse(state.isVictory());
    }

    @Test
    void gameState_setGameOver_true() {
        state.setGameOver(true);
        assertTrue(state.isGameOver());
    }

    @Test
    void gameState_setVictory_true() {
        state.setVictory(true);
        assertTrue(state.isVictory());
    }

    @Test
    void gameState_gameOverWithVictory() {
        state.setGameOver(true);
        state.setVictory(true);
        assertTrue(state.isGameOver());
        assertTrue(state.isVictory());
    }

    @Test
    void gameState_gameOverWithoutVictory() {
        state.setGameOver(true);
        state.setVictory(false);
        assertTrue(state.isGameOver());
        assertFalse(state.isVictory());
    }

    @Test
    void gameState_dungeonMap_firstRoomNotNull() {
        assertNotNull(state.getDungeonMap().getCurrentRoom());
    }

    @Test
    void gameState_dungeonMap_hasNextRoom() {
        assertTrue(state.getDungeonMap().hasNextRoom(),
                "Il dungeon deve avere almeno una stanza successiva");
    }
}
