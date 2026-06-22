package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica le abilità speciali dei nemici:
 *   - DragonBreathAbility: applica BurnEffect al giocatore
 *   - WitchSummonAbility: evoca nemici e rende la Strega immune
 *   - ReGoblinThrowAbility: infligge danno diretto
 */
public class EnemyAbilityTest {

    @Test
    void dragonBreath_returnsNonNullBurn() {
        DragonBreathAbility ability = new DragonBreathAbility(new Random(42));
        Enemy dragon = EnemyFactory.createDragon();
        Warrior player = new Warrior("P");
        EnemyAbility.AbilityResult result = ability.use(dragon, player);
        assertNotNull(result.burnEffect(), "DragonBreath deve restituire un BurnEffect");
        assertEquals(0, result.totalDamage(), "Il danno immediato deve essere 0");
        assertTrue(result.summonedEnemies().isEmpty());
    }

    @Test
    void dragonBreath_burnDamageInRange() {
        DragonBreathAbility ability = new DragonBreathAbility(new Random(42));
        Enemy dragon = EnemyFactory.createDragon();
        Warrior player = new Warrior("P");
        BurnEffect burn = ability.use(dragon, player).burnEffect();
        assertTrue(burn.getDamagePerTurn() >= 5 && burn.getDamagePerTurn() <= 8);
        assertTrue(burn.getTurnsRemaining() >= 3 && burn.getTurnsRemaining() <= 5);
    }

    @Test
    void dragonBreath_messageContainsBurnInfo() {
        DragonBreathAbility ability = new DragonBreathAbility(new Random(42));
        Enemy dragon = EnemyFactory.createDragon();
        Warrior player = new Warrior("Player");
        String msg = ability.use(dragon, player).message();
        assertTrue(msg.contains("Player") || msg.contains("Drago"),
                "Il messaggio deve menzionare il giocatore o il Drago");
    }

    @Test
    void witchSummon_evokesEnemies() {
        Enemy witch = EnemyFactory.createWitch();
        assertTrue(witch.hasAbility());
        Warrior player = new Warrior("P");
        EnemyAbility.AbilityResult result = witch.getAbility().use(witch, player);
        assertFalse(result.summonedEnemies().isEmpty(),
                "La Strega deve evocare almeno un nemico");
    }

    @Test
    void witchSummon_makesWitchImmune() {
        Enemy witch = EnemyFactory.createWitch();
        Warrior player = new Warrior("P");
        witch.getAbility().use(witch, player);
        assertTrue(witch.isImmune(), "La Strega deve diventare immune dopo l'evocazione");
    }

    @Test
    void reGoblinThrow_dealsDamage() {
        Enemy reGoblin = EnemyFactory.createReGoblin();
        assertTrue(reGoblin.hasAbility());
        Warrior player = new Warrior("P");
        int hpBefore = player.getCurrentHp();
        reGoblin.getAbility().use(reGoblin, player);
        assertTrue(player.getCurrentHp() < hpBefore,
                "ReGoblinThrow deve infliggere danno diretto");
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
