package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * Implementazione della UI tramite JavaFX.
 * Fornisce una interfaccia grafica suddivisa in:
 * - pannello sinistro: statistiche giocatore con HP bar e inventario
 * - centro: log di gioco
 * - pannello destro: informazioni sulla stanza corrente e nemici
 * - basso: barra dei pulsanti di azione
 */
public class GameUI extends Application implements UIInterface {

    private GameController controller;

    // --- Log ---
    private TextArea logArea;

    // --- Pannello giocatore (sinistra) ---
    private Label playerNameLabel;
    private Label playerClassLabel;
    private Label hpTextLabel;
    private Rectangle hpBarFill;
    private Label staminaLabel;
    private Label agilityLabel;
    private ListView<String> inventoryList;

    // --- Pannello stanza (destra) ---
    private Label roomNameLabel;
    private Label roomDescLabel;
    private ListView<String> enemyList;

    // --- Pulsanti azione ---
    private Button attackBtn;
    private Button advanceBtn;
    private Button saveBtn;
    private Button potionBtn;

    @Override
    public void start(Stage primaryStage) {
        this.controller = new GameController(new JsonPersistenceManager());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #1a1a2e;");

        root.setLeft(buildPlayerPanel());
        root.setCenter(buildLogPanel());
        root.setRight(buildRoomPanel());
        root.setBottom(buildButtonBar());

        setGameButtonsDisabled(true);

        Scene scene = new Scene(root, 980, 560);
        primaryStage.setTitle("Dungeon RPG");
        primaryStage.setScene(scene);
        primaryStage.show();

        showMessage("=== Dungeon RPG ===");
        showMessage("Clicca 'Nuovo Gioco' per iniziare o 'Carica' per riprendere.");
    }

    // -------------------------
    // Costruzione pannelli
    // -------------------------

    private VBox buildPlayerPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(200);
        panel.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");

        Label title = styledLabel("PERSONAGGIO", "-fx-font-weight: bold; -fx-text-fill: #e94560; -fx-font-size: 13;");

        playerNameLabel = styledLabel("---", "-fx-text-fill: #eaeaea; -fx-font-size: 14; -fx-font-weight: bold;");
        playerClassLabel = styledLabel("", "-fx-text-fill: #a8a8b3; -fx-font-size: 11;");

        // HP bar
        hpTextLabel = styledLabel("HP: --/--", "-fx-text-fill: #eaeaea; -fx-font-size: 11;");
        StackPane hpBarContainer = new StackPane();
        Rectangle hpBarBg = new Rectangle(180, 16);
        hpBarBg.setFill(Color.web("#0f3460"));
        hpBarBg.setArcWidth(8); hpBarBg.setArcHeight(8);
        hpBarFill = new Rectangle(180, 16);
        hpBarFill.setFill(Color.web("#4caf50"));
        hpBarFill.setArcWidth(8); hpBarFill.setArcHeight(8);
        hpBarContainer.getChildren().addAll(hpBarBg, hpBarFill);
        hpBarContainer.setAlignment(Pos.CENTER_LEFT);

        staminaLabel  = styledLabel("Stamina: --/--",  "-fx-text-fill: #a8a8b3; -fx-font-size: 11;");
        agilityLabel  = styledLabel("Agilita': --",    "-fx-text-fill: #a8a8b3; -fx-font-size: 11;");

        Label invTitle = styledLabel("INVENTARIO", "-fx-font-weight: bold; -fx-text-fill: #e94560; -fx-font-size: 12;");
        VBox.setMargin(invTitle, new Insets(10, 0, 0, 0));

        inventoryList = new ListView<>();
        inventoryList.setPrefHeight(160);
        inventoryList.setStyle("-fx-background-color: #0f3460; -fx-control-inner-background: #0f3460;"
                + "-fx-text-fill: #eaeaea;");
        inventoryList.setPlaceholder(new Label("Inventario vuoto"));

