package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Ladro: alta critica.
 * Bonus passivo: primo attacco di ogni stanza e' sempre critico (danno x2).
 */
public class Thief extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean stealthBonusActive = false;

    public Thief(String name) {
        super(name, 90, 20, 5, 12, 0.25);
    }

    @Override
    public void applyPassiveBonus() {
        this.stealthBonusActive = true;
    }

    public boolean isStealthBonusActive() { return stealthBonusActive; }
    public void consumeStealthBonus() { this.stealthBonusActive = false; }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.THIEF; }
}
