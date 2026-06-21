package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;

/**
 * Rappresenta un nemico generico del gioco.
 * Estende GameCharacter aggiungendo:
 *   - attackType        : tipo di danno inflitto
 *   - critModifierOnPlayer : modifica la crit chance dell'attaccante avversario
 *   - isBoss            : flag per nemici boss
 *   - agility           : passato al super per gestire l'iniziativa
 */
public class Enemy extends GameCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private final AttackType attackType;
    private final double critModifierOnPlayer;
    private final boolean isBoss;

    /**
     * Costruttore completo usato da EnemyFactory.
     * agility determina se il nemico attacca prima o dopo il giocatore.
     */
    public Enemy(String name, int maxHp, int attack, int defense,
                 int agility, double critChance, AttackType attackType,
                 double critModifierOnPlayer, boolean isBoss) {
        super(name, maxHp, attack, defense, agility, 0, critChance);
        this.attackType            = attackType;
        this.critModifierOnPlayer  = critModifierOnPlayer;
        this.isBoss                = isBoss;
    }

    /** Costruttore semplificato per i test. */
    public Enemy(String name, int maxHp, int attack, int defense,
                 AttackType attackType, double critChance) {
        this(name, maxHp, attack, defense, 0, critChance, attackType, 0.0, false);
    }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.ENEMY; }

    @Override
    public void applyPassiveBonus() { /* i nemici base non hanno bonus passivi */ }

    public AttackType getAttackType()            { return attackType; }
    public double getCritModifierOnPlayer()       { return critModifierOnPlayer; }
    public boolean isBoss()                      { return isBoss; }
}
