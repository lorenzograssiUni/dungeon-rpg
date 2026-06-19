package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Pozione di cura. Ripristina una quantita' fissa di HP al personaggio.
 * Implementa equals/hashCode basati su nome e healAmount per supportare
 * HashSet e le operazioni di deduplicazione dell'inventario.
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

    /**
     * Due pozioni sono uguali se hanno lo stesso nome e lo stesso healAmount.
     * La descrizione e' ignorata per permettere varianti descrittive dello stesso oggetto.
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

    @Override public String getName() { return name; }
    @Override public String getDescription() { return description; }
    public int getHealAmount() { return healAmount; }
}
