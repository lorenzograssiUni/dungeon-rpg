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
 * Scudo (equipaggiamento difensivo).
 *
 * Bonus stat:
 *   Warrior : DEF+2, AGI-2
 *   Mage    : DEF+2, AGI-2
 *   Thief   : DEF+2, AGI-2
 *
 * Nessun attacco speciale proprio; sblocca pero' la Carica! della Sword
 * se entrambi sono equipaggiati (logica nel controller).
 */
public class Shield extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public Shield() {
        super("Scudo", "DEF+2 per tutti | AGI-2 per tutti",
                Map.of(
                        CharacterClass.WARRIOR, new StatModifier(0, 2, -2, 0, 0, 0.0),
                        CharacterClass.MAGE,    new StatModifier(0, 2, -2, 0, 0, 0.0),
                        CharacterClass.THIEF,   new StatModifier(0, 2, -2, 0, 0, 0.0)
                ));
    }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return Collections.emptyList();
    }
}
