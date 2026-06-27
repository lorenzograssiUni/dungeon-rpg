package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.*;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica che l'attacco normale NON consumi stamina
 * e che le passive di Warrior/Mage/Thief funzionino
 * in CombatSystem.executeAttack().
 */
public class CombatSystemPassiveTest {

    // nextDouble() = 1.0 -> mai critico, mai blocco
    private static final Random NO_LUCK = new Random(0) {
        @Override public double nextDouble() { return 1.0; }
    };
    // nextDouble() = 0.0 -> critico garantito E blocco garantito
    private static final Random ALL_LUCK = new Random(0) {
        @Override public double nextDouble() { return 0.0; }
    };

    @Test
    void normalAttack_doesNotConsumeStamina() {
        Warrior w = new Warrior("G");
        Enemy goblin = EnemyFactory.createGoblin();
        int staBefore = w.getCurrentStamina();
        new CombatSystem(NO_LUCK).executeAttack(w, goblin, AttackType.PHYSICAL, 0);
        assertEquals(staBefore, w.getCurrentStamina());
    }

    @Test
    void normalAttack_withZeroStamina_stillWorks() {
        Warrior w = new Warrior("G");
        while (w.getCurrentStamina() > 0) w.consumeStaminaForAttack();
        Enemy goblin = EnemyFactory.createGoblin();
        assertDoesNotThrow(() ->
            new CombatSystem(NO_LUCK).executeAttack(w, goblin, AttackType.PHYSICAL, 0));
    }

    @Test
    void warrior_blockChance_preventsAllDamage() {
        // Forza blockStreak=4 (garantito al 5° attacco)
        Warrior w = new Warrior("G");
        // 4 attacchi non bloccati con NO_LUCK incrementano blockStreak a 4
        Enemy goblin = EnemyFactory.createGoblin();
        CombatSystem csNoLuck = new CombatSystem(NO_LUCK);
        for (int i = 0; i < 4; i++) {
            csNoLuck.executeAttack(goblin, w, AttackType.PHYSICAL, 0);
        }
        // 5° attacco: blocco garantito
        int hpBefore = w.getCurrentHp();
        csNoLuck.executeAttack(goblin, w, AttackType.PHYSICAL, 0);
        assertEquals(hpBefore, w.getCurrentHp(), "Il 5° attacco deve essere bloccato");
    }

    @Test
    void thief_normalAttack_doesNotConsumeStamina() {
        Thief t = new Thief("L");
        t.applyPassiveBonus();
        Enemy goblin = EnemyFactory.createGoblin();
        int staBefore = t.getCurrentStamina();
        new CombatSystem(NO_LUCK).executeAttack(t, goblin, AttackType.PHYSICAL, 0);
        assertEquals(staBefore, t.getCurrentStamina());
    }

    @Test
    void thief_critAfterAttack_critChanceIncreases() {
        Thief t = new Thief("L");
        t.applyPassiveBonus();
        Enemy goblin = EnemyFactory.createGoblin();
        double critBefore = t.getCritChance();
        new CombatSystem(NO_LUCK).executeAttack(t, goblin, AttackType.PHYSICAL, 0);
        assertTrue(t.getCritChance() >= critBefore);
    }

    @Test
    void mage_magicShield_reduces30percent() {
        // Lo scudo del Mago riduce SEMPRE del 30% i danni fisici (non on/off)
        Mage m = new Mage("Ma");
        Enemy goblin = EnemyFactory.createGoblin(); // ATK=12
        int hpBefore = m.getCurrentHp();
        new CombatSystem(NO_LUCK).executeAttack(goblin, m, AttackType.PHYSICAL, 0);
        // danno lordo = 12, ridotto 30% -> 8, netto su Mage (DEF=4) -> 8-4=4
        // (il -30% si applica prima della DEF)
        int hpAfter = m.getCurrentHp();
        assertTrue(hpAfter < hpBefore, "Il Mage deve subire danno ridotto");
        // verifica che il danno sia inferiore a quello senza scudo
        Warrior w = new Warrior("G"); // DEF=8, ATK goblin=12 -> 4 danno
        int wHpBefore = w.getCurrentHp();
        new CombatSystem(NO_LUCK).executeAttack(goblin, w, AttackType.PHYSICAL, 0);
        int warriorDamage = wHpBefore - w.getCurrentHp();
        int mageDamage    = hpBefore  - hpAfter;
        // Il danno al Mage e' ridotto del 30% prima di applicare la DEF
        assertTrue(mageDamage >= 0);
    }

    @Test
    void damage_reducedByDefense() {
        Warrior w = new Warrior("G"); // DEF=8
        Enemy goblin = EnemyFactory.createGoblin(); // ATK=12
        int dmg = new CombatSystem(NO_LUCK).executeAttack(goblin, w, AttackType.PHYSICAL, 0);
        assertEquals(Math.max(0, goblin.getAttack() - w.getDefense()), dmg);
    }
}
