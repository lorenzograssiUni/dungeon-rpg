package it.unicam.cs.mpgc.rpg123891.model.combat;

import java.io.Serial;
import java.io.Serializable;

/**
 * Buff passivo de L'Ultimo Drago.
 *
 * Se nella Sala del Tesoro il giocatore ha sconfitto tutti i Cuccioli
 * e le Uova prima di entrare nella stanza del boss, il Drago ottiene
 * +20% al danno per l'intera durata del combattimento.
 *
 * Il controller chiama activate() una sola volta prima del combattimento
 * se la condizione è soddisfatta. Il CombatSystem moltiplica il danno
 * del boss per getDamageMultiplier() al momento dell'attacco.
 */
public class DragonPassiveBuff implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final double BUFF_MULTIPLIER = 1.20;

    private boolean active = false;

    /** Attiva il buff (chiamato dal controller se la condizione è soddisfatta). */
    public void activate() { this.active = true; }

    public boolean isActive() { return active; }

    /**
     * Restituisce il moltiplicatore di danno da applicare all'attacco del boss.
     * 1.0 se il buff non è attivo, 1.20 se è attivo.
     */
    public double getDamageMultiplier() { return active ? BUFF_MULTIPLIER : 1.0; }
}
