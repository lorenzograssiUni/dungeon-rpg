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
        // Crea la scene con le dimensioni esatte del contenuto
        Scene scene = new Scene(game.getRoot(), GameScreen.WIN_W, GameScreen.WIN_H);
        stage.setScene(scene);
        // sizeToScene adatta la finestra alla scene (considera title bar + bordi OS)
        stage.sizeToScene();
        // Blocca il resize DOPO aver impostato le dimensioni
        stage.setMinWidth(stage.getWidth());
        stage.setMaxWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        stage.setMaxHeight(stage.getHeight());
        stage.show();
    }

    public GameController getGameController() { return gameController; }
}
