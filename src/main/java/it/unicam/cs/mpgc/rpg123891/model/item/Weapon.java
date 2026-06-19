package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Arma raccoglibile nelle stanze. Aumenta attacco e critico del personaggio.
 * Implementa equals/hashCode basati su nome e attackBonus per supportare
 * HashSet e le operazioni di deduplicazione dell'inventario.
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

    @Override
    public void use(GameCharacter character) {
        character.increaseAttack(attackBonus);
        character.increaseCritChance(critBonus);
    }

    /**
     * Due armi sono uguali se hanno lo stesso nome e lo stesso attackBonus.
     * La descrizione e il critBonus sono ignorati come da test ItemEqualsTest.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weapon other)) return false;
        return attackBonus == other.attackBonus && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, attackBonus);
    }

    @Override public String getName() { return name; }
    @Override public String getDescription() { return description; }
    public int getAttackBonus() { return attackBonus; }
    public double getCritBonus() { return critBonus; }
}
