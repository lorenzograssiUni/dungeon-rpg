package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Ladro: alta probabilita' di critico e buona agilita'.
 * Stat base: HP 90, ATK 18, DEF 6, AGI 8, STA 12, CRIT 25%
 *
 * Passive in combattimento:
 *   1. Primo attacco della stanza SEMPRE critico (stealthBonusActive).
 *      Viene consumato da CombatSystem al primo executeAttack().
 *   2. Dopo ogni attacco NORMALE: +2% crit, fino a cap 50%.
 *      Il CombatSystem chiama incrementCritAfterAttack() dopo ogni normale.
 *      Al cambio stanza applyPassiveBonus() resetta il crit a baseCritChance
 *      e riattiva lo stealth bonus.
 *
 * Bonus passivo (applyPassiveBonus, a ogni nuova stanza):
 *   - Riattiva stealth bonus
 *   - Resetta critChance a baseCritChance (25%)
 *   - Restituisce 2 stamina
 */
public class Thief extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final double BASE_CRIT_CHANCE = 0.25;
    private static final double CRIT_INCREMENT   = 0.02;  // +2% per attacco
    private static final double CRIT_CAP         = 0.50;  // massimo 50%

    private boolean stealthBonusActive = false;

    public Thief(String name) {
        super(name, 90, 18, 6, 8, 12, BASE_CRIT_CHANCE);
    }

    /**
     * Chiamato dal CombatSystem dopo ogni attacco normale.
     * Aumenta critChance di +2%, fino al cap del 50%.
     */
    public void incrementCritAfterAttack() {
        this.critChance = Math.min(CRIT_CAP, this.critChance + CRIT_INCREMENT);
    }

    @Override
    public void applyPassiveBonus() {
        this.stealthBonusActive = true;
        this.critChance = BASE_CRIT_CHANCE;  // reset a inizio nuova stanza
        restoreStamina(2);
    }

    public boolean isStealthBonusActive()  { return stealthBonusActive; }
    public void consumeStealthBonus()      { this.stealthBonusActive = false; }

    public double getBaseCritChance()      { return BASE_CRIT_CHANCE; }
    public double getCritCap()             { return CRIT_CAP; }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.THIEF; }
}
