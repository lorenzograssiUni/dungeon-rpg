package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.CombatController;
import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;

public class GameScreen {

    // ── Dimensioni ──────────────────────────────────────────────────────────
    private static final double WIN_W        = 980;
    private static final double WIN_H        = 640;
    private static final double COL_LEFT     = 420;
    private static final double COL_MID      = 330;
    private static final double COL_RIGHT    = 192;
    private static final double ROW_TOP      = 300;
    private static final double ROW_BOT      = 240;
    private static final double SYS_H        =  32;
    private static final double GAP          =   8;
    private static final double PAD          =   8;
    private static final double RADIUS       =  10;

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final String BG           = "#212121";
    private static final String CARD_BG      = "#140E2C";
    private static final String BORDER       = "#9E6554";
    private static final double GRID_OPACITY = 0.06;
    private static final int    GRID_SIZE    = 24;

    private final BorderPane     root;
    private final GameController gc;
    private final Stage          stage;
    private final FxApp          app;
    private final CombatController combatController;
    private final EquipmentManager equipmentManager;

    private Enemy  selectedEnemy  = null;
    private String lastLoggedWave = null;

    // ── Pane contenuto card (vuoti — riempiti negli step successivi) ───────────
    private final StackPane paneEncounter  = new StackPane();
    private final VBox      paneEnemyStats = new VBox();
    private final VBox      paneCharacter  = new VBox();
    private final VBox      paneAction     = new VBox();
    private final StackPane paneMiniMap    = new StackPane();
    private final VBox      paneStatus     = new VBox();
    private final VBox      paneLog        = new VBox();

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

    // =========================================================================
    // LAYOUT
    //
    //  StackPane (root)
    //   ├─ Canvas bgCanvas      ← sfondo #212121 + griglia  (layer 0)
    //   ├─ VBox mainBox         ← card layout               (layer 1)
    //   └─ Canvas gridOverlay   ← stessa griglia, clip card  (layer 2)
    //
    // La griglia viene disegnata due volte:
    //   - sotto le card (bgCanvas)
    //   - sopra le card ma DENTRO il clip di ogni card (gridOverlay),
    //     così i bordi restano visibili.
    // =========================================================================
    private void buildLayout() {

        // Layer 0: sfondo pieno + griglia base
        Canvas bgCanvas = new Canvas(WIN_W, WIN_H);
        drawGrid(bgCanvas);
        bgCanvas.setMouseTransparent(true);

        // ── Calcolo altezze colonna destra ─────────────────────────────────────
        double mapH    = Math.round(ROW_TOP * 0.58);
        double statusH = ROW_TOP - mapH - GAP;

        // Layer 1: card
        StackPane cardEncounter  = makeCard(paneEncounter,  COL_LEFT,  ROW_TOP);
        StackPane cardCharacter  = makeCard(paneCharacter,  COL_MID,   ROW_TOP);
        StackPane cardMiniMap    = makeCard(paneMiniMap,    COL_RIGHT, mapH);
        StackPane cardStatus     = makeCard(paneStatus,     COL_RIGHT, statusH);
        StackPane cardEnemyStats = makeCard(paneEnemyStats, COL_LEFT,  ROW_BOT);
        StackPane cardAction     = makeCard(paneAction,     COL_MID,   ROW_BOT);
        StackPane cardLog        = makeCard(paneLog,        COL_RIGHT, ROW_BOT);

        VBox rightTop = new VBox(GAP, cardMiniMap, cardStatus);
        rightTop.setPrefSize(COL_RIGHT, ROW_TOP);
        rightTop.setMinSize(COL_RIGHT, ROW_TOP);
        rightTop.setMaxSize(COL_RIGHT, ROW_TOP);

        HBox rowTop = new HBox(GAP, cardEncounter, cardCharacter, rightTop);
        rowTop.setAlignment(Pos.TOP_LEFT);

        HBox rowBot = new HBox(GAP, cardEnemyStats, cardAction, cardLog);
        rowBot.setAlignment(Pos.TOP_LEFT);

        // Barra di sistema
        Region sysBar = new Region();
        sysBar.setPrefHeight(SYS_H);
        sysBar.setStyle("-fx-background-color:#0a0a0a;");

        VBox mainBox = new VBox(GAP, rowTop, rowBot, sysBar);
        mainBox.setPadding(new Insets(PAD));
        mainBox.setStyle("-fx-background-color:transparent;");

        // Layer 2: griglia sovrapposta alle card (clippata dentro ogni card)
        Canvas gridOverlay = buildGridOverlay(
            new double[][]{
                {PAD,                    PAD,                   COL_LEFT,  ROW_TOP},
                {PAD + COL_LEFT + GAP,   PAD,                   COL_MID,   ROW_TOP},
                {PAD + COL_LEFT + GAP + COL_MID + GAP, PAD,    COL_RIGHT, mapH},
                {PAD + COL_LEFT + GAP + COL_MID + GAP, PAD + mapH + GAP, COL_RIGHT, statusH},
                {PAD,                    PAD + ROW_TOP + GAP,   COL_LEFT,  ROW_BOT},
                {PAD + COL_LEFT + GAP,   PAD + ROW_TOP + GAP,   COL_MID,   ROW_BOT},
                {PAD + COL_LEFT + GAP + COL_MID + GAP, PAD + ROW_TOP + GAP, COL_RIGHT, ROW_BOT}
            }
        );
        gridOverlay.setMouseTransparent(true);

        // Assembla i layer
        StackPane stack = new StackPane(bgCanvas, mainBox, gridOverlay);
        stack.setAlignment(Pos.TOP_LEFT);
        stack.setStyle("-fx-background-color:" + BG + ";");
        root.setCenter(stack);
    }

