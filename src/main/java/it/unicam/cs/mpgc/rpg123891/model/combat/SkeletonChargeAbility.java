package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

/**
 * Abilita' Speciale dello Scheletro Guardia: Carica!
 * Si attiva ogni 3 turni.
 * Danno = ATK * 2, bypassa la difesa (danno diretto).
 */
public class SkeletonChargeAbility implements EnemyAbility {

    private static final long serialVersionUID = 1L;
    private static final int COOLDOWN = 3;
    private int turnCounter = 0;

    @Override
    public String getName() {
        return "Carica!";
    }

    @Override
    public boolean isReady() {
        return turnCounter >= COOLDOWN;
    }

    @Override
    public void tick() {
        turnCounter++;
    }

    @Override
    public AbilityResult use(Enemy user, GameCharacter target) {
        turnCounter = 0;
        int damage = user.getAttack() * 2;
        target.applyBurnDamage(damage); // ignora difesa
        return AbilityResult.of(user.getName() + " usa Carica! e infligge " + damage + " danni diretti!", damage);
    }
}
