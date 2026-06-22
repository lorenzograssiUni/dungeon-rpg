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
 *     - Applicato in executeAttack() quando defender e' Warrior.
 *
 *   MAGE (difensiva):
 *     - Scudo magico assorbe il primo attacco FISICO (poi si disattiva).
 *     - Vulnerabile a MAGICAL/MIXED: +30% danno subito.
 *     - Applicato in executeAttack() quando defender e' Mage.
 *
 *   THIEF (offensiva):
 *     - Primo attacco della stanza SEMPRE critico (stealthBonusActive).
 *     - Dopo ogni attacco NORMALE: +2% crit, fino a 50%.
 *     - Applicato/incrementato in executeAttack() quando attacker e' Thief.
 *
 * Le passive si applicano ANCHE negli attacchi speciali (executeSpecialAttack)
 * per il lato DIFENSIVO (blocco Warrior, scudo Mage, vulnerabilita' Mage).
 * Il lato offensivo del Ladro (stealth, incremento crit) NON si applica
 * agli speciali (solo agli attacchi normali).
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
     *   1. Controlla/consuma stamina (solo giocatori)
     *   2. Calcola critico (stealth Ladro > normale)
     *   3. Applica critico Ladro (+2% dopo l'attacco)
     *   4. Blocco Warrior (20% su fisico)
     *   5. Scudo/vulnerabilita' Mago
     *   6. takeDamage()
     *
     * @throws IllegalStateException se il giocatore ha stamina 0
     */
    public int executeAttack(GameCharacter attacker, GameCharacter defender,
                             AttackType attackType, double enemyCritModifier) {

        boolean isPlayer = !(attacker instanceof Enemy);

        // 1. Stamina (solo giocatori)
        if (isPlayer) {
            if (!attacker.canAttack()) {
                throw new IllegalStateException(
                        attacker.getName() + " non puo' attaccare: stamina esaurita!");
            }
            attacker.consumeStaminaForAttack();
        }

        // 2. Critico
        boolean isCritical;
        if (attacker instanceof Thief thief && thief.isStealthBonusActive()) {
            isCritical = true;
            thief.consumeStealthBonus();
        } else {
            double effectiveCrit = attacker.getCritChance() + enemyCritModifier;
            isCritical = random.nextDouble() < effectiveCrit;
        }

        // 3. Incremento crit Ladro dopo ogni attacco normale
        if (attacker instanceof Thief thief) {
            thief.incrementCritAfterAttack();
        }

        // 4. Danno lordo
        int baseDamage = attacker.getAttack();
        int damage = isCritical ? baseDamage * 2 : baseDamage;

        // 5. Blocco Warrior (solo fisico)
        if (defender instanceof Warrior warrior && attackType == AttackType.PHYSICAL) {
            if (random.nextDouble() < warrior.getBlockChance()) {
                return 0;  // attacco completamente bloccato
            }
        }

        // 6. Scudo e vulnerabilita' Mago
        damage = applyMagePassive(defender, attackType, damage);
        if (damage < 0) return 0;  // scudo assorbito

        // 7. Danno netto
        int hpBefore = defender.getCurrentHp();
        defender.takeDamage(damage);
        return hpBefore - defender.getCurrentHp();
    }

    // -------------------------------------------------------------------------
    // Attacco speciale
    // -------------------------------------------------------------------------

    /**
     * Esegue un attacco speciale.
     *
     * Le passive DIFENSIVE (blocco Warrior, scudo/vulnerabilita' Mage)
     * sono gia' incorporate nel lambda dell'attacco speciale tramite takeDamage().
     * Qui applichiamo solo la verifica stamina.
     *
     * NOTA: il danno restituito dallo speciale e' gia' netto (calcolato dal lambda).
     * Le passive difensive vengono applicate all'interno del lambda se usa takeDamage().
     * Per gli speciali che usano applyBurnDamage() (danno diretto) le passive
     * difensive NON si applicano per design (e' danno che bypassa le protezioni).
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
     *
     * @return danno modificato; -1 se lo scudo ha assorbito l'attacco
     */
    private int applyMagePassive(GameCharacter defender, AttackType attackType, int damage) {
        if (!(defender instanceof Mage mageChar)) return damage;

        // Scudo magico: assorbe il primo attacco fisico
        if (attackType == AttackType.PHYSICAL && mageChar.isMagicShieldActive()) {
            mageChar.setMagicShieldActive(false);
            return -1;  // segnale: attacco assorbito
        }

        // Vulnerabilita' a MAGICAL e MIXED
        if (attackType == AttackType.MAGICAL || attackType == AttackType.MIXED) {
            return (int)(damage * 1.30);
        }

        return damage;
    }
}
