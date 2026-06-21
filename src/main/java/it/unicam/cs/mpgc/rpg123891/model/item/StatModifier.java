package it.unicam.cs.mpgc.rpg123891.model.item;

import java.io.Serializable;

/**
 * Contiene i delta delle stat che un'arma applica a un personaggio.
 * Usato in Weapon per differenziare i bonus per classe (Warrior/Mage/Thief).
 *
 * Campi negativi = penalita' (es. armatura riduce agilita').
 */
public record StatModifier(
        int attackDelta,
        int defenseDelta,
        int agilityDelta,
        int maxHpDelta,
        int maxStaminaDelta,
        double critDelta
) implements Serializable {

    /** Modificatore vuoto (nessun effetto). */
    public static StatModifier empty() {
        return new StatModifier(0, 0, 0, 0, 0, 0.0);
    }

    /** Costruttore comodo per modificatori solo ATK/AGI/stamina (i piu' comuni). */
    public static StatModifier of(int atk, int def, int agi) {
        return new StatModifier(atk, def, agi, 0, 0, 0.0);
    }
}
