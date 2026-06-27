package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Pozione: riempie COMPLETAMENTE la stamina del personaggio (GAME_SPEC).
 */
public class Potion implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;

    public Potion() {
        this.name        = "Pozione";
        this.description = "Riempie completamente la stamina";
    }

    @Override
    public void use(GameCharacter character) {
        character.restoreStamina(character.getMaxStamina());
    }

    @Override public String getName()        { return name; }
    @Override public String getDescription() { return description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof Potion;
    }

    @Override
    public int hashCode() { return Objects.hash(name); }
}
