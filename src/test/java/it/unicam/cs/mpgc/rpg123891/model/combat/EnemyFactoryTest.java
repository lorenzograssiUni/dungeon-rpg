package it.unicam.cs.mpgc.rpg123891.model.combat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica che EnemyFactory crei nemici con stat coerenti:
 *  - nomi corretti
 *  - HP > 0
 *  - isBoss() solo per L'Ultimo Drago
 *  - Strega ha abilita'
 *  - Re Goblin e' boss
 */
public class EnemyFactoryTest {

    @Test
    void createCinghiale_hasCorrectName() {
        assertEquals("Cinghiale", EnemyFactory.createCinghiale().getName());
    }

    @Test
    void createLupo_hasPositiveHp() {
        assertTrue(EnemyFactory.createLupo().getMaxHp() > 0);
    }

    @Test
    void createGoblin_isNotBoss() {
        assertFalse(EnemyFactory.createGoblin().isBoss());
    }

    @Test
    void createReGoblin_isBoss() {
        assertTrue(EnemyFactory.createReGoblin().isBoss());
    }

    @Test
    void createScheletro_hasPhysicalAttack() {
        assertEquals(AttackType.PHYSICAL, EnemyFactory.createScheletro().getAttackType());
    }

    @Test
    void createStrega_hasAbility() {
        assertTrue(EnemyFactory.createStrega().hasAbility());
    }

    @Test
    void createUltimoDrago_isBossAndHasPassiveBuff() {
        Enemy dragon = EnemyFactory.createUltimoDrago();
        assertTrue(dragon.isBoss());
        assertNotNull(dragon.getPassiveBuff());
    }

    @Test
    void createUovo_isAliveInitially() {
        assertTrue(EnemyFactory.createUovo().isAlive());
    }

    @Test
    void createCuccioloDrago_hasPositiveAttack() {
        assertTrue(EnemyFactory.createCuccioloDrago().getAttack() > 0);
    }

    @Test
    void stun_and_clearStun_work() {
        Enemy e = EnemyFactory.createGoblin();
        assertFalse(e.isStunned());
        e.stun();
        assertTrue(e.isStunned());
        e.clearStun();
        assertFalse(e.isStunned());
    }

    @Test
    void setImmune_and_isImmune_work() {
        Enemy e = EnemyFactory.createGoblin();
        assertFalse(e.isImmune());
        e.setImmune(true);
        assertTrue(e.isImmune());
    }
}
