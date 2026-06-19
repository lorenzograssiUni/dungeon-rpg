package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per CombatSystem.
 * Verifica la logica di combattimento: calcolo danno, gestione critico,
 * interazioni speciali tra classi (Warrior block, Mage shield, Thief stealth).
 */
class CombatSystemTest {

    private CombatSystem combatSystem;
    private Warrior warrior;
    private Mage mage;
    private Thief thief;
    private Enemy goblin;

    @BeforeEach
    void setUp() {
        combatSystem = new CombatSystem();
        warrior = new Warrior("Eroe");
        mage = new Mage("Gandalf");
        thief = new Thief("Ombra");
        // costruttore semplificato: name, maxHp, attack, defense, attackType, critChance
        goblin = new Enemy("Goblin", 30, 10, 2, AttackType.PHYSICAL, 0.0);
    }

    @Test
    @DisplayName("Un attacco fisico riduce gli HP del difensore")
    void testAttackReducesDefenderHp() {
        int hpBefore = goblin.getCurrentHp();
        combatSystem.executeAttack(warrior, goblin, AttackType.PHYSICAL, 0);
        assertTrue(goblin.getCurrentHp() < hpBefore);
    }

    @Test
    @DisplayName("Un attacco non porta mai gli HP sotto zero")
    void testHpNeverNegativeAfterAttack() {
        for (int i = 0; i < 20; i++) {
            combatSystem.executeAttack(warrior, goblin, AttackType.PHYSICAL, 0);
        }
        assertTrue(goblin.getCurrentHp() >= 0);
    }

    @Test
    @DisplayName("Il danno e' almeno zero anche con difesa altissima")
    void testDamageIsNonNegative() {
        Enemy tank = new Enemy("Tank", 100, 5, 9999, AttackType.PHYSICAL, 0.0);
        int hpBefore = tank.getCurrentHp();
        combatSystem.executeAttack(warrior, tank, AttackType.PHYSICAL, 0);
        assertTrue(tank.getCurrentHp() <= hpBefore);
    }

    @Test
    @DisplayName("Il Thief con stealth bonus infligge sempre un critico (danno doppio)")
    void testThiefStealthFirstAttackIsCritical() {
        thief.applyPassiveBonus();
        int baseDamage = thief.getAttack();
        int hpBefore = goblin.getCurrentHp();
        combatSystem.executeAttack(thief, goblin, AttackType.PHYSICAL, 0);
        int actualDamage = hpBefore - goblin.getCurrentHp();
        int expectedFinal = Math.max(0, (baseDamage * 2) - goblin.getDefense());
        assertEquals(expectedFinal, actualDamage);
    }

    @Test
    @DisplayName("Lo stealth bonus del Thief viene consumato dopo il primo attacco")
    void testThiefStealthBonusConsumedAfterUse() {
        thief.applyPassiveBonus();
        assertTrue(thief.isStealthBonusActive());
        combatSystem.executeAttack(thief, goblin, AttackType.PHYSICAL, 0);
        assertFalse(thief.isStealthBonusActive());
    }

    @Test
    @DisplayName("Lo scudo magico del Mage blocca un attacco fisico")
    void testMagicShieldBlocksPhysicalAttack() {
        mage.setMagicShieldActive(true);
        int hpBefore = mage.getCurrentHp();
        combatSystem.executeAttack(goblin, mage, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, mage.getCurrentHp());
        assertFalse(mage.isMagicShieldActive());
    }

    @Test
    @DisplayName("Il Mage subisce danno aumentato da attacchi magici (+30%)")
    void testMageVulnerableToMagicAttack() {
        int hpBefore = mage.getCurrentHp();
        combatSystem.executeAttack(goblin, mage, AttackType.MAGICAL, 0);
        int damage = hpBefore - mage.getCurrentHp();
        int expectedDmg = Math.max(0, (int)(goblin.getAttack() * 1.30) - mage.getDefense());
        assertEquals(expectedDmg, damage);
    }
}
