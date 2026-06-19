package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Mago: alto attacco, bassa difesa.
 * Bonus passivo: scudo magico che assorbe il prossimo attacco fisico.
 * Vulnerabile agli attacchi MAGICAL e MIXED (+30% danno subito).
 */
public class Mage extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean magicShieldActive = false;

    public Mage(String name) {
        super(name, 80, 25, 3, 10, 0.15);
    }

    @Override
    public void applyPassiveBonus() {
        this.magicShieldActive = true;
    }

    public boolean isMagicShieldActive() { return magicShieldActive; }
    public void setMagicShieldActive(boolean active) { this.magicShieldActive = active; }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.MAGE; }
}
