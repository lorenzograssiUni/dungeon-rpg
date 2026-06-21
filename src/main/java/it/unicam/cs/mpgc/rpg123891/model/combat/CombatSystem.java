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
 * Regole stamina:
 *   - Solo i personaggi giocabili (non Enemy) consumano stamina.
 *   - Se un PlayerCharacter ha stamina 0, non puo' attaccare.
 *   - I nemici non hanno stamina e attaccano sempre liberamente.
 *
 * Iniziativa: chi ha agility >= agility avversario attacca per primo.
 *   In parita' il giocatore ha priorita'.
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
     * Se l'attaccante e' un giocatore (non Enemy):
     *   - Controlla che abbia almeno 1 stamina (lancia IllegalStateException se 0)
     *   - Consuma 1 stamina
     * I nemici (Enemy) non consumano stamina.
     *
     * @throws IllegalStateException se un giocatore ha stamina 0
     */
    public int executeAttack(GameCharacter attacker, GameCharacter defender,
                             AttackType attackType, double enemyCritModifier) {

        boolean isPlayer = !(attacker instanceof Enemy);

        if (isPlayer) {
            if (!attacker.canAttack()) {
                throw new IllegalStateException(
                        attacker.getName() + " non puo' attaccare: stamina esaurita!");
            }
            attacker.consumeStaminaForAttack();
        }

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

        // Blocco Warrior (solo attacchi fisici)
        if (defender instanceof Warrior warrior && attackType == AttackType.PHYSICAL) {
            if (random.nextDouble() < warrior.getBlockChance()) {
                return 0;
            }
        }

        // Scudo e vulnerabilita' Mago
        if (defender instanceof Mage mageChar) {
            if (attackType == AttackType.PHYSICAL && mageChar.isMagicShieldActive()) {
                mageChar.setMagicShieldActive(false);
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
    // Attacco speciale (solo giocatori)
    // -------------------------------------------------------------------------

    /**
     * Esegue un attacco speciale consumando la stamina richiesta.
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
}
