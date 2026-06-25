package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;

public class GameScreen {

    private static final double COL_LEFT     = 390;
    private static final double COL_MID      = 310;
    private static final double COL_RIGHT    = 280;
    private static final double ROW_TOP      = 280;
    private static final double ROW_BOT      = 220;
    private static final double SYS_H        =  44;
    private static final double GAP          =   8;
    private static final double PAD          =  20;  // margine bordo finestra
    private static final double RADIUS       =  10;
    private static final int    BORDER_W     =   4;
    private static final double LABEL_H      =  28;
    private static final double LABEL_OFFSET =  14;
    private static final int    FONT_SIZE    =  11;

    // Dimensioni finestra calcolate dal contenuto
    static final double WIN_W =
        COL_LEFT + GAP + COL_MID + GAP + COL_RIGHT + PAD * 2;
    static final double WIN_H =
        PAD + LABEL_OFFSET + ROW_TOP + GAP + LABEL_OFFSET + ROW_BOT + GAP + LABEL_OFFSET + SYS_H + PAD;

    private static final String BG           = "#212121";
    private static final String CARD_BG      = "#140E2C";
    private static final String SYS_BG       = "#000000";
    private static final String BORDER       = "#9E6554";
    private static final String LABEL_FG     = "#D4A96A";
    private static final String SYS_TEXT     = "#ffffff";
    private static final double GRID_OPACITY = 0.06;
    private static final int    GRID_SIZE    = 24;

    private Font pixelFont;
    private Font pixelFontSmall;

    private final BorderPane     root;
    private final GameController gc;
    private final Stage          stage;
    private final FxApp          app;

    private final StackPane paneEncounter  = new StackPane();
    private final VBox      paneEnemyStats = new VBox();
    private final VBox      paneCharacter  = new VBox();
    private final VBox      paneAction     = new VBox();
    private final VBox      paneRightTop   = new VBox();
    private final VBox      paneLog        = new VBox();

    public GameScreen(GameController gc, Stage stage, FxApp app) {
        this.gc    = gc;
        this.stage = stage;
        this.app   = app;
        this.root  = new BorderPane();
        loadFont();
        buildLayout();
    }

    public BorderPane getRoot() { return root; }

