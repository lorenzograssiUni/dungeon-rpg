package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;

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
    private Button attackBtn;
    private Button advanceBtn;
    private Button saveBtn;
    private Button potionBtn;

    @Override
    public void start(Stage primaryStage) {
        this.controller = new GameController(new JsonPersistenceManager());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(300);
        root.setCenter(logArea);

        statusLabel = new Label("Benvenuto in Dungeon RPG! Scegli il tuo personaggio.");
        root.setTop(statusLabel);

        HBox buttonBar = createButtonBar();
        root.setBottom(buttonBar);

        setGameButtonsDisabled(true);

        Scene scene = new Scene(root, 750, 470);
        primaryStage.setTitle("Dungeon RPG");
        primaryStage.setScene(scene);
        primaryStage.show();

        showMessage("=== Dungeon RPG ===");
        showMessage("Clicca 'Nuovo Gioco' per iniziare o 'Carica' per riprendere una partita salvata.");
    }

    private HBox createButtonBar() {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(10, 0, 0, 0));
        bar.setAlignment(Pos.CENTER);

        Button newGameBtn = new Button("Nuovo Gioco");
        newGameBtn.setOnAction(e -> startNewGame());

        Button loadBtn = new Button("Carica");
        loadBtn.setOnAction(e -> handleLoad());

        attackBtn = new Button("Attacca");
        attackBtn.setOnAction(e -> handleAttack());

        advanceBtn = new Button("Avanza");
        advanceBtn.setOnAction(e -> handleAdvance());

        potionBtn = new Button("Usa Pozione");
        potionBtn.setOnAction(e -> handlePotion());

        saveBtn = new Button("Salva");
        saveBtn.setOnAction(e -> handleSave());

        bar.getChildren().addAll(newGameBtn, loadBtn, attackBtn, advanceBtn, potionBtn, saveBtn);
        return bar;
    }

    private void startNewGame() {
        Optional<PlayerCharacter> choice = showClassChoiceDialog();
        if (choice.isEmpty()) return;
        controller.startNewGame(choice.get());
        setGameButtonsDisabled(false);
        updateGameView();
        showMessage("\n--- Nuova partita iniziata con " + controller.getPlayer().getCharacterClass() + "! ---");
        showMessage(controller.getCurrentRoom().getName() + ": " + controller.getCurrentRoom().getDescription());
        showEnemiesInRoom();
    }

    private Optional<PlayerCharacter> showClassChoiceDialog() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Guerriero", "Guerriero", "Mago", "Ladro");
        dialog.setTitle("Scelta Classe");
        dialog.setHeaderText("Scegli la classe del tuo personaggio");
        dialog.setContentText("Classe:");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return Optional.empty();
        return switch (result.get()) {
            case "Mago"  -> Optional.of(new Mage("Eroe"));
            case "Ladro" -> Optional.of(new Thief("Eroe"));
            default      -> Optional.of(new Warrior("Eroe"));
        };
    }

    private void handleLoad() {
        if (!controller.hasSavedGame()) {
            showMessage("Nessun salvataggio trovato!");
            return;
        }
        controller.loadGame();
        setGameButtonsDisabled(false);
        updateGameView();
        showMessage("\n--- Partita caricata! ---");
        showMessage(controller.getCurrentRoom().getName() + ": " + controller.getCurrentRoom().getDescription());
        showEnemiesInRoom();
    }

    private void handleAttack() {
        if (controller.getGameState() == null) { showMessage("Avvia una nuova partita!"); return; }
        if (controller.getGameState().isGameOver()) { showMessage("La partita e' terminata. Inizia una nuova!"); return; }
        var aliveEnemies = controller.getCurrentRoom().getEnemies().stream()
                .filter(e -> e.isAlive()).toList();
        if (aliveEnemies.isEmpty()) { showMessage("Nessun nemico vivo nella stanza. Avanza!"); return; }
        var enemy = aliveEnemies.get(0);
        int dmg = controller.playerAttack(enemy);
        showMessage("[ATK] Attacchi " + enemy.getName() + " per " + dmg + " danni!");
        if (!enemy.isAlive()) {
            showMessage("[KILL] " + enemy.getName() + " e' stato sconfitto!");
            controller.checkRoomCleared();
            if (controller.getGameState().isVictory()) {
                showMessage("\n*** HAI VINTO! Hai completato il dungeon! ***");
                setGameButtonsDisabled(true);
            }
        } else {
            int enmDmg = controller.enemyAttack(enemy);
            showMessage("[DMG] " + enemy.getName() + " ti attacca per " + enmDmg + " danni!");
            if (controller.checkPlayerDead()) {
                showMessage("\n*** SEI MORTO! Game Over. ***");
                setGameButtonsDisabled(true);
            }
        }
        updateGameView();
    }

    private void handleAdvance() {
        if (controller.getGameState() == null) { showMessage("Avvia una nuova partita!"); return; }
        boolean advanced = controller.advanceRoom();
        if (advanced) {
            showMessage("\n[>>>] Avanzi nella prossima stanza...");
            showMessage(controller.getCurrentRoom().getName() + ": " + controller.getCurrentRoom().getDescription());
            showEnemiesInRoom();
        } else {
            showMessage("[!] Non puoi avanzare. Sconfiggi tutti i nemici prima!");
        }
        updateGameView();
    }

    private void handlePotion() {
        if (controller.getGameState() == null) { showMessage("Avvia una nuova partita!"); return; }
        boolean used = controller.useFirstPotion();
        if (used) {
            showMessage("[HEAL] Hai usato una pozione! HP: " + controller.getPlayer().getCurrentHp()
                    + "/" + controller.getPlayer().getMaxHp());
        } else {
            showMessage("Nessuna pozione nell'inventario!");
        }
        updateGameView();
    }

    private void handleSave() {
        if (controller.getGameState() == null) { showMessage("Nessuna partita da salvare!"); return; }
        controller.saveGame();
        showMessage("[SAVE] Partita salvata!");
    }

    private void showEnemiesInRoom() {
        var enemies = controller.getCurrentRoom().getEnemies();
        if (enemies.isEmpty()) {
            showMessage("La stanza e' libera.");
        } else {
            enemies.stream()
                    .filter(e -> e.isAlive())
                    .forEach(e -> showMessage("[NEMICO] " + e.getName() + " (HP: " + e.getCurrentHp() + ")"));
        }
    }

    private void setGameButtonsDisabled(boolean disabled) {
        attackBtn.setDisable(disabled);
        advanceBtn.setDisable(disabled);
        potionBtn.setDisable(disabled);
        saveBtn.setDisable(disabled);
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
        long potions = controller.countPotions();
        statusLabel.setText(player.toString() + " | Pozioni: " + potions);
    }
}
