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
 * interazioni speciali tra classi (Warrior block, Mage passive, Thief stealth).
 *
 * Tutti i test che coinvolgono RNG usano un Random a seed fisso o
 * un Random che restituisce sempre 0.9999 (nessun critico, nessun blocco)
 * per garantire determinismo.
 *
 * Nota GAME_SPEC:
 *   - Il blocco Warrior e' probabilistico (20%) oppure garantito al 5o attacco.
 *     I test verificano il comportamento del counter, non il singolo blocco casuale.
 *   - Lo scudo Mage e' una passive PERMANENTE (-30% danno fisico), non un'abilita' on/off.
 */
class CombatSystemTest {

    /**
     * Random che restituisce sempre 0.9999: nessun critico, nessun blocco random.
     */
    private static final Random NO_RNG = new Random() {
        @Override public double nextDouble() { return 0.9999999999; }
    };

    /**
     * Random che restituisce sempre 0.0: critico garantito al primo attacco.
     * NON usato per il blocco Warrior (il blocco si attiva sempre a 0.0 < 0.20).
     */
    private static final Random ALWAYS_RNG = new Random() {
        @Override public double nextDouble() { return 0.0; }
    };

    private CombatSystem combatSystemNoRng;
    private Warrior warrior;
    private Mage mage;
    private Thief thief;
    private Enemy goblin;

    @BeforeEach
    void setUp() {
        combatSystemNoRng = new CombatSystem(NO_RNG);
        warrior = new Warrior("Eroe");
        mage    = new Mage("Gandalf");
        thief   = new Thief("Ombra");
        goblin  = new Enemy("Goblin", 30, 10, 2, AttackType.PHYSICAL, 0.0);
    }

    // -------------------------------------------------------------------------
    // Attacco base
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Thief - stealth bonus
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
        int baseDamage = thief.getAttack();
        int defense    = goblin.getDefense();
        int hpBefore   = goblin.getCurrentHp();

        combatSystemNoRng.executeAttack(thief, goblin, AttackType.PHYSICAL, 0);

        int actualDamage   = hpBefore - goblin.getCurrentHp();
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
    // Warrior - block counter
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Il Warrior blocca al 5o attacco fisico consecutivo (garantito)")
    void testWarriorBlockGuaranteedAtFifthHit() {
        // Usa NO_RNG (0.9999): il blocco casuale (20%) non scatta mai.
        // Dopo 4 attacchi il counter e' 4; il 5o deve essere bloccato.
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

    // -------------------------------------------------------------------------
    // Mage - passive permanente (GAME_SPEC)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Il Mage subisce il 70% del danno fisico (scudo passivo -30%)")
    void testMagePhysicalDamageReduced() {
        int hpBefore = mage.getCurrentHp();
        combatSystemNoRng.executeAttack(goblin, mage, AttackType.PHYSICAL, 0);
        int damage      = hpBefore - mage.getCurrentHp();
        // danno lordo ridotto del 30%, poi sottratta la difesa del mago
        int reducedRaw  = (int)(goblin.getAttack() * 0.70);
        int expectedDmg = Math.max(0, reducedRaw - mage.getDefense());
        assertEquals(expectedDmg, damage, "Il Mage deve subire il 70% del danno fisico lordo (scudo passivo)");
    }

    @Test
    @DisplayName("Il Mage subisce danno aumentato da attacchi magici (+30%)")
    void testMageVulnerableToMagicAttack() {
        int hpBefore    = mage.getCurrentHp();
        combatSystemNoRng.executeAttack(goblin, mage, AttackType.MAGICAL, 0);
        int damage      = hpBefore - mage.getCurrentHp();
        int expectedDmg = Math.max(0, (int)(goblin.getAttack() * 1.30) - mage.getDefense());
        assertEquals(expectedDmg, damage);
    }
}
