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
 * Pendente Magico — BODY.
 * Non equipaggiabile insieme all'Armatura (stesso slot BODY).
 *
 * Bonus: W: HP+2 DEF-3 | M: HP+5 DEF-3 | T: HP+2 DEF-3
 */
public class MagicAmulet extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public MagicAmulet() {
        super("Pendente Magico",
              "[Corpo] W:+2HP | M:+5HP | T:+2HP | DEF-3 per tutti",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier(0, -3, 0, 2, 0, 0.0),
                  CharacterClass.MAGE,    new StatModifier(0, -3, 0, 5, 0, 0.0),
                  CharacterClass.THIEF,   new StatModifier(0, -3, 0, 2, 0, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()       { return EquipSlot.BODY; }
    @Override public boolean isTwoHanded()     { return false; }
    @Override public List<SpecialAttack> getSpecialAttacks() { return Collections.emptyList(); }
}
