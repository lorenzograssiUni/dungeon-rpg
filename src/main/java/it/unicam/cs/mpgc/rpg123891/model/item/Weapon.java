package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Arma raccoglibile nelle stanze. Aumenta attacco e critico del personaggio.
 */
public class Weapon implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final int attackBonus;
    private final double critBonus;

    public Weapon(String name, String description, int attackBonus, double critBonus) {
        this.name = name;
        this.description = description;
        this.attackBonus = attackBonus;
        this.critBonus = critBonus;
    }

    /** Equipaggiare l'arma aumenta attacco e chance critica del personaggio. */
    @Override
    public void use(GameCharacter character) {
        character.attack += attackBonus;
        character.critChance += critBonus;
    }

    @Override public String getName() { return name; }
    @Override public String getDescription() { return description; }
    public int getAttackBonus() { return attackBonus; }
    public double getCritBonus() { return critBonus; }
}
