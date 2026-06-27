package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

/**
 * Abilita' Speciale dello Scheletro Guardia: Carica!
 * Si attiva ogni 3 turni.
 * Danno = ATK * 2, bypassa la difesa (danno diretto).
 */
public class SkeletonChargeAbility implements EnemyAbility {

    private static final int COOLDOWN = 3;
    private int turnCounter = 0;

    @Override
    public boolean isReady() {
        return turnCounter >= COOLDOWN;
    }

    @Override
    public int execute(GameCharacter user, GameCharacter target) {
        turnCounter = 0;
        int damage = user.getAttack() * 2;
        target.applyBurnDamage(damage); // ignora difesa
        return damage;
    }

    @Override
    public void tick() {
        turnCounter++;
    }

    @Override
    public String getDescription() {
        return "Carica! (x2 ATK, ignora difesa, ogni 3 turni)";
    }
}
