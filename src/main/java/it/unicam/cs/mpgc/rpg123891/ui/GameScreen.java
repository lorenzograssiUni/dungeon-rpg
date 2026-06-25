package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.CombatController;
import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.*;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.MagicAmulet;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.MagicStaff;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameScreen {

    // ── Dimensioni finestra e colonne ─────────────────────────────────────────
    private static final double WIN_W      = 980;
    private static final double WIN_H      = 620;
    private static final double COL_LEFT   = 420;  // ENCOUNTER / ENEMY STATS
    private static final double COL_MID    = 340;  // CHARACTER / ACTION
    private static final double COL_RIGHT  = 200;  // MINI-MAP / STATUS / COMBAT LOG
    private static final double ROW_TOP    = 310;  // altezza riga superiore
    private static final double ROW_BOT    = 230;  // altezza riga inferiore
    private static final double SYS_H      =  32;  // barra SYSTEM INFO
    private static final double GAP        =   6;  // spazio tra card
    private static final double PAD        =   6;  // padding esterno

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final String BG          = "#212121";
    private static final String PANEL       = "#1a1a2e";
    private static final String PANEL_DARK  = "#0f0f1a";
    private static final String BORDER      = "#c8922a";   // oro RPG
    private static final String TITLE_BG    = "#2a1f0a";   // sfondo titolo card
    private static final String GOLD        = "#f0d060";
    private static final String WHITE       = "#dcdcdc";
    private static final String RED         = "#e05555";
    private static final String GREEN       = "#55e077";
    private static final String ORANGE      = "#e0a030";
    private static final String BLUE        = "#4a9eff";
    private static final double GRID_OPACITY = 0.06;  // griglia quasi invisibile
    private static final int    GRID_SIZE    = 24;    // cella griglia px

    private Font pixelFont;       // 9px
    private Font pixelFontSmall;  // 7px

    private final BorderPane     root;
    private final GameController gc;
    private final Stage          stage;
    private final FxApp          app;
    private final CombatController  combatController;
    private final EquipmentManager  equipmentManager;

    // ── Aree contenuto (riempite negli step successivi) ───────────────────────
    private final StackPane paneEncounter  = new StackPane();
    private final VBox      paneEnemyStats = new VBox(4);
    private final VBox      paneCharacter  = new VBox(5);
    private final VBox      paneAction     = new VBox(4);
    private final StackPane paneMiniMap    = new StackPane();
    private final VBox      paneStatus     = new VBox(4);
    private final TextArea  logArea        = new TextArea();

    private Enemy  selectedEnemy  = null;
    private String lastLoggedWave = null;

    // ── Sprite maps ───────────────────────────────────────────────────────────
    private static final Map<String, String> ENEMY_SPRITE = Map.ofEntries(
        Map.entry("Cinghiale",          "/assets/enemies/Cinghiale1.png"),
        Map.entry("Cinghiale Feroce",   "/assets/enemies/cinghiale2.png"),
        Map.entry("Lupo",               "/assets/enemies/lupo.png"),
        Map.entry("Goblin",             "/assets/enemies/Goblin.png"),
        Map.entry("Goblin Guerriero",   "/assets/enemies/Goblin2.png"),
        Map.entry("Goblin Guardia",     "/assets/enemies/goblinGuard.png"),
        Map.entry("Re Goblin",          "/assets/enemies/regoblin.png"),
        Map.entry("Scheletro",          "/assets/enemies/scheletro.png"),
        Map.entry("Scheletro Antico",   "/assets/enemies/scheletro2.png"),
        Map.entry("Scheletro Guardia",  "/assets/enemies/scheletroGuardia.png"),
        Map.entry("Strega",             "/assets/enemies/Strega.png"),
        Map.entry("Uovo",               "/assets/enemies/uovo1.png"),
        Map.entry("Uovo del Drago",     "/assets/enemies/uovo2.png"),
        Map.entry("Cucciolo di Drago",  "/assets/enemies/cucciolo1.png"),
        Map.entry("L'Ultimo Drago",     "/assets/enemies/UltimoDrago.png")
    );

    private static final Map<String, String> ROOM_BG = Map.of(
        "r1", "/assets/backgrounds/foresta.png",
        "r2", "/assets/backgrounds/GoblinVillage.png",
        "r3", "/assets/backgrounds/Caverne.png",
        "r4", "/assets/backgrounds/catacombe.png",
        "r5", "/assets/backgrounds/StanzaFinale.png"
    );

    // ── Costruttore ───────────────────────────────────────────────────────────
    public GameScreen(GameController gc, Stage stage, FxApp app) {
        this.gc    = gc;
        this.stage = stage;
        this.app   = app;
        this.root  = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        this.equipmentManager = new EquipmentManager((GameCharacter) gc.getPlayer());
        loadFonts();

        combatController = new CombatController(gc, gc.getGameState().getDungeonMap());
        combatController.setListener(new CombatController.CombatListener() {
            public void onEvent(String msg) { appendLog(msg); }
            public void onTurnEnd(List<String> log, boolean dead, boolean cleared) {}
        });

        buildLayout();
        refresh();
        logRoomEntry();
    }

    public BorderPane getRoot() { return root; }

    // ── Font ──────────────────────────────────────────────────────────────────
    private void loadFonts() {
        try (InputStream is = getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf")) {
            if (is != null) {
                pixelFont      = Font.loadFont(is, 9);
                pixelFontSmall = Font.loadFont(
                    getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), 7);
            }
        } catch (Exception e) { /* fallback */ }
        if (pixelFont      == null) pixelFont      = Font.font("Courier New", FontWeight.BOLD, 9);
        if (pixelFontSmall == null) pixelFontSmall = Font.font("Courier New", FontWeight.BOLD, 7);
    }

    // =========================================================================
    //  LAYOUT
    //
    //  StackPane root
    //   ├── Canvas gridCanvas          (sfondo #212121 + griglia)
    //   └── VBox main
    //        ├── HBox rowTop
    //        │    ├── card ENCOUNTER    COL_LEFT  × ROW_TOP
    //        │    ├── card CHARACTER    COL_MID   × ROW_TOP
    //        │    └── VBox rightTop
    //        │         ├── card MINI-MAP   COL_RIGHT × ~ROW_TOP*0.55
    //        │         └── card STATUS     COL_RIGHT × ~ROW_TOP*0.45
    //        ├── HBox rowBot
    //        │    ├── card ENEMY STATS  COL_LEFT  × ROW_BOT
    //        │    ├── card ACTION       COL_MID   × ROW_BOT
    //        │    └── card COMBAT LOG   COL_RIGHT × ROW_BOT
    //        └── HBox sysBar            WIN_W     × SYS_H
    // =========================================================================
    private void buildLayout() {

        // ── Sfondo + griglia ─────────────────────────────────────────────────
        Canvas gridCanvas = new Canvas(WIN_W, WIN_H);
        drawGrid(gridCanvas);
        gridCanvas.setMouseTransparent(true);

        // ── ROW TOP ──────────────────────────────────────────────────────────
        VBox cardEncounter = wrapCard("ENCOUNTER", paneEncounter,
                                      COL_LEFT,  ROW_TOP);

        paneCharacter.setPadding(new Insets(8));
        VBox cardCharacter = wrapCard("CHARACTER", paneCharacter,
                                      COL_MID,   ROW_TOP);

        double mapH    = Math.round(ROW_TOP * 0.58);
        double statusH = ROW_TOP - mapH - GAP;
        paneMiniMap.setStyle("-fx-background-color:" + PANEL_DARK + ";");
        VBox cardMiniMap = wrapCard("MINI-MAP", paneMiniMap, COL_RIGHT, mapH);

        paneStatus.setPadding(new Insets(8));
        paneStatus.getChildren().add(pxLabel("Conditions", WHITE, 8));
        VBox cardStatus  = wrapCard("STATUS",   paneStatus,  COL_RIGHT, statusH);

        VBox rightTop = new VBox(GAP, cardMiniMap, cardStatus);
        rightTop.setPrefSize(COL_RIGHT, ROW_TOP);
        rightTop.setMinSize(COL_RIGHT, ROW_TOP);
        rightTop.setMaxSize(COL_RIGHT, ROW_TOP);

        HBox rowTop = new HBox(GAP, cardEncounter, cardCharacter, rightTop);
        rowTop.setAlignment(Pos.TOP_LEFT);

        // ── ROW BOTTOM ───────────────────────────────────────────────────────
        paneEnemyStats.setPadding(new Insets(8));
        VBox cardEnemyStats = wrapCard("ENEMY STATS", paneEnemyStats,
                                       COL_LEFT,  ROW_BOT);

        paneAction.setPadding(new Insets(8));
        VBox cardAction = wrapCard("ACTION", paneAction,
                                   COL_MID,  ROW_BOT);

        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setFont(pixelFontSmall);
        logArea.setStyle(
            "-fx-control-inner-background:" + PANEL_DARK + ";" +
            "-fx-text-fill:" + WHITE + ";" +
            "-fx-font-size:7px;"
        );
        logArea.setPrefSize(COL_RIGHT, ROW_BOT - 26);
        logArea.setMinSize(COL_RIGHT, ROW_BOT - 26);
        logArea.setMaxSize(COL_RIGHT, ROW_BOT - 26);
        VBox logInner = new VBox(logArea);
        logInner.setStyle("-fx-background-color:" + PANEL_DARK + ";");
        VBox cardLog = wrapCard("COMBAT LOG / MESSAGES", logInner,
                                COL_RIGHT, ROW_BOT);

        HBox rowBot = new HBox(GAP, cardEnemyStats, cardAction, cardLog);
        rowBot.setAlignment(Pos.TOP_LEFT);

        // ── SYSTEM INFO BAR ───────────────────────────────────────────────────
        Label sysLabel = new Label("DUNGEON RPG  v1.0  —  by Lorenzo Grassi");
        sysLabel.setFont(pixelFontSmall);
        sysLabel.setStyle("-fx-text-fill:" + GOLD + ";-fx-padding:0 8;");
        HBox sysBar = new HBox(sysLabel);
        sysBar.setAlignment(Pos.CENTER);
        sysBar.setPrefHeight(SYS_H);
        sysBar.setStyle(
            "-fx-background-color:#0a0a0a;" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:1 0 0 0;"
        );

        // ── Main VBox ─────────────────────────────────────────────────────────
        VBox mainBox = new VBox(GAP, rowTop, rowBot, sysBar);
        mainBox.setPadding(new Insets(PAD));
        mainBox.setStyle("-fx-background-color:transparent;");

        // ── StackPane root: griglia sotto, UI sopra ───────────────────────────
        StackPane stack = new StackPane(gridCanvas, mainBox);
        stack.setAlignment(Pos.TOP_LEFT);
        stack.setStyle("-fx-background-color:" + BG + ";");
        root.setCenter(stack);
    }

    // ── Disegna la griglia sul Canvas ─────────────────────────────────────────
    private void drawGrid(Canvas canvas) {
        GraphicsContext gc2 = canvas.getGraphicsContext2D();

        // Sfondo solido
        gc2.setFill(Color.web(BG));
        gc2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Griglia bianca a bassa opacità
        gc2.setStroke(Color.web("#ffffff", GRID_OPACITY));
        gc2.setLineWidth(1);
        for (double x = 0; x <= canvas.getWidth(); x += GRID_SIZE) {
            gc2.strokeLine(x, 0, x, canvas.getHeight());
        }
        for (double y = 0; y <= canvas.getHeight(); y += GRID_SIZE) {
            gc2.strokeLine(0, y, canvas.getWidth(), y);
        }
    }

    // ── wrapCard: card con titolo centrato stile RPG ───────────────────────────
    //   Dimensioni FISSE: pref = min = max
    private VBox wrapCard(String title, Region content, double w, double h) {
        Label lbl = new Label("  " + title + "  ");
        lbl.setFont(pixelFont);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle(
            "-fx-text-fill:" + GOLD + ";" +
            "-fx-background-color:" + TITLE_BG + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:0 0 1 0;" +
            "-fx-padding:4 10;"
        );

        // Altezza contenuto = h totale card - titolo (~22px) - bordi (4px)
        double contentH = h - 26;
        content.setPrefSize(w - 4, contentH);
        content.setMinSize(w - 4, contentH);
        content.setMaxSize(w - 4, contentH);

        VBox card = new VBox(0, lbl, content);
        card.setPrefSize(w, h);
        card.setMinSize(w, h);
        card.setMaxSize(w, h);
        card.setStyle(
            "-fx-background-color:" + PANEL + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:2;" +
            "-fx-border-radius:0;" +
            "-fx-background-radius:0;"
        );
        return card;
    }

    // =========================================================================
    // REFRESH  (contenuti — da riempire step-by-step)
    // =========================================================================
    private void refresh() {
        refreshEncounter();
        refreshEnemyStats();
        refreshCharacter();
        refreshAction();
        refreshMiniMap();
        logCurrentWaveIfNew();
    }

    private void selectEnemy(Enemy enemy) {
        if (enemy == selectedEnemy) return;
        selectedEnemy = enemy;
        refreshEncounter();
        refreshEnemyStats();
        appendLog("[MIRA] " + enemy.getName() + " selezionato.");
    }

    // ── ENCOUNTER ─────────────────────────────────────────────────────────────
    private void refreshEncounter() {
        paneEncounter.getChildren().clear();

        String bgPath = ROOM_BG.getOrDefault(gc.getCurrentRoom().getId(),
                                             "/assets/backgrounds/foresta.png");
        ImageView bg = loadBg(bgPath, COL_LEFT - 4, ROW_TOP - 26);
        if (bg != null) paneEncounter.getChildren().add(bg);

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color:rgba(0,0,0,0.15);");
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlay.setMouseTransparent(true);
        paneEncounter.getChildren().add(overlay);

        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null || gc.getCurrentRoom().isCleared()) {
            paneEncounter.getChildren().add(pxLabel("- STANZA LIBERATA -", GOLD, 9));
            return;
        }
        List<Enemy> alive = wave.getEnemies().stream().filter(Enemy::isAlive).toList();
        if (alive.isEmpty()) return;

        HBox row = new HBox(20);
        row.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(row, Pos.BOTTOM_CENTER);
        StackPane.setMargin(row, new Insets(0, 0, 14, 0));
        for (Enemy e : alive) row.getChildren().add(buildEnemySprite(e));
        paneEncounter.getChildren().add(row);
    }

    private VBox buildEnemySprite(Enemy enemy) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.BOTTOM_CENTER);

        ImageView sprite = loadSprite(ENEMY_SPRITE.get(enemy.getName()), 130);
        if (sprite == null) {
            Label ph = new Label(enemy.getName().substring(0, Math.min(4, enemy.getName().length())));
            ph.setStyle("-fx-text-fill:" + RED + ";-fx-font-size:18px;");
            card.getChildren().add(ph);
        } else {
            StackPane wrap = new StackPane(sprite);
            wrap.setStyle(enemy == selectedEnemy
                ? "-fx-border-color:" + GOLD + ";-fx-border-width:3;"
                : "-fx-border-color:transparent;-fx-border-width:3;");
            card.getChildren().add(wrap);
        }
        double hpR = (double) enemy.getCurrentHp() / enemy.getMaxHp();
        String hpC = hpR > 0.5 ? GREEN : hpR > 0.25 ? ORANGE : RED;
        ProgressBar hpBar = new ProgressBar(hpR);
        hpBar.setPrefWidth(100); hpBar.setPrefHeight(7);
        hpBar.setStyle("-fx-accent:" + hpC + ";-fx-background-color:#333;");
        Label nameL = pxLabel(enemy.getName(), enemy == selectedEnemy ? GOLD : WHITE, 7);
        nameL.setWrapText(false);
        card.getChildren().addAll(nameL, hpBar);
        card.setPadding(new Insets(0, 4, 0, 4));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> selectEnemy(enemy));
        return card;
    }

    // ── ENEMY STATS ───────────────────────────────────────────────────────────
    private void refreshEnemyStats() {
        paneEnemyStats.getChildren().clear();
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null || gc.getCurrentRoom().isCleared()) {
            paneEnemyStats.getChildren().add(pxLabel("Nessun nemico.", WHITE, 7));
            return;
        }
        for (Enemy e : wave.getEnemies().stream().filter(Enemy::isAlive).toList()) {
            boolean sel = e == selectedEnemy;
            Label name = pxLabel((sel ? "► " : "") + e.getName(), sel ? GOLD : WHITE, 7);
            double hpR = (double) e.getCurrentHp() / e.getMaxHp();
            String hpC = hpR > 0.5 ? GREEN : hpR > 0.25 ? ORANGE : RED;
            Label stats = pxLabel("HP " + e.getCurrentHp() + "/" + e.getMaxHp()
                + "  ATK " + e.getAttack() + "  DEF " + e.getDefense(), hpC, 6);
            paneEnemyStats.getChildren().addAll(name, stats, new Separator());
        }
    }

    // ── CHARACTER ─────────────────────────────────────────────────────────────
    private void refreshCharacter() {
        paneCharacter.getChildren().clear();
        GameCharacter p = player();

        String classSprite = switch (p.getCharacterClass()) {
            case WARRIOR -> "/assets/classes/warrior.png";
            case MAGE    -> "/assets/classes/mage.png";
            case THIEF   -> "/assets/classes/thief.png";
            default      -> null;
        };
        HBox portraitRow = new HBox(8);
        portraitRow.setAlignment(Pos.TOP_LEFT);
        if (classSprite != null) {
            ImageView portrait = loadImg(classSprite, 60, 60);
            if (portrait != null)
                portrait.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:2;");
            if (portrait != null) portraitRow.getChildren().add(portrait);
        }
        VBox info = new VBox(3,
            pxLabel(p.getName(), GOLD, 8),
            pxLabel(p.getCharacterClass().toString(), WHITE, 7),
            pxLabel("HP: " + p.getCurrentHp() + "/" + p.getMaxHp(), GREEN, 7),
            pxLabel("STA: " + p.getCurrentStamina() + "/" + p.getMaxStamina(), BLUE, 7)
        );
        portraitRow.getChildren().add(info);
        paneCharacter.getChildren().add(portraitRow);
        paneCharacter.getChildren().add(new Separator());

        paneCharacter.getChildren().addAll(
            pxLabel("ATK: " + p.getAttack(), WHITE, 7),
            pxLabel("DEF: " + p.getDefense(), WHITE, 7),
            pxLabel("AGI: " + p.getAgility(), WHITE, 7),
            pxLabel("CRI: " + String.format("%.0f%%", p.getCritChance() * 100), WHITE, 7)
        );
        paneCharacter.getChildren().add(new Separator());

        for (EquipSlot slot : EquipSlot.values()) {
            Optional<Weapon> eq = equipmentManager.getEquipped(slot);
            String prefix = switch (slot) { case MAIN_HAND -> "W: "; case OFF_HAND -> "S: "; case BODY -> "A: "; };
            paneCharacter.getChildren().add(
                pxLabel(prefix + eq.map(Weapon::getName).orElse("-"), eq.isPresent() ? GOLD : "#666688", 7));
        }

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        paneCharacter.getChildren().add(spacer);
        paneCharacter.getChildren().add(new Separator());
        Button btnSave = rpgBtn("> Save", GOLD, "#1a1a3a");
        Button btnMenu = rpgBtn("> Menu", RED,  "#2a0a0a");
        btnSave.setOnAction(e -> { gc.saveGame(); appendLog("[SAVE] Partita salvata."); });
        btnMenu.setOnAction(e -> confirmMenu());
        paneCharacter.getChildren().addAll(btnSave, btnMenu);
    }

    // ── ACTION ────────────────────────────────────────────────────────────────
    private void refreshAction() {
        paneAction.getChildren().clear();
        boolean cleared = gc.getCurrentRoom().isCleared();
        if (!cleared) {
            String[][] defs = {
                {"► Fight",  WHITE, "#1a1a3a"},
                {"▶ Skills", WHITE, "#1a1a3a"},
                {"▶ Items",  WHITE, "#1a1a3a"},
                {"▶ Equip",  WHITE, "#1a1a3a"},
                {"→ Run",    RED,   "#2a0a0a"}
            };
            Button[] btns = new Button[defs.length];
            for (int i = 0; i < defs.length; i++) btns[i] = rpgBtn(defs[i][0], defs[i][1], defs[i][2]);
            btns[0].setOnAction(e -> doNormalAttack());
            btns[1].setOnAction(e -> showSkills());
            btns[2].setOnAction(e -> showInventario());
            btns[3].setOnAction(e -> showEquip());
            btns[4].setOnAction(e -> doFlee());
            btns[4].setDisable(!gc.canFlee());
            for (Button b : btns) paneAction.getChildren().add(b);
        } else {
            if (gc.getGameState().getDungeonMap().hasNextRoom()) {
                Button adv = rpgBtn("► Avanza ►", GREEN, "#0a2a0a");
                adv.setOnAction(e -> doAdvanceRoom());
                paneAction.getChildren().add(adv);
            } else { showVictory(); }
        }
    }

    // ── MINI-MAP (placeholder) ────────────────────────────────────────────────
    private void refreshMiniMap() {
        paneMiniMap.getChildren().clear();
        Label ph = pxLabel("[ MAP ]", GOLD, 8);
        paneMiniMap.getChildren().add(ph);
    }

    // ── Sub-screens (CHARACTER panel) ─────────────────────────────────────────
    private void showInventario() {
        paneCharacter.getChildren().clear();
        paneCharacter.getChildren().add(pxLabel("INVENTARIO", GOLD, 9));
        paneCharacter.getChildren().add(new Separator());
        List<Item> inv = player().getInventory();
        if (inv.isEmpty()) {
            paneCharacter.getChildren().add(pxLabel("(vuoto)", WHITE, 8));
        } else {
            LinkedHashMap<String, Long> cons = new LinkedHashMap<>();
            for (Item item : inv) if (!(item instanceof Weapon)) cons.merge(item.getName(), 1L, Long::sum);
            cons.forEach((nome, count) -> {
                Button b = rpgBtn("> " + nome + (count > 1 ? " x" + count : ""), WHITE, "#0d0d25");
                b.setOnAction(e -> {
                    Item found = inv.stream().filter(i -> !(i instanceof Weapon) && i.getName().equals(nome)).findFirst().orElse(null);
                    if (found == null) return;
                    if (found instanceof Potion) { gc.useFirstPotion(); appendLog("[ITEM] Pozione usata!"); }
                    else if (found instanceof Meat meat) { meat.use(player()); player().getInventory().remove(meat); appendLog("[ITEM] Carne mangiata!"); }
                    showInventario();
                });
                paneCharacter.getChildren().add(b);
            });
            for (Item item : inv) if (item instanceof Weapon w) {
                Button b = rpgBtn("> [W] " + w.getName(), GOLD, "#0d0d25");
                b.setOnAction(e -> showEquipPreview(w));
                paneCharacter.getChildren().add(b);
            }
        }
        paneCharacter.getChildren().add(new Separator());
        Button back = rpgBtn("< Indietro", WHITE, "#1a1a3a"); back.setOnAction(e -> refreshCharacter());
        paneCharacter.getChildren().add(back);
    }

    private void showEquip() {
        paneCharacter.getChildren().clear();
        paneCharacter.getChildren().add(pxLabel("EQUIPAGGIAMENTO", GOLD, 9));
        paneCharacter.getChildren().add(new Separator());
        for (EquipSlot slot : EquipSlot.values()) {
            String prefix = switch (slot) { case MAIN_HAND -> "W: "; case OFF_HAND -> "S: "; case BODY -> "A: "; };
            Optional<Weapon> eq = equipmentManager.getEquipped(slot);
            Label l = pxLabel(prefix + eq.map(Weapon::getName).orElse("-"), eq.isPresent() ? GOLD : WHITE, 7);
            HBox row = new HBox(6, l);
            if (eq.isPresent()) {
                Button unb = rpgBtn("X", RED, "#2a0a0a"); unb.setMaxWidth(28);
                unb.setOnAction(e -> { equipmentManager.unequip(slot); appendLog("[EQUIP] Rimosso."); showEquip(); });
                row.getChildren().add(unb);
            }
            paneCharacter.getChildren().add(row);
        }
        paneCharacter.getChildren().add(new Separator());
        Button back = rpgBtn("< Indietro", WHITE, "#1a1a3a"); back.setOnAction(e -> refreshCharacter());
        paneCharacter.getChildren().add(back);
    }

    private void showEquipPreview(Weapon weapon) {
        paneCharacter.getChildren().clear();
        paneCharacter.getChildren().add(pxLabel("EQUIPAGGIA: " + weapon.getName(), GOLD, 8));
        paneCharacter.getChildren().add(new Separator());
        GameCharacter p = player();
        StatModifier mod = weapon.getModifierFor(p.getCharacterClass());
        if (mod.attackDelta()    != 0) paneCharacter.getChildren().add(deltaPx("ATK", p.getAttack(),     mod.attackDelta()));
        if (mod.defenseDelta()   != 0) paneCharacter.getChildren().add(deltaPx("DEF", p.getDefense(),    mod.defenseDelta()));
        if (mod.agilityDelta()   != 0) paneCharacter.getChildren().add(deltaPx("AGI", p.getAgility(),    mod.agilityDelta()));
        if (mod.maxHpDelta()     != 0) paneCharacter.getChildren().add(deltaPx("HP+", p.getMaxHp(),       mod.maxHpDelta()));
        if (mod.maxStaminaDelta()!= 0) paneCharacter.getChildren().add(deltaPx("STA", p.getMaxStamina(), mod.maxStaminaDelta()));
        paneCharacter.getChildren().add(new Separator());
        EquipmentManager.EquipResult can = equipmentManager.canEquip(weapon);
        if (!can.success()) { Label w = pxLabel(can.message(), RED, 7); w.setWrapText(true); paneCharacter.getChildren().add(w); }
        Button eq   = rpgBtn("> Equipaggia", can.success() ? GREEN : "#555", "#0a2a0a"); eq.setDisable(!can.success());
        eq.setOnAction(e -> { equipmentManager.equip(weapon); appendLog("[EQUIP] " + weapon.getName()); refreshCharacter(); });
        Button back = rpgBtn("< Indietro", WHITE, "#1a1a3a"); back.setOnAction(e -> showInventario());
        paneCharacter.getChildren().addAll(eq, back);
    }

    private void showSkills() {
        paneCharacter.getChildren().clear();
        paneCharacter.getChildren().add(pxLabel("SKILLS", GOLD, 9));
        paneCharacter.getChildren().add(new Separator());
        List<SpecialAttack> specials = equipmentManager.getEquippedSpecials();
        boolean staffEquipped  = equipmentManager.getEquipped(EquipSlot.MAIN_HAND).map(w -> w instanceof MagicStaff).orElse(false);
        boolean amuletEquipped = equipmentManager.getEquipped(EquipSlot.BODY).map(w -> w instanceof MagicAmulet).orElse(false);
        boolean isMage         = player().getCharacterClass() == CharacterClass.MAGE;
        boolean staffLocked    = staffEquipped && !isMage && !amuletEquipped;
        if (specials.isEmpty()) {
            paneCharacter.getChildren().add(pxLabel("Nessuna skill. Equipa un'arma.", WHITE, 7));
        } else {
            for (SpecialAttack s : specials) {
                boolean isStaffSpecial = staffEquipped && (s.getName().equals("Onda Magica") || s.getName().equals("Colpo Vitale"));
                boolean locked = isStaffSpecial && staffLocked;
                boolean canUse = !locked && player().canUseSpecial(s.getStaminaCost());
                Button b = rpgBtn("> " + s.getName() + " [" + s.getStaminaCost() + " STA]",
                    locked ? "#555" : canUse ? WHITE : RED, "#0d0d25");
                b.setDisable(locked || !canUse);
                b.setOnAction(e -> executeSpecial(s));
                paneCharacter.getChildren().add(b);
            }
        }
        paneCharacter.getChildren().add(new Separator());
        Button back = rpgBtn("< Indietro", WHITE, "#1a1a3a"); back.setOnAction(e -> refreshCharacter());
        paneCharacter.getChildren().add(back);
    }

    private Label deltaPx(String name, int before, int delta) {
        Label l = pxLabel(name + ": " + before + " → " + (before + delta), delta > 0 ? GREEN : RED, 7);
        l.setWrapText(true); return l;
    }

    // ── Log ───────────────────────────────────────────────────────────────────
    private void logRoomEntry() {
        Room room = gc.getCurrentRoom();
        appendLog(""); appendLog("===============================");
        appendLog("  " + room.getName().toUpperCase());
        appendLog("===============================");
        appendLog(room.getDescription());
        if (room.getId().equals("r1")) appendLog("[*] Raccogli il Bastone Magico.");
    }

    private void logCurrentWaveIfNew() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null) return;
        String key = gc.getCurrentRoom().getId() + "|" + wave.getName();
        if (key.equals(lastLoggedWave)) return;
        lastLoggedWave = key;
        if (!wave.getDescription().isBlank()) {
            appendLog(""); appendLog("--- " + wave.getName() + " ---"); appendLog(wave.getDescription());
        }
        if (wave.getEnemies().isEmpty()) { wave.setCleared(true); gc.checkWaveCleared(); refresh(); }
    }

    private void appendLog(String msg) {
        Platform.runLater(() -> { logArea.appendText(msg + "\n"); logArea.setScrollTop(Double.MAX_VALUE); });
    }

    // ── Combat ────────────────────────────────────────────────────────────────
    private void doNormalAttack() {
        if (selectedEnemy == null || !selectedEnemy.isAlive()) { appendLog("[!] Clicca un nemico per selezionarlo."); return; }
        if (selectedEnemy.isImmune()) { appendLog("[!] " + selectedEnemy.getName() + " e' immune!"); return; }
        if (selectedEnemy.isEgg())    { appendLog("[!] Non puoi attaccare un Uovo!"); return; }
        handleTurnResult(combatController.playerNormalAttack(selectedEnemy));
    }

    private void executeSpecial(SpecialAttack special) {
        boolean isAoe = special.getName().equals("Onda Magica") || special.getName().equals("Spazzatutto");
        if (isAoe) { handleTurnResult(combatController.playerAoeAttack(special)); }
        else {
            if (selectedEnemy == null || !selectedEnemy.isAlive()) { appendLog("[!] Seleziona un nemico."); return; }
            handleTurnResult(combatController.playerSpecialAttack(special, selectedEnemy));
        }
    }

    private void doFlee() { handleTurnResult(combatController.playerFlee()); }

    private void doAdvanceRoom() {
        gc.advanceRoom(); selectedEnemy = null; lastLoggedWave = null;
        Room newRoom = gc.getCurrentRoom();
        if (newRoom.getId().equals("r5")) {
            Wave w = newRoom.getCurrentWave();
            if (w != null) w.getEnemies().stream().findFirst().ifPresent(d -> combatController.checkAndActivateDragonBuff(d));
        }
        logRoomEntry(); refresh();
    }

    private void handleTurnResult(CombatController.TurnResult result) {
        if (result == null) return;
        result.log().forEach(this::appendLog);
        if (result.playerDead())  { showGameOver(); return; }
        if (result.fleeSuccess()) { appendLog("[FUGA] Sei fuggito!"); refresh(); return; }
        if (result.waveCleared()) {
            appendLog("[WAVE] Ondata completata!"); gc.checkWaveCleared();
            if (gc.getGameState().isVictory()) { showVictory(); return; }
            selectedEnemy = null;
        }
        refresh(); flashEncounter();
    }

    private void flashEncounter() {
        Region flash = new Region();
        flash.setStyle("-fx-background-color:rgba(200,30,30,0.22);");
        flash.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        flash.setMouseTransparent(true);
        paneEncounter.getChildren().add(flash);
        new Timeline(new KeyFrame(Duration.millis(180), e -> paneEncounter.getChildren().remove(flash))).play();
    }

    // ── End screens ───────────────────────────────────────────────────────────
    private void showGameOver() {
        VBox vb = new VBox(20); vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color:" + BG + ";");
        vb.getChildren().addAll(pxLabel("GAME OVER", RED, 28), pxLabel("Sei stato sconfitto.", WHITE, 10));
        Button back = rpgBtn("> Torna al Menu", GOLD, "#1a1a3a"); back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().add(back); root.setCenter(vb);
    }

    private void showVictory() {
        VBox vb = new VBox(20); vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color:" + BG + ";");
        vb.getChildren().addAll(pxLabel("VITTORIA!", GOLD, 24), pxLabel("L'Ultimo Drago e' sconfitto!", WHITE, 10));
        Button back = rpgBtn("> Torna al Menu", GOLD, "#1a1a3a"); back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().add(back); root.setCenter(vb);
    }

    private void confirmMenu() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Menu"); a.setHeaderText("Tornare al menu principale?");
        a.setContentText("I progressi non salvati andranno persi.");
        a.showAndWait().ifPresent(r -> { if (r == ButtonType.OK) app.showMenu(stage); });
    }

    // =========================================================================
    // Helper UI
    // =========================================================================

    private Label pxLabel(String text, String color, int size) {
        Label l = new Label(text);
        l.setFont(size >= 9 ? pixelFont : pixelFontSmall);
        l.setStyle("-fx-text-fill:" + color + ";-fx-font-size:" + size + "px;");
        l.setWrapText(true);
        return l;
    }

    private Button rpgBtn(String text, String textColor, String bgColor) {
        Button b = new Button(text);
        b.setFont(pixelFontSmall);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(
            "-fx-text-fill:" + textColor + ";" +
            "-fx-background-color:" + bgColor + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:1;" +
            "-fx-padding:5 10;" +
            "-fx-background-radius:0;" +
            "-fx-border-radius:0;" +
            "-fx-cursor:hand;"
        );
        return b;
    }

    // ── Image loaders ─────────────────────────────────────────────────────────
    private ImageView loadBg(String path, double w, double h) {
        if (path == null) return null;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            Image img = new Image(is, w, h, false, false);
            ImageView iv = new ImageView(img);
            iv.setSmooth(false);
            iv.setFitWidth(w); iv.setFitHeight(h);
            iv.setPreserveRatio(false);
            return iv;
        } catch (Exception e) { return null; }
    }

    private ImageView loadSprite(String path, double height) {
        if (path == null) return null;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            Image img = new Image(is);
            ImageView iv = new ImageView(img);
            iv.setSmooth(true); iv.setPreserveRatio(true); iv.setFitHeight(height);
            return iv;
        } catch (Exception e) { return null; }
    }

    private ImageView loadImg(String path, double w, double h) {
        if (path == null) return null;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            Image img = new Image(is, w, h, true, false);
            ImageView iv = new ImageView(img);
            iv.setSmooth(false); iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) { return null; }
    }

    private GameCharacter player() { return (GameCharacter) gc.getPlayer(); }
}