    // ── Crea una card con sfondo #140E2C e bordi stondati #9E6554 ───────────────
    private StackPane makeCard(Region content, double w, double h) {
        content.setPrefSize(w, h);
        content.setMinSize(w, h);
        content.setMaxSize(w, h);

        StackPane card = new StackPane(content);
        card.setPrefSize(w, h);
        card.setMinSize(w, h);
        card.setMaxSize(w, h);
        card.setStyle(
            "-fx-background-color:" + CARD_BG + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:2;" +
            "-fx-border-radius:" + RADIUS + ";" +
            "-fx-background-radius:" + RADIUS + ";"
        );
        return card;
    }

    // ── Sfondo + griglia base ───────────────────────────────────────────────
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

    /**
     * Crea un Canvas trasparente grande quanto la finestra.
     * Per ogni card disegna la griglia SOLO dentro un rettangolo
     * leggermente più piccolo del bordo (RADIUS di margine),
     * così il bordo stondato rimane visibile sopra la griglia.
     *
     * @param cards array di {x, y, w, h} per ogni card
     */
    private Canvas buildGridOverlay(double[][] cards) {
        Canvas canvas = new Canvas(WIN_W, WIN_H);
        GraphicsContext g = canvas.getGraphicsContext2D();

        for (double[] c : cards) {
            double cx = c[0], cy = c[1], cw = c[2], ch = c[3];
            double inset = 3;  // margine interno rispetto al bordo

            // Clip a rettangolo arrotondato dentro la card
            g.save();
            g.beginPath();
            g.moveTo(cx + inset + RADIUS, cy + inset);
            g.arcTo(cx + cw - inset, cy + inset,     cx + cw - inset, cy + ch - inset, RADIUS);
            g.arcTo(cx + cw - inset, cy + ch - inset, cx + inset,     cy + ch - inset, RADIUS);
            g.arcTo(cx + inset,      cy + ch - inset, cx + inset,     cy + inset,      RADIUS);
            g.arcTo(cx + inset,      cy + inset,      cx + cw - inset, cy + inset,     RADIUS);
            g.closePath();
            g.clip();

            // Disegna la griglia dentro il clip
            g.setStroke(Color.web("#ffffff", GRID_OPACITY));
            g.setLineWidth(1);

            // Allinea la griglia alla posizione assoluta della finestra
            double startX = cx - (cx % GRID_SIZE);
            double startY = cy - (cy % GRID_SIZE);
            for (double x = startX; x <= cx + cw; x += GRID_SIZE)
                g.strokeLine(x, cy + inset, x, cy + ch - inset);
            for (double y = startY; y <= cy + ch; y += GRID_SIZE)
                g.strokeLine(cx + inset, y, cx + cw - inset, y);

            g.restore();
        }
        return canvas;
    }
}
