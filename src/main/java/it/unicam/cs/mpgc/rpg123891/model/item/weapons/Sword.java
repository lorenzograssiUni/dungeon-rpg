package it.unicam.cs.mpgc.rpg123891.model.item.weapons;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.StatModifier;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * Spada Semplice.
 *
 * Bonus stat:
 *   Warrior : ATK+2, DEF 0, AGI+2, STA 0
 *   Mage    : ATK+2, DEF 0, AGI  0, STA-3
 *   Thief   : ATK+2, DEF 0, AGI  0, STA 0
 *
 * Attacchi speciali:
 *   - Fendente  : +25% ATK, +5% crit  | costo 2
 *   - Carica!   : danno base + 0 danno aggiuntivo quel turno,
 *                 ma aumenta di 1 il modificatore di difesa per il turno  | costo 4
 */
public class Sword extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public Sword() {
        super("Spada Semplice", "+2 ATK per tutti | W: +2 AGI | M: -3 STA",
                Map.of(
                        CharacterClass.WARRIOR, new StatModifier(2, 0, 2, 0, 0, 0.0),
                        CharacterClass.MAGE,    new StatModifier(2, 0, 0, 0, -3, 0.0),
                        CharacterClass.THIEF,   new StatModifier(2, 0, 0, 0, 0, 0.0)
                ));
    }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(
            new SpecialAttack(
                "Fendente",
                "+25% ATK e +5% probabilita' critico per questo attacco",
                2,
                (attacker, defender) -> {
                    int boostedAtk = (int)(attacker.getAttack() * 1.25);
                    int damage = (int)(boostedAtk * (attacker.getCritChance() + 0.05 >= Math.random() ? 2 : 1));
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(damage);
                    return hpBefore - defender.getCurrentHp();
                }
            ),
            new SpecialAttack(
                "Carica!",
                "Attacco potenziato: danno base piu' bonus difesa temporanea (+3 DEF questo turno)",
                4,
                (attacker, defender) -> {
                    // Applica temporaneamente +3 difesa all'attaccante (simulato aumentando difesa e poi ripristinando)
                    attacker.increaseDefense(3);
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    attacker.increaseDefense(-3); // ripristina
                    return hpBefore - defender.getCurrentHp();
                }
            )
        );
    }
}
