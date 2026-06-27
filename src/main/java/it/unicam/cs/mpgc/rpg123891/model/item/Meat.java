package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Carne: ripristina 40 HP e 3 stamina al personaggio (GAME_SPEC).
 */
public class Meat implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int HP_RESTORE      = 40;
    private static final int STAMINA_RESTORE = 3;

    private final String name;
    private final String description;

    public Meat() {
        this.name        = "Carne";
        this.description = "Ripristina " + HP_RESTORE + " HP e " + STAMINA_RESTORE + " stamina";
    }

    @Override
    public void use(GameCharacter character) {
        character.heal(HP_RESTORE);
        character.restoreStamina(STAMINA_RESTORE);
    }

    @Override public String getName()        { return name; }
    @Override public String getDescription() { return description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof Meat;
    }

    @Override
    public int hashCode() { return Objects.hash(name); }
}
