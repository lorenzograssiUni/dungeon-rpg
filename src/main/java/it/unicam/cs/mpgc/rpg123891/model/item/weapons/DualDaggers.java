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
 * Doppie Daghe — MAIN_HAND, 2 mani (blocca OFF_HAND).
 *
 * Bonus: T: ATK+5 AGI+2 STA+2 | W: DEF-2 AGI+2 | M: ATK-2 AGI+2 STA-1
 * Speciali: Sfuriata (costo 5), Ira (costo 3)
 */
public class DualDaggers extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public DualDaggers() {
        super("Doppie Daghe",
              "[Mano DX+SX] T:+5ATK +2AGI +2STA | W:-2DEF +2AGI | M:-2ATK +2AGI -1STA",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier( 0, -2,  2, 0,  0, 0.0),
                  CharacterClass.MAGE,    new StatModifier(-2,  0,  2, 0, -1, 0.0),
                  CharacterClass.THIEF,   new StatModifier( 5,  0,  2, 0,  2, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()   { return EquipSlot.MAIN_HAND; }
    @Override public boolean isTwoHanded() { return true; }  // blocca OFF_HAND

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(
            new SpecialAttack("Sfuriata",
                "3 colpi: 100% + 50% + 50% del danno base", 5,
                (attacker, defender) -> {
                    int atk   = attacker.getAttack();
                    int total = atk + atk / 2 + atk / 2;
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(total);
                    return hpBefore - defender.getCurrentHp();
                }),
            new SpecialAttack("Ira",
                "Attacco base + 25%+crit% probabilita' di un secondo colpo", 3,
                (attacker, defender) -> {
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    if (Math.random() < 0.25 + attacker.getCritChance()) {
                        defender.takeDamage(attacker.getAttack());
                    }
                    return hpBefore - defender.getCurrentHp();
                })
        );
    }
}
