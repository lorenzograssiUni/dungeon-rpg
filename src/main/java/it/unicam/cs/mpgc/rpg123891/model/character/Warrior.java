package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Guerriero: alta vita e difesa, attacco solido.
 * Stat base: HP 120, ATK 22, DEF 8, AGI 4, STA 8, CRIT 5%
 *
 * Bonus passivo (applicato a ogni nuova stanza):
 *   +5 difesa, +20 HP massimi.
 * Abilita' speciale passiva in combattimento:
 *   20% di probabilita' di bloccare completamente un attacco fisico.
 */
public class Warrior extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final double BLOCK_CHANCE = 0.20;

    public Warrior(String name) {
        super(name, 120, 22, 8, 4, 8, 0.05);
    }

    @Override
    public void applyPassiveBonus() {
        this.defense  += 5;
        this.maxHp    += 20;
        this.currentHp = Math.min(currentHp + 20, maxHp);
        restoreStamina(2);
    }

    public double getBlockChance() { return BLOCK_CHANCE; }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.WARRIOR; }
}
