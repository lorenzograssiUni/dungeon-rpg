package it.unicam.cs.mpgc.rpg123891.model.combat;

import java.io.Serializable;

/**
 * Tipo di attacco usato nel calcolo del danno.
 * MIXED: attacco che combina fisico e magico (usato dal boss).
 */
public enum AttackType implements Serializable {
    PHYSICAL,
    MAGICAL,
    POISON,
    MIXED
}
