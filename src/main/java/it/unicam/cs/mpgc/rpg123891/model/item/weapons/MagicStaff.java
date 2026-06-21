package it.unicam.cs.mpgc.rpg123891.model.item.weapons;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.StatModifier;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * Bastone Magico.
 *
 * Bonus stat:
 *   Warrior : ATK-3, DEF 0, AGI 0, STA-3
 *   Mage    : ATK+5, DEF 0, AGI+2, STA+3
 *   Thief   : ATK 0, DEF 0, AGI 0, STA-3
 *
 * Attacchi speciali:
 *   - Onda Magica  : colpisce tutti i nemici nella stanza per ATK base | costo 4
 *     (il defender passato e' il primo nemico; il danno agli altri
 *      va gestito dal GameController che conosce la lista nemici)
 *   - Colpo Vitale : danno = HP correnti dell'attaccante / 2         | costo 6
 */
public class MagicStaff extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public MagicStaff() {
        super("Bastone Magico", "M: +5 ATK +2 AGI +3 STA | W: -3 ATK -3 STA | T: -3 STA",
                Map.of(
                        CharacterClass.WARRIOR, new StatModifier(-3, 0, 0, 0, -3, 0.0),
                        CharacterClass.MAGE,    new StatModifier(5, 0, 2, 0, 3, 0.0),
                        CharacterClass.THIEF,   new StatModifier(0, 0, 0, 0, -3, 0.0)
                ));
    }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(
            new SpecialAttack(
                "Onda Magica",
                "Colpisce tutti i nemici nella stanza per ATK base (gestito dal controller)",
                4,
                (attacker, defender) -> {
                    // Danno al singolo bersaglio; il controller applica lo stesso agli altri
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    return hpBefore - defender.getCurrentHp();
                }
            ),
            new SpecialAttack(
                "Colpo Vitale",
                "Infligge danno pari alla meta' degli HP correnti dell'attaccante",
                6,
                (attacker, defender) -> {
                    int damage = attacker.getCurrentHp() / 2;
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(damage);
                    return hpBefore - defender.getCurrentHp();
                }
            )
        );
    }
}
