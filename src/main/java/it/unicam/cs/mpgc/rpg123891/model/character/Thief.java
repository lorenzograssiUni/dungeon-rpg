package it.unicam.cs.mpgc.rpg123891.model.character;

/**
 * Personaggio giocabile di classe Ladro.
 * Bonus passivo: FURTIVITÀ — il primo attacco di ogni combattimento è sempre critico.
 * Può sempre tentare la fuga da qualsiasi scontro.
 */
public class Thief extends PlayerCharacter {

    private static final int BASE_HP      = 85;
    private static final int BASE_ATTACK  = 12;
    private static final int BASE_DEFENSE = 5;
    private static final int BASE_STAMINA = 100;
    private static final double BASE_CRIT = 0.20;

    /** Se true, il prossimo attacco sarà automaticamente critico. */
    private boolean stealthBonusActive;

    public Thief(String name) {
        super(name, BASE_HP, BASE_ATTACK, BASE_DEFENSE, BASE_STAMINA, BASE_CRIT, CharacterClass.THIEF);
        this.stealthBonusActive = true;
    }

    /**
     * Applica il bonus passivo FURTIVITÀ.
     * Riattiva il bonus critico garantito ad ogni inizio di combattimento.
     */
    @Override
    public void applyPassiveBonus() {
        this.stealthBonusActive = true;
    }

    public boolean isStealthBonusActive() {
        return stealthBonusActive;
    }

    /** Consuma il bonus furtività dopo il primo attacco. */
    public void consumeStealthBonus() {
        this.stealthBonusActive = false;
    }

    @Override
    public CharacterClass getCharacterClass() {
        return CharacterClass.THIEF;
    }
}