        panel.getChildren().addAll(
                title, playerNameLabel, playerClassLabel,
                hpTextLabel, hpBarContainer,
                staminaLabel, agilityLabel,
                invTitle, inventoryList
        );
        return panel;
    }

    private VBox buildLogPanel() {
        VBox panel = new VBox(6);
        panel.setPadding(new Insets(0, 10, 0, 10));

        Label title = styledLabel("LOG DI GIOCO",
                "-fx-font-weight: bold; -fx-text-fill: #e94560; -fx-font-size: 13;");

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(420);
        logArea.setStyle("-fx-control-inner-background: #0f3460; -fx-text-fill: #eaeaea;"
                + "-fx-font-family: monospace; -fx-font-size: 12;");
        VBox.setVgrow(logArea, Priority.ALWAYS);

        panel.getChildren().addAll(title, logArea);
        return panel;
    }

    private VBox buildRoomPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(200);
        panel.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");

        Label title = styledLabel("STANZA",
                "-fx-font-weight: bold; -fx-text-fill: #e94560; -fx-font-size: 13;");

        roomNameLabel = styledLabel("---",
                "-fx-text-fill: #eaeaea; -fx-font-weight: bold; -fx-font-size: 12;");
        roomNameLabel.setWrapText(true);

        roomDescLabel = styledLabel("",
                "-fx-text-fill: #a8a8b3; -fx-font-size: 10;");
        roomDescLabel.setWrapText(true);

        Label enemyTitle = styledLabel("NEMICI",
                "-fx-font-weight: bold; -fx-text-fill: #e94560; -fx-font-size: 12;");
        VBox.setMargin(enemyTitle, new Insets(10, 0, 0, 0));

        enemyList = new ListView<>();
        enemyList.setPrefHeight(200);
        enemyList.setStyle("-fx-background-color: #0f3460; -fx-control-inner-background: #0f3460;"
                + "-fx-text-fill: #eaeaea;");
        enemyList.setPlaceholder(new Label("Nessun nemico"));

        panel.getChildren().addAll(title, roomNameLabel, roomDescLabel, enemyTitle, enemyList);
        return panel;
    }

    private HBox buildButtonBar() {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(12, 0, 0, 0));
        bar.setAlignment(Pos.CENTER);

        Button newGameBtn = actionButton("Nuovo Gioco", "#4caf50");
        newGameBtn.setOnAction(e -> startNewGame());

        Button loadBtn = actionButton("Carica", "#2196f3");
        loadBtn.setOnAction(e -> handleLoad());

        attackBtn = actionButton("Attacca", "#e94560");
        attackBtn.setOnAction(e -> handleAttack());

        advanceBtn = actionButton("Avanza >>>", "#ff9800");
        advanceBtn.setOnAction(e -> handleAdvance());

        potionBtn = actionButton("Usa Pozione", "#9c27b0");
        potionBtn.setOnAction(e -> handlePotion());

        saveBtn = actionButton("Salva", "#607d8b");
        saveBtn.setOnAction(e -> handleSave());

        bar.getChildren().addAll(newGameBtn, loadBtn, attackBtn, advanceBtn, potionBtn, saveBtn);
        return bar;
    }

    // -------------------------
    // Helpers stile
    // -------------------------

    private Label styledLabel(String text, String style) {
        Label l = new Label(text);
        l.setStyle(style);
        return l;
    }

    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;"
                + "-fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16;");
        return btn;
    }

    // -------------------------
    // Logica azioni
    // -------------------------

    private void startNewGame() {
        Optional<PlayerCharacter> choice = showClassChoiceDialog();
        if (choice.isEmpty()) return;
        controller.startNewGame(choice.get());
        setGameButtonsDisabled(false);
        showMessage("\n--- Nuova partita iniziata con " + controller.getPlayer().getCharacterClass() + "! ---");
        showMessage(controller.getCurrentRoom().getName() + ": " + controller.getCurrentRoom().getDescription());
        refreshRoomPanel();
        updateGameView();
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
        if (!controller.hasSavedGame()) { showMessage("Nessun salvataggio trovato!"); return; }
        controller.loadGame();
        setGameButtonsDisabled(false);
        showMessage("\n--- Partita caricata! ---");
        showMessage(controller.getCurrentRoom().getName() + ": " + controller.getCurrentRoom().getDescription());
        refreshRoomPanel();
        updateGameView();
    }

    private void handleAttack() {
        if (controller.getGameState() == null) { showMessage("Avvia una nuova partita!"); return; }
        if (controller.getGameState().isGameOver()) { showMessage("La partita e' terminata."); return; }
        var aliveEnemies = controller.getCurrentRoom().getEnemies().stream()
                .filter(e -> e.isAlive()).toList();
        if (aliveEnemies.isEmpty()) { showMessage("Nessun nemico vivo. Avanza!"); return; }
        var enemy = aliveEnemies.get(0);
        int dmg = controller.playerAttack(enemy);
        showMessage("[ATK] Attacchi " + enemy.getName() + " per " + dmg + " danni!");
        if (!enemy.isAlive()) {
            showMessage("[KILL] " + enemy.getName() + " sconfitto!");
            controller.checkRoomCleared();
            if (controller.getGameState().isVictory()) {
                showMessage("\n*** HAI VINTO! Dungeon completato! ***");
                setGameButtonsDisabled(true);
            }
        } else {
            int enmDmg = controller.enemyAttack(enemy);
            showMessage("[DMG] " + enemy.getName() + " ti colpisce per " + enmDmg + " danni!");
            if (controller.checkPlayerDead()) {
                showMessage("\n*** SEI MORTO! Game Over. ***");
                setGameButtonsDisabled(true);
            }
        }
        refreshRoomPanel();
        updateGameView();
    }

    private void handleAdvance() {
        if (controller.getGameState() == null) { showMessage("Avvia una nuova partita!"); return; }
        boolean advanced = controller.advanceRoom();
        if (advanced) {
            showMessage("\n[>>>] Avanzi nella prossima stanza...");
            showMessage(controller.getCurrentRoom().getName() + ": " + controller.getCurrentRoom().getDescription());
        } else {
            showMessage("[!] Non puoi avanzare. Sconfiggi tutti i nemici prima!");
        }
        refreshRoomPanel();
        updateGameView();
    }

    private void handlePotion() {
        if (controller.getGameState() == null) { showMessage("Avvia una nuova partita!"); return; }
        boolean used = controller.useFirstPotion();
        if (used) {
            showMessage("[HEAL] Pozione usata! HP: " + controller.getPlayer().getCurrentHp()
                    + "/" + controller.getPlayer().getMaxHp());
        } else {
            showMessage("[!] Nessuna pozione nell'inventario!");
        }
        updateGameView();
    }

    private void handleSave() {
        if (controller.getGameState() == null) { showMessage("Nessuna partita da salvare!"); return; }
        controller.saveGame();
        showMessage("[SAVE] Partita salvata!");
    }

    // -------------------------
    // Aggiornamento UI
    // -------------------------

    /**
     * Aggiorna il pannello del personaggio: nome, classe, HP bar, stamina, agilita', inventario.
     * Chiamato dopo ogni azione che modifica lo stato del giocatore.
     */
    @Override
    public void updateGameView() {
        if (controller.getGameState() == null) return;
        var player = controller.getPlayer();

        playerNameLabel.setText(player.getName());
        playerClassLabel.setText("Classe: " + player.getCharacterClass());
        hpTextLabel.setText("HP: " + player.getCurrentHp() + " / " + player.getMaxHp());
        staminaLabel.setText("Stamina: " + player.getCurrentStamina() + " / " + player.getMaxStamina());
        agilityLabel.setText("Agilita': " + player.getAgility());

        // HP bar: calcola larghezza e colore in base alla percentuale
        double hpPct = (double) player.getCurrentHp() / player.getMaxHp();
        double barWidth = Math.max(0, 180 * hpPct);
        hpBarFill.setWidth(barWidth);
        if (hpPct > 0.60) hpBarFill.setFill(Color.web("#4caf50"));      // verde
        else if (hpPct > 0.30) hpBarFill.setFill(Color.web("#ff9800")); // arancione
        else hpBarFill.setFill(Color.web("#e94560"));                   // rosso

        // Inventario: mostra nome di ogni oggetto
        List<Item> inv = player.getInventory();
        inventoryList.getItems().setAll(
                inv.stream().map(Item::getName).toList()
        );
    }

    /**
     * Aggiorna il pannello della stanza: nome, descrizione e lista nemici vivi.
     */
    private void refreshRoomPanel() {
        if (controller.getGameState() == null) return;
        var room = controller.getCurrentRoom();
        roomNameLabel.setText(room.getName());
        roomDescLabel.setText(room.getDescription());
        enemyList.getItems().setAll(
                room.getEnemies().stream()
                        .filter(e -> e.isAlive())
                        .map(e -> e.getName() + " (HP: " + e.getCurrentHp() + ")")
                        .toList()
        );
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
}
