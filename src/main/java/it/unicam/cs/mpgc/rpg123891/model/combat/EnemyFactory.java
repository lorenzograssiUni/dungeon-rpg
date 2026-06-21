package it.unicam.cs.mpgc.rpg123891.model.combat;

/**
 * Factory per la creazione dei nemici predefiniti.
 * Stat nemici: name, maxHp, attack, defense, agility, critChance,
 *              attackType, critModifierOnPlayer, isBoss
 *
 * Agilita' dei nemici:
 *   Goblin    4  (pari al Warrior, batte Warrior in parita' no — giocatore ha prio)
 *   Scheletro 3  (lento)
 *   Strega    7  (veloce, batte Warrior e quasi Mage)
 *   Drago     5  (medio, batte Warrior)
 */
public class EnemyFactory {

    public static Enemy createGoblin() {
        return new Enemy("Goblin", 30, 8, 2, 4, 0.10,
                AttackType.PHYSICAL, +0.05, false);
    }

    public static Enemy createSkeleton() {
        return new Enemy("Scheletro", 45, 10, 5, 3, 0.08,
                AttackType.PHYSICAL, 0.0, false);
    }

    public static Enemy createWitch() {
        return new Enemy("Strega", 35, 14, 3, 7, 0.12,
                AttackType.MAGICAL, 0.0, false);
    }

    public static Enemy createDragonBoss() {
        return new Enemy("Drago Boss", 150, 20, 10, 5, 0.15,
                AttackType.MIXED, -0.10, true);
    }
}
