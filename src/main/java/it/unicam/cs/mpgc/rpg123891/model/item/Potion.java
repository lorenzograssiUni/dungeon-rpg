package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Pozione di cura: ripristina una quantita' fissa di HP al personaggio.
 * Valore standard: +40 HP.
 */
public class Potion implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final int healAmount;

    public Potion(String name, String description, int healAmount) {
        this.name        = name;
        this.description = description;
        this.healAmount  = healAmount;
    }

    public Potion(String name, int healAmount) {
        this(name, "Ripristina " + healAmount + " HP", healAmount);
    }

    /** Costruttore standard: Pozione da 40 HP. */
    public Potion() {
        this("Pozione", 40);
    }

    @Override
    public void use(GameCharacter character) {
        character.heal(healAmount);
    }

    @Override public String getName()        { return name; }
    @Override public String getDescription() { return description; }
    public int getHealAmount()               { return healAmount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Potion other)) return false;
        return healAmount == other.healAmount && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name, healAmount); }
}
