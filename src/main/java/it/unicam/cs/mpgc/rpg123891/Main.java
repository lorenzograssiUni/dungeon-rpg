package it.unicam.cs.mpgc.rpg123891;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import it.unicam.cs.mpgc.rpg123891.ui.FxApp;
import javafx.application.Application;

/**
 * Punto di ingresso. Lancia la UI JavaFX.
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(FxApp.class, args);
    }
}
