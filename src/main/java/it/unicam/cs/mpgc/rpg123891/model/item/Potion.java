package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Pozione di cura. Ripristina una quantita' fissa di HP al personaggio.
 */
public class Potion implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final int healAmount;

    public Potion(String name, String description, int healAmount) {
        this.name = name;
        this.description = description;
        this.healAmount = healAmount;
    }

    /** Costruttore semplificato usato nei test. */
    public Potion(String name, int healAmount) {
        this(name, "Ripristina " + healAmount + " HP", healAmount);
    }

    @Override
    public void use(GameCharacter character) {
        character.heal(healAmount);
    }

    @Override public String getName() { return name; }
    @Override public String getDescription() { return description; }
    public int getHealAmount() { return healAmount; }
}
