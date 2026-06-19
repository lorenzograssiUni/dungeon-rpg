package it.unicam.cs.mpgc.rpg123891.model.combat;

/**
 * Factory per la creazione dei nemici predefiniti del gioco.
 * Centralizza le statistiche dei nemici, semplificando l'aggiunta di nuovi tipi.
 * Rispetta il principio Open/Closed: per aggiungere un nemico si aggiunge solo un metodo.
 */
public class EnemyFactory {

    public static Enemy createGoblin() {
        return new Enemy("Goblin", 30, 8, 2, 0.10,
                AttackType.PHYSICAL, +0.05, false);
    }

    public static Enemy createSkeleton() {
        return new Enemy("Scheletro", 45, 10, 5, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createWitch() {
        return new Enemy("Strega", 35, 14, 3, 0.12,
                AttackType.MAGICAL, 0.0, false);
    }

    public static Enemy createDragonBoss() {
        return new Enemy("Drago Boss", 150, 20, 10, 0.15,
                AttackType.MIXED, -0.10, true);
    }
}
