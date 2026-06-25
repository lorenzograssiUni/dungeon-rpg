package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxApp extends Application {

    private GameController gameController;

    @Override
    public void start(Stage primaryStage) {
        gameController = new GameController(new JsonPersistenceManager());
        primaryStage.setTitle("Dungeon RPG");
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
        double w = GameScreen.WIN_W;
        double h = GameScreen.WIN_H;
        Scene scene = new Scene(game.getRoot(), w, h);
        stage.setMinWidth(w);  stage.setMaxWidth(w);
        stage.setMinHeight(h); stage.setMaxHeight(h);
        stage.setScene(scene);
        stage.show();
    }

    public GameController getGameController() { return gameController; }
}
