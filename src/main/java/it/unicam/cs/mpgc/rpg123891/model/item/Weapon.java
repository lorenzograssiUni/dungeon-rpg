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
 * Classe astratta base per tutte le armi.
 *
 * Ogni arma concreta:
 *   1. Definisce i bonus stat per classe tramite una Map<CharacterClass, StatModifier>
 *      (bonus diversi per Warrior / Mage / Thief).
 *   2. Implementa getSpecialAttacks() restituendo la lista degli attacchi speciali
 *      specifici di quell'arma.
 *
 * Il metodo use() applica automaticamente il StatModifier corretto in base
 * alla classe del personaggio che equipaggia l'arma.
 */
public abstract class Weapon implements Item, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    /** Bonus per classe: CharacterClass -> modificatori stat. */
    private final Map<CharacterClass, StatModifier> modifiers;

    protected Weapon(String name, String description,
                     Map<CharacterClass, StatModifier> modifiers) {
        this.name        = name;
        this.description = description;
        this.modifiers   = Collections.unmodifiableMap(modifiers);
    }

    /**
     * Applica i bonus dell'arma al personaggio in base alla sua classe.
     * Se la classe non e' mappata, non applica nulla.
     */
    @Override
    public void use(GameCharacter character) {
        StatModifier mod = modifiers.getOrDefault(
                character.getCharacterClass(), StatModifier.empty());
        if (mod.attackDelta()     != 0) character.increaseAttack(mod.attackDelta());
        if (mod.defenseDelta()    != 0) character.increaseDefense(mod.defenseDelta());
        if (mod.agilityDelta()    != 0) character.increaseAgility(mod.agilityDelta());
        if (mod.maxHpDelta()      != 0) character.increaseMaxHp(mod.maxHpDelta());
        if (mod.maxStaminaDelta() != 0) character.increaseMaxStamina(mod.maxStaminaDelta());
        if (mod.critDelta()       != 0) character.increaseCritChance(mod.critDelta());
    }

    /** Lista degli attacchi speciali disponibili con questa arma. */
    public abstract List<SpecialAttack> getSpecialAttacks();

    public StatModifier getModifierFor(CharacterClass cls) {
        return modifiers.getOrDefault(cls, StatModifier.empty());
    }

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
