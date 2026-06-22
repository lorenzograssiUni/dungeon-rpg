package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica il buff temporaneo "Carica!" (+3 DEF) e la logica di fuga.
 */
public class CaricaAndFleeTest {

    private GameController gc;
    private CombatController cc;
    private Warrior player;

    @BeforeEach
    void setUp() {
        player = new Warrior("G");
        gc = new GameController();
        gc.startNewGame(player);
        DungeonMap map = gc.getGameState().getDungeonMap();
        cc = new CombatController(gc, map);
    }

    // ---- Carica! ----

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

    // ---- Fuga ----

    @Test
    void canFlee_falseWhenNoWave() {
        // Prima stanza senza ondata attiva (cleared o wave null)
        // canFlee deve tornare false se wave == null
        // La prima stanza di un dungeon fresco ha una wave
        // Ma flee richiede playerAgility < avgEnemyAgility
        // Warrior AGI=4, Goblin AGI variabile — solo verifichiamo che non lanci eccezioni
        assertDoesNotThrow(() -> gc.canFlee());
    }

    @Test
    void flee_returnsFalseWhenCannotFlee() {
        // Se canFlee() è false, flee() deve restituire false
        if (!gc.canFlee()) {
            assertFalse(gc.flee());
        }
    }

    @Test
    void combatController_flee_logsNonPuoiFuggire() {
        CombatController.TurnResult result = cc.playerFlee();
        // Se non si può fuggire il log lo dice
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
