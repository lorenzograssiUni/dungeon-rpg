package it.unicam.cs.mpgc.rpg123891.model.combat;

/**
 * Enum che rappresenta i tipi di attacco disponibili in combattimento.
 * Usato dal CombatSystem per applicare modificatori specifici
 * (es. lo schermo magico del Mago blocca solo attacchi PHYSICAL).
 */
public enum AttackType {
    PHYSICAL,
    MAGICAL,
    MIXED
}
