package it.unicam.cs.mpgc.rpg123891.model.combat;

/**
 * Factory per la creazione di tutti i nemici definiti in GAME_SPEC.md.
 *
 * Firme: name, maxHp, attack, defense, agility, critChance,
 *        attackType, critModifierOnPlayer, isBoss
 *
 * Agilità assegnata secondo la logica della spec:
 *   Cinghiale       3
 *   Lupo            5
 *   Goblin          4
 *   Goblin Guardia  4
 *   Re Goblin       4   (miniboss)
 *   Scheletro       3
 *   Scheletro Guardia 3
 *   Strega          6   (miniboss, veloce)
 *   Uovo            1   (lentissimo)
 *   Cucciolo Drago  5
 *   L'Ultimo Drago  5   (boss)
 */
public class EnemyFactory {

    // -------------------------------------------------------------------------
    // Stanza 1 — Foresta
    // -------------------------------------------------------------------------

    /** HP 36, ATK 7–10 → media 8, 50% drop Carne */
    public static Enemy createCinghiale() {
        return new Enemy("Cinghiale", 36, 8, 2, 3, 0.05,
                AttackType.PHYSICAL, 0.0, false);
    }

    /** HP 45, ATK 10–12 → media 11, 50% drop Carne */
    public static Enemy createLupo() {
        return new Enemy("Lupo", 45, 11, 3, 5, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    // -------------------------------------------------------------------------
    // Stanza 2 — Villaggio Goblin
    // -------------------------------------------------------------------------

    /** HP 42, ATK 10–15 → media 12 */
    public static Enemy createGoblin() {
        return new Enemy("Goblin", 42, 12, 2, 4, 0.10,
                AttackType.PHYSICAL, 0.05, false);
    }

    /**
     * HP come Goblin (42), equipaggiamento Spada + Scudo o Armatura.
     * Drop assicurato gestito dalla logica della stanza.
     */
    public static Enemy createGoblinGuardia() {
        return new Enemy("Goblin Guardia", 42, 14, 4, 4, 0.10,
                AttackType.PHYSICAL, 0.05, false);
    }

    /**
     * Re Goblin — Miniboss.
     * HP 55, ATK 15–20 → media 17.
     * Abilità speciale: 3 Lanci (20 ATK l'uno) — gestita dal controller.
     */
    public static Enemy createReGoblin() {
        return new Enemy("Re Goblin", 55, 17, 4, 4, 0.12,
                AttackType.PHYSICAL, 0.0, true);
    }

    // -------------------------------------------------------------------------
    // Stanza 3 — Catacombe
    // -------------------------------------------------------------------------

    /** HP 50, ATK 12–17 → media 14 */
    public static Enemy createScheletro() {
        return new Enemy("Scheletro", 50, 14, 3, 3, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    /**
     * HP come Scheletro (50), equipaggiamento Spada + Scudo/Armatura.
     * Abilità speciale: Carica! — gestita dal controller.
     * Drop assicurato: l'item mancante al giocatore.
     */
    public static Enemy createScheletroGuardia() {
        return new Enemy("Scheletro Guardia", 50, 16, 5, 3, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    /**
     * Strega — Miniboss.
     * HP 80, ATK 15–25 → media 20.
     * Abilità speciale: Evoca 3 scheletri + immunità — gestita dal controller.
     * Drop assicurato: Pendente Magico + 3 Pozioni.
     */
    public static Enemy createStrega() {
        return new Enemy("Strega", 80, 20, 3, 6, 0.12,
                AttackType.MAGICAL, 0.0, true);
    }

    // -------------------------------------------------------------------------
    // Stanza 4 — Sala del Tesoro
    // -------------------------------------------------------------------------

    /**
     * Uovo.
     * HP 20, ATK 1 (danno sicuro, ignora difesa — gestito dal controller).
     * Se non sconfitto entro 3 turni si trasforma in Cucciolo di Drago.
     * 50% drop Carne (solo i cuccioli in spec — le uova no).
     */
    public static Enemy createUovo() {
        return new Enemy("Uovo", 20, 1, 0, 1, 0.0,
                AttackType.PHYSICAL, 0.0, false);
    }

    /** HP 65, ATK 20–25 → media 22, 50% drop Carne */
    public static Enemy createCuccioloDrago() {
        return new Enemy("Cucciolo di Drago", 65, 22, 4, 5, 0.10,
                AttackType.PHYSICAL, 0.0, false);
    }

    // -------------------------------------------------------------------------
    // Boss Finale
    // -------------------------------------------------------------------------

    /**
     * L'Ultimo Drago — Boss Finale.
     * HP 140, ATK 30–60 → media 45.
     * Buff passivo +20% danno se ha visto morire tutti i cuccioli/uova
     *   nella Sala del Tesoro — gestito dal controller tramite applyPassiveBonus().
     * Abilità speciale: Soffio del Drago (bruciatura 5–8 HP × 3–5 turni)
     *   — gestita dal controller.
     */
    public static Enemy createUltimoDrago() {
        return new Enemy("L'Ultimo Drago", 140, 45, 10, 5, 0.15,
                AttackType.MIXED, -0.10, true);
    }
}
