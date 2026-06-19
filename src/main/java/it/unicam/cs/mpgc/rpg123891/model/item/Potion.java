package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.util.Objects;

/**
 * Rappresenta una pozione di cura utilizzabile dal personaggio.
 * Una pozione ripristina una quantità fissa di HP.
 * Due pozioni sono considerate uguali se hanno lo stesso nome e healAmount.
 */
public class Potion implements Item {

    private final String name;
    private final String description;
    private final int healAmount;

    public Potion(String name, int healAmount) {
        this(name, "Ripristina " + healAmount + " HP.", healAmount);
    }

    public Potion(String name, String description, int healAmount) {
        this.name = name;
        this.description = description;
        this.healAmount = healAmount;
    }

    /** Usa la pozione sul personaggio specificato. */
    public void use(GameCharacter character) {
        character.heal(healAmount);
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    public int getHealAmount() { return healAmount; }

    /**
     * Due pozioni sono uguali se hanno lo stesso nome e lo stesso healAmount.
     * Utile per confronti nell'inventario e nelle strutture dati (HashSet, HashMap).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Potion other)) return false;
        return healAmount == other.healAmount && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, healAmount);
    }

    @Override
    public String toString() {
        return "Pozione(" + name + ", cura=" + healAmount + ")";
    }
}
