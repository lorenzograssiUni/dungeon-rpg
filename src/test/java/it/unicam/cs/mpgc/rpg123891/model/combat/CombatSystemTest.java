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
 * interazioni speciali tra classi (Warrior block, Mage shield).
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
        // Attacchiamo ripetutamente finché il goblin non è morto
        for (int i = 0; i < 20; i++) {
            combatSystem.executeAttack(warrior, goblin, AttackType.PHYSICAL, 0);
        }
        assertTrue(goblin.getCurrentHp() >= 0);
    }

    @Test
    @DisplayName("Il danno è almeno zero (attacco con difesa alta non dà danno negativo)")
    void testDamageIsNonNegative() {
        // Nemico con difesa altissima
        Enemy tankEnemy = new Enemy("Tank", 100, 5, 9999, AttackType.PHYSICAL, 0.0);
        int hpBefore = tankEnemy.getCurrentHp();
        combatSystem.executeAttack(warrior, tankEnemy, AttackType.PHYSICAL, 0);
        assertTrue(tankEnemy.getCurrentHp() <= hpBefore);
    }

    @Test
    @DisplayName("Il Thief con stealth bonus infligge sempre un critico (danno doppio)")
    void testThiefStealthFirstAttackIsCritical() {
        thief.applyPassiveBonus(); // attiva lo stealth bonus
        int baseDamage = thief.getAttack();
        int expectedCritDamage = baseDamage * 2;
        int hpBefore = goblin.getCurrentHp();
        combatSystem.executeAttack(thief, goblin, AttackType.PHYSICAL, 0);
        int actualDamage = hpBefore - goblin.getCurrentHp();
        // Il danno critico è il doppio dell'attacco meno la difesa del goblin
        int expectedFinal = Math.max(0, expectedCritDamage - goblin.getDefense());
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
        assertEquals(hpBefore, mage.getCurrentHp()); // nessun danno
        assertFalse(mage.isMagicShieldActive()); // scudo consumato
    }

    @Test
    @DisplayName("Il Mage subisce danno aumentato da attacchi magici")
    void testMageVulnerableToMagicAttack() {
        int hpBefore = mage.getCurrentHp();
        combatSystem.executeAttack(goblin, mage, AttackType.MAGICAL, 0);
        int damage = hpBefore - mage.getCurrentHp();
        // Il danno magico è aumentato del 30% sul base, poi ridotto dalla difesa
        int base = goblin.getAttack();
        int expectedDmg = Math.max(0, (int)(base * 1.30) - mage.getDefense());
        assertEquals(expectedDmg, damage);
    }
}
