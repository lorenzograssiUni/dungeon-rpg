package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per CombatSystem.
 * Verifica la logica di combattimento: calcolo danno, gestione critico,
 * interazioni speciali tra classi (Warrior block, Thief stealth).
 */
class CombatSystemTest {

    private static final Random NO_RNG = new Random() {
        @Override public double nextDouble() { return 0.9999999999; }
    };

    private static final Random ALWAYS_RNG = new Random() {
        @Override public double nextDouble() { return 0.0; }
    };

    private CombatSystem combatSystemNoRng;
    private Warrior warrior;
    private Thief thief;
    private Enemy goblin;

    @BeforeEach
    void setUp() {
        combatSystemNoRng = new CombatSystem(NO_RNG);
        warrior = new Warrior("Eroe");
        thief   = new Thief("Ombra");
        goblin  = new Enemy("Goblin", 30, 10, 2, AttackType.PHYSICAL, 0.0);
    }

    @Test
    @DisplayName("Un attacco fisico riduce gli HP del difensore")
    void testAttackReducesDefenderHp() {
        int hpBefore = goblin.getCurrentHp();
        combatSystemNoRng.executeAttack(warrior, goblin, AttackType.PHYSICAL, 0);
        assertTrue(goblin.getCurrentHp() < hpBefore);
    }

    @Test
    @DisplayName("Un attacco non porta mai gli HP sotto zero")
    void testHpNeverNegativeAfterAttack() {
        while (goblin.isAlive() && warrior.canAttack()) {
            combatSystemNoRng.executeAttack(warrior, goblin, AttackType.PHYSICAL, 0);
        }
        assertTrue(goblin.getCurrentHp() >= 0);
    }

    @Test
    @DisplayName("Il danno e' almeno zero anche con difesa altissima")
    void testDamageIsNonNegative() {
        Enemy tank = new Enemy("Tank", 100, 5, 9999, AttackType.PHYSICAL, 0.0);
        int hpBefore = tank.getCurrentHp();
        combatSystemNoRng.executeAttack(warrior, tank, AttackType.PHYSICAL, 0);
        assertTrue(tank.getCurrentHp() <= hpBefore);
    }

    @Test
    @DisplayName("Stealth bonus attivo prima del primo attacco")
    void testThiefStealthBonusActiveBeforeAttack() {
        thief.applyPassiveBonus();
        assertTrue(thief.isStealthBonusActive());
    }

    @Test
    @DisplayName("Lo stealth bonus del Thief viene consumato dopo il primo attacco")
    void testThiefStealthBonusConsumedAfterUse() {
        thief.applyPassiveBonus();
        assertTrue(thief.isStealthBonusActive());
        combatSystemNoRng.executeAttack(thief, goblin, AttackType.PHYSICAL, 0);
        assertFalse(thief.isStealthBonusActive());
    }

    @Test
    @DisplayName("Il Warrior blocca al 5o attacco fisico consecutivo (garantito)")
    void testWarriorBlockGuaranteedAtFifthHit() {
        CombatSystem cs = new CombatSystem(NO_RNG);
        Enemy e = new Enemy("TestEnemy", 200, 12, 0, AttackType.PHYSICAL, 0.0);
        for (int i = 0; i < 4; i++) {
            cs.executeAttack(e, warrior, AttackType.PHYSICAL, 0);
        }
        assertEquals(4, warrior.getBlockStreak(), "Dopo 4 attacchi non bloccati il counter deve essere 4");
        int hpBefore = warrior.getCurrentHp();
        cs.executeAttack(e, warrior, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, warrior.getCurrentHp(), "Il 5o attacco deve essere bloccato (garantito)");
        assertEquals(0, warrior.getBlockStreak(), "Dopo un blocco il counter si azzera");
    }

    @Test
    @DisplayName("Il Warrior non blocca attacchi magici")
    void testWarriorDoesNotBlockMagicAttack() {
        Enemy strongEnemy = new Enemy("Troll", 100, 50, 0, AttackType.MAGICAL, 0.0);
        CombatSystem alwaysBlock = new CombatSystem(ALWAYS_RNG);
        int hpBefore = warrior.getCurrentHp();
        alwaysBlock.executeAttack(strongEnemy, warrior, AttackType.MAGICAL, 0);
        assertTrue(warrior.getCurrentHp() < hpBefore);
    }
}
