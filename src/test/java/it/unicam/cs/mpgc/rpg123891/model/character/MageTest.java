package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Mage.
 *
 * Scudo Magico (GAME_SPEC): riduce del 30% i danni fisici in arrivo.
 * Non si tratta di assorbimento totale: lo scudo è applicato da CombatSystem
 * tramite applyMagePassive() ad ogni attacco fisico ricevuto.
 * Vulnerabilità: +30% danno MAGICAL/MIXED.
 */
public class MageTest {

    // nextDouble() = 1.0 → mai critico
    private static final Random NO_LUCK = new Random(0) {
        @Override public double nextDouble() { return 1.0; }
    };

    @Test
    void mage_baseStats() {
        Mage m = new Mage("Mago");
        assertEquals(90,   m.getMaxHp(),      "HP base deve essere 90");
        assertEquals(15,   m.getAttack(),      "ATK base deve essere 15");
        assertEquals(4,    m.getDefense(),     "DEF base deve essere 4");
        assertEquals(6,    m.getAgility(),     "AGI base deve essere 6");
        assertEquals(15,   m.getMaxStamina(),  "STA base deve essere 15");
        assertEquals(0.05, m.getCritChance(), 0.001, "CRIT base deve essere 5%");
    }

    @Test
    void mage_physicalDamage_reducedBy30Percent() {
        // Lo scudo -30% fisico è sempre attivo via CombatSystem.applyMagePassive()
        Mage m = new Mage("Mago"); // DEF=4
        int hpBefore = m.getCurrentHp();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        Enemy goblin = EnemyFactory.createGoblin(); // ATK variabile, usiamo attacco diretto

        // Creiamo un nemico con ATK fisso per rendere il test deterministico
        Enemy attacker = new Enemy("Test", 100, 20, 0, AttackType.PHYSICAL, 0.0);
        cs.executeAttack(attacker, m, AttackType.PHYSICAL, 0);

        // Danno atteso: (20 * 0.70) = 14, poi 14 - 4(DEF) = 10
        int expectedDamage = Math.max(0, (int)(20 * 0.70) - m.getDefense());
        assertEquals(hpBefore - expectedDamage, m.getCurrentHp(),
                "Il danno fisico deve essere ridotto del 30% dallo scudo magico");
    }

    @Test
    void mage_physicalDamage_isLowerThanRaw() {
        // Verifica che il danno fisico subito sia < danno senza scudo
        Mage m = new Mage("Mago");
        int hpBefore = m.getCurrentHp();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        Enemy attacker = new Enemy("Test", 100, 30, 0, AttackType.PHYSICAL, 0.0);
        cs.executeAttack(attacker, m, AttackType.PHYSICAL, 0);
        int actualDamage = hpBefore - m.getCurrentHp();

        // Danno senza scudo sarebbe: 30 - 4(DEF) = 26
        int rawDamage = Math.max(0, 30 - m.getDefense());
        assertTrue(actualDamage < rawDamage,
                "Il danno fisico con scudo deve essere minore del danno grezzo");
    }

    @Test
    void mage_magicVulnerability_increasesMagicalDamage() {
        Mage m = new Mage("Mago"); // DEF=4
        int hpBefore = m.getCurrentHp();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        Enemy attacker = new Enemy("Test", 100, 20, 0, AttackType.MAGICAL, 0.0);
        cs.executeAttack(attacker, m, AttackType.MAGICAL, 0);

        // Danno atteso: (20 * 1.30) = 26, poi 26 - 4(DEF) = 22
        int expectedDamage = Math.max(0, (int)(20 * 1.30) - m.getDefense());
        assertEquals(hpBefore - expectedDamage, m.getCurrentHp(),
                "Il danno magico deve essere aumentato del 30% per la vulnerabilità");
    }

    @Test
    void mage_magicVulnerability_isHigherThanRaw() {
        // Verifica che danno MAGICAL subito sia > danno grezzo senza vulnerabilità
        Mage m = new Mage("Mago");
        int hpBefore = m.getCurrentHp();
        CombatSystem cs = new CombatSystem(NO_LUCK);
        Enemy attacker = new Enemy("Test", 100, 20, 0, AttackType.MAGICAL, 0.0);
        cs.executeAttack(attacker, m, AttackType.MAGICAL, 0);
        int actualDamage = hpBefore - m.getCurrentHp();

        // Danno senza vulnerabilità: 20 - 4(DEF) = 16
        int rawDamage = Math.max(0, 20 - m.getDefense());
        assertTrue(actualDamage > rawDamage,
                "Il danno magico con vulnerabilità deve essere maggiore del danno grezzo");
    }

    @Test
    void mage_applyPassiveBonus_restoresStamina() {
        Mage m = new Mage("Mago");
        // Consuma tutta la stamina
        while (m.getCurrentStamina() > 0) m.consumeStaminaForAttack();
        assertEquals(0, m.getCurrentStamina());
        m.applyPassiveBonus();
        assertTrue(m.getCurrentStamina() > 0,
                "applyPassiveBonus deve ripristinare stamina");
    }

    @Test
    void mage_characterClass_isMage() {
        Mage m = new Mage("Mago");
        assertEquals(CharacterClass.MAGE, m.getCharacterClass());
    }
}
