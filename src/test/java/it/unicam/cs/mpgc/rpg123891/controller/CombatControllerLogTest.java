package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.*;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.world.*;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica il contenuto del log prodotto da CombatController:
 *  - attacco normale: stamina invariata, log contiene "ATK"
 *  - pozione: log contiene HP e Stamina
 *  - turno nemico: log contiene danno lordo e difesa
 *  - attacco normale con stamina 0: non lancia eccezioni
 */
public class CombatControllerLogTest {

    private GameController gc;
    private CombatController cc;
    private Enemy goblin;

    @BeforeEach
    void setUp() {
        Warrior player = new Warrior("G");
        player.applyPassiveBonus();
        gc = new GameController(new JsonPersistenceManager());
        gc.startNewGame(player);
        DungeonMap map = gc.getGameState().getDungeonMap();
        cc = new CombatController(gc, map);
        goblin = EnemyFactory.createGoblin();
    }

    @Test
    void normalAttack_stamina_unchanged() {
        int staBefore = ((GameCharacter) gc.getPlayer()).getCurrentStamina();
        cc.playerNormalAttack(goblin);
        assertEquals(staBefore, ((GameCharacter) gc.getPlayer()).getCurrentStamina(),
                "L'attacco normale non deve consumare stamina");
    }

    @Test
    void normalAttack_log_containsAtkTag() {
        CombatController.TurnResult result = cc.playerNormalAttack(goblin);
        boolean hasAtk = result.log().stream().anyMatch(l -> l.contains("[ATK]"));
        assertTrue(hasAtk);
    }

    @Test
    void normalAttack_withZeroStamina_doesNotThrow() {
        GameCharacter player = (GameCharacter) gc.getPlayer();
        while (player.getCurrentStamina() > 0) player.consumeStaminaForAttack();
        assertDoesNotThrow(() -> cc.playerNormalAttack(goblin));
    }

    @Test
    void usePotion_log_containsHpAndStamina() {
        CombatController.TurnResult result = cc.playerUsePotion();
        String combined = String.join(" ", result.log());
        assertTrue(combined.contains("HP") && combined.contains("Stamina"),
                "Il log della pozione deve menzionare HP e Stamina");
    }

    @Test
    void usePotion_whenEmpty_logContainsNessuna() {
        while (gc.countPotions() > 0) gc.useFirstPotion();
        CombatController.TurnResult result = cc.playerUsePotion();
        assertTrue(result.log().stream().anyMatch(l -> l.contains("Nessuna")));
    }

    @Test
    void enemyTurn_log_containsDamageDetails() {
        CombatController.TurnResult result = cc.playerNormalAttack(goblin);
        if (!result.waveCleared()) {
            boolean hasEnemyLog = result.log().stream()
                    .anyMatch(l -> l.contains("[ENEMY]") && l.contains("lordo"));
            assertTrue(hasEnemyLog,
                    "Il log del nemico deve contenere il danno lordo");
        }
    }

    @Test
    void flee_whenNotAllowed_logContainsNonPuoi() {
        CombatController.TurnResult result = cc.playerFlee();
        assertTrue(result.log().stream().anyMatch(l -> l.contains("Non puoi fuggire")));
        assertFalse(result.fleeSuccess());
    }
}
