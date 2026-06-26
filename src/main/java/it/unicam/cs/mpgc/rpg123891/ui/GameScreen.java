package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipSlot;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipmentManager;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScreen {

    private static final double COL_LEFT     = 390;
    private static final double COL_MID      = 310;
    private static final double COL_RIGHT    = 280;
    private static final double ROW_TOP      = 280;
    private static final double ROW_BOT      = 220;
    private static final double SYS_H        =  44;
    private static final double GAP          =   8;
    private static final double PAD          =  20;
    private static final double RADIUS       =  10;
    private static final int    BORDER_W     =   4;
    private static final double LABEL_H      =  28;
    private static final double LABEL_OFFSET =  14;
    private static final int    FONT_SIZE    =  11;

    private static final double TOTAL_W = COL_LEFT + GAP + COL_MID + GAP + COL_RIGHT;

    public static final double WIN_W = TOTAL_W + PAD * 2;
    public static final double WIN_H =
        PAD + LABEL_OFFSET
        + ROW_TOP + GAP
        + LABEL_OFFSET + ROW_BOT + GAP
        + LABEL_OFFSET + SYS_H
        + PAD;

    private static final String BG           = "#212121";
    private static final String CARD_BG      = "#140E2C";
    private static final String SYS_BG       = "#000000";
    private static final String BORDER       = "#9E6554";
    private static final String LABEL_FG     = "#D4A96A";
    private static final String SYS_TEXT     = "#ffffff";
    private static final String WHITE        = "#cccccc";
    private static final double GRID_OPACITY = 0.06;
    private static final int    GRID_SIZE    = 24;

    private static final double ACTION_ICON_SIZE = 20;
    private static final int    ACTION_FONT_SIZE = 13;
    private static final double ICON_SIZE        = 14;

    private static final double PORTRAIT_SIZE   = 140;
    private static final double PORTRAIT_RADIUS = 12;

    private static final double ENEMY_SPRITE_H  = 130;
    private static final double ENEMY_SPRITE_W  = 110;

    // HP bar
    private static final double HP_BAR_H        = 8;
    private static final String HP_BAR_BG       = "#3a1a1a";
    private static final String HP_BAR_FG_HIGH  = "#4caf50";
    private static final String HP_BAR_FG_MID   = "#ff9800";
    private static final String HP_BAR_FG_LOW   = "#f44336";
    private static final String BADGE_STUN      = "#a855f7";
    private static final String BADGE_IMMUNE    = "#2196f3";
    private static final String BADGE_BOSS      = "#c0392b";
    private static final String BADGE_EGG       = "#78909c";

    // Larghezza utile della card ENEMY STATS
    private static final double ENEMY_ROW_W = COL_LEFT - 28.0;

    private Font pixelFont;
    private Font pixelFontSmall;
    private Font pixelFontAction;
    private Font pixelFontTiny;

    private final BorderPane       root;
    private final GameController   gc;
    private final Stage            stage;
    private final FxApp            app;
    private final EquipmentManager equipmentManager;

    private final StackPane paneEncounter  = new StackPane();
    private final VBox      paneEnemyStats = new VBox();
    private final VBox      paneCharacter  = new VBox();
    private final VBox      paneAction     = new VBox();
    private final VBox      paneRightTop   = new VBox();
    private final VBox      paneLog        = new VBox();

    private static final Map<String, List<String>> ENEMY_SPRITES = new HashMap<>();
    static {
        ENEMY_SPRITES.put("Cinghiale",        List.of("/assets/enemies/Cinghiale1.png", "/assets/enemies/cinghiale2.png"));
        ENEMY_SPRITES.put("Lupo",             List.of("/assets/enemies/lupo.png"));
        ENEMY_SPRITES.put("Goblin",           List.of("/assets/enemies/Goblin.png", "/assets/enemies/Goblin2.png"));
        ENEMY_SPRITES.put("Goblin Guardia",   List.of("/assets/enemies/goblinGuard.png", "/assets/enemies/goblinGuard2.png"));
        ENEMY_SPRITES.put("Re Goblin",        List.of("/assets/enemies/regoblin.png"));
        ENEMY_SPRITES.put("Scheletro",        List.of("/assets/enemies/scheletro.png", "/assets/enemies/scheletro2.png"));
        ENEMY_SPRITES.put("Scheletro Guardia",List.of("/assets/enemies/scheletroGuardia.png", "/assets/enemies/scheletroGuardia2.png"));
        ENEMY_SPRITES.put("Strega",           List.of("/assets/enemies/Strega.png"));
        ENEMY_SPRITES.put("Uovo",             List.of("/assets/enemies/uovo1.png", "/assets/enemies/uovo2.png"));
        ENEMY_SPRITES.put("Cucciolo Drago",   List.of("/assets/enemies/cucciolo1.png", "/assets/enemies/cucciolo2.png"));
        ENEMY_SPRITES.put("Cucciolo Uovo",    List.of("/assets/enemies/cuccioloUovo1.png", "/assets/enemies/cuccioloUovo2.png"));
        ENEMY_SPRITES.put("L'Ultimo Drago",   List.of("/assets/enemies/UltimoDrago.png"));
    }

    private static final Map<String, String> ROOM_BG = new HashMap<>();
    static {
        ROOM_BG.put("r1", "/assets/backgrounds/foresta.png");
        ROOM_BG.put("r2", "/assets/backgrounds/GoblinVillage.png");
        ROOM_BG.put("r3", "/assets/backgrounds/catacombe.png");
        ROOM_BG.put("r4", "/assets/backgrounds/Caverne.png");
        ROOM_BG.put("r5", "/assets/backgrounds/StanzaFinale.png");
    }

    public GameScreen(GameController gc, Stage stage, FxApp app) {
        this.gc               = gc;
        this.stage            = stage;
        this.app              = app;
        this.root             = new BorderPane();
        this.equipmentManager = new EquipmentManager((GameCharacter) gc.getPlayer());
        loadFont();
        buildLayout();
        buildCharacterPanel();
        buildActionPanel();
        buildEncounterPanel();
        buildEnemyStatsPanel();
        stage.setOnCloseRequest(e -> Platform.exit());
    }

    public BorderPane getRoot() { return root; }

    private void loadFont() {
        try {
            pixelFont = Font.loadFont(
                getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), FONT_SIZE);
            pixelFontSmall = Font.loadFont(
                getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), 10);
            pixelFontAction = Font.loadFont(
                getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), ACTION_FONT_SIZE);
            pixelFontTiny = Font.loadFont(
                getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), 8);
        } catch (Exception ignored) {}
        if (pixelFont       == null) pixelFont       = Font.font("Courier New", FontWeight.BOLD, FONT_SIZE);
        if (pixelFontSmall  == null) pixelFontSmall  = Font.font("Courier New", FontWeight.BOLD, 10);
        if (pixelFontAction == null) pixelFontAction = Font.font("Courier New", FontWeight.BOLD, ACTION_FONT_SIZE);
        if (pixelFontTiny   == null) pixelFontTiny   = Font.font("Courier New", FontWeight.BOLD, 8);
    }

    // ── ENCOUNTER card ────────────────────────────────────────────────────
    private void buildEncounterPanel() {
        paneEncounter.getChildren().clear();
        paneEncounter.setStyle("-fx-background-color:transparent;");

        double clipInset = BORDER_W;
        double clipW     = COL_LEFT  - clipInset * 2;
        double clipH     = ROW_TOP   - clipInset * 2;

        String roomId = gc.getCurrentRoom().getId();
        String bgPath = ROOM_BG.getOrDefault(roomId, "/assets/backgrounds/foresta.png");

        try (InputStream is = getClass().getResourceAsStream(bgPath)) {
            if (is != null) {
                Image bgImg  = new Image(is, COL_LEFT, ROW_TOP, false, true);
                ImageView bg = new ImageView(bgImg);
                bg.setFitWidth(COL_LEFT);
                bg.setFitHeight(ROW_TOP);
                bg.setPreserveRatio(false);
                bg.setOpacity(0.80);

                Rectangle clip = new Rectangle(clipInset, clipInset, clipW, clipH);
                clip.setArcWidth(RADIUS * 2);
                clip.setArcHeight(RADIUS * 2);
                bg.setClip(clip);

                StackPane.setAlignment(bg, Pos.CENTER);
                paneEncounter.getChildren().add(bg);
            }
        } catch (Exception ignored) {}

        Wave wave = gc.getCurrentRoom().getCurrentWave();
        List<Enemy> alive = wave == null ? List.of() :
            wave.getEnemies().stream().filter(Enemy::isAlive).toList();

        if (!alive.isEmpty()) {
            HBox enemyRow = buildEnemyRow(alive);
            StackPane.setAlignment(enemyRow, Pos.BOTTOM_CENTER);
            paneEncounter.getChildren().add(enemyRow);
        }
    }

    private HBox buildEnemyRow(List<Enemy> enemies) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.BOTTOM_CENTER);
        row.setPadding(new Insets(0, 8, 10, 8));

        Map<String, Integer> usageCount = new HashMap<>();
        for (Enemy enemy : enemies) {
            String name = enemy.getName();
            int used    = usageCount.getOrDefault(name, 0);
            usageCount.put(name, used + 1);

            List<String> variants = ENEMY_SPRITES.getOrDefault(name, List.of());
            if (variants.isEmpty()) continue;

            String spritePath = variants.get(used % variants.size());
            ImageView iv      = loadImage(spritePath, ENEMY_SPRITE_W, ENEMY_SPRITE_H);
            if (iv == null) continue;

            row.getChildren().add(iv);
        }
        return row;
    }

    // ── ENEMY STATS card ────────────────────────────────────────────────────
    private void buildEnemyStatsPanel() {
        paneEnemyStats.getChildren().clear();
        paneEnemyStats.setAlignment(Pos.CENTER);          // centrato verticalmente
        paneEnemyStats.setStyle("-fx-background-color:transparent;");
        paneEnemyStats.setPadding(new Insets(14, 20, 10, 20));
        paneEnemyStats.setSpacing(12);

        Wave wave = gc.getCurrentRoom().getCurrentWave();
        List<Enemy> alive = wave == null ? List.of() :
            wave.getEnemies().stream().filter(Enemy::isAlive).toList();

        if (alive.isEmpty()) {
            Label empty = new Label("No enemies");
            empty.setFont(pixelFontSmall);
            empty.setStyle("-fx-text-fill:" + WHITE + ";");
            paneEnemyStats.getChildren().add(empty);
            return;
        }

        for (Enemy enemy : alive) {
            paneEnemyStats.getChildren().add(buildEnemyStatRow(enemy));
        }
    }

    /**
     * Riga centrata per un nemico:
     *   Riga 1 (centrata): Nome  BOSS?  EGG?  STUN?  IMMUNE?
     *   Riga 2 (centrata): [HP bar full-width]
     *   Riga 3 (centrata): ATK xx  DEF xx  AGI xx  CRIT xx%
     */
    private VBox buildEnemyStatRow(Enemy enemy) {
        double rowW = ENEMY_ROW_W;

        // ─ Riga 1: nome centrato + badge ─
        HBox nameRow = new HBox(6);
        nameRow.setAlignment(Pos.CENTER);
        nameRow.setMaxWidth(rowW);

        Label nameLbl = new Label(enemy.getName());
        nameLbl.setFont(pixelFontSmall);   // più grande: 10px invece di 8px
        nameLbl.setStyle("-fx-text-fill:" + LABEL_FG + ";");

        nameRow.getChildren().add(nameLbl);
        if (enemy.isBoss())    nameRow.getChildren().add(badge("BOSS",   BADGE_BOSS));
        if (enemy.isEgg())     nameRow.getChildren().add(badge("EGG",    BADGE_EGG));
        if (enemy.isStunned()) nameRow.getChildren().add(badge("STUN",   BADGE_STUN));
        if (enemy.isImmune())  nameRow.getChildren().add(badge("IMMUNE", BADGE_IMMUNE));

        // ─ Riga 2: HP bar full-width ─
        double hpRatio  = (double) enemy.getCurrentHp() / Math.max(1, enemy.getMaxHp());
        String barColor = hpRatio > 0.5 ? HP_BAR_FG_HIGH
                        : hpRatio > 0.25 ? HP_BAR_FG_MID
                        : HP_BAR_FG_LOW;
        double fillW    = Math.max(1, hpRatio * rowW);
        StackPane hpBar = buildBar(rowW, HP_BAR_H, fillW, HP_BAR_BG, barColor);

        // ─ Riga 3: chips stats centrati ─
        HBox statsRow = new HBox(16,
            statChip("ATK", String.valueOf(enemy.getAttack())),
            statChip("DEF", String.valueOf(enemy.getDefense())),
            statChip("AGI", String.valueOf(enemy.getAgility())),
            statChip("CRIT", String.format("%.0f%%", enemy.getCritChance() * 100))
        );
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setMaxWidth(rowW);

        VBox row = new VBox(5, nameRow, hpBar, statsRow);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(rowW);
        return row;
    }

    /** Barra colorata con sfondo. */
    private StackPane buildBar(double totalW, double h, double fillW,
                               String bgColor, String fgColor) {
        Rectangle bg   = new Rectangle(totalW, h);
        bg.setFill(Color.web(bgColor));
        bg.setArcWidth(h); bg.setArcHeight(h);

        Rectangle fill = new Rectangle(fillW, h);
        fill.setFill(Color.web(fgColor));
        fill.setArcWidth(h); fill.setArcHeight(h);

        StackPane sp = new StackPane(bg, fill);
        sp.setAlignment(Pos.CENTER_LEFT);
        sp.setPrefSize(totalW, h);
        sp.setMaxSize(totalW, h);
        sp.setMinSize(totalW, h);
        return sp;
    }

    /** Chip inline centrato: label grigia sopra, valore bianco sotto. */
    private VBox statChip(String key, String val) {
        Label k = new Label(key);
        k.setFont(pixelFontTiny);
        k.setStyle("-fx-text-fill:#888888;");
        Label v = new Label(val);
        v.setFont(pixelFontSmall);   // più grande: 10px
        v.setStyle("-fx-text-fill:" + WHITE + ";");
        VBox chip = new VBox(2, k, v);
        chip.setAlignment(Pos.CENTER);
        return chip;
    }

    /** Badge colorato con testo. */
    private Label badge(String text, String color) {
        Label b = new Label(text);
        b.setFont(pixelFontTiny);
        b.setStyle(
            "-fx-text-fill:#ffffff;" +
            "-fx-background-color:" + color + ";" +
            "-fx-background-radius:3;" +
            "-fx-padding:1 4;"
        );
        return b;
    }

    // ── CHARACTER card ──────────────────────────────────────────────────────
    private void buildCharacterPanel() {
        paneCharacter.getChildren().clear();
        paneCharacter.setAlignment(Pos.TOP_LEFT);
        paneCharacter.setStyle("-fx-background-color:transparent;");
        paneCharacter.setPadding(new Insets(22, 0, 0, 12));

        GameCharacter p = player();

        StackPane portraitBox = new StackPane();
        portraitBox.setPrefSize(PORTRAIT_SIZE, PORTRAIT_SIZE);
        portraitBox.setMinSize(PORTRAIT_SIZE, PORTRAIT_SIZE);
        portraitBox.setMaxSize(PORTRAIT_SIZE, PORTRAIT_SIZE);
        portraitBox.setStyle(
            "-fx-background-color:#0d0d1f;" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:3;" +
            "-fx-border-radius:" + PORTRAIT_RADIUS + ";" +
            "-fx-background-radius:" + PORTRAIT_RADIUS + ";"
        );
        String spritePath = switch (p.getCharacterClass()) {
            case WARRIOR -> "/assets/classes/warrior.png";
            case MAGE    -> "/assets/classes/mage.png";
            case THIEF   -> "/assets/classes/thief.png";
            default      -> null;
        };
        ImageView portrait = loadImage(spritePath, PORTRAIT_SIZE - 10, PORTRAIT_SIZE - 10);
        if (portrait != null) portraitBox.getChildren().add(portrait);

        VBox statsBox = new VBox(6);
        statsBox.setAlignment(Pos.TOP_LEFT);
        statsBox.setPadding(new Insets(4, 0, 0, 10));
        statsBox.getChildren().addAll(
            statLine(p.getName(),                                                 LABEL_FG, pixelFont),
            statLine(p.getCharacterClass().toString(),                            WHITE,    pixelFontSmall),
            statLine("HP:  " + p.getCurrentHp()      + "/" + p.getMaxHp(),       WHITE,    pixelFontSmall),
            statLine("STA: " + p.getCurrentStamina() + "/" + p.getMaxStamina(),  WHITE,    pixelFontSmall),
            statLine("ATK: " + p.getAttack(),                                     WHITE,    pixelFontSmall),
            statLine("DEF: " + p.getDefense(),                                    WHITE,    pixelFontSmall),
            statLine("AGI: " + p.getAgility(),                                    WHITE,    pixelFontSmall),
            statLine("CRI: " + String.format("%.0f%%", p.getCritChance() * 100), WHITE,    pixelFontSmall)
        );

        HBox topRow = new HBox(0, portraitBox, statsBox);
        topRow.setAlignment(Pos.TOP_LEFT);

        String rh = equipmentManager.getEquipped(EquipSlot.MAIN_HAND).map(Weapon::getName).orElse("none");
        String lh = equipmentManager.getEquipped(EquipSlot.OFF_HAND).map(Weapon::getName).orElse("none");
        String ar = equipmentManager.getEquipped(EquipSlot.BODY).map(Weapon::getName).orElse("none");

        VBox equipBox = new VBox(8);
        equipBox.setAlignment(Pos.TOP_LEFT);
        equipBox.setPadding(new Insets(14, 0, 0, 0));
        equipBox.getChildren().addAll(
            equipRow("Right Hand", rh),
            equipRow("Left Hand",  lh),
            equipRow("Armour",     ar)
        );

        paneCharacter.getChildren().addAll(topRow, equipBox);
    }

    // ── ACTION card ──────────────────────────────────────────────────────────
    private void buildActionPanel() {
        paneAction.getChildren().clear();
        paneAction.setAlignment(Pos.CENTER);
        paneAction.setStyle("-fx-background-color:transparent;");
        paneAction.setPadding(new Insets(18, 20, 14, 20));
        paneAction.setSpacing(10);

        Button btnAttack    = makeTextButtonWithIcon("ATTACK",    "/assets/icons/arrow.svg", false);
        Button btnSAttack   = makeTextButtonWithIcon("S. ATTACK", "/assets/icons/arrow.svg", true);
        Button btnInventory = makeTextButtonWithIcon("INVENTORY", "/assets/icons/arrow.svg", false);
        Button btnRun       = makeTextButtonWithIcon("RUN",        "/assets/icons/arrow.svg", true);

        btnAttack   .setOnAction(e -> { /* TODO */ });
        btnSAttack  .setOnAction(e -> { /* TODO */ });
        btnInventory.setOnAction(e -> { /* TODO */ });
        btnRun      .setOnAction(e -> { /* TODO */ });

        paneAction.getChildren().addAll(
            makeSplitRow(btnAttack, btnSAttack),
            makeSplitRow(btnInventory, btnRun),
            makeSaveMenuRow()
        );
    }

    private HBox makeSplitRow(Button left, Button right) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(left, spacer, right);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private HBox makeSaveMenuRow() {
        Button btnSave = makeBorderedButtonWithIcon("SAVE", "/assets/icons/save.svg");
        Button btnMenu = makeBorderedButtonWithIcon("MENU", "/assets/icons/exit.svg");
        btnSave.setOnAction(e -> { /* TODO */ });
        btnMenu.setOnAction(e -> app.showMenu(stage));
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnMenu.setMaxWidth(Double.MAX_VALUE);
        HBox row = new HBox(10, btnSave, btnMenu);
        row.setAlignment(Pos.CENTER);
        HBox.setHgrow(btnSave, Priority.ALWAYS);
        HBox.setHgrow(btnMenu, Priority.ALWAYS);
        return row;
    }

    private Button makeTextButtonWithIcon(String text, String iconPath, boolean pushRight) {
        String baseStyle =
            "-fx-background-color:transparent;" +
            "-fx-border-color:transparent;" +
            "-fx-border-width:0;" +
            "-fx-padding:8 4;" +
            "-fx-cursor:hand;";

        ImageView iconWhite = SvgUtil.load(iconPath, WHITE,    ACTION_ICON_SIZE);
        ImageView iconGold  = SvgUtil.load(iconPath, LABEL_FG, ACTION_ICON_SIZE);

        Label lbl = new Label(text);
        lbl.setFont(pixelFontAction);
        lbl.setStyle("-fx-text-fill:" + WHITE + ";");

        HBox content = new HBox(8);
        content.setAlignment(Pos.CENTER_LEFT);
        if (iconWhite != null) content.getChildren().add(iconWhite);
        content.getChildren().add(lbl);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.setStyle(baseStyle);

        btn.setOnMouseEntered(e -> {
            lbl.setStyle("-fx-text-fill:" + LABEL_FG + ";");
            if (!content.getChildren().isEmpty() && iconGold != null)
                content.getChildren().set(0, iconGold);
        });
        btn.setOnMouseExited(e -> {
            lbl.setStyle("-fx-text-fill:" + WHITE + ";");
            if (!content.getChildren().isEmpty() && iconWhite != null)
                content.getChildren().set(0, iconWhite);
        });

        return btn;
    }

    private Button makeBorderedButtonWithIcon(String text, String iconPath) {
        String base =
            "-fx-background-color:" + CARD_BG + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:2;" +
            "-fx-border-radius:" + RADIUS + ";" +
            "-fx-background-radius:" + RADIUS + ";" +
            "-fx-padding:6 10;" +
            "-fx-cursor:hand;";
        String hover =
            "-fx-background-color:#1e1640;" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:2;" +
            "-fx-border-radius:" + RADIUS + ";" +
            "-fx-background-radius:" + RADIUS + ";" +
            "-fx-padding:6 10;" +
            "-fx-cursor:hand;";

        ImageView iconGold  = SvgUtil.load(iconPath, LABEL_FG, ICON_SIZE);
        ImageView iconWhite = SvgUtil.load(iconPath, WHITE,    ICON_SIZE);

        Label lbl = new Label(text);
        lbl.setFont(pixelFontSmall);
        lbl.setStyle("-fx-text-fill:" + LABEL_FG + ";");

        HBox content = new HBox(6);
        content.setAlignment(Pos.CENTER);
        if (iconGold != null) content.getChildren().add(iconGold);
        content.getChildren().add(lbl);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.setStyle(base);
        btn.setMaxWidth(Double.MAX_VALUE);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hover);
            lbl.setStyle("-fx-text-fill:" + WHITE + ";");
            if (!content.getChildren().isEmpty() && iconWhite != null)
                content.getChildren().set(0, iconWhite);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(base);
            lbl.setStyle("-fx-text-fill:" + LABEL_FG + ";");
            if (!content.getChildren().isEmpty() && iconGold != null)
                content.getChildren().set(0, iconGold);
        });

        return btn;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private HBox equipRow(String label, String value) {
        Label lbl = new Label(label + ": ");
        lbl.setFont(pixelFontSmall);
        lbl.setStyle("-fx-text-fill:" + LABEL_FG + ";");
        Label val = new Label(value);
        val.setFont(pixelFontSmall);
        val.setStyle("-fx-text-fill:" + WHITE + ";");
        HBox row = new HBox(0, lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label statLine(String text, String color, Font font) {
        Label l = new Label(text);
        l.setFont(font);
        l.setStyle("-fx-text-fill:" + color + ";");
        l.setWrapText(false);
        return l;
    }

    private ImageView loadImage(String path, double w, double h) {
        if (path == null) return null;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            Image img = new Image(is, w, h, true, true);
            ImageView iv = new ImageView(img);
            iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) { return null; }
    }

    private GameCharacter player() { return (GameCharacter) gc.getPlayer(); }

    // ── Layout principale ─────────────────────────────────────────────────────
    private void buildLayout() {
        Canvas bgCanvas = new Canvas(WIN_W, WIN_H);
        drawGrid(bgCanvas);
        bgCanvas.setMouseTransparent(true);

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

        Label sysContent = new Label("DUNGEON RPG  v1.0  \u2014  by Lorenzo Grassi");
        sysContent.setFont(pixelFontSmall);
        sysContent.setStyle("-fx-text-fill:" + SYS_TEXT + ";");
        sysContent.setAlignment(Pos.CENTER);
        sysContent.setMaxWidth(Double.MAX_VALUE);

        StackPane sysInner = new StackPane(sysContent);
        sysInner.setPrefSize(TOTAL_W, SYS_H); sysInner.setMinSize(TOTAL_W, SYS_H); sysInner.setMaxSize(TOTAL_W, SYS_H);

        StackPane sysCard = new StackPane(sysInner);
        sysCard.setPrefSize(TOTAL_W, SYS_H); sysCard.setMinSize(TOTAL_W, SYS_H); sysCard.setMaxSize(TOTAL_W, SYS_H);
        sysCard.setStyle("-fx-background-color:" + SYS_BG + ";-fx-border-color:" + BORDER +
            ";-fx-border-width:" + BORDER_W + ";-fx-border-radius:" + RADIUS + ";-fx-background-radius:" + RADIUS + ";");

        Label sysTitle = new Label("  SYSTEM INFO  ");
        sysTitle.setFont(pixelFont); sysTitle.setPrefHeight(LABEL_H);
        sysTitle.setStyle("-fx-text-fill:" + LABEL_FG + ";-fx-background-color:" + SYS_BG +
            ";-fx-border-color:" + BORDER + ";-fx-border-width:" + BORDER_W +
            ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:3 12;");

        StackPane sysWrapper = new StackPane();
        sysWrapper.setPrefSize(TOTAL_W, SYS_H + LABEL_OFFSET);
        sysWrapper.setMinSize(TOTAL_W, SYS_H + LABEL_OFFSET);
        sysWrapper.setMaxSize(TOTAL_W, SYS_H + LABEL_OFFSET);
        StackPane.setAlignment(sysCard,  Pos.BOTTOM_CENTER);
        StackPane.setAlignment(sysTitle, Pos.TOP_CENTER);
        sysWrapper.getChildren().addAll(sysCard, sysTitle);

        VBox mainBox = new VBox(GAP, rowTop, rowBot, sysWrapper);
        mainBox.setPadding(new Insets(PAD + LABEL_OFFSET, PAD, PAD, PAD));
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setPrefSize(WIN_W, WIN_H); mainBox.setMinSize(WIN_W, WIN_H); mainBox.setMaxSize(WIN_W, WIN_H);
        mainBox.setStyle("-fx-background-color:transparent;");

        double xOff  = PAD;
        double yTop2 = PAD + LABEL_OFFSET;
        double yBot2 = yTop2 + ROW_TOP + GAP + LABEL_OFFSET;
        double ySys  = yBot2 + ROW_BOT + GAP + LABEL_OFFSET;

        Canvas gridOverlay = buildGridOverlay(new double[][]{
            {xOff + COL_LEFT + GAP,                 yTop2, COL_MID,   ROW_TOP},
            {xOff + COL_LEFT + GAP + COL_MID + GAP, yTop2, COL_RIGHT, ROW_TOP},
            {xOff,                                  yBot2, COL_LEFT,  ROW_BOT},
            {xOff + COL_LEFT + GAP,                 yBot2, COL_MID,   ROW_BOT},
            {xOff + COL_LEFT + GAP + COL_MID + GAP, yBot2, COL_RIGHT, ROW_BOT},
            {xOff,                                  ySys,  TOTAL_W,   SYS_H}
        });
        gridOverlay.setMouseTransparent(true);

        StackPane stack = new StackPane(bgCanvas, mainBox, gridOverlay);
        stack.setPrefSize(WIN_W, WIN_H); stack.setMinSize(WIN_W, WIN_H); stack.setMaxSize(WIN_W, WIN_H);
        stack.setAlignment(Pos.TOP_LEFT);
        stack.setStyle("-fx-background-color:" + BG + ";");
        root.setCenter(stack);
    }

    private StackPane makeCardWithTitle(String title, Region content, double w, double h) {
        content.setPrefSize(w, h); content.setMinSize(w, h); content.setMaxSize(w, h);
        StackPane card = new StackPane(content);
        card.setPrefSize(w, h); card.setMinSize(w, h); card.setMaxSize(w, h);
        card.setStyle("-fx-background-color:" + CARD_BG +
            ";-fx-border-color:" + BORDER +
            ";-fx-border-width:" + BORDER_W +
            ";-fx-border-radius:" + RADIUS +
            ";-fx-background-radius:" + RADIUS + ";");
        Label lbl = new Label("  " + title + "  ");
        lbl.setFont(pixelFont); lbl.setPrefHeight(LABEL_H);
        lbl.setStyle("-fx-text-fill:" + LABEL_FG +
            ";-fx-background-color:" + CARD_BG +
            ";-fx-border-color:" + BORDER +
            ";-fx-border-width:" + BORDER_W +
            ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:3 12;");
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
