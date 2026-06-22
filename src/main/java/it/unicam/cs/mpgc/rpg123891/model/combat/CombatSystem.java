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
 * Passive attive integrate:
 *
 *   WARRIOR (difensiva):
 *     - 20% blocco attacchi FISICI in arrivo.
 *
 *   MAGE (difensiva):
 *     - Scudo magico assorbe il primo attacco FISICO (poi si disattiva).
 *     - Vulnerabile a MAGICAL/MIXED: +30% danno subito.
 *
 *   THIEF (offensiva):
 *     - Primo attacco della stanza SEMPRE critico (stealthBonusActive).
 *     - Dopo ogni attacco NORMALE: +2% crit, fino a 50%.
 *
 * NOTA: l'attacco NORMALE del giocatore NON consuma stamina.
 * La stamina e' riservata esclusivamente agli attacchi speciali.
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
     *   4. Blocco Warrior (20% su fisico)
     *   5. Scudo/vulnerabilita' Mago
     *   6. takeDamage()
     *
     * La stamina NON viene consumata dagli attacchi normali del giocatore;
     * e' riservata agli attacchi speciali.
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

        // 4. Blocco Warrior (solo fisico)
        if (defender instanceof Warrior warrior && attackType == AttackType.PHYSICAL) {
            if (random.nextDouble() < warrior.getBlockChance()) {
                return 0;
            }
        }

        // 5. Scudo e vulnerabilita' Mago
        damage = applyMagePassive(defender, attackType, damage);
        if (damage < 0) return 0;

        // 6. Danno netto
        int hpBefore = defender.getCurrentHp();
        defender.takeDamage(damage);
        return hpBefore - defender.getCurrentHp();
    }

    // -------------------------------------------------------------------------
    // Attacco speciale
    // -------------------------------------------------------------------------

    /**
     * Esegue un attacco speciale (consuma stamina).
     *
     * @throws IllegalStateException se la stamina e' insufficiente
     */
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
     * Applica le passive del Mago come difensore.
     * @return danno modificato; -1 se lo scudo ha assorbito l'attacco
     */
    private int applyMagePassive(GameCharacter defender, AttackType attackType, int damage) {
        if (!(defender instanceof Mage mageChar)) return damage;

        if (attackType == AttackType.PHYSICAL && mageChar.isMagicShieldActive()) {
            mageChar.setMagicShieldActive(false);
            return -1;
        }

        if (attackType == AttackType.MAGICAL || attackType == AttackType.MIXED) {
            return (int)(damage * 1.30);
        }

        return damage;
    }

    // -------------------------------------------------------------------------
    // Utility pubblica per il log dettagliato
    // -------------------------------------------------------------------------

    /**
     * Calcola il danno netto sottraendo la difesa del difensore,
     * senza applicare passives o effetti speciali.
     * Usato per costruire messaggi di log dettagliati.
     */
    public static int netDamage(int rawDamage, int defense) {
        return Math.max(0, rawDamage - defense);
    }
}
