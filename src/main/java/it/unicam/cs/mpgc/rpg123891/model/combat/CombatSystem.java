package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;

import java.util.Random;

/**
 * Gestisce tutta la logica del combattimento a turni.
 * Calcola il danno, applica critici, bonus passivi e modificatori
 * legati al tipo di attacco e alle abilita' speciali dei personaggi.
 */
public class CombatSystem {

    private final Random random = new Random();

    /**
     * Esegue un attacco dell'attaccante verso il difensore.
     *
     * @param attacker          il combattente che attacca
     * @param defender          il combattente che difende
     * @param attackType        il tipo di attacco sferrato
     * @param enemyCritModifier modificatore al critico imposto dal nemico
     * @return il danno finale inflitto
     */
    public int executeAttack(GameCharacter attacker, GameCharacter defender,
                             AttackType attackType, double enemyCritModifier) {

        // 1. Calcolo critico
        boolean isCritical;
        if (attacker instanceof Thief thief && thief.isStealthBonusActive()) {
            // Bonus furtivita' Ladro: primo attacco sempre critico
            isCritical = true;
            thief.consumeStealthBonus();
        } else {
            double effectiveCrit = attacker.getCritChance() + enemyCritModifier;
            isCritical = random.nextDouble() < effectiveCrit;
        }

        // 2. Calcolo danno base
        int baseDamage = attacker.getAttack();
        int damage = isCritical ? baseDamage * 2 : baseDamage;

        // 3. Blocco Guerriero: 20% probabilita' di annullare attacco fisico
        if (defender instanceof Warrior warrior && attackType == AttackType.PHYSICAL) {
            if (random.nextDouble() < warrior.getBlockChance()) {
                return 0;
            }
        }

        // 4. Schermo Magico del Mago
        if (defender instanceof Mage mage) {
            if (attackType == AttackType.PHYSICAL && mage.isMagicShieldActive()) {
                mage.setMagicShieldActive(false);
                return 0; // schermo assorbe l'attacco fisico
            }
            if (attackType == AttackType.MAGICAL || attackType == AttackType.MIXED) {
                damage = (int) (damage * 1.30); // vulnerabilita' magica
            }
        }

        // 5. Applica danno al difensore (la difesa e' sottratta in takeDamage)
        defender.takeDamage(damage);
        return damage;
    }
}
