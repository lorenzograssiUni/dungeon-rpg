package it.unicam.cs.mpgc.rpg123891.model.item.weapons;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.StatModifier;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * Spadone (2 mani).
 *
 * Bonus stat:
 *   Warrior : ATK+5, DEF 0, AGI 0, STA+3
 *   Mage    : ATK-2, DEF 0, AGI-4, STA-2
 *   Thief   : ATK+1, DEF 0, AGI-4, STA 0
 *
 * Attacchi speciali:
 *   - Spazzatutto  : colpisce tutti i nemici e li stordisce 1 turno  | costo 4
 *   - Taglio Profondo : danno = meta' HP correnti del difensore       | costo 7
 */
public class Greatsword extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public Greatsword() {
        super("Spadone", "W: +5 ATK +3 STA | T: +1 ATK -4 AGI | M: -2 ATK -4 AGI -2 STA",
                Map.of(
                        CharacterClass.WARRIOR, new StatModifier(5, 0, 0, 0, 3, 0.0),
                        CharacterClass.MAGE,    new StatModifier(-2, 0, -4, 0, -2, 0.0),
                        CharacterClass.THIEF,   new StatModifier(1, 0, -4, 0, 0, 0.0)
                ));
    }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(
            new SpecialAttack(
                "Spazzatutto",
                "Colpisce tutti i nemici nella stanza e li stordisce per 1 turno (gestito dal controller)",
                4,
                (attacker, defender) -> {
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    return hpBefore - defender.getCurrentHp();
                }
            ),
            new SpecialAttack(
                "Taglio Profondo",
                "Infligge danno pari alla meta' degli HP correnti del nemico",
                7,
                (attacker, defender) -> {
                    int damage = defender.getCurrentHp() / 2;
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(damage);
                    return hpBefore - defender.getCurrentHp();
                }
            )
        );
    }
}
