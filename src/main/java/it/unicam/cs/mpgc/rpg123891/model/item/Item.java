package it.unicam.cs.mpgc.rpg123891.model.item;

/**
 * Interfaccia che rappresenta un oggetto generico dell'inventario.
 * Tutti gli oggetti del gioco devono implementare questa interfaccia.
 * Estendibile per aggiungere nuovi tipi di oggetti senza modificare il codice esistente.
 */
public interface Item {

    /**
     * Restituisce il nome dell'oggetto.
     */
    String getName();

    /**
     * Restituisce la descrizione dell'oggetto.
     */
    String getDescription();
}
