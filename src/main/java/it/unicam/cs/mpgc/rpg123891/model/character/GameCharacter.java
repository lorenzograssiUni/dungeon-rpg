package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.combat.Combatable;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe astratta che rappresenta un personaggio generico del gioco.
 * Contiene le statistiche base comuni a tutti i personaggi (giocatori e nemici).
 * Le sottoclassi devono implementare il comportamento specifico della propria classe/tipo.
 */
public abstract class GameCharacter implements Combatable {

    protected String name;
    protected int maxHp;
    protected int currentHp;
    protected int attack;
    protected int defense;
    protected int stamina;
    protected int currentStamina;
    protected double critChance; // valore tra 0.0 e 1.0

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

    // -------------------------
    // Metodi base comuni
    // -------------------------

    public boolean isAlive() {
        return currentHp > 0;
    }

    public void heal(int amount) {
        currentHp = Math.min(currentHp + amount, maxHp);
    }

    public void takeDamage(int damage) {
        int reduced = Math.max(0, damage - defense);
        currentHp = Math.max(0, currentHp - reduced);
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public List<Item> getInventory() {
        return List.copyOf(inventory);
    }

    // -------------------------
    // Metodo astratto: bonus passivo specifico della sottoclasse
    // -------------------------

    /**
     * Applica il bonus passivo specifico della classe/tipo.
     * Ogni sottoclasse implementa la propria meccanica unica.
     */
    public abstract void applyPassiveBonus();

    // -------------------------
    // Getters
    // -------------------------

    public String getName() { return name; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getCurrentStamina() { return currentStamina; }
    public int getStamina() { return stamina; }
    public double getCritChance() { return critChance; }

    @Override
    public String toString() {
        return String.format("%s [HP: %d/%d | ATK: %d | DEF: %d | CRIT: %.0f%%]",
                name, currentHp, maxHp, attack, defense, critChance * 100);
    }
}