    private void loadFont() {
        try (InputStream is = getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf")) {
            if (is != null) {
                pixelFont      = Font.loadFont(is, FONT_SIZE);
                pixelFontSmall = Font.loadFont(
                    getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), 8);
            }
        } catch (Exception ignored) {}
        if (pixelFont      == null) pixelFont      = Font.font("Courier New", FontWeight.BOLD, FONT_SIZE);
        if (pixelFontSmall == null) pixelFontSmall = Font.font("Courier New", FontWeight.BOLD, 8);
    }

    private void buildLayout() {

        Canvas bgCanvas = new Canvas(WIN_W, WIN_H);
        drawGrid(bgCanvas);
        bgCanvas.setMouseTransparent(true);

        // ── 6 card principali
        StackPane cardEncounter  = makeCardWithTitle("ENCOUNTER",  paneEncounter,  COL_LEFT,  ROW_TOP);
        StackPane cardCharacter  = makeCardWithTitle("CHARACTER",  paneCharacter,  COL_MID,   ROW_TOP);
        StackPane cardRightTop   = makeCardWithTitle("Map",         paneRightTop,   COL_RIGHT, ROW_TOP);
        StackPane cardEnemyStats = makeCardWithTitle("ENEMY STATS", paneEnemyStats, COL_LEFT,  ROW_BOT);
        StackPane cardAction     = makeCardWithTitle("ACTION",      paneAction,     COL_MID,   ROW_BOT);
        StackPane cardLog        = makeCardWithTitle("COMBAT LOG",  paneLog,        COL_RIGHT, ROW_BOT);

        double rowTopH = ROW_TOP + LABEL_OFFSET;
        double rowBotH = ROW_BOT + LABEL_OFFSET;

        HBox rowTop = new HBox(GAP, cardEncounter, cardCharacter, cardRightTop);
        rowTop.setAlignment(Pos.BOTTOM_LEFT);
        rowTop.setPrefHeight(rowTopH); rowTop.setMinHeight(rowTopH); rowTop.setMaxHeight(rowTopH);

        HBox rowBot = new HBox(GAP, cardEnemyStats, cardAction, cardLog);
        rowBot.setAlignment(Pos.BOTTOM_LEFT);
        rowBot.setPrefHeight(rowBotH); rowBot.setMinHeight(rowBotH); rowBot.setMaxHeight(rowBotH);

        // ── SYSTEM INFO card
        double totalW = COL_LEFT + GAP + COL_MID + GAP + COL_RIGHT;

        Label sysContent = new Label("DUNGEON RPG  v1.0  —  by Lorenzo Grassi");
        sysContent.setFont(pixelFontSmall);
        sysContent.setStyle("-fx-text-fill:" + SYS_TEXT + ";");
        sysContent.setAlignment(Pos.CENTER);
        sysContent.setMaxWidth(Double.MAX_VALUE);

        StackPane sysInner = new StackPane(sysContent);
        sysInner.setPrefSize(totalW, SYS_H);
        sysInner.setMinSize(totalW, SYS_H);
        sysInner.setMaxSize(totalW, SYS_H);

        StackPane sysCard = new StackPane(sysInner);
        sysCard.setPrefSize(totalW, SYS_H);
        sysCard.setMinSize(totalW, SYS_H);
        sysCard.setMaxSize(totalW, SYS_H);
        sysCard.setStyle(
            "-fx-background-color:" + SYS_BG + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:" + BORDER_W + " " + BORDER_W + " 0 " + BORDER_W + ";" +
            "-fx-border-radius:" + RADIUS + " " + RADIUS + " 0 0;" +
            "-fx-background-radius:" + RADIUS + " " + RADIUS + " 0 0;"
        );

        Label sysTitle = new Label("  SYSTEM INFO  ");
        sysTitle.setFont(pixelFont);
        sysTitle.setPrefHeight(LABEL_H);
        sysTitle.setStyle(
            "-fx-text-fill:" + LABEL_FG + ";" +
            "-fx-background-color:" + SYS_BG + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:" + BORDER_W + ";" +
            "-fx-border-radius:6;-fx-background-radius:6;" +
            "-fx-padding:3 12;"
        );

        StackPane sysWrapper = new StackPane();
        sysWrapper.setPrefSize(totalW, SYS_H + LABEL_OFFSET);
        sysWrapper.setMinSize(totalW, SYS_H + LABEL_OFFSET);
        sysWrapper.setMaxSize(totalW, SYS_H + LABEL_OFFSET);
        StackPane.setAlignment(sysCard,  Pos.BOTTOM_CENTER);
        StackPane.setAlignment(sysTitle, Pos.TOP_CENTER);
        sysWrapper.getChildren().addAll(sysCard, sysTitle);

        // ── Main VBox — padding PAD su tutti i lati tranne bottom (sysBar tocca il fondo)
        VBox mainBox = new VBox(GAP, rowTop, rowBot, sysWrapper);
        mainBox.setPadding(new Insets(PAD + LABEL_OFFSET, PAD, 0, PAD));
        mainBox.setStyle("-fx-background-color:transparent;");

        double fullW = totalW + PAD * 2;
        double fullH = WIN_H;
        mainBox.setMaxSize(fullW, fullH);
        mainBox.setMinSize(fullW, fullH);
        StackPane.setAlignment(mainBox, Pos.BOTTOM_CENTER);

        // ── Grid overlay
        double xOff = PAD;
        double yTop2 = PAD + LABEL_OFFSET;
        double yBot2 = yTop2 + ROW_TOP + GAP + LABEL_OFFSET;
        double ySys  = yBot2 + ROW_BOT + GAP + LABEL_OFFSET;

        Canvas gridOverlay = buildGridOverlay(new double[][]{
            {xOff,                               yTop2, COL_LEFT,  ROW_TOP},
            {xOff + COL_LEFT + GAP,               yTop2, COL_MID,   ROW_TOP},
            {xOff + COL_LEFT + GAP + COL_MID + GAP, yTop2, COL_RIGHT, ROW_TOP},
            {xOff,                               yBot2, COL_LEFT,  ROW_BOT},
            {xOff + COL_LEFT + GAP,               yBot2, COL_MID,   ROW_BOT},
            {xOff + COL_LEFT + GAP + COL_MID + GAP, yBot2, COL_RIGHT, ROW_BOT},
            {xOff,                               ySys,  totalW,    SYS_H}
        });
        gridOverlay.setMouseTransparent(true);

        StackPane stack = new StackPane(bgCanvas, mainBox, gridOverlay);
        stack.setAlignment(Pos.BOTTOM_CENTER);
        stack.setStyle("-fx-background-color:" + BG + ";");
        root.setCenter(stack);
    }

    private StackPane makeCardWithTitle(String title, Region content, double w, double h) {
        content.setPrefSize(w, h); content.setMinSize(w, h); content.setMaxSize(w, h);
        StackPane card = new StackPane(content);
        card.setPrefSize(w, h); card.setMinSize(w, h); card.setMaxSize(w, h);
        card.setStyle(
            "-fx-background-color:" + CARD_BG + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:" + BORDER_W + ";" +
            "-fx-border-radius:" + RADIUS + ";" +
            "-fx-background-radius:" + RADIUS + ";"
        );
        Label lbl = new Label("  " + title + "  ");
        lbl.setFont(pixelFont); lbl.setPrefHeight(LABEL_H);
        lbl.setStyle(
            "-fx-text-fill:" + LABEL_FG + ";" +
            "-fx-background-color:" + CARD_BG + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:" + BORDER_W + ";" +
            "-fx-border-radius:6;-fx-background-radius:6;" +
            "-fx-padding:3 12;"
        );
        StackPane wrapper = new StackPane();
        wrapper.setPrefSize(w, h + LABEL_OFFSET);
        wrapper.setMinSize(w, h + LABEL_OFFSET);
        wrapper.setMaxSize(w, h + LABEL_OFFSET);
        StackPane.setAlignment(card, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(lbl,  Pos.TOP_CENTER);
        wrapper.getChildren().addAll(card, lbl);
        return wrapper;
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

    private Canvas buildGridOverlay(double[][] cards) {
        Canvas canvas = new Canvas(WIN_W, WIN_H);
        GraphicsContext g = canvas.getGraphicsContext2D();
        for (double[] c : cards) {
            double cx = c[0], cy = c[1], cw = c[2], ch = c[3];
            double inset = BORDER_W + 1;
            g.save();
            g.beginPath();
            g.moveTo(cx + inset + RADIUS, cy + inset);
            g.arcTo(cx + cw - inset, cy + inset,      cx + cw - inset, cy + ch - inset, RADIUS);
            g.arcTo(cx + cw - inset, cy + ch - inset, cx + inset,      cy + ch - inset, RADIUS);
            g.arcTo(cx + inset,      cy + ch - inset, cx + inset,      cy + inset,      RADIUS);
            g.arcTo(cx + inset,      cy + inset,      cx + cw - inset, cy + inset,      RADIUS);
            g.closePath();
            g.clip();
            g.setStroke(Color.web("#ffffff", GRID_OPACITY));
            g.setLineWidth(1);
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
