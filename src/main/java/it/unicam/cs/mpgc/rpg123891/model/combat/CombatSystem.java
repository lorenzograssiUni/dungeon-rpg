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
 *
 * NOTA: executeAttack restituisce il danno NETTO effettivamente subito
 * dal difensore (cioe' gia' ridotto dalla sua difesa), non il danno lordo.
 * Questo e' coerente con quanto si legge dall'esterno (HP prima - HP dopo).
 */
public class CombatSystem {

    private final Random random;

    /** Costruttore di default: usa un Random non deterministico (produzione). */
    public CombatSystem() {
        this.random = new Random();
    }

    /**
     * Costruttore per i test: accetta un Random controllato.
     *
     * @param random istanza di Random da usare (es. mock o seed fisso)
     */
    public CombatSystem(Random random) {
        this.random = random;
    }

    /**
     * Esegue un attacco dell'attaccante verso il difensore.
     *
     * @param attacker          il combattente che attacca
     * @param defender          il combattente che difende
     * @param attackType        il tipo di attacco sferrato
     * @param enemyCritModifier modificatore al critico imposto dal nemico
     * @return il danno netto inflitto (HP persi dal difensore)
     */
    public int executeAttack(GameCharacter attacker, GameCharacter defender,
                             AttackType attackType, double enemyCritModifier) {

        // 1. Critico
        boolean isCritical;
        if (attacker instanceof Thief thief && thief.isStealthBonusActive()) {
            // Bonus furtivita' Ladro: primo attacco di ogni stanza sempre critico
            isCritical = true;
            thief.consumeStealthBonus();
        } else {
            double effectiveCrit = attacker.getCritChance() + enemyCritModifier;
            isCritical = random.nextDouble() < effectiveCrit;
        }

        // 2. Danno lordo
        int baseDamage = attacker.getAttack();
        int damage = isCritical ? baseDamage * 2 : baseDamage;

        // 3. Blocco Guerriero: 20% probabilita' di annullare attacco fisico
        if (defender instanceof Warrior warrior && attackType == AttackType.PHYSICAL) {
            if (random.nextDouble() < warrior.getBlockChance()) {
                return 0;
            }
        }

        // 4. Schermo e vulnerabilita' del Mago
        if (defender instanceof Mage mage) {
            if (attackType == AttackType.PHYSICAL && mage.isMagicShieldActive()) {
                mage.setMagicShieldActive(false);
                return 0; // schermo assorbe completamente
            }
            if (attackType == AttackType.MAGICAL || attackType == AttackType.MIXED) {
                damage = (int) (damage * 1.30); // vulnerabilita' magica +30%
            }
        }

        // 5. Applica e restituisce danno NETTO (HP persi = differenza prima/dopo)
        int hpBefore = defender.getCurrentHp();
        defender.takeDamage(damage);
        return hpBefore - defender.getCurrentHp();
    }
}
