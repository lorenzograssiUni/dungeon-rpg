package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CaricaAndFleeTest {

    private GameController gc;
    private CombatController cc;
    private Warrior player;

    @BeforeEach
    void setUp() {
        player = new Warrior("G");
        gc = new GameController(new JsonPersistenceManager());
        gc.startNewGame(player);
        DungeonMap map = gc.getGameState().getDungeonMap();
        cc = new CombatController(gc, map);
    }

    @Test
    void carica_notActiveAtStart() {
        assertFalse(gc.isCaricaActive());
    }

    @Test
    void rollbackCarica_whenNotActive_doesNothing() {
        int defBefore = player.getDefense();
        gc.rollbackCarica();
        assertEquals(defBefore, player.getDefense());
        assertFalse(gc.isCaricaActive());
    }

    @Test
    void canFlee_doesNotThrow() {
        assertDoesNotThrow(() -> gc.canFlee());
    }

    @Test
    void flee_returnsFalseWhenCannotFlee() {
        if (!gc.canFlee()) {
            assertFalse(gc.flee());
        }
    }

    @Test
    void combatController_flee_logsNonPuoiFuggire() {
        CombatController.TurnResult result = cc.playerFlee();
        if (!result.fleeSuccess()) {
            assertTrue(result.log().stream()
                    .anyMatch(l -> l.contains("Non puoi fuggire")));
        }
    }

    @Test
    void combatController_normalAttack_doesNotConsumeStamina() {
        Enemy goblin = EnemyFactory.createGoblin();
        int staBefore = player.getCurrentStamina();
        cc.playerNormalAttack(goblin);
        assertEquals(staBefore, player.getCurrentStamina());
    }
}
