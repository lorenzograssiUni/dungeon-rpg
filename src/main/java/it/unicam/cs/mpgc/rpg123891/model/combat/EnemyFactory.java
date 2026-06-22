package it.unicam.cs.mpgc.rpg123891.model.combat;

/**
 * Factory per la creazione di tutti i nemici definiti in GAME_SPEC.md.
 *
 * Firme: name, maxHp, attack, defense, agility, critChance,
 *        attackType, critModifierOnPlayer, isBoss
 *
 * Agilità assegnata secondo la logica della spec:
 *   Cinghiale          3
 *   Lupo               5
 *   Goblin             4
 *   Goblin Guardia     4
 *   Re Goblin          4   (miniboss)
 *   Scheletro          3
 *   Scheletro Guardia  3
 *   Strega             6   (miniboss, veloce)
 *   Uovo               1   (lentissimo)
 *   Cucciolo Drago     5
 *   L'Ultimo Drago     5   (boss)
 */
public class EnemyFactory {

    // -------------------------------------------------------------------------
    // Stanza 1 — Foresta
    // -------------------------------------------------------------------------

    public static Enemy createCinghiale() {
        return new Enemy("Cinghiale", 36, 8, 2, 3, 0.05,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createLupo() {
        return new Enemy("Lupo", 45, 11, 3, 5, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    // -------------------------------------------------------------------------
    // Stanza 2 — Villaggio Goblin
    // -------------------------------------------------------------------------

    public static Enemy createGoblin() {
        return new Enemy("Goblin", 42, 12, 2, 4, 0.10,
                AttackType.PHYSICAL, 0.05, false);
    }

    public static Enemy createGoblinGuardia() {
        return new Enemy("Goblin Guardia", 42, 14, 4, 4, 0.10,
                AttackType.PHYSICAL, 0.05, false);
    }

    /** Re Goblin — Miniboss con abilità speciale: 3 Lanci. */
    public static Enemy createReGoblin() {
        Enemy e = new Enemy("Re Goblin", 55, 17, 4, 4, 0.12,
                AttackType.PHYSICAL, 0.0, true);
        e.setAbility(new ReGoblinThrowAbility());
        return e;
    }

    // -------------------------------------------------------------------------
    // Stanza 3 — Catacombe
    // -------------------------------------------------------------------------

    public static Enemy createScheletro() {
        return new Enemy("Scheletro", 50, 14, 3, 3, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createScheletroGuardia() {
        return new Enemy("Scheletro Guardia", 50, 16, 5, 3, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    /** Strega — Miniboss con abilità speciale: Evocazione Scheletri. */
    public static Enemy createStrega() {
        Enemy e = new Enemy("Strega", 80, 20, 3, 6, 0.12,
                AttackType.MAGICAL, 0.0, true);
        e.setAbility(new WitchSummonAbility());
        return e;
    }

    // -------------------------------------------------------------------------
    // Stanza 4 — Sala del Tesoro
    // -------------------------------------------------------------------------

    /** Uovo — ATK 1 ignora difesa (applyBurnDamage), si trasforma in Cucciolo dopo 3 turni. */
    public static Enemy createUovo() {
        return new Enemy("Uovo", 20, 1, 0, 1, 0.0,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createCuccioloDrago() {
        return new Enemy("Cucciolo di Drago", 65, 22, 4, 5, 0.10,
                AttackType.PHYSICAL, 0.0, false);
    }

    // -------------------------------------------------------------------------
    // Boss Finale
    // -------------------------------------------------------------------------

    /**
     * L'Ultimo Drago — Boss Finale.
     * Abilità speciale: Soffio del Drago (bruciatura).
     * Buff passivo: +20% danno se attivato dal controller.
     */
    public static Enemy createUltimoDrago() {
        Enemy e = new Enemy("L'Ultimo Drago", 140, 45, 10, 5, 0.15,
                AttackType.MIXED, -0.10, true);
        e.setAbility(new DragonBreathAbility());
        e.setPassiveBuff(new DragonPassiveBuff());
        return e;
    }
}
