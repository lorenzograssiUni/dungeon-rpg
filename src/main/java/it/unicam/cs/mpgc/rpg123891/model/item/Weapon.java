package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Classe astratta base per tutte le armi e gli equipaggiamenti.
 *
 * Ogni sottoclasse concreta definisce:
 *   - getSlot()           : quale slot occupa (MAIN_HAND / OFF_HAND / BODY)
 *   - isTwoHanded()       : se occupa anche OFF_HAND (blocca Shield)
 *   - getSpecialAttacks() : lista attacchi speciali (vuota per item non offensivi)
 *   - modifiers           : Map<CharacterClass, StatModifier> con bonus per classe
 *
 * Il metodo use() applica i bonus; il metodo remove() li rimuove (rollback Opzione A).
 */
public abstract class Weapon implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final Map<CharacterClass, StatModifier> modifiers;

    protected Weapon(String name, String description,
                     Map<CharacterClass, StatModifier> modifiers) {
        this.name      = name;
        this.description = description;
        this.modifiers = Collections.unmodifiableMap(modifiers);
    }

    // -------------------------------------------------------------------------
    // Slot e tipo
    // -------------------------------------------------------------------------

    /** Slot che questo item occupa quando equipaggiato. */
    public abstract EquipSlot getSlot();

    /**
     * True se l'arma e' a due mani (occupa MAIN_HAND e blocca OFF_HAND).
     * Default: false. Override in Greatsword e DualDaggers.
     */
    public boolean isTwoHanded() { return false; }

    /** Lista attacchi speciali. Vuota per item puramente difensivi. */
    public abstract List<SpecialAttack> getSpecialAttacks();

    // -------------------------------------------------------------------------
    // Applicazione e rimozione bonus
    // -------------------------------------------------------------------------

    /**
     * Applica i bonus stat al personaggio in base alla sua classe.
     * Chiamato da EquipmentManager.equip().
     */
    @Override
    public void use(GameCharacter character) {
        applyMod(character, getModifierFor(character.getCharacterClass()), +1);
    }

    /**
     * Rimuove i bonus stat (rollback). Chiamato da EquipmentManager.unequip().
     * Applica il modificatore invertito; le stat non scendono sotto 0.
     */
    public void remove(GameCharacter character) {
        applyMod(character, getModifierFor(character.getCharacterClass()), -1);
    }

    private void applyMod(GameCharacter character, StatModifier mod, int sign) {
        if (mod.attackDelta()     != 0) character.increaseAttack(mod.attackDelta()     * sign);
        if (mod.defenseDelta()    != 0) character.increaseDefense(mod.defenseDelta()   * sign);
        if (mod.agilityDelta()    != 0) character.increaseAgility(mod.agilityDelta()   * sign);
        if (mod.maxHpDelta()      != 0) character.increaseMaxHp(mod.maxHpDelta()       * sign);
        if (mod.maxStaminaDelta() != 0) character.increaseMaxStamina(mod.maxStaminaDelta() * sign);
        if (mod.critDelta()       != 0) character.increaseCritChance(mod.critDelta()   * sign);
    }

    public StatModifier getModifierFor(CharacterClass cls) {
        return modifiers.getOrDefault(cls, StatModifier.empty());
    }

    // -------------------------------------------------------------------------
    // Getter e utility
    // -------------------------------------------------------------------------

    @Override public String getName()        { return name; }
    @Override public String getDescription() { return description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weapon other)) return false;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }
}
