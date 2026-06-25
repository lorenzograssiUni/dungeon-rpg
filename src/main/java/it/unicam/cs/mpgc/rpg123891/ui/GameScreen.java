package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.CombatController;
import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;

public class GameScreen {

    private static final double WIN_W       = 980;
    private static final double WIN_H       = 620;
    private static final int    GRID_SIZE   = 24;
    private static final double GRID_OPACITY = 0.06;
    private static final String BG          = "#212121";

    private final BorderPane     root;
    private final GameController gc;
    private final Stage          stage;
    private final FxApp          app;
    private final CombatController combatController;
    private final EquipmentManager equipmentManager;

    private Enemy  selectedEnemy  = null;
    private String lastLoggedWave = null;

    public GameScreen(GameController gc, Stage stage, FxApp app) {
        this.gc    = gc;
        this.stage = stage;
        this.app   = app;
        this.root  = new BorderPane();

        this.equipmentManager = new EquipmentManager((GameCharacter) gc.getPlayer());

        combatController = new CombatController(gc, gc.getGameState().getDungeonMap());
        combatController.setListener(new CombatController.CombatListener() {
            public void onEvent(String msg) {}
            public void onTurnEnd(List<String> log, boolean dead, boolean cleared) {}
        });

        buildLayout();
    }

    public BorderPane getRoot() { return root; }

    private void buildLayout() {
        Canvas grid = new Canvas(WIN_W, WIN_H);
        drawGrid(grid);

        StackPane stack = new StackPane(grid);
        stack.setStyle("-fx-background-color:" + BG + ";");
        root.setCenter(stack);
    }

    private void drawGrid(Canvas canvas) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web(BG));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.setStroke(Color.web("#ffffff", GRID_OPACITY));
        g.setLineWidth(1);
        for (double x = 0; x <= canvas.getWidth(); x += GRID_SIZE)
            g.strokeLine(x, 0, x, canvas.getHeight());
        for (double y = 0; y <= canvas.getHeight(); y += GRID_SIZE)
            g.strokeLine(0, y, canvas.getWidth(), y);
    }
}
