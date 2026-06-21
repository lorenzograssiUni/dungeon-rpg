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
 * Armatura — BODY.
 * Non equipaggiabile insieme al Pendente Magico (stesso slot BODY).
 *
 * Bonus (uguali per tutte le classi): DEF+4, AGI-4
 */
public class Armor extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public Armor() {
        super("Armatura",
              "[Corpo] DEF+4 AGI-4 per tutti",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier(0, 4, -4, 0, 0, 0.0),
                  CharacterClass.MAGE,    new StatModifier(0, 4, -4, 0, 0, 0.0),
                  CharacterClass.THIEF,   new StatModifier(0, 4, -4, 0, 0, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()       { return EquipSlot.BODY; }
    @Override public boolean isTwoHanded()     { return false; }
    @Override public List<SpecialAttack> getSpecialAttacks() { return Collections.emptyList(); }
}
