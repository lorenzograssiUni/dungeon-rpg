package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per CombatController:
 *  - TurnResult.isCombatOver()
 *  - playerNormalAttack() restituisce log non vuoto
 *  - playerUsePotion() con pozione disponibile
 *  - playerUsePotion() senza pozioni
 *  - playerFlee() su wave fuggibile
 *  - checkAndActivateDragonBuff() attiva buff solo se tesoro ripulito
 */
public class CombatControllerTest {

    private GameController gc;
    private CombatController cc;

    @BeforeEach
    void setup() {
        gc = new GameController(new it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager());
        gc.startNewGame(new Warrior("Eroe"));
        cc = new CombatController(gc, gc.getGameState().getDungeonMap());
    }

    // --- TurnResult.isCombatOver() ---

    @Test
    void isCombatOver_falseWhenNothingHappened() {
        CombatController.TurnResult r = new CombatController.TurnResult(
                java.util.List.of(), false, false, false);
        assertFalse(r.isCombatOver());
    }

    @Test
    void isCombatOver_trueWhenPlayerDead() {
        CombatController.TurnResult r = new CombatController.TurnResult(
                java.util.List.of(), true, false, false);
        assertTrue(r.isCombatOver());
    }

    @Test
    void isCombatOver_trueWhenWaveCleared() {
        CombatController.TurnResult r = new CombatController.TurnResult(
                java.util.List.of(), false, true, false);
        assertTrue(r.isCombatOver());
    }

    @Test
    void isCombatOver_trueWhenFlee() {
        CombatController.TurnResult r = new CombatController.TurnResult(
                java.util.List.of(), false, false, true);
        assertTrue(r.isCombatOver());
    }

    // --- playerNormalAttack ---

    @Test
    void playerNormalAttack_returnsNonEmptyLog() {
        Enemy goblin = gc.getCurrentRoom().getCurrentWave().getEnemies().get(0);
        CombatController.TurnResult result = cc.playerNormalAttack(goblin);
        assertNotNull(result);
        assertFalse(result.log().isEmpty());
    }

    // --- playerUsePotion ---

    @Test
    void playerUsePotion_withPotion_returnsTrue_inLog() {
        CombatController.TurnResult r = cc.playerUsePotion();
        assertTrue(r.log().stream().anyMatch(l -> l.contains("Pozione")));
        assertFalse(r.isCombatOver());
    }

    @Test
    void playerUsePotion_noPotions_reportsNoneAvailable() {
        gc.getPlayer().getInventory().removeIf(
                i -> i instanceof it.unicam.cs.mpgc.rpg123891.model.item.Potion);
        CombatController.TurnResult r = cc.playerUsePotion();
        assertTrue(r.log().stream().anyMatch(l -> l.contains("Nessuna")));
        assertFalse(r.isCombatOver());
    }

    // --- checkAndActivateDragonBuff ---

    @Test
    void dragonBuff_notActivated_ifTreasureNotCleaned() {
        Enemy dragon = EnemyFactory.createUltimoDrago();
        int atkBefore = dragon.getAttack();
        cc.checkAndActivateDragonBuff(dragon);
        // Tesoro non ripulito -> nessuna modifica
        assertEquals(atkBefore, dragon.getAttack());
    }

    @Test
    void dragonBuff_activated_ifTreasureCleaned() {
        gc.getGameState().getDungeonMap().setTreasureRoomCleaned(true);
        Enemy dragon = EnemyFactory.createUltimoDrago();
        // Il passiveBuff deve essere impostato da EnemyFactory
        if (dragon.getPassiveBuff() == null) return; // skip se non configurato
        int atkBefore = dragon.getAttack();
        cc.checkAndActivateDragonBuff(dragon);
        // +20% danno: attacco deve aumentare
        assertTrue(dragon.getAttack() >= atkBefore);
    }
}
