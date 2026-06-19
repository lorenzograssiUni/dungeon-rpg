package it.unicam.cs.mpgc.rpg123891.persistence;

import it.unicam.cs.mpgc.rpg123891.model.game.GameState;

/**
 * Interfaccia per la gestione della persistenza dei dati di gioco.
 * Definisce il contratto per salvare e caricare lo stato della partita.
 *
 * Estendibilità: questa interfaccia permette di implementare diversi
 * meccanismi di persistenza (JSON su file, database, cloud) senza
 * modificare il GameController o il resto dell'applicazione.
 */
public interface PersistenceManager {

    /**
     * Salva lo stato della partita.
     * @param gameState lo stato da persistere
     */
    void save(GameState gameState);

    /**
     * Carica lo stato della partita salvata in precedenza.
     * @return lo stato caricato, o null se non esiste
     */
    GameState load();

    /**
     * Verifica se esiste un salvataggio disponibile.
     */
    boolean hasSave();
}
