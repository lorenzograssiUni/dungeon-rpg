package it.unicam.cs.mpgc.rpg123891.model.combat;

/**
 * Interfaccia che definisce il contratto per qualsiasi entità
 * che può partecipare a un combattimento.
 * Implementata da GameCharacter e da tutte le sue sottoclassi.
 */
public interface Combatable {

    /**
     * Riceve danno da un attacco.
     * @param damage il danno base dell'attacco (prima delle riduzioni)
     */
    void takeDamage(int damage);

    /**
     * Restituisce il valore di attacco del combattente.
     */
    int getAttack();

    /**
     * Restituisce la percentuale di critico del combattente.
     */
    double getCritChance();

    /**
     * Indica se il combattente è ancora in vita.
     */
    boolean isAlive();

    /**
     * Applica il bonus passivo specifico del combattente.
     */
    void applyPassiveBonus();
}
