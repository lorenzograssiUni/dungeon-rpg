package it.unicam.cs.mpgc.rpg123891.persistence;

import it.unicam.cs.mpgc.rpg123891.model.game.GameState;

/**
 * Interfaccia per la gestione della persistenza del gioco.
 * Definisce le operazioni di salvataggio, caricamento e verifica esistenza salvataggio.
 * L'implementazione concreta e' disaccoppiata: e' possibile cambiare il meccanismo
 * (binario, JSON, database) senza modificare il resto del codice.
 */
public interface PersistenceManager {
    void save(GameState gameState);
    GameState load();
    boolean hasSave();
}
