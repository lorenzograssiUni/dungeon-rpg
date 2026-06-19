package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;

import java.util.Random;

/**
 * Gestisce tutta la logica del combattimento a turni.
 * Si occupa di calcolare il danno, applicare critici, bonus passivi
 * e modificatori legati al tipo di attacco e al tipo di nemico.
 *
 * Responsabilità: CombatSystem conosce le regole del combattimento,
 * non i dettagli interni dei personaggi.
 */
public class CombatSystem {

    private final Random random = new Random();

    /**
     * Esegue un attacco del personaggio attaccante verso il difensore.
     * Calcola danno base, eventuale critico e applica bonus/malus passivi.
     *
     * @param attacker il combattente che attacca
     * @param defender il combattente che difende
     * @param attackType il tipo di attacco sferrato
     * @param enemyCritModifier modificatore al critico imposto dal tipo di nemico
     * @return il danno finale inflitto
     */
    public int executeAttack(GameCharacter attacker, GameCharacter defender,
                              AttackType attackType, double enemyCritModifier) {

        // 1. Calcolo critico
        double effectiveCrit = attacker.getCritChance() + enemyCritModifier;
        boolean isCritical = false;

        // Bonus furtività Ladro: primo attacco sempre critico
        if (attacker instanceof Thief thief && thief.isStealthBonusActive()) {
            isCritical = true;
            thief.consumeStealthBonus();
        } else {
            isCritical = random.nextDouble() < effectiveCrit;
        }

        // 2. Calcolo danno base
        int baseDamage = attacker.getAttack();
        int damage = isCritical ? (int) (baseDamage * 2.0) : baseDamage;

        // 3. Blocco Guerriero: possibilità di annullare l'attacco fisico
        if (defender instanceof Warrior warrior && attackType == AttackType.PHYSICAL) {
            if (random.nextDouble() < warrior.getBlockChance()) {
                return 0; // attacco bloccato
            }
        }

        // 4. Schermo Magico del Mago
        if (defender instanceof Mage mage) {
            if (attackType == AttackType.PHYSICAL && mage.isMagicShieldActive()) {
                mage.setMagicShieldActive(false);
                return 0; // schermo assorbe l'attacco fisico
            }
            if (attackType == AttackType.MAGICAL || attackType == AttackType.MIXED) {
                damage = (int) (damage * 1.30); // vulnerabilità magica
            }
        }

        // 5. Applica danno al difensore
        defender.takeDamage(damage);
        return damage;
    }
}
