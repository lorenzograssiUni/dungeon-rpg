package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;

/**
 * Rappresenta un nemico generico del gioco.
 *
 * turnsToHatch: se > 0, questo nemico e' un Uovo che si schiude dopo N turni.
 * hatchCounter: conta i turni passati; quando raggiunge turnsToHatch
 *               il CombatController sostituisce questo Enemy con un CuccioloDrago.
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
    private boolean stunned = false;

    /** -1 = non e' un uovo. Se >= 1 si schiude dopo questo numero di turni. */
    private int turnsToHatch = -1;
    private int hatchCounter  = 0;

    public Enemy(String name, int maxHp, int attack, int defense,
                 int agility, double critChance, AttackType attackType,
                 double critModifierOnPlayer, boolean isBoss) {
        super(name, maxHp, attack, defense, agility, 0, critChance);
        this.attackType           = attackType;
        this.critModifierOnPlayer = critModifierOnPlayer;
        this.isBoss               = isBoss;
    }

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

    public void stun()      { this.stunned = true; }
    public void clearStun() { this.stunned = false; }
    public boolean isStunned() { return stunned; }

    // -------------------------------------------------------------------------
    // Uovo / schiusa
    // -------------------------------------------------------------------------

    public void setTurnsToHatch(int turns) { this.turnsToHatch = turns; }
    public int  getTurnsToHatch()          { return turnsToHatch; }
    public int  getHatchCounter()          { return hatchCounter; }
    public boolean isEgg()                 { return turnsToHatch > 0; }
    public boolean isReadyToHatch()        { return isEgg() && hatchCounter >= turnsToHatch; }

    /**
     * Incrementa il contatore di turni.
     * @return true se l'uovo e' pronto a schiudersi (hatchCounter >= turnsToHatch)
     */
    public boolean tickHatch() {
        if (!isEgg() || !isAlive()) return false;
        hatchCounter++;
        return hatchCounter >= turnsToHatch;
    }

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
