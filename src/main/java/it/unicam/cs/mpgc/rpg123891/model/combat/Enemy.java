package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;

/**
 * Rappresenta un nemico generico del gioco.
 * Estende GameCharacter con le proprieta' specifiche dei nemici.
 */
public class Enemy extends GameCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private final AttackType attackType;
    private final double critModifierOnPlayer;
    private final boolean isBoss;

    /** Costruttore completo usato da EnemyFactory. */
    public Enemy(String name, int maxHp, int attack, int defense,
                 double critChance, AttackType attackType,
                 double critModifierOnPlayer, boolean isBoss) {
        super(name, maxHp, attack, defense, 0, critChance);
        this.attackType = attackType;
        this.critModifierOnPlayer = critModifierOnPlayer;
        this.isBoss = isBoss;
    }

    /** Costruttore semplificato usato nei test: name, maxHp, attack, defense, attackType, critChance. */
    public Enemy(String name, int maxHp, int attack, int defense,
                 AttackType attackType, double critChance) {
        this(name, maxHp, attack, defense, critChance, attackType, 0.0, false);
    }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.ENEMY; }

    @Override
    public void applyPassiveBonus() { /* nemici base senza bonus */ }

    public AttackType getAttackType() { return attackType; }
    public double getCritModifierOnPlayer() { return critModifierOnPlayer; }
    public boolean isBoss() { return isBoss; }
}
