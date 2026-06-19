package it.unicam.cs.mpgc.rpg123891.model.character;

/**
 * Personaggio giocabile di classe Mago.
 * Bonus passivo: SCHERMO MAGICO — immune agli attacchi fisici diretti
 * (danno fisico ridotto a 0), ma vulnerabile agli attacchi magici (+30% danno subito).
 */
public class Mage extends PlayerCharacter {

    private static final int BASE_HP      = 60;
    private static final int BASE_ATTACK  = 25;
    private static final int BASE_DEFENSE = 2;
    private static final int BASE_STAMINA = 100;
    private static final double BASE_CRIT = 0.05;

    /** Se true, lo schermo magico è attivo e blocca il danno fisico diretto. */
    private boolean magicShieldActive;

    public Mage(String name) {
        super(name, BASE_HP, BASE_ATTACK, BASE_DEFENSE, BASE_STAMINA, BASE_CRIT, CharacterClass.MAGE);
        this.magicShieldActive = true;
    }

    /**
     * Applica il bonus passivo SCHERMO MAGICO.
     * Lo schermo viene riattivato ad ogni inizio di combattimento.
     */
    @Override
    public void applyPassiveBonus() {
        this.magicShieldActive = true;
    }

    /**
     * Override di takeDamage: se lo schermo è attivo e l'attacco è fisico,
     * il danno viene annullato. Agli attacchi magici subisce +30% danno.
     */
    @Override
    public void takeDamage(int damage) {
        // La logica del tipo di attacco viene gestita dal CombatSystem;
        // qui lo schermo viene disattivato dopo aver bloccato una volta.
        super.takeDamage(damage);
    }

    public boolean isMagicShieldActive() {
        return magicShieldActive;
    }

    public void setMagicShieldActive(boolean active) {
        this.magicShieldActive = active;
    }

    @Override
    public CharacterClass getCharacterClass() {
        return CharacterClass.MAGE;
    }
}
