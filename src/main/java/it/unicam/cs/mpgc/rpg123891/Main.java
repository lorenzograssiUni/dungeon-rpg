package it.unicam.cs.mpgc.rpg123891;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import it.unicam.cs.mpgc.rpg123891.ui.GameUI;

import java.io.PrintStream;

/**
 * Punto di ingresso dell'applicazione.
 * Forza autoFlush su System.out per evitare il blocco dell'output
 * quando il gioco gira tramite Gradle su Windows.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // autoFlush=true: svuota il buffer dopo ogni println/print
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.setErr(new PrintStream(System.err, true, "UTF-8"));

        GameController controller = new GameController(new JsonPersistenceManager());
        GameUI ui = new GameUI(controller);
        ui.run();
    }
}
