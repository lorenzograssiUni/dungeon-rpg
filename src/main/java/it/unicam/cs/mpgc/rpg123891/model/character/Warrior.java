package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;

/**
 * Guerriero: alta difesa e HP. Bonus passivo: +5 difesa, +20 HP massimi.
 */
public class Warrior extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    public Warrior(String name) {
        super(name, 120, 18, 10, 8, 0.05);
    }

    /**
     * Bonus passivo del Guerriero: +5 difesa e +20 HP massimi.
     * Applicato all'inizio della partita e ad ogni nuova stanza.
     */
    @Override
    public void applyPassiveBonus() {
        this.defense += 5;
        this.maxHp += 20;
        this.currentHp = Math.min(currentHp + 20, maxHp);
    }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.WARRIOR; }
}
