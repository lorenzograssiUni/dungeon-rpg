package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

/**
 * Rappresenta un nemico generico del gioco.
 * Estende GameCharacter con le proprietà specifiche dei nemici:
 * tipo di attacco e modificatore al critico del giocatore.
 */
public class Enemy extends GameCharacter {

    private final AttackType attackType;
    private final double critModifierOnPlayer; // modifica il critico del giocatore contro questo nemico
    private final boolean isBoss;

    public Enemy(String name, int maxHp, int attack, int defense,
                 double critChance, AttackType attackType,
                 double critModifierOnPlayer, boolean isBoss) {
        super(name, maxHp, attack, defense, 0, critChance);
        this.attackType = attackType;
        this.critModifierOnPlayer = critModifierOnPlayer;
        this.isBoss = isBoss;
    }

    /**
     * I nemici non hanno bonus passivi particolari nella versione base.
     * Estendibile in future versioni per nemici con abilità speciali.
     */
    @Override
    public void applyPassiveBonus() {
        // nessun bonus passivo per i nemici base
    }

    public AttackType getAttackType() { return attackType; }
    public double getCritModifierOnPlayer() { return critModifierOnPlayer; }
    public boolean isBoss() { return isBoss; }
}
