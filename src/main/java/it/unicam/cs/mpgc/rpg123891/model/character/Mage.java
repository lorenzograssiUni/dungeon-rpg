package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Mago: alto attacco magico, bassa difesa fisica.
 * Stat base: HP 90, ATK 15, DEF 4, AGI 6, STA 15, CRIT 5%
 *
 * Passive (GAME_SPEC):
 *   - Scudo Magico: riduce SEMPRE del 30% i danni fisici in arrivo.
 *   - Vulnerabilita': subisce +30% danno da attacchi MAGICAL e MIXED.
 *
 * Nota: lo scudo magico e' un passive PERMANENTE, non un effetto on/off.
 * La riduzione e' applicata da CombatSystem.applyMagePassive().
 */
public class Mage extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    public Mage(String name) {
        super(name, 90, 15, 4, 6, 15, 0.05);
    }

    @Override
    public void applyPassiveBonus() {
        restoreStamina(2);
    }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.MAGE; }
}
