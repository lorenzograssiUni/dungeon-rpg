package it.unicam.cs.mpgc.rpg123891.model.combat;

import java.io.Serializable;

/**
 * Tipo di attacco. Serializable per supportare la serializzazione di Enemy.
 */
public enum AttackType implements Serializable {
    PHYSICAL, MAGICAL, POISON
}
