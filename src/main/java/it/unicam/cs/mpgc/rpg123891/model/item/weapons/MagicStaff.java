package it.unicam.cs.mpgc.rpg123891.model.item.weapons;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipSlot;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.StatModifier;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * Bastone Magico — MAIN_HAND, 1 mano.
 *
 * Bonus: M: ATK+5 AGI+2 STA+3 | W: ATK-3 STA-3 | T: STA-3
 * Speciali: Onda Magica (costo 4), Colpo Vitale (costo 6)
 */
public class MagicStaff extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public MagicStaff() {
        super("Bastone Magico",
              "[Mano DX] M:+5ATK +2AGI +3STA | W:-3ATK -3STA | T:-3STA",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier(-3, 0,  0, 0, -3, 0.0),
                  CharacterClass.MAGE,    new StatModifier( 5, 0,  2, 0,  3, 0.0),
                  CharacterClass.THIEF,   new StatModifier( 0, 0,  0, 0, -3, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()   { return EquipSlot.MAIN_HAND; }
    @Override public boolean isTwoHanded() { return false; }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(
            new SpecialAttack("Onda Magica",
                "Colpisce tutti i nemici nella stanza per ATK base", 4,
                (attacker, defender) -> {
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    return hpBefore - defender.getCurrentHp();
                }),
            new SpecialAttack("Colpo Vitale",
                "Danno = meta' degli HP correnti dell'attaccante", 6,
                (attacker, defender) -> {
                    int damage   = attacker.getCurrentHp() / 2;
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(damage);
                    return hpBefore - defender.getCurrentHp();
                })
        );
    }
}
