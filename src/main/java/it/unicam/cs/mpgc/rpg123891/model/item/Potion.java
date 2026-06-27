package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Pozione: ripristina HP e stamina del personaggio.
 *
 * Costruttori disponibili:
 *   Potion()                          -> nome="Pozione", heal=30, stamina=5
 *   Potion(name, healAmount)          -> stamina=5 di default
 *   Potion(name, healAmount, stamina)
 *   Potion(name, description, healAmount, stamina)
 */
public class Potion implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_HEAL    = 30;
    private static final int DEFAULT_STAMINA = 5;

    private final String name;
    private final String description;
    private final int healAmount;
    private final int staminaAmount;

    public Potion() {
        this("Pozione", "Ripristina HP e stamina", DEFAULT_HEAL, DEFAULT_STAMINA);
    }

    public Potion(String name, int healAmount) {
        this(name, "Ripristina HP e stamina", healAmount, DEFAULT_STAMINA);
    }

    public Potion(String name, int healAmount, int staminaAmount) {
        this(name, "Ripristina HP e stamina", healAmount, staminaAmount);
    }

    public Potion(String name, String description, int healAmount, int staminaAmount) {
        this.name          = name;
        this.description   = description;
        this.healAmount    = healAmount;
        this.staminaAmount = staminaAmount;
    }

    @Override
    public void use(GameCharacter character) {
        character.heal(healAmount);
        character.restoreStamina(staminaAmount);
    }

    @Override public String getName()        { return name; }
    @Override public String getDescription() { return description; }

    public int getHealAmount()    { return healAmount; }
    public int getStaminaAmount() { return staminaAmount; }

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
