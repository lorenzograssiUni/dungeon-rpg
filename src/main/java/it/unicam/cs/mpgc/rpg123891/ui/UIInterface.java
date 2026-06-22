package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;

/**
 * Interfaccia minima per le implementazioni UI.
 * Permette di sostituire la console UI con una GUI in futuro.
 */
public interface UIInterface {
    void run();
    void showMessage(String message);
    void start(GameController controller);
}
