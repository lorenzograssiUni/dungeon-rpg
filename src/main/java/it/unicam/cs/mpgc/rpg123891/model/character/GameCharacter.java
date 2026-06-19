package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.combat.Combatable;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe astratta che rappresenta un personaggio generico del gioco.
 * Implementa Serializable per supportare la persistenza dell'intera gerarchia.
 */
public abstract class GameCharacter implements Combatable, Serializable {

    private static final long serialVersionUID = 1L;

    protected String name;
    protected int maxHp;
    protected int currentHp;
    protected int attack;
    protected int defense;
    protected int stamina;
    protected int currentStamina;
    protected double critChance;

    protected List<Item> inventory = new ArrayList<>();

    protected GameCharacter(String name, int maxHp, int attack, int defense, int stamina, double critChance) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.stamina = stamina;
        this.currentStamina = stamina;
        this.critChance = critChance;
    }

    public boolean isAlive() { return currentHp > 0; }

    public void heal(int amount) {
        currentHp = Math.min(currentHp + amount, maxHp);
    }

    public void takeDamage(int damage) {
        int reduced = Math.max(0, damage - defense);
        currentHp = Math.max(0, currentHp - reduced);
    }

    public void addItem(Item item) { inventory.add(item); }
    public List<Item> getInventory() { return inventory; }

    public abstract void applyPassiveBonus();
    public abstract CharacterClass getCharacterClass();

    public String getName() { return name; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getCurrentStamina() { return currentStamina; }
    public int getStamina() { return stamina; }
    public double getCritChance() { return critChance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameCharacter other)) return false;
        return Objects.equals(name, other.name)
                && Objects.equals(getCharacterClass(), other.getCharacterClass());
    }

    @Override
    public int hashCode() { return Objects.hash(name, getCharacterClass()); }

    @Override
    public String toString() {
        return String.format("%s [HP: %d/%d | ATK: %d | DEF: %d | CRIT: %.0f%%]",
                name, currentHp, maxHp, attack, defense, critChance * 100);
    }
}
