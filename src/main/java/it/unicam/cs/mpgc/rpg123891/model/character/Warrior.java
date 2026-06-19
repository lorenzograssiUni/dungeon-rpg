package it.unicam.cs.mpgc.rpg123891.model.character;

/**
 * Personaggio giocabile di classe Guerriero.
 * Bonus passivo: BLOCCO — ad ogni turno ha una probabilità di bloccare
 * completamente l'attacco nemico, in base alla propria difesa.
 */
public class Warrior extends PlayerCharacter {

    private static final int BASE_HP      = 120;
    private static final int BASE_ATTACK  = 15;
    private static final int BASE_DEFENSE = 10;
    private static final int BASE_STAMINA = 100;
    private static final double BASE_CRIT = 0.08;

    /** Probabilità base di blocco (aumenta con la difesa). */
    private double blockChance;

    public Warrior(String name) {
        super(name, BASE_HP, BASE_ATTACK, BASE_DEFENSE, BASE_STAMINA, BASE_CRIT, CharacterClass.WARRIOR);
        this.blockChance = calculateBlockChance();
    }

    /**
     * Calcola la probabilità di blocco in base alla difesa attuale.
     * Formula: difesa / 100.0, con un massimo del 40%.
     */
    private double calculateBlockChance() {
        return Math.min(defense / 100.0, 0.40);
    }

    /**
     * Applica il bonus passivo BLOCCO.
     * Il Guerriero può bloccare completamente un attacco fisico
     * con probabilità pari a blockChance.
     */
    @Override
    public void applyPassiveBonus() {
        this.blockChance = calculateBlockChance();
    }

    public double getBlockChance() {
        return blockChance;
    }

    @Override
    public CharacterClass getCharacterClass() {
        return CharacterClass.WARRIOR;
    }
}
