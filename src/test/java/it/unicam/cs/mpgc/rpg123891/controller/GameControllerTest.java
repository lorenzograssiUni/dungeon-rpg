package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test d'integrazione per GameController:
 * avvio partita, inventario iniziale, attacchi, pozioni, game over.
 */
public class GameControllerTest {

    private GameController gc;
    private Warrior player;

    @BeforeEach
    void setUp() {
        player = new Warrior("Guerriero");
        gc = new GameController();
        gc.startNewGame(player);
    }

    @Test
    void startNewGame_player3Potions() {
        assertEquals(3, gc.countPotions());
    }

    @Test
    void startNewGame_firstRoomVisited() {
        assertTrue(gc.getCurrentRoom().isVisited());
    }

    @Test
    void startNewGame_dungeonMapNotNull() {
        assertNotNull(gc.getGameState().getDungeonMap());
    }

    @Test
    void playerAttack_returnsNonNegativeDamage() {
        Enemy goblin = EnemyFactory.createGoblin();
        int dmg = gc.playerAttack(goblin);
        assertTrue(dmg >= 0);
    }

    @Test
    void playerAttack_withZeroStamina_doesNotThrow() {
        while (player.getCurrentStamina() > 0) player.consumeStaminaForAttack();
        Enemy goblin = EnemyFactory.createGoblin();
        assertDoesNotThrow(() -> gc.playerAttack(goblin));
    }

    @Test
    void enemyAttack_returnsNonNegativeDamage() {
        Enemy goblin = EnemyFactory.createGoblin();
        int dmg = gc.enemyAttack(goblin);
        assertTrue(dmg >= 0);
    }

    @Test
    void useFirstPotion_reducePotionCount() {
        long before = gc.countPotions();
        gc.useFirstPotion();
        assertEquals(before - 1, gc.countPotions());
    }

    @Test
    void useFirstPotion_healsPlayer() {
        player.takeDamage(50);
        int hpBefore = player.getCurrentHp();
        gc.useFirstPotion();
        assertTrue(player.getCurrentHp() > hpBefore);
    }

    @Test
    void useFirstPotion_whenNone_returnsFalse() {
        while (gc.countPotions() > 0) gc.useFirstPotion();
        assertFalse(gc.useFirstPotion());
    }

    @Test
    void checkPlayerDead_alive_returnsFalse() {
        assertFalse(gc.checkPlayerDead());
    }

    @Test
    void checkPlayerDead_dead_setsGameOver() {
        player.takeDamage(9999);
        assertTrue(gc.checkPlayerDead());
        assertTrue(gc.getGameState().isGameOver());
        assertFalse(gc.getGameState().isVictory());
    }

    @Test
    void advanceRoom_failsIfCurrentNotCleared() {
        assertFalse(gc.advanceRoom(),
                "Non si deve avanzare se la stanza corrente non è liberata");
    }

    @Test
    void collectEntryLoot_emptyAfterCollection() {
        // La prima stanza ha già avuto il loot raccolto in startNewGame
        assertEquals(0, gc.getCurrentRoom().getEntryLoot().size());
    }
}
