package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe JavaFX Application.
 * Crea il GameController, mostra il MenuScreen e poi il GameScreen.
 */
public class FxApp extends Application {

    private GameController gameController;

    @Override
    public void start(Stage primaryStage) {
        gameController = new GameController(new JsonPersistenceManager());

        primaryStage.setTitle("Dungeon RPG");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        showMenu(primaryStage);
    }

    public void showMenu(Stage stage) {
        MenuScreen menu = new MenuScreen(gameController, stage, this);
        Scene scene = new Scene(menu.getRoot(), 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    public void showGame(Stage stage) {
        GameScreen game = new GameScreen(gameController, stage, this);
        Scene scene = new Scene(game.getRoot(), 960, 640);
        stage.setScene(scene);
        stage.show();
    }

    public GameController getGameController() { return gameController; }
}
