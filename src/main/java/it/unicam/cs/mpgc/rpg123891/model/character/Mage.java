package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Mago: alto attacco magico, bassa difesa fisica.
 * Stat base: HP 90, ATK 15, DEF 4, AGI 6, STA 15, CRIT 5%
 *
 * Passive (GAME_SPEC):
 *   - Scudo Magico: riduce del 30% i danni fisici in arrivo (quando attivo).
 *   - Vulnerabilita': subisce +30% danno da attacchi MAGICAL e MIXED.
 */
public class Mage extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Lo scudo magico e' attivo di default (passive permanente). */
    private boolean magicShieldActive = true;

    public Mage(String name) {
        super(name, 90, 15, 4, 6, 15, 0.05);
    }

    @Override
    public void applyPassiveBonus() {
        restoreStamina(2);
    }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.MAGE; }

    /**
     * Restituisce true se lo scudo magico e' attivo.
     * Lo scudo riduce del 30% i danni fisici in arrivo.
     */
    public boolean isMagicShieldActive() {
        return magicShieldActive;
    }

    /**
     * Attiva o disattiva lo scudo magico.
     * Usato nei test per controllare il comportamento del CombatSystem.
     */
    public void setMagicShieldActive(boolean active) {
        this.magicShieldActive = active;
    }
}
