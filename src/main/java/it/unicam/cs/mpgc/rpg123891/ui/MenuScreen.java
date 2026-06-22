package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Schermata principale: Nuova Partita, Carica, Esci.
 * In Nuova Partita mostra scelta classe + campo nome.
 */
public class MenuScreen {

    private final VBox root;
    private final GameController gc;
    private final Stage stage;
    private final FxApp app;

    public MenuScreen(GameController gc, Stage stage, FxApp app) {
        this.gc    = gc;
        this.stage = stage;
        this.app   = app;
        this.root  = build();
    }

    public VBox getRoot() { return root; }

    private VBox build() {
        VBox vb = new VBox(18);
        vb.setAlignment(Pos.CENTER);
        vb.setPadding(new Insets(60));
        vb.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("DUNGEON RPG");
        title.setFont(Font.font("System", FontWeight.BOLD, 36));
        title.setStyle("-fx-text-fill: #e0c46c;");

        Button btnNew  = menuBtn("Nuova Partita");
        Button btnLoad = menuBtn("Carica Partita");
        Button btnExit = menuBtn("Esci");

        btnLoad.setDisable(!gc.hasSavedGame());

        btnNew.setOnAction(e -> showClassChoice());
        btnLoad.setOnAction(e -> {
            gc.loadGame();
            app.showGame(stage);
        });
        btnExit.setOnAction(e -> stage.close());

        vb.getChildren().addAll(title, btnNew, btnLoad, btnExit);
        return vb;
    }

    private void showClassChoice() {
        VBox vb = new VBox(14);
        vb.setAlignment(Pos.CENTER);
        vb.setPadding(new Insets(40));
        vb.setStyle("-fx-background-color: #1a1a2e;");

        Label lbl = new Label("Scegli la tua classe");
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setStyle("-fx-text-fill: #e0c46c;");

        ToggleGroup tg = new ToggleGroup();
        RadioButton rbW = classRadio("Guerriero  HP:120 ATK:22 DEF:8  AGI:4  STA:8  | Passive: blocco 20%", tg);
        RadioButton rbM = classRadio("Mago       HP:75  ATK:15 DEF:4  AGI:6  STA:10 | Passive: scudo magico", tg);
        RadioButton rbT = classRadio("Ladro      HP:90  ATK:18 DEF:6  AGI:8  STA:12 | Passive: primo attacco critico", tg);
        rbW.setSelected(true);

        TextField nameField = new TextField();
        nameField.setPromptText("Nome personaggio (invio = Eroe)");
        nameField.setMaxWidth(300);
        nameField.setStyle("-fx-background-color:#2a2a4e;-fx-text-fill:white;");

        Button btnOk = menuBtn("Inizia");
        btnOk.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) name = "Eroe";
            PlayerCharacter player;
            if (rbM.isSelected())      player = new Mage(name);
            else if (rbT.isSelected()) player = new Thief(name);
            else                       player = new Warrior(name);
            gc.startNewGame(player);
            app.showGame(stage);
        });

        Button btnBack = menuBtn("Indietro");
        btnBack.setOnAction(e -> app.showMenu(stage));

        vb.getChildren().addAll(lbl, rbW, rbM, rbT, nameField, btnOk, btnBack);
        stage.getScene().setRoot(vb);
    }

    private Button menuBtn(String text) {
        Button b = new Button(text);
        b.setPrefWidth(280);
        b.setStyle("-fx-background-color:#2a2a4e;-fx-text-fill:#e0c46c;" +
                   "-fx-font-size:15px;-fx-cursor:hand;");
        return b;
    }

    private RadioButton classRadio(String text, ToggleGroup tg) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(tg);
        rb.setStyle("-fx-text-fill:#cccccc;-fx-font-size:12px;");
        return rb;
    }
}
