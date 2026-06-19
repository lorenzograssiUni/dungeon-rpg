package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Implementazione della UI tramite JavaFX.
 * Estende Application (richiesto da JavaFX) e implementa UIInterface.
 * Per sostituire JavaFX con un'altra tecnologia UI basta creare una
 * nuova classe che implementi UIInterface e modificare Main.java.
 */
public class GameUI extends Application implements UIInterface {

    private GameController controller;
    private TextArea logArea;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        this.controller = new GameController(new JsonPersistenceManager());

        // Layout principale
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Area log di gioco
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(300);
        root.setCenter(logArea);

        // Status bar
        statusLabel = new Label("Benvenuto in Dungeon RPG! Scegli il tuo personaggio.");
        root.setTop(statusLabel);

        // Pulsanti azione
        HBox buttonBar = createButtonBar();
        root.setBottom(buttonBar);

        Scene scene = new Scene(root, 700, 450);
        primaryStage.setTitle("Dungeon RPG");
        primaryStage.setScene(scene);
        primaryStage.show();

        showMessage("=== Dungeon RPG ===");
        showMessage("Clicca 'Nuovo Gioco' per iniziare la tua avventura!");
    }

    private HBox createButtonBar() {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(10, 0, 0, 0));
        bar.setAlignment(Pos.CENTER);

        Button newGameBtn = new Button("Nuovo Gioco");
        newGameBtn.setOnAction(e -> startNewGame());

        Button attackBtn = new Button("Attacca");
        attackBtn.setOnAction(e -> handleAttack());

        Button advanceBtn = new Button("Avanza");
        advanceBtn.setOnAction(e -> handleAdvance());

        Button saveBtn = new Button("Salva");
        saveBtn.setOnAction(e -> handleSave());

        bar.getChildren().addAll(newGameBtn, attackBtn, advanceBtn, saveBtn);
        return bar;
    }

    private void startNewGame() {
        // Per ora usa Warrior come default; estendibile con dialogo di scelta classe
        controller.startNewGame(new Warrior("Eroe"));
        updateGameView();
        showMessage("Nuova partita iniziata con il Guerriero!");
        showMessage(controller.getCurrentRoom().getName() + ": " + controller.getCurrentRoom().getDescription());
    }

    private void handleAttack() {
        if (controller.getGameState() == null) { showMessage("Avvia una nuova partita!"); return; }
        var enemies = controller.getCurrentRoom().getEnemies();
        var aliveEnemies = enemies.stream().filter(e -> e.isAlive()).toList();
        if (aliveEnemies.isEmpty()) { showMessage("Nessun nemico nella stanza."); return; }
        var enemy = aliveEnemies.get(0);
        int dmg = controller.playerAttack(enemy);
        showMessage("Attacchi " + enemy.getName() + " per " + dmg + " danni!");
        if (!enemy.isAlive()) {
            showMessage(enemy.getName() + " è stato sconfitto!");
            controller.checkRoomCleared();
        } else {
            int enmDmg = controller.enemyAttack(enemy);
            showMessage(enemy.getName() + " ti attacca per " + enmDmg + " danni!");
        }
        updateGameView();
    }

    private void handleAdvance() {
        if (controller.getGameState() == null) { showMessage("Avvia una nuova partita!"); return; }
        boolean advanced = controller.advanceRoom();
        if (advanced) {
            showMessage("Avanzi nella prossima stanza...");
            showMessage(controller.getCurrentRoom().getName() + ": " + controller.getCurrentRoom().getDescription());
        } else {
            showMessage("Non puoi avanzare. Sconfiggi tutti i nemici prima!");
        }
        updateGameView();
    }

    private void handleSave() {
        if (controller.getGameState() == null) { showMessage("Nessuna partita da salvare!"); return; }
        controller.saveGame();
        showMessage("Partita salvata!");
    }

    @Override
    public void start(GameController controller) {
        this.controller = controller;
    }

    @Override
    public void showMessage(String message) {
        if (logArea != null) logArea.appendText(message + "\n");
    }

    @Override
    public void updateGameView() {
        if (controller.getGameState() == null) return;
        var player = controller.getPlayer();
        statusLabel.setText(player.toString());
    }
}
