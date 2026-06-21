package it.unicam.cs.mpgc.rpg123891.model.item.weapons;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipSlot;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.StatModifier;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Scudo — OFF_HAND.
 * Non equipaggiabile se MAIN_HAND ha un'arma a 2 mani.
 *
 * Bonus (uguali per tutte le classi): DEF+2, AGI-2
 */
public class Shield extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public Shield() {
        super("Scudo",
              "[Mano SX] DEF+2 AGI-2 per tutti",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier(0, 2, -2, 0, 0, 0.0),
                  CharacterClass.MAGE,    new StatModifier(0, 2, -2, 0, 0, 0.0),
                  CharacterClass.THIEF,   new StatModifier(0, 2, -2, 0, 0, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()       { return EquipSlot.OFF_HAND; }
    @Override public boolean isTwoHanded()     { return false; }
    @Override public List<SpecialAttack> getSpecialAttacks() { return Collections.emptyList(); }
}
