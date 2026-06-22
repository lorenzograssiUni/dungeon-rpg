package it.unicam.cs.mpgc.rpg123891.model.combat;

/**
 * Factory per la creazione di tutti i nemici definiti in GAME_SPEC.md.
 */
public class EnemyFactory {

    public static Enemy createCinghiale() {
        return new Enemy("Cinghiale", 36, 8, 2, 3, 0.05,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createLupo() {
        return new Enemy("Lupo", 45, 11, 3, 5, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createGoblin() {
        return new Enemy("Goblin", 42, 12, 2, 4, 0.10,
                AttackType.PHYSICAL, 0.05, false);
    }

    public static Enemy createGoblinGuardia() {
        return new Enemy("Goblin Guardia", 42, 14, 4, 4, 0.10,
                AttackType.PHYSICAL, 0.05, false);
    }

    public static Enemy createReGoblin() {
        Enemy e = new Enemy("Re Goblin", 55, 17, 4, 4, 0.12,
                AttackType.PHYSICAL, 0.0, true);
        e.setAbility(new ReGoblinThrowAbility());
        return e;
    }

    public static Enemy createScheletro() {
        return new Enemy("Scheletro", 50, 14, 3, 3, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createScheletroGuardia() {
        return new Enemy("Scheletro Guardia", 50, 16, 5, 3, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createStrega() {
        Enemy e = new Enemy("Strega", 80, 20, 3, 6, 0.12,
                AttackType.MAGICAL, 0.0, true);
        e.setAbility(new WitchSummonAbility());
        return e;
    }

    /**
     * Uovo — si schiude in Cucciolo di Drago dopo 3 turni.
     * ATK=1 ignora difesa (danno diretto tramite applyBurnDamage nel controller).
     */
    public static Enemy createUovo() {
        Enemy e = new Enemy("Uovo", 20, 1, 0, 1, 0.0,
                AttackType.PHYSICAL, 0.0, false);
        e.setTurnsToHatch(3);
        return e;
    }

    public static Enemy createCuccioloDrago() {
        return new Enemy("Cucciolo di Drago", 65, 22, 4, 5, 0.10,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createUltimoDrago() {
        Enemy e = new Enemy("L'Ultimo Drago", 140, 45, 10, 5, 0.15,
                AttackType.MIXED, -0.10, true);
        e.setAbility(new DragonBreathAbility());
        e.setPassiveBuff(new DragonPassiveBuff());
        return e;
    }
}
