package it.unicam.cs.mpgc.rpg123891.model.item;

/**
 * Rappresenta un'arma equipaggiabile dal personaggio.
 * Un'arma aggiunge bonus all'attacco e alla probabilità di critico.
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
}
