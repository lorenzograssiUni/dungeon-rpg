package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxApp extends Application {

    private static final double WIN_W = 980;
    private static final double WIN_H = 640;

    private GameController gameController;

    @Override
    public void start(Stage primaryStage) {
        gameController = new GameController(new JsonPersistenceManager());
        primaryStage.setTitle("Dungeon RPG");

        // Blocca resize e schermo intero
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false);
        primaryStage.setFullScreenExitHint("");

        showMenu(primaryStage);
    }

    public void showMenu(Stage stage) {
        MenuScreen menu = new MenuScreen(gameController, stage, this);
        Scene scene = new Scene(menu.getRoot(), 960, 680);
        stage.setScene(scene);
        stage.show();
    }

    public void showGame(Stage stage) {
        GameScreen game = new GameScreen(gameController, stage, this);
        Scene scene = new Scene(game.getRoot(), WIN_W, WIN_H);
        stage.setWidth(WIN_W);
        stage.setHeight(WIN_H);
        stage.setMinWidth(WIN_W);
        stage.setMinHeight(WIN_H);
        stage.setMaxWidth(WIN_W);
        stage.setMaxHeight(WIN_H);
        stage.setScene(scene);
        stage.show();
    }

    public GameController getGameController() { return gameController; }
}
