package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.combat.Combatable;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipmentManager;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe astratta che rappresenta un personaggio generico del gioco.
 *
 * Stat:
 *   - hp        : punti vita
 *   - attack    : danno base per attacco
 *   - defense   : riduzione danno subito
 *   - agility   : determina l'iniziativa
 *   - stamina   : risorsa consumata dagli attacchi
 *   - critChance: probabilita' di critico (0.0 - 1.0)
 *
 * Ogni personaggio possiede un EquipmentManager che gestisce gli slot
 * MAIN_HAND, OFF_HAND e BODY. Il manager viene creato on-demand al primo
 * accesso cosi' da non rompere la serializzazione degli oggetti esistenti.
 */
public abstract class GameCharacter implements Combatable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    protected String name;
    protected int maxHp;
    protected int currentHp;
    protected int attack;
    protected int defense;
    protected int agility;
    protected int maxStamina;
    protected int currentStamina;
    protected double critChance;

    protected List<Item> inventory = new ArrayList<>();

    /**
     * Manager degli slot di equipaggiamento.
     * Inizializzato lazy (transient per compatibilita' serializzazione;
     * viene ricreato al primo accesso dopo deserializzazione).
     */
    private transient EquipmentManager equipmentManager;

    protected GameCharacter(String name, int maxHp, int attack, int defense,
                            int agility, int maxStamina, double critChance) {
        this.name           = name;
        this.maxHp          = maxHp;
        this.currentHp      = maxHp;
        this.attack         = attack;
        this.defense        = defense;
        this.agility        = agility;
        this.maxStamina     = maxStamina;
        this.currentStamina = maxStamina;
        this.critChance     = critChance;
    }

    // -------------------------------------------------------------------------
    // Equipaggiamento
    // -------------------------------------------------------------------------

    /**
     * Restituisce l'EquipmentManager del personaggio.
     * Creato on-demand; sicuro anche dopo deserializzazione (campo transient).
     */
    public EquipmentManager getEquipmentManager() {
        if (equipmentManager == null) {
            equipmentManager = new EquipmentManager(this);
        }
        return equipmentManager;
    }

    // -------------------------------------------------------------------------
    // Combattimento
    // -------------------------------------------------------------------------

    public boolean isAlive() { return currentHp > 0; }

    public boolean canAttack() { return currentStamina > 0; }

    public boolean canUseSpecial(int staminaCost) { return currentStamina >= staminaCost; }

    public void consumeStaminaForAttack() {
        currentStamina = Math.max(0, currentStamina - 1);
    }

    public void consumeStaminaForSpecial(int cost) {
        currentStamina = Math.max(0, currentStamina - cost);
    }

    public void restoreStamina(int amount) {
        currentStamina = Math.min(maxStamina, currentStamina + amount);
    }

    public void heal(int amount) {
        currentHp = Math.min(currentHp + amount, maxHp);
    }

    public void takeDamage(int damage) {
        int reduced = Math.max(0, damage - defense);
        currentHp = Math.max(0, currentHp - reduced);
    }

    /**
     * Applica danno diretto ignorando completamente la difesa.
     * Usato da:
     *   - BurnEffect (bruciatura del Drago)
     *   - ReGoblinThrowAbility (3 lanci)
     *   - Uovo (ATK 1, danno sicuro)
     * HP non scendono sotto 0.
     */
    public void applyBurnDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    // -------------------------------------------------------------------------
    // Inventario
    // -------------------------------------------------------------------------

    public void addItem(Item item)    { inventory.add(item); }
    public void removeItem(Item item) { inventory.remove(item); }
    public List<Item> getInventory()  { return inventory; }

    // -------------------------------------------------------------------------
    // Modificatori stat (usati dagli item)
    // -------------------------------------------------------------------------

    public void increaseAttack(int amount)        { this.attack     += amount; }
    public void increaseDefense(int amount)       { this.defense    += amount; }
    public void increaseAgility(int amount)       { this.agility    += amount; }
    public void increaseMaxHp(int amount)         { this.maxHp      += amount; currentHp = Math.min(currentHp, maxHp); }
    public void increaseMaxStamina(int amount)    { this.maxStamina += amount; }
    public void increaseCritChance(double amount) { this.critChance += amount; }

    // -------------------------------------------------------------------------
    // Astratti
    // -------------------------------------------------------------------------

    public abstract void applyPassiveBonus();
    public abstract CharacterClass getCharacterClass();

    // -------------------------------------------------------------------------
    // Getter
    // -------------------------------------------------------------------------

    public String getName()           { return name; }
    public int getCurrentHp()         { return currentHp; }
    public int getMaxHp()             { return maxHp; }
    public int getAttack()            { return attack; }
    public int getDefense()           { return defense; }
    public int getAgility()           { return agility; }
    public int getCurrentStamina()    { return currentStamina; }
    public int getMaxStamina()        { return maxStamina; }
    public double getCritChance()     { return critChance; }

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
        return String.format("%s [HP:%d/%d | ATK:%d | DEF:%d | AGI:%d | STA:%d/%d | CRIT:%.0f%%]",
                name, currentHp, maxHp, attack, defense, agility,
                currentStamina, maxStamina, critChance * 100);
    }
}
