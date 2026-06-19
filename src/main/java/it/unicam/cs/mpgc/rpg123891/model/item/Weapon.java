package it.unicam.cs.mpgc.rpg123891.model.item;

import java.util.Objects;

/**
 * Rappresenta un'arma equipaggiabile dal personaggio.
 * Un'arma aggiunge bonus all'attacco e alla probabilità di critico.
 * Due armi sono considerate uguali se hanno lo stesso nome e attackBonus.
 */
public class Weapon implements Item {

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
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    public int getAttackBonus() { return attackBonus; }
    public double getCritBonus() { return critBonus; }

    /**
     * Due armi sono uguali se hanno lo stesso nome e lo stesso attackBonus.
     * Utile per confronti nell'inventario e nelle strutture dati (HashSet, HashMap).
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

    @Override
    public String toString() {
        return "Arma(" + name + ", atk+" + attackBonus + ", crit+" + (int)(critBonus * 100) + "%)";
    }
}
