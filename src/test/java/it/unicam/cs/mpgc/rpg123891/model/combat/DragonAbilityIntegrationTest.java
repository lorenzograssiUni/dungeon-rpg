package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.controller.CombatController;
import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class DragonAbilityIntegrationTest {

    private CombatController cc;
    private Warrior player;
    private Enemy dragon;

    @BeforeEach
    void setUp() {
        player = new Warrior("P");
        GameController gc = new GameController(new JsonPersistenceManager());
        gc.startNewGame(player);
        DungeonMap map = gc.getGameState().getDungeonMap();
        cc = new CombatController(gc, map);
        dragon = EnemyFactory.createUltimoDrago();
    }

    @Test
    void dragonBreath_burnAppliedToCombatController() {
        DragonBreathAbility ability = new DragonBreathAbility(new Random(1));
        BurnEffect burn = ability.use(dragon, player).burnEffect();
        assertNotNull(burn);
        cc.applyBurn(burn);
        assertNotNull(cc.getActiveBurn());
    }

    @Test
    void burnEffect_tickReducesHp() {
        BurnEffect burn = new BurnEffect(new Random(0));
        int hpBefore = player.getCurrentHp();
        burn.applyTo(player);
        assertTrue(player.getCurrentHp() < hpBefore);
    }

    @Test
    void burnEffect_expiresAfterMaxTurns() {
        BurnEffect burn = new BurnEffect(new Random(0));
        int maxTurns = burn.getTurnsRemaining();
        for (int i = 0; i < maxTurns; i++) burn.applyTo(player);
        assertTrue(burn.isExpired());
    }

    @Test
    void burnEffect_doesNotExpireBeforeMaxTurns() {
        BurnEffect burn = new BurnEffect(new Random(0));
        int maxTurns = burn.getTurnsRemaining();
        for (int i = 0; i < maxTurns - 1; i++) burn.applyTo(player);
        assertFalse(burn.isExpired());
    }

    @Test
    void dragonPassiveBuff_increasesAttackOnActivation() {
        Enemy dragon2 = EnemyFactory.createUltimoDrago();
        int atkBefore = dragon2.getAttack();
        dragon2.applyPassiveBonus();
        assertTrue(dragon2.getAttack() >= atkBefore);
    }

    @Test
    void checkAndActivateDragonBuff_doesNotActivateWhenTreasureNotCleaned() {
        int atkBefore = dragon.getAttack();
        cc.checkAndActivateDragonBuff(dragon);
        assertEquals(atkBefore, dragon.getAttack());
    }
}
