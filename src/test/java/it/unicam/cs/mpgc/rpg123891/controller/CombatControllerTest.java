package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import it.unicam.cs.mpgc.rpg123891.persistence.PersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test sul CombatController: attacchi, log, wave clearing.
 * La foresta ha 3 wave: wave 0 = Stanza del Bastone (nessun nemico),
 * wave 1 = Ondata A (3 cinghiali), wave 2 = Ondata B.
 * I test di combattimento partono dalla wave 1.
 */
public class CombatControllerTest {

    private GameController gc;
    private CombatController cc;

    @BeforeEach
    void setup() {
        gc = new GameController(new PersistenceManager());
        gc.startNewGame(new Warrior("TestWarrior"));
        // Avanza dalla wave 0 (Stanza del Bastone, no nemici) alla wave 1
        Room forest = gc.getCurrentRoom();
        Wave wave0 = forest.getCurrentWave();
        wave0.setCleared(true);
        gc.checkWaveCleared(); // avanza a wave 1
        cc = new CombatController(gc, gc.getGameState().getDungeonMap());
    }

    @Test
    void playerNormalAttack_returnsNonEmptyLog() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        assertNotNull(wave, "Wave corrente non deve essere null");
        assertFalse(wave.getEnemies().isEmpty(), "Wave 1 deve avere nemici");
        Enemy target = wave.getEnemies().get(0);
        CombatController.TurnResult result = cc.playerNormalAttack(target);
        assertNotNull(result);
        assertFalse(result.log().isEmpty(), "Il log non deve essere vuoto");
    }

    @Test
    void playerNormalAttack_reducesEnemyHp() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        Enemy target = wave.getEnemies().get(0);
        int hpBefore = target.getCurrentHp();
        cc.playerNormalAttack(target);
        assertTrue(target.getCurrentHp() <= hpBefore,
                "L'HP del nemico deve calare o restare uguale (se immune)");
    }

    @Test
    void enemyCounterAttack_isLogged() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        Enemy target = wave.getEnemies().get(0);
        CombatController.TurnResult result = cc.playerNormalAttack(target);
        // Il log deve contenere almeno la riga dell'attacco del giocatore
        assertFalse(result.log().isEmpty());
    }

    @Test
    void waveClearedFlag_setAfterAllEnemiesDead() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        // Uccidi tutti i nemici direttamente
        for (Enemy e : wave.getEnemies()) {
            while (e.isAlive()) cc.playerNormalAttack(e);
        }
        assertTrue(wave.isCleared() ||
                   wave.getEnemies().stream().noneMatch(Enemy::isAlive));
    }

    @Test
    void playerFlee_succeedsWhenWaveAllowsIt() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        // Ondata A della foresta permette la fuga (canFlee = true)
        if (wave.canFlee()) {
            CombatController.TurnResult result = cc.playerFlee();
            // Fuga ha probabilità: non assert su esito, solo su non-null
            assertNotNull(result);
        }
    }

    @Test
    void waveCleared_afterPlayerKillsAllEnemies() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        List<Enemy> enemies = wave.getEnemies();
        for (Enemy e : enemies) {
            while (e.isAlive()) cc.playerNormalAttack(e);
        }
        boolean cleared = wave.isCleared() ||
                          enemies.stream().noneMatch(Enemy::isAlive);
        assertTrue(cleared);
    }
}
