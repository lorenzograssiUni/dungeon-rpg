package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;

/**
 * Interfaccia per qualsiasi implementazione di UI del gioco.
 * Questo è il punto chiave per l'estendibilità multi-piattaforma:
 * implementando questa interfaccia si può creare una UI desktop (JavaFX),
 * mobile (Android), web (REST/WebSocket) o testuale (CLI)
 * senza modificare nulla del model o del controller.
 */
public interface UIInterface {

    /**
     * Inizializza e avvia l'interfaccia utente.
     * @param controller il controller del gioco da collegare alla UI
     */
    void start(GameController controller);

    /**
     * Mostra un messaggio all'utente.
     */
    void showMessage(String message);

    /**
     * Aggiorna la visualizzazione dello stato del gioco.
     */
    void updateGameView();
}
