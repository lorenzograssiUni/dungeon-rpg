package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Mago: alto attacco magico, bassa difesa fisica.
 * Stat base: HP 75, ATK 15, DEF 4, AGI 6, STA 10, CRIT 5%
 *
 * Bonus passivo (applicato a ogni nuova stanza):
 *   Riattiva lo scudo magico.
 * Abilita' passiva in combattimento:
 *   Lo scudo magico assorbe il primo attacco fisico ricevuto.
 *   Vulnerabile agli attacchi MAGICAL e MIXED (+30% danno subito).
 */
public class Mage extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean magicShieldActive = false;

    public Mage(String name) {
        super(name, 75, 15, 4, 6, 10, 0.05);
    }

    @Override
    public void applyPassiveBonus() {
        this.magicShieldActive = true;
        restoreStamina(2);
    }

    public boolean isMagicShieldActive()              { return magicShieldActive; }
    public void setMagicShieldActive(boolean active)  { this.magicShieldActive = active; }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.MAGE; }
}
