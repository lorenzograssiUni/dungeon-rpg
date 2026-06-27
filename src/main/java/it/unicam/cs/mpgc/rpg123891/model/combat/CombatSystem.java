package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;

import java.util.Random;

/**
 * Gestisce la logica del combattimento a turni.
 *
 * Passive attive integrate (GAME_SPEC):
 *
 *   WARRIOR (difensiva):
 *     - 20% chance blocco per ogni attacco FISICO ricevuto.
 *     - Il 5o attacco consecutivo senza blocco blocca sicuramente.
 *     - Il contatore NON si azzera al cambio wave.
 *
 *   MAGE (difensiva):
 *     - Scudo Magico PERMANENTE: -30% danno fisico in arrivo (sempre attivo).
 *     - Vulnerabile a MAGICAL/MIXED: +30% danno subito.
 *
 *   THIEF (offensiva):
 *     - Primo attacco della wave SEMPRE critico (stealthBonusActive).
 *     - Dopo ogni attacco NORMALE: +2% crit, fino a 50%.
 *
 * NOTA: l'attacco NORMALE del giocatore NON consuma stamina.
 */
public class CombatSystem {

    private final Random random;

    public CombatSystem() {
        this.random = new Random();
    }

    public CombatSystem(Random random) {
        this.random = random;
    }

    // -------------------------------------------------------------------------
    // Iniziativa
    // -------------------------------------------------------------------------

    public boolean hasInitiative(GameCharacter attacker, GameCharacter defender) {
        return attacker.getAgility() >= defender.getAgility();
    }

    // -------------------------------------------------------------------------
    // Attacco normale
    // -------------------------------------------------------------------------

    /**
     * Esegue un attacco normale.
     *
     * Flusso:
     *   1. Critico (stealth Ladro > normale)
     *   2. Incremento crit Ladro dopo ogni attacco
     *   3. Danno lordo
     *   4. Blocco Warrior (20% su fisico, 5o garantito) — usa random iniettato
     *   5. Passive Mago: -30% su fisico / +30% su magico (sempre attivo)
     *   6. Sottrazione difesa (netDamage)
     *   7. takeDamage()
     *
     * @return danno effettivamente inflitto
     */
    public int executeAttack(GameCharacter attacker, GameCharacter defender,
                             AttackType attackType, double enemyCritModifier) {

        // 1. Critico
        boolean isCritical;
        if (attacker instanceof Thief thief && thief.isStealthBonusActive()) {
            isCritical = true;
            thief.consumeStealthBonus();
        } else {
            double effectiveCrit = attacker.getCritChance() + enemyCritModifier;
            isCritical = random.nextDouble() < effectiveCrit;
        }

        // 2. Incremento crit Ladro
        if (attacker instanceof Thief thief) {
            thief.incrementCritAfterAttack();
        }

        // 3. Danno lordo
        int baseDamage = attacker.getAttack();
        int damage = isCritical ? baseDamage * 2 : baseDamage;

        // 4. Blocco Warrior (solo fisico) — usa il random iniettato per determinismo
        if (defender instanceof Warrior warrior && attackType == AttackType.PHYSICAL) {
            if (warrior.testBlock(random)) {
                return 0;
            }
        }

        // 5. Passive Mago (sempre attiva per GAME_SPEC)
        damage = applyMagePassive(defender, attackType, damage);
        if (damage < 0) return 0;

        // 6. Sottrazione difesa
        damage = netDamage(damage, defender.getDefense());

        // 7. Danno netto
        int hpBefore = defender.getCurrentHp();
        defender.takeDamage(damage);
        return hpBefore - defender.getCurrentHp();
    }

    // -------------------------------------------------------------------------
    // Attacco speciale
    // -------------------------------------------------------------------------

    public int executeSpecialAttack(GameCharacter attacker, GameCharacter defender,
                                    SpecialAttack specialAttack) {
        int cost = specialAttack.getStaminaCost();
        if (!attacker.canUseSpecial(cost)) {
            throw new IllegalStateException(
                    attacker.getName() + " non ha abbastanza stamina per " +
                    specialAttack.getName() + " (richiede " + cost + ")");
        }
        attacker.consumeStaminaForSpecial(cost);
        return specialAttack.execute(attacker, defender);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Applica le passive del Mago come difensore (GAME_SPEC):
     *   - Fisico: -30% danno (scudo magico permanente)
     *   - Magico/Misto: +30% danno (vulnerabilita')
     *
     * @return danno modificato
     */
    private int applyMagePassive(GameCharacter defender, AttackType attackType, int damage) {
        if (!(defender instanceof Mage)) return damage;

        if (attackType == AttackType.PHYSICAL) {
            return (int)(damage * 0.70);
        }
        if (attackType == AttackType.MAGICAL || attackType == AttackType.MIXED) {
            return (int)(damage * 1.30);
        }
        return damage;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    public static int netDamage(int rawDamage, int defense) {
        return Math.max(0, rawDamage - defense);
    }
}
