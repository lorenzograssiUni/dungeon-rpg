package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;

import java.util.Random;

/**
 * Gestisce la logica del combattimento a turni.
 *
 * Novita' rispetto alla versione precedente:
 *   - Iniziativa: chi ha agility >= agility avversario attacca per primo.
 *     In caso di parita' va prima il giocatore.
 *   - Stamina: ogni executeAttack consuma 1 stamina all'attaccante.
 *     Se stamina = 0, il metodo lancia IllegalStateException (il chiamante
 *     deve verificare canAttack() prima di invocare).
 *   - executeSpecialAttack: esegue un SpecialAttack consumando la stamina
 *     richiesta dall'attacco speciale.
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

    /**
     * Restituisce true se l'attaccante ha l'iniziativa sul difensore.
     * In parita' l'attaccante (giocatore) ha priorita'.
     */
    public boolean hasInitiative(GameCharacter attacker, GameCharacter defender) {
        return attacker.getAgility() >= defender.getAgility();
    }

    // -------------------------------------------------------------------------
    // Attacco normale
    // -------------------------------------------------------------------------

    /**
     * Esegue un attacco normale dell'attaccante verso il difensore.
     * Consuma 1 stamina all'attaccante.
     *
     * @throws IllegalStateException se l'attaccante non ha stamina sufficiente
     */
    public int executeAttack(GameCharacter attacker, GameCharacter defender,
                             AttackType attackType, double enemyCritModifier) {
        if (!attacker.canAttack()) {
            throw new IllegalStateException(
                    attacker.getName() + " non puo' attaccare: stamina esaurita!");
        }

        // Consuma 1 stamina
        attacker.consumeStaminaForAttack();

        // Critico
        boolean isCritical;
        if (attacker instanceof Thief thief && thief.isStealthBonusActive()) {
            isCritical = true;
            thief.consumeStealthBonus();
        } else {
            double effectiveCrit = attacker.getCritChance() + enemyCritModifier;
            isCritical = random.nextDouble() < effectiveCrit;
        }

        // Danno lordo
        int baseDamage = attacker.getAttack();
        int damage = isCritical ? baseDamage * 2 : baseDamage;

        // Blocco Guerriero (solo attacchi fisici)
        if (defender instanceof Warrior warrior && attackType == AttackType.PHYSICAL) {
            if (random.nextDouble() < warrior.getBlockChance()) {
                return 0;
            }
        }

        // Schermo e vulnerabilita' Mago
        if (defender instanceof Mage mage) {
            if (attackType == AttackType.PHYSICAL && mage.isMagicShieldActive()) {
                mage.setMagicShieldActive(false);
                return 0;
            }
            if (attackType == AttackType.MAGICAL || attackType == AttackType.MIXED) {
                damage = (int)(damage * 1.30);
            }
        }

        // Danno netto
        int hpBefore = defender.getCurrentHp();
        defender.takeDamage(damage);
        return hpBefore - defender.getCurrentHp();
    }

    // -------------------------------------------------------------------------
    // Attacco speciale
    // -------------------------------------------------------------------------

    /**
     * Esegue un attacco speciale.
     * Consuma la stamina richiesta dall'attacco speciale.
     *
     * @throws IllegalStateException se l'attaccante non ha stamina sufficiente
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
}
