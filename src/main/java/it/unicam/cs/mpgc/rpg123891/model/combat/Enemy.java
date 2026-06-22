package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;

/**
 * Rappresenta un nemico generico del gioco.
 *
 * Campi aggiunti rispetto alla versione precedente:
 *   - ability           : abilità speciale (null se il nemico non ne ha)
 *   - dragonPassiveBuff : buff passivo del boss finale (null per tutti gli altri)
 *   - isImmune          : flag impostato dalla WitchSummonAbility per
 *                         bloccare gli attacchi del giocatore su questo nemico
 */
public class Enemy extends GameCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private final AttackType attackType;
    private final double critModifierOnPlayer;
    private final boolean isBoss;

    /** Abilità speciale del nemico. null se non ne ha. */
    private EnemyAbility ability;

    /** Buff passivo del drago. null per tutti i nemici tranne L'Ultimo Drago. */
    private DragonPassiveBuff passiveBuff;

    /** Immunità agli attacchi (usata dalla Strega finché gli scheletri sono vivi). */
    private boolean immune = false;

    /** Costruttore completo usato da EnemyFactory. */
    public Enemy(String name, int maxHp, int attack, int defense,
                 int agility, double critChance, AttackType attackType,
                 double critModifierOnPlayer, boolean isBoss) {
        super(name, maxHp, attack, defense, agility, 0, critChance);
        this.attackType           = attackType;
        this.critModifierOnPlayer = critModifierOnPlayer;
        this.isBoss               = isBoss;
    }

    /** Costruttore semplificato per i test. */
    public Enemy(String name, int maxHp, int attack, int defense,
                 AttackType attackType, double critChance) {
        this(name, maxHp, attack, defense, 0, critChance, attackType, 0.0, false);
    }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.ENEMY; }

    /**
     * Il buff passivo del Drago viene attivato dal controller
     * se la condizione della Sala del Tesoro è soddisfatta.
     * Per tutti gli altri nemici non fa nulla.
     */
    @Override
    public void applyPassiveBonus() {
        if (passiveBuff != null) {
            passiveBuff.activate();
            // Applica +20% all'attacco base del boss
            this.attack = (int)(this.attack * passiveBuff.getDamageMultiplier());
        }
    }

    // -------------------------------------------------------------------------
    // Getter / Setter
    // -------------------------------------------------------------------------

    public AttackType getAttackType()              { return attackType; }
    public double getCritModifierOnPlayer()        { return critModifierOnPlayer; }
    public boolean isBoss()                        { return isBoss; }

    public EnemyAbility getAbility()               { return ability; }
    public void setAbility(EnemyAbility ability)   { this.ability = ability; }
    public boolean hasAbility()                    { return ability != null; }

    public DragonPassiveBuff getPassiveBuff()      { return passiveBuff; }
    public void setPassiveBuff(DragonPassiveBuff b){ this.passiveBuff = b; }

    public boolean isImmune()                      { return immune; }
    public void setImmune(boolean immune)          { this.immune = immune; }
}
