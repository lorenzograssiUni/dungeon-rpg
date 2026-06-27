package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Mago: alto attacco magico, bassa difesa fisica.
 * Stat base: HP 90, ATK 15, DEF 4, AGI 6, STA 15, CRIT 5%
 *
 * Scudo Magico (GAME_SPEC): riduce del 30% i danni fisici in arrivo.
 * Vulnerabilita': subisce +30% danno da attacchi MAGICAL e MIXED.
 */
public class Mage extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean magicShieldActive = false;

    public Mage(String name) {
        super(name, 90, 15, 4, 6, 15, 0.05);
    }

    @Override
    public void applyPassiveBonus() {
        restoreStamina(2);
    }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.MAGE; }

    /** Attiva lo Scudo Magico (riduce del 30% i danni fisici in arrivo). */
    public void activateMagicShield() {
        this.magicShieldActive = true;
    }

    /** Disattiva lo Scudo Magico. */
    public void deactivateMagicShield() {
        this.magicShieldActive = false;
    }

    /** Restituisce true se lo Scudo Magico è attualmente attivo. */
    public boolean isMagicShieldActive() {
        return magicShieldActive;
    }
}
