package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Guerriero: alta difesa e HP.
 * Bonus passivo: +5 difesa, +20 HP massimi ogni stanza.
 * Abilita' speciale: 20% di probabilita' di bloccare un attacco fisico.
 */
public class Warrior extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final double BLOCK_CHANCE = 0.20;

    public Warrior(String name) {
        super(name, 120, 18, 10, 8, 0.05);
    }

    @Override
    public void applyPassiveBonus() {
        this.defense += 5;
        this.maxHp += 20;
        this.currentHp = Math.min(currentHp + 20, maxHp);
    }

    /** Probabilita' di bloccare un attacco fisico (20%). */
    public double getBlockChance() { return BLOCK_CHANCE; }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.WARRIOR; }
}
