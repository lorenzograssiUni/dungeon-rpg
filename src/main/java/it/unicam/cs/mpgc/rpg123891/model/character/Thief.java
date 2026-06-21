package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Ladro: alta probabilita' di critico e buona agilita'.
 * Stat base: HP 90, ATK 18, DEF 6, AGI 8, STA 12, CRIT 25%
 *
 * Bonus passivo (applicato a ogni nuova stanza):
 *   Attiva lo stealth bonus: il primo attacco della stanza e' sempre critico.
 */
public class Thief extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean stealthBonusActive = false;

    public Thief(String name) {
        super(name, 90, 18, 6, 8, 12, 0.25);
    }

    @Override
    public void applyPassiveBonus() {
        this.stealthBonusActive = true;
        restoreStamina(2);
    }

    public boolean isStealthBonusActive()  { return stealthBonusActive; }
    public void consumeStealthBonus()      { this.stealthBonusActive = false; }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.THIEF; }
}
