package it.unicam.cs.mpgc.rpg123891.model.combat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica Enemy: stordimento, immunità, passive buff drago,
 * attribiti base e costruttore semplificato.
 */
public class EnemyTest {

    private Enemy makeEnemy() {
        return new Enemy("Test", 50, 10, 3, AttackType.PHYSICAL, 0.05);
    }

    @Test
    void enemy_baseStats() {
        Enemy e = makeEnemy();
        assertEquals("Test", e.getName());
        assertEquals(50, e.getMaxHp());
        assertEquals(50, e.getCurrentHp());
        assertEquals(10, e.getAttack());
        assertEquals(3,  e.getDefense());
    }

    @Test
    void enemy_isAlive_untilHpZero() {
        Enemy e = makeEnemy();
        assertTrue(e.isAlive());
        e.takeDamage(999);
        assertFalse(e.isAlive());
    }

    @Test
    void enemy_stun_makesStunned() {
        Enemy e = makeEnemy();
        assertFalse(e.isStunned());
        e.stun();
        assertTrue(e.isStunned());
    }

    @Test
    void enemy_clearStun_removesStun() {
        Enemy e = makeEnemy();
        e.stun();
        e.clearStun();
        assertFalse(e.isStunned());
    }

    @Test
    void enemy_immune_flagWorks() {
        Enemy e = makeEnemy();
        assertFalse(e.isImmune());
        e.setImmune(true);
        assertTrue(e.isImmune());
        e.setImmune(false);
        assertFalse(e.isImmune());
    }

    @Test
    void enemy_hasAbility_falseByDefault() {
        Enemy e = makeEnemy();
        assertFalse(e.hasAbility());
        assertNull(e.getAbility());
    }

    @Test
    void enemy_setAbility_works() {
        Enemy dragon = EnemyFactory.createDragon();
        assertTrue(dragon.hasAbility());
        assertNotNull(dragon.getAbility());
    }

    @Test
    void enemy_applyPassiveBonus_withBuff_increasesAttack() {
        Enemy dragon = EnemyFactory.createDragon();
        int atkBefore = dragon.getAttack();
        dragon.applyPassiveBonus();
        assertTrue(dragon.getAttack() >= atkBefore,
                "Il buff passivo deve aumentare o mantenere l'attacco del Drago");
    }

    @Test
    void enemy_applyPassiveBonus_noBuff_noChange() {
        Enemy e = makeEnemy(); // nessun passiveBuff
        int atkBefore = e.getAttack();
        e.applyPassiveBonus();
        assertEquals(atkBefore, e.getAttack());
    }

    @Test
    void enemy_takeDamage_reducedByDefense() {
        Enemy e = makeEnemy(); // DEF=3
        e.takeDamage(10);
        assertEquals(43, e.getCurrentHp()); // 50-(10-3)=43
    }

    @Test
    void enemy_takeDamage_minimumZero() {
        Enemy e = makeEnemy(); // DEF=3
        e.takeDamage(2); // < DEF → 0 danno
        assertEquals(50, e.getCurrentHp());
    }
}
