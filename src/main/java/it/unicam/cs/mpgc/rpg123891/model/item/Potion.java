package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Pozione: ripristina HP e riempie COMPLETAMENTE la stamina del personaggio (GAME_SPEC).
 *
 * Costruttori disponibili:
 *   Potion()           -> nome="Pozione", heal=30
 *   Potion(name, heal) -> heal personalizzato
 */
public class Potion implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_HEAL = 30;

    private final String name;
    private final String description;
    private final int healAmount;

    public Potion() {
        this("Pozione", DEFAULT_HEAL);
    }

    public Potion(String name, int healAmount) {
        this.name        = name;
        this.description = "Ripristina " + healAmount + " HP e riempie completamente la stamina";
        this.healAmount  = healAmount;
    }

    /**
     * Usa la pozione: ripristina gli HP e riempie la stamina al massimo (GAME_SPEC).
     */
    @Override
    public void use(GameCharacter character) {
        character.heal(healAmount);
        // GAME_SPEC: "riempe completamente la stamina del giocatore"
        character.restoreStamina(character.getMaxStamina());
    }

    @Override public String getName()        { return name; }
    @Override public String getDescription() { return description; }

    public int getHealAmount() { return healAmount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Potion other)) return false;
        return healAmount == other.healAmount && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name, healAmount); }
}
