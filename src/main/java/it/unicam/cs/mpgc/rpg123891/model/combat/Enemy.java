package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;

/**
 * Rappresenta un nemico generico del gioco.
 *
 * Campi speciali:
 *   - ability           : abilità speciale (null se il nemico non ne ha)
 *   - dragonPassiveBuff : buff passivo del boss finale (null per tutti gli altri)
 *   - immune            : flag impostato dalla WitchSummonAbility
 *   - stunned           : flag impostato da Spazzatutto (Spadone);
 *                         il controller fa saltare il turno al nemico stordito
 *                         e chiama clearStun() a fine turno.
 */
public class Enemy extends GameCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private final AttackType attackType;
    private final double critModifierOnPlayer;
    private final boolean isBoss;

    private EnemyAbility ability;
    private DragonPassiveBuff passiveBuff;
    private boolean immune  = false;

    /**
     * Indica se il nemico è stordito (Spazzatutto).
     * Quando true il controller salta il turno di questo nemico
     * e chiama clearStun() a fine round.
     */
    private boolean stunned = false;

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

    @Override
    public void applyPassiveBonus() {
        if (passiveBuff != null) {
            passiveBuff.activate();
            this.attack = (int)(this.attack * passiveBuff.getDamageMultiplier());
        }
    }

    // -------------------------------------------------------------------------
    // Stordimento
    // -------------------------------------------------------------------------

    /** Stordisce il nemico per 1 turno. */
    public void stun()      { this.stunned = true; }

    /** Rimuove lo stordimento (chiamato dal controller a fine round). */
    public void clearStun() { this.stunned = false; }

    public boolean isStunned() { return stunned; }

    // -------------------------------------------------------------------------
    // Getter / Setter
    // -------------------------------------------------------------------------

    public AttackType getAttackType()               { return attackType; }
    public double getCritModifierOnPlayer()         { return critModifierOnPlayer; }
    public boolean isBoss()                         { return isBoss; }

    public EnemyAbility getAbility()                { return ability; }
    public void setAbility(EnemyAbility ability)    { this.ability = ability; }
    public boolean hasAbility()                     { return ability != null; }

    public DragonPassiveBuff getPassiveBuff()       { return passiveBuff; }
    public void setPassiveBuff(DragonPassiveBuff b) { this.passiveBuff = b; }

    public boolean isImmune()                       { return immune; }
    public void setImmune(boolean immune)           { this.immune = immune; }
}
