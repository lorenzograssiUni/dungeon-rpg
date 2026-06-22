package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyAbilityTest {

    @Test
    void dragonBreath_returnsNonNullBurn() {
        DragonBreathAbility ability = new DragonBreathAbility(new Random(42));
        Enemy dragon = EnemyFactory.createUltimoDrago();
        Warrior player = new Warrior("P");
        EnemyAbility.AbilityResult result = ability.use(dragon, player);
        assertNotNull(result.burnEffect());
        assertEquals(0, result.totalDamage());
        assertTrue(result.summonedEnemies().isEmpty());
    }

    @Test
    void dragonBreath_burnDamageInRange() {
        DragonBreathAbility ability = new DragonBreathAbility(new Random(42));
        Enemy dragon = EnemyFactory.createUltimoDrago();
        Warrior player = new Warrior("P");
        BurnEffect burn = ability.use(dragon, player).burnEffect();
        assertTrue(burn.getDamagePerTurn() >= 5 && burn.getDamagePerTurn() <= 8);
        assertTrue(burn.getTurnsRemaining() >= 3 && burn.getTurnsRemaining() <= 5);
    }

    @Test
    void dragonBreath_messageContainsBurnInfo() {
        DragonBreathAbility ability = new DragonBreathAbility(new Random(42));
        Enemy dragon = EnemyFactory.createUltimoDrago();
        Warrior player = new Warrior("Player");
        String msg = ability.use(dragon, player).message();
        assertTrue(msg.contains("Player") || msg.contains("Drago"));
    }

    @Test
    void witchSummon_evokesEnemies() {
        Enemy witch = EnemyFactory.createStrega();
        assertTrue(witch.hasAbility());
        Warrior player = new Warrior("P");
        EnemyAbility.AbilityResult result = witch.getAbility().use(witch, player);
        assertFalse(result.summonedEnemies().isEmpty());
    }

    @Test
    void witchSummon_makesWitchImmune() {
        Enemy witch = EnemyFactory.createStrega();
        Warrior player = new Warrior("P");
        witch.getAbility().use(witch, player);
        assertTrue(witch.isImmune());
    }

    @Test
    void reGoblinThrow_dealsDamage() {
        Enemy reGoblin = EnemyFactory.createReGoblin();
        assertTrue(reGoblin.hasAbility());
        Warrior player = new Warrior("P");
        int hpBefore = player.getCurrentHp();
        reGoblin.getAbility().use(reGoblin, player);
        assertTrue(player.getCurrentHp() < hpBefore);
    }

    @Test
    void abilityResult_of_factoryMethod() {
        EnemyAbility.AbilityResult r = EnemyAbility.AbilityResult.of("test", 15);
        assertEquals("test", r.message());
        assertEquals(15, r.totalDamage());
        assertTrue(r.summonedEnemies().isEmpty());
        assertNull(r.burnEffect());
    }
}
