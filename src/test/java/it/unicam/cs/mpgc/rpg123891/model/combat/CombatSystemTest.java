package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
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
 * interazioni speciali tra classi (Warrior block, Mage shield, Thief stealth).
 *
 * Tutti i test che coinvolgono RNG usano un Random a seed fisso o
 * un Random che restituisce sempre 1.0 (nessun critico, nessun blocco)
 * per garantire determinismo.
 */
class CombatSystemTest {

    /**
     * Random che restituisce sempre il valore massimo (0.9999...):
     * - nessun critico (serve nextDouble < critChance)
     * - nessun blocco Warrior (serve nextDouble < blockChance)
     * Usato nei test dove l'RNG non deve interferire.
     */
    private static final Random NO_RNG = new Random() {
        @Override
        public double nextDouble() { return 0.9999999999; }
    };

    /**
     * Random che restituisce sempre 0.0:
     * - critico garantito
     * - blocco Warrior garantito
     * Usato nei test che verificano questi comportamenti.
     */
    private static final Random ALWAYS_RNG = new Random() {
        @Override
        public double nextDouble() { return 0.0; }
    };

    private CombatSystem combatSystem;
    private CombatSystem combatSystemNoRng;
    private Warrior warrior;
    private Mage mage;
    private Thief thief;
    private Enemy goblin;

    @BeforeEach
    void setUp() {
        combatSystem      = new CombatSystem();          // produzione (non deterministico)
        combatSystemNoRng = new CombatSystem(NO_RNG);    // test: nessun critico/blocco casuale
        warrior = new Warrior("Eroe");
        mage    = new Mage("Gandalf");
        thief   = new Thief("Ombra");
        // ricreato ad ogni test per evitare stato residuo
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
        for (int i = 0; i < 20; i++) {
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

    // -------------------------------------------------------------------------
    // Thief – stealth bonus
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Stealth bonus attivo prima del primo attacco")
    void testThiefStealthBonusActiveBeforeAttack() {
        thief.applyPassiveBonus();
        assertTrue(thief.isStealthBonusActive());
    }

    @Test
    @DisplayName("Il Thief con stealth bonus infligge sempre un critico (danno doppio)")
    void testThiefStealthFirstAttackIsCritical() {
        thief.applyPassiveBonus();
        int baseDamage = thief.getAttack();   // 20
        int defense    = goblin.getDefense(); // 2
        int hpBefore   = goblin.getCurrentHp(); // 30

        combatSystemNoRng.executeAttack(thief, goblin, AttackType.PHYSICAL, 0);

        int actualDamage = hpBefore - goblin.getCurrentHp();
        // Il danno lordo e' baseDamage*2 - defense, ma cappato agli HP disponibili
        int expectedDamage = Math.min(hpBefore, Math.max(0, (baseDamage * 2) - defense));
        assertEquals(expectedDamage, actualDamage);
    }

    @Test
    @DisplayName("Lo stealth bonus del Thief viene consumato dopo il primo attacco")
    void testThiefStealthBonusConsumedAfterUse() {
        thief.applyPassiveBonus();
        assertTrue(thief.isStealthBonusActive());
        combatSystemNoRng.executeAttack(thief, goblin, AttackType.PHYSICAL, 0);
        assertFalse(thief.isStealthBonusActive());
    }

    // -------------------------------------------------------------------------
    // Warrior – block
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Il Warrior con blocco garantito annulla l'attacco fisico")
    void testWarriorBlockCancelsPhysicalAttack() {
        CombatSystem alwaysBlock = new CombatSystem(ALWAYS_RNG);
        int hpBefore = warrior.getCurrentHp();
        alwaysBlock.executeAttack(goblin, warrior, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, warrior.getCurrentHp());
    }

    @Test
    @DisplayName("Il Warrior non blocca attacchi magici")
    void testWarriorDoesNotBlockMagicAttack() {
        // Usiamo un nemico con attacco alto (50) per bucare la difesa del Warrior (10)
        // danno netto atteso = 50 - 10 = 40, certamente > 0
        Enemy strongEnemy = new Enemy("Troll", 100, 50, 0, AttackType.MAGICAL, 0.0);
        CombatSystem alwaysBlock = new CombatSystem(ALWAYS_RNG);
        int hpBefore = warrior.getCurrentHp();
        alwaysBlock.executeAttack(strongEnemy, warrior, AttackType.MAGICAL, 0);
        assertTrue(warrior.getCurrentHp() < hpBefore);
    }

    // -------------------------------------------------------------------------
    // Mage – shield e vulnerabilita' magica
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Lo scudo magico del Mage blocca un attacco fisico")
    void testMagicShieldBlocksPhysicalAttack() {
        mage.setMagicShieldActive(true);
        int hpBefore = mage.getCurrentHp();
        combatSystemNoRng.executeAttack(goblin, mage, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, mage.getCurrentHp());
        assertFalse(mage.isMagicShieldActive());
    }

    @Test
    @DisplayName("Il Mage subisce danno aumentato da attacchi magici (+30%)")
    void testMageVulnerableToMagicAttack() {
        int hpBefore = mage.getCurrentHp();
        combatSystemNoRng.executeAttack(goblin, mage, AttackType.MAGICAL, 0);
        int damage      = hpBefore - mage.getCurrentHp();
        int expectedDmg = Math.max(0, (int)(goblin.getAttack() * 1.30) - mage.getDefense());
        assertEquals(expectedDmg, damage);
    }
}
