package it.unicam.cs.mpgc.rpg123891.model.item.weapons;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.StatModifier;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Pendente Magico.
 *
 * Bonus stat:
 *   Warrior : DEF-3, HP+2
 *   Mage    : DEF-3, HP+5
 *   Thief   : DEF-3, HP+2
 */
public class MagicAmulet extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public MagicAmulet() {
        super("Pendente Magico", "HP+ per tutti | DEF-3 per tutti",
                Map.of(
                        CharacterClass.WARRIOR, new StatModifier(0, -3, 0, 2, 0, 0.0),
                        CharacterClass.MAGE,    new StatModifier(0, -3, 0, 5, 0, 0.0),
                        CharacterClass.THIEF,   new StatModifier(0, -3, 0, 2, 0, 0.0)
                ));
    }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return Collections.emptyList();
    }
}
