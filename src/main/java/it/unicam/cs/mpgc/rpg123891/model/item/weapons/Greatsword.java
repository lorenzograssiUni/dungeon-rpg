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
 * Spadone — MAIN_HAND, 2 mani (blocca OFF_HAND).
 *
 * Bonus: W: ATK+5 STA+3 | M: ATK-2 AGI-4 STA-2 | T: ATK+1 AGI-4
 * Speciali: Spazzatutto (costo 4), Taglio Profondo (costo 7)
 */
public class Greatsword extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public Greatsword() {
        super("Spadone",
              "[Mano DX+SX] W:+5ATK +3STA | M:-2ATK -4AGI -2STA | T:+1ATK -4AGI",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier( 5, 0,  0, 0,  3, 0.0),
                  CharacterClass.MAGE,    new StatModifier(-2, 0, -4, 0, -2, 0.0),
                  CharacterClass.THIEF,   new StatModifier( 1, 0, -4, 0,  0, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()   { return EquipSlot.MAIN_HAND; }
    @Override public boolean isTwoHanded() { return true; }  // blocca OFF_HAND

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(
            new SpecialAttack("Spazzatutto",
                "Colpisce tutti i nemici nella stanza e li stordisce 1 turno", 4,
                (attacker, defender) -> {
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    return hpBefore - defender.getCurrentHp();
                }),
            new SpecialAttack("Taglio Profondo",
                "Danno = meta' degli HP correnti del nemico", 7,
                (attacker, defender) -> {
                    int damage   = defender.getCurrentHp() / 2;
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(damage);
                    return hpBefore - defender.getCurrentHp();
                })
        );
    }
}
