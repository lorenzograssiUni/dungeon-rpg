package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Pozione: ripristina HP e Stamina al personaggio.
 * Valori standard: +10 HP, +5 Stamina.
 */
public class Potion implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final int healAmount;
    private final int staminaAmount;

    public Potion(String name, String description, int healAmount, int staminaAmount) {
        this.name          = name;
        this.description   = description;
        this.healAmount    = healAmount;
        this.staminaAmount = staminaAmount;
    }

    public Potion(String name, int healAmount, int staminaAmount) {
        this(name, "Ripristina " + healAmount + " HP e " + staminaAmount + " Stamina", healAmount, staminaAmount);
    }

    public Potion(String name, int healAmount) {
        this(name, healAmount, 5);
    }

    /** Costruttore standard: +10 HP, +5 Stamina. */
    public Potion() {
        this("Pozione", 10, 5);
    }

    @Override
    public void use(GameCharacter character) {
        character.heal(healAmount);
        character.restoreStamina(staminaAmount);
    }

    @Override public String getName()        { return name; }
    @Override public String getDescription() { return description; }
    public int getHealAmount()               { return healAmount; }
    public int getStaminaAmount()            { return staminaAmount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Potion other)) return false;
        return healAmount == other.healAmount
                && staminaAmount == other.staminaAmount
                && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name, healAmount, staminaAmount); }
}
