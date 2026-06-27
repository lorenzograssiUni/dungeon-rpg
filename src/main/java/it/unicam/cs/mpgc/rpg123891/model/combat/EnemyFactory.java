package it.unicam.cs.mpgc.rpg123891.model.combat;

import java.util.Random;

/**
 * Factory per la creazione di tutti i nemici definiti in GAME_SPEC.md.
 * Gli attacchi a range usano un Random per scegliere un valore tra min e max.
 */
public class EnemyFactory {

    private static final Random RNG = new Random();

    /** Ritorna un valore int casuale tra min e max inclusi. */
    private static int range(int min, int max) {
        return min + RNG.nextInt(max - min + 1);
    }

    public static Enemy createCinghiale() {
        return new Enemy("Cinghiale", 36, range(7, 10), 0, 3, 0.0,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createLupo() {
        return new Enemy("Lupo", 45, range(10, 12), 0, 3, 0.0,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createGoblin() {
        return new Enemy("Goblin", 42, range(15, 20), 0, 4, 0.0,
                AttackType.PHYSICAL, 0.0, false);
    }

    /**
     * Goblin Guardia: HP e ATK base come Goblin + bonus equipaggiamento.
     * Spada: +2 ATK; Scudo: +2 DEF; o Armatura: +4 DEF.
     * Per semplicita' usiamo lo stesso range ATK del Goblin + 2 e DEF 2 (scudo).
     */
    public static Enemy createGoblinGuardia() {
        return new Enemy("Goblin Guardia", 42, range(15, 20) + 2, 2, 4, 0.0,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createReGoblin() {
        Enemy e = new Enemy("Re Goblin", 90, range(20, 25), 0, 4, 0.0,
                AttackType.PHYSICAL, 0.0, true);
        e.setAbility(new ReGoblinThrowAbility());
        return e;
    }

    public static Enemy createScheletro() {
        return new Enemy("Scheletro", 55, range(12, 20), 0, 3, 0.0,
                AttackType.PHYSICAL, 0.0, false);
    }

    /**
     * Scheletro Guardia: HP e ATK come Scheletro normale + bonus equipaggiamento.
     * Ha l'abilita' Carica! ogni 3 turni.
     */
    public static Enemy createScheletroGuardia() {
        Enemy e = new Enemy("Scheletro Guardia", 55, range(12, 20) + 2, 2, 3, 0.0,
                AttackType.PHYSICAL, 0.0, false);
        e.setAbility(new SkeletonChargeAbility());
        return e;
    }

    public static Enemy createStrega() {
        Enemy e = new Enemy("Strega", 90, range(15, 25), 0, 6, 0.0,
                AttackType.MAGICAL, 0.0, true);
        e.setAbility(new WitchSummonAbility());
        return e;
    }

    /**
     * Uovo: HP 40, ATK 1 (danno diretto, ignora difesa), si schiude dopo 3 turni.
     * La carne viene droppata SOLO se l'uovo si trasforma in cucciolo e poi viene sconfitto.
     */
    public static Enemy createUovo() {
        Enemy e = new Enemy("Uovo", 40, 1, 0, 1, 0.0,
                AttackType.PHYSICAL, 0.0, false);
        e.setTurnsToHatch(3);
        return e;
    }

    public static Enemy createCuccioloDrago() {
        return new Enemy("Cucciolo di Drago", 65, range(20, 25), 0, 5, 0.0,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createUltimoDrago() {
        Enemy e = new Enemy("L'Ultimo Drago", 140, range(30, 60), 0, 5, 0.0,
                AttackType.MIXED, 0.0, true);
        e.setAbility(new DragonBreathAbility());
        e.setPassiveBuff(new DragonPassiveBuff());
        return e;
    }
}
