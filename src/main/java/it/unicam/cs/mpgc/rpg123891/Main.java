package it.unicam.cs.mpgc.rpg123891;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import it.unicam.cs.mpgc.rpg123891.ui.GameUI;

/**
 * Punto di ingresso dell'applicazione.
 * Avvia la UI testuale a console.
 */
public class Main {

    public static void main(String[] args) {
        GameController controller = new GameController(new JsonPersistenceManager());
        GameUI ui = new GameUI(controller);
        ui.run();
    }
}
