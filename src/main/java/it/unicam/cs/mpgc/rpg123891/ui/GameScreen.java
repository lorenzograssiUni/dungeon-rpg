package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.CombatController;
import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipSlot;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipmentManager;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Schermata principale di gioco JavaFX.
 */
public class GameScreen {

    // ── Layout constants ──────────────────────────────────────────────
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
    public  static final double WIN_W   = TOTAL_W + PAD * 2;
    public  static final double WIN_H   =
        PAD + LABEL_OFFSET + ROW_TOP + GAP
        + LABEL_OFFSET + ROW_BOT + GAP
        + LABEL_OFFSET + SYS_H + PAD;

    // ── Colours ───────────────────────────────────────────────────────
    private static final String BG           = "#212121";
    private static final String CARD_BG      = "#140E2C";
    private static final String SYS_BG       = "#000000";
    private static final String BORDER       = "#9E6554";
    private static final String LABEL_FG     = "#D4A96A";
    private static final String SYS_TEXT     = "#ffffff";
    private static final String WHITE        = "#cccccc";
    private static final double GRID_OPACITY = 0.06;
    private static final int    GRID_SIZE    = 24;

    // ── Sprite sizes ──────────────────────────────────────────────────
    private static final double ACTION_ICON_SIZE  = 20;
    private static final int    ACTION_FONT_SIZE  = 13;
    private static final double ICON_SIZE         = 14;
    private static final double PORTRAIT_SIZE     = 140;
    private static final double PORTRAIT_RADIUS   = 12;
    private static final double ENEMY_SPRITE_H    = 130;
    private static final double ENEMY_SPRITE_W    = 110;
    private static final double MINIBOSS_SPRITE_H = 190;
    private static final double MINIBOSS_SPRITE_W = 160;
    private static final double DRAGON_SPRITE_H   = 220;
    private static final double DRAGON_SPRITE_W   = 200;
    private static final double LOOT_SPRITE_H     = 160;
    private static final double LOOT_SPRITE_W     = 120;
    /** Offset verticale (px) per abbassare lo sprite dell'arma nella card Encounter */
    private static final double LOOT_SPRITE_OFFSET_Y = 10;

    /** Nemici che usano le dimensioni miniboss */
    private static final Set<String> MINIBOSS_NAMES = Set.of("Re Goblin", "Strega", "Cucciolo Drago", "Cucciolo Uovo");
    /** Nemici che usano le dimensioni drago */
    private static final Set<String> DRAGON_NAMES   = Set.of("L'Ultimo Drago");

    // ── HP bar ────────────────────────────────────────────────────────
    private static final double HP_BAR_H       = 8;
    private static final String HP_BAR_BG      = "#3a1a1a";
    private static final String HP_BAR_FG_HIGH = "#4caf50";
    private static final String HP_BAR_FG_MID  = "#ff9800";
    private static final String HP_BAR_FG_LOW  = "#f44336";
    private static final String BADGE_STUN     = "#a855f7";
    private static final String BADGE_IMMUNE   = "#2196f3";
    private static final String BADGE_BOSS     = "#c0392b";
    private static final String BADGE_EGG      = "#78909c";
    private static final double ENEMY_ROW_W    = COL_LEFT - 28.0;

    // ── Static data ───────────────────────────────────────────────────
    private static final Map<String, String> ROOM_NAMES_EN = Map.of(
        "r1", "Forest",
        "r2", "Goblin Village",
        "r3", "Catacombs",
        "r4", "Treasure Room",
        "r5", "Dragon's Lair"
    );

    private static final Map<String, String> LOOT_WAVE_SPRITES = new HashMap<>();
    static {
        LOOT_WAVE_SPRITES.put("stanza del bastone",  "/assets/weapons/bastone.png");
        LOOT_WAVE_SPRITES.put("stanza dello spadone", "/assets/weapons/spadone.png");
    }

    private static final Map<String, List<String>> ENEMY_SPRITES = new HashMap<>();
    static {
        ENEMY_SPRITES.put("Cinghiale",         List.of("/assets/enemies/Cinghiale1.png","/assets/enemies/cinghiale2.png"));
        ENEMY_SPRITES.put("Lupo",              List.of("/assets/enemies/lupo.png"));
        ENEMY_SPRITES.put("Goblin",            List.of("/assets/enemies/Goblin.png","/assets/enemies/Goblin2.png"));
        ENEMY_SPRITES.put("Goblin Guardia",    List.of("/assets/enemies/goblinGuard.png","/assets/enemies/goblinGuard2.png"));
        ENEMY_SPRITES.put("Re Goblin",         List.of("/assets/enemies/regoblin.png"));
        ENEMY_SPRITES.put("Scheletro",         List.of("/assets/enemies/scheletro.png","/assets/enemies/scheletro2.png"));
        ENEMY_SPRITES.put("Scheletro Guardia", List.of("/assets/enemies/scheletroGuardia.png","/assets/enemies/scheletroGuardia2.png"));
        ENEMY_SPRITES.put("Strega",            List.of("/assets/enemies/Strega.png"));
        ENEMY_SPRITES.put("Uovo",              List.of("/assets/enemies/uovo1.png","/assets/enemies/uovo2.png"));
        ENEMY_SPRITES.put("Cucciolo Drago",    List.of("/assets/enemies/cucciolo1.png","/assets/enemies/cucciolo2.png"));
        ENEMY_SPRITES.put("Cucciolo Uovo",     List.of("/assets/enemies/cuccioloUovo1.png","/assets/enemies/cuccioloUovo2.png"));
        ENEMY_SPRITES.put("L'Ultimo Drago",    List.of("/assets/enemies/UltimoDrago.png"));
    }

    private static final Map<String, String> ROOM_BG = new HashMap<>();
    static {
        ROOM_BG.put("r1", "/assets/backgrounds/foresta.png");
        ROOM_BG.put("r2", "/assets/backgrounds/GoblinVillage.png");
        ROOM_BG.put("r3", "/assets/backgrounds/catacombe.png");
        ROOM_BG.put("r4", "/assets/backgrounds/Caverne.png");
        ROOM_BG.put("r5", "/assets/backgrounds/StanzaFinale.png");
    }

    // ── State ─────────────────────────────────────────────────────────
    private Font pixelFont;
    private Font pixelFontSmall;
    private Font pixelFontAction;
    private Font pixelFontTiny;

    private final BorderPane       root;
    private final GameController   gc;
    private final Stage            stage;
    private final FxApp            app;
    private final EquipmentManager equipmentManager;
    private final CombatController combatController;

    private final StackPane paneEncounter  = new StackPane();
    private final VBox      paneEnemyStats = new VBox();
    private final VBox      paneCharacter  = new VBox();
    private final VBox      paneAction     = new VBox();
    private final StackPane paneRightTop   = new StackPane();
    private final VBox      paneLog        = new VBox();

    private final List<String> logEntries = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────

    public GameScreen(GameController gc, Stage stage, FxApp app) {
        this.gc               = gc;
        this.stage            = stage;
        this.app              = app;
        this.root             = new BorderPane();
        this.equipmentManager = new EquipmentManager(player());
        this.combatController = new CombatController(
                gc, gc.getGameState().getDungeonMap());
        combatController.setListener(new CombatListenerImpl());
        loadFont();
        buildLayout();
        refresh();
        handleLootWaveAutoAdvance();
        stage.setOnCloseRequest(e -> Platform.exit());
    }

    public BorderPane getRoot() { return root; }

    // ── Public refresh ────────────────────────────────────────────────

    public void refresh() {
        buildEncounterPanel();
        buildEnemyStatsPanel();
        buildCharacterPanel();
        buildMapPanel();
        buildLogPanel();
        buildActionPanel();
    }

    public void addLogEntry(String message) {
        logEntries.add(message);
        buildLogPanel();
    }

    // ── Font loader ───────────────────────────────────────────────────

    private void loadFont() {
        try {
            pixelFont       = Font.loadFont(getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), FONT_SIZE);
            pixelFontSmall  = Font.loadFont(getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), 10);
            pixelFontAction = Font.loadFont(getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), ACTION_FONT_SIZE);
            pixelFontTiny   = Font.loadFont(getClass().getResourceAsStream("/assets/fonts/PressStart2P-Regular.ttf"), 8);
        } catch (Exception ignored) {}
        if (pixelFont       == null) pixelFont       = Font.font("Courier New", FontWeight.BOLD, FONT_SIZE);
        if (pixelFontSmall  == null) pixelFontSmall  = Font.font("Courier New", FontWeight.BOLD, 10);
        if (pixelFontAction == null) pixelFontAction = Font.font("Courier New", FontWeight.BOLD, ACTION_FONT_SIZE);
        if (pixelFontTiny   == null) pixelFontTiny   = Font.font("Courier New", FontWeight.BOLD, 8);
    }

    // =========================================================================
    // COMBAT ACTIONS
    // =========================================================================

    private void onAttack() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null) return;
        List<Enemy> alive = aliveTargets(wave);
        if (alive.isEmpty()) return;
        if (alive.size() == 1) {
            executeTurn(combatController.playerNormalAttack(alive.get(0)));
        } else {
            showTargetDialog(alive, target ->
                executeTurn(combatController.playerNormalAttack(target)));
        }
    }

    private void onSpecialAttack() {
        List<SpecialAttack> specials = equippedSpecials();
        if (specials.isEmpty()) {
            addLogEntry("[!] Nessun attacco speciale disponibile.");
            return;
        }
        showSpecialDialog(specials);
    }

    private void onInventory() { showInventoryDialog(); }

    private void onRun() { executeTurn(combatController.playerFlee()); }

    private void onSave() {
        gc.saveGame();
        addLogEntry("\u2726 Partita salvata.");
    }

    // ── Execute a turn result ─────────────────────────────────────────

    private void executeTurn(CombatController.TurnResult result) {
        if (result == null) return;
        result.log().forEach(this::addLogEntry);

        if (result.fleeSuccess()) {
            addLogEntry("[FUGA] Sei fuggito dalla stanza!");
            refresh();
            return;
        }
        if (result.playerDead()) {
            refresh();
            showGameOverOverlay();
            return;
        }
        if (result.waveCleared()) {
            Wave wave = gc.getCurrentRoom().getCurrentWave();
            gc.checkWaveCleared();
            addLogEntry("\u2605 Ondata completata!");
            if (wave != null) {
                wave.getLoot().forEach(item -> addLogEntry("\u2726 Hai ottenuto: " + item.getName()));
            }
            refresh();
            if (gc.getGameState().isVictory()) {
                showVictoryOverlay();
                return;
            }
            handleLootWaveAutoAdvance();
            return;
        }
        refresh();
    }

    private void handleLootWaveAutoAdvance() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null) return;
        if (!wave.getEnemies().isEmpty()) return;
        wave.setCleared(true);
        List<Item> loot = new ArrayList<>(wave.getLoot());
        gc.collectWaveLoot(wave);
        loot.forEach(item -> addLogEntry("\u2726 Hai trovato: " + item.getName()));
        refresh();
        buildActionPanel();
    }

    // ── Dialogs ───────────────────────────────────────────────────────

    private void showTargetDialog(List<Enemy> targets, java.util.function.Consumer<Enemy> callback) {
        Alert dlg = new Alert(Alert.AlertType.NONE);
        dlg.setTitle("Scegli bersaglio");
        dlg.setHeaderText(null);
        VBox box = new VBox(8);
        box.setPadding(new Insets(12));
        ToggleGroup tg = new ToggleGroup();
        for (Enemy e : targets) {
            RadioButton rb = new RadioButton(
                e.getName() + "  HP:" + e.getCurrentHp() + "/" + e.getMaxHp());
            rb.setToggleGroup(tg);
            rb.setUserData(e);
            box.getChildren().add(rb);
        }
        ((RadioButton) box.getChildren().get(0)).setSelected(true);
        dlg.getDialogPane().setContent(box);
        dlg.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK && tg.getSelectedToggle() != null) {
                callback.accept((Enemy) tg.getSelectedToggle().getUserData());
            }
        });
    }

    private void showSpecialDialog(List<SpecialAttack> specials) {
        Alert dlg = new Alert(Alert.AlertType.NONE);
        dlg.setTitle("Attacco Speciale");
        dlg.setHeaderText(null);
        VBox box = new VBox(8);
        box.setPadding(new Insets(12));
        ToggleGroup tg = new ToggleGroup();
        for (SpecialAttack s : specials) {
            boolean ok = player().canUseSpecial(s.getStaminaCost());
            String label = s.getName() + "  STA:" + s.getStaminaCost()
                + "  \u2014 " + s.getDescription() + (ok ? "" : "  [STAMINA INSUFF.]");
            RadioButton rb = new RadioButton(label);
            rb.setToggleGroup(tg);
            rb.setUserData(s);
            rb.setDisable(!ok);
            box.getChildren().add(rb);
        }
        box.getChildren().stream()
            .filter(n -> !n.isDisabled())
            .findFirst()
            .ifPresent(n -> ((RadioButton) n).setSelected(true));
        dlg.getDialogPane().setContent(box);
        dlg.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK || tg.getSelectedToggle() == null) return;
            SpecialAttack chosen = (SpecialAttack) tg.getSelectedToggle().getUserData();
            Wave wave = gc.getCurrentRoom().getCurrentWave();
            if (wave == null) return;
            if (isAoe(chosen)) {
                executeTurn(combatController.playerAoeAttack(chosen));
            } else {
                List<Enemy> alive = aliveTargets(wave);
                if (alive.isEmpty()) return;
                if (alive.size() == 1) {
                    executeTurn(combatController.playerSpecialAttack(chosen, alive.get(0)));
                } else {
                    showTargetDialog(alive, target ->
                        executeTurn(combatController.playerSpecialAttack(chosen, target)));
                }
            }
        });
    }

    private void showInventoryDialog() {
        List<Item> inv = gc.getPlayer().getInventory();
        Alert dlg = new Alert(Alert.AlertType.NONE);
        dlg.setTitle("Inventario");
        dlg.setHeaderText(null);
        if (inv.isEmpty()) {
            dlg.setContentText("L'inventario \u00e8 vuoto.");
            dlg.getButtonTypes().add(ButtonType.OK);
            dlg.showAndWait();
            return;
        }
        VBox box = new VBox(8);
        box.setPadding(new Insets(12));
        ToggleGroup tg = new ToggleGroup();
        for (Item item : inv) {
            RadioButton rb = new RadioButton(item.getName() + " \u2014 " + item.getDescription());
            rb.setToggleGroup(tg);
            rb.setUserData(item);
            box.getChildren().add(rb);
        }
        ((RadioButton) box.getChildren().get(0)).setSelected(true);
        ButtonType useBtn   = new ButtonType("Usa",   ButtonBar.ButtonData.OK_DONE);
        ButtonType equipBtn = new ButtonType("Equip", ButtonBar.ButtonData.OTHER);
        dlg.getDialogPane().setContent(box);
        dlg.getButtonTypes().addAll(useBtn, equipBtn, ButtonType.CANCEL);
        dlg.showAndWait().ifPresent(bt -> {
            if (tg.getSelectedToggle() == null) return;
            Item selected = (Item) tg.getSelectedToggle().getUserData();
            if (bt == useBtn)   useItem(selected);
            else if (bt == equipBtn) equipItem(selected);
        });
    }

    private void useItem(Item item) {
        if (item instanceof it.unicam.cs.mpgc.rpg123891.model.item.Potion) {
            gc.useFirstPotion();
            addLogEntry("\u2726 Hai usato una Pozione! +10 HP, +5 Stamina.");
        } else if (item instanceof it.unicam.cs.mpgc.rpg123891.model.item.Meat) {
            item.use(player());
            gc.getPlayer().getInventory().remove(item);
            addLogEntry("\u2726 Hai mangiato Carne! +10 HP, +2 Stamina.");
        } else {
            addLogEntry("[!] Questo oggetto non pu\u00f2 essere usato direttamente.");
        }
        refresh();
    }

    private void equipItem(Item item) {
        if (!(item instanceof Weapon w)) {
            addLogEntry("[!] Solo le armi possono essere equipaggiate.");
            return;
        }
        EquipmentManager.EquipResult result = equipmentManager.equip(w);
        if (result.success()) {
            addLogEntry("\u2726 Hai equipaggiato: " + w.getName());
        } else {
            addLogEntry("[!] " + result.message());
        }
        refresh();
    }

    private void showGameOverOverlay() {
        showEndOverlay("GAME OVER", "Sei caduto nelle tenebre del dungeon.", false);
    }

    private void showVictoryOverlay() {
        showEndOverlay("VITTORIA!", "L'Ultimo Drago \u00e8 stato sconfitto. Sei un leggendario eroe!", true);
    }

    private void showEndOverlay(String title, String message, boolean victory) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color:rgba(0,0,0,0.78);");
        overlay.setPrefSize(WIN_W, WIN_H);
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        Label titleLbl = new Label(title);
        titleLbl.setFont(pixelFont != null ? pixelFont : Font.font("Courier New", FontWeight.BOLD, 20));
        titleLbl.setStyle("-fx-text-fill:" + (victory ? LABEL_FG : "#f44336") + ";");
        Label msgLbl = new Label(message);
        msgLbl.setFont(pixelFontSmall);
        msgLbl.setStyle("-fx-text-fill:" + WHITE + ";");
        msgLbl.setWrapText(true);
        msgLbl.setMaxWidth(500);
        msgLbl.setAlignment(Pos.CENTER);
        Button btnMenu = new Button("Torna al Menu");
        btnMenu.setStyle("-fx-background-color:#2a2a4e;-fx-text-fill:" + LABEL_FG + ";" +
            "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:8 24;");
        btnMenu.setOnAction(e -> app.showMenu(stage));
        box.getChildren().addAll(titleLbl, msgLbl, btnMenu);
        overlay.getChildren().add(box);
        if (root.getCenter() instanceof StackPane sp) {
            sp.getChildren().add(overlay);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private List<Enemy> aliveTargets(Wave wave) {
        return wave.getEnemies().stream()
            .filter(e -> e.isAlive() && !e.isImmune()).toList();
    }

    private List<SpecialAttack> equippedSpecials() {
        return gc.getPlayer().getInventory().stream()
            .filter(i -> i instanceof Weapon)
            .flatMap(i -> ((Weapon) i).getSpecialAttacks().stream())
            .toList();
    }

    private boolean isAoe(SpecialAttack s) {
        return s.getName().equals("Onda Magica") || s.getName().equals("Spazzatutto");
    }

    private GameCharacter player() {
        return (GameCharacter) gc.getPlayer();
    }

    // =========================================================================
    // PANEL BUILDERS
    // =========================================================================

    private void buildEncounterPanel() {
        paneEncounter.getChildren().clear();
        paneEncounter.setStyle("-fx-background-color:transparent;");
        double clipInset = BORDER_W;
        double clipW     = COL_LEFT - clipInset * 2;
        double clipH     = ROW_TOP  - clipInset * 2;
        String roomId = gc.getCurrentRoom().getId();
        String bgPath = ROOM_BG.getOrDefault(roomId, "/assets/backgrounds/foresta.png");
        try (InputStream is = getClass().getResourceAsStream(bgPath)) {
            if (is != null) {
                Image bgImg  = new Image(is, COL_LEFT, ROW_TOP, false, true);
                ImageView bg = new ImageView(bgImg);
                bg.setFitWidth(COL_LEFT); bg.setFitHeight(ROW_TOP);
                bg.setPreserveRatio(false); bg.setOpacity(0.80);
                Rectangle clip = new Rectangle(clipInset, clipInset, clipW, clipH);
                clip.setArcWidth(RADIUS * 2); clip.setArcHeight(RADIUS * 2);
                bg.setClip(clip);
                StackPane.setAlignment(bg, Pos.CENTER);
                paneEncounter.getChildren().add(bg);
            }
        } catch (Exception ignored) {}
        Wave wave  = gc.getCurrentRoom().getCurrentWave();
        List<Enemy> alive = wave == null ? List.of() :
            wave.getEnemies().stream().filter(Enemy::isAlive).toList();
        if (alive.isEmpty() && wave != null) {
            String key        = wave.getName().toLowerCase();
            String spritePath = LOOT_WAVE_SPRITES.get(key);
            if (spritePath != null) {
                ImageView weaponIv = loadImage(spritePath, LOOT_SPRITE_W, LOOT_SPRITE_H);
                if (weaponIv != null) {
                    StackPane glowBox = new StackPane(weaponIv);
                    glowBox.setStyle("-fx-effect: dropshadow(gaussian, #D4A96A, 28, 0.55, 0, 0);");
                    StackPane.setAlignment(glowBox, Pos.CENTER);
                    // Abbassa lo sprite di LOOT_SPRITE_OFFSET_Y px
                    StackPane.setMargin(glowBox, new Insets(LOOT_SPRITE_OFFSET_Y, 0, 0, 0));
                    paneEncounter.getChildren().add(glowBox);
                }
                if (!wave.getLoot().isEmpty()) {
                    Label lootLbl = new Label("\u2726  " + wave.getLoot().get(0).getName().toUpperCase() + "  \u2726");
                    lootLbl.setFont(pixelFontSmall);
                    lootLbl.setStyle("-fx-text-fill:" + LABEL_FG +
                        ";-fx-background-color:rgba(20,14,44,0.75);-fx-background-radius:6;-fx-padding:4 10;");
                    StackPane.setAlignment(lootLbl, Pos.BOTTOM_CENTER);
                    StackPane.setMargin(lootLbl, new Insets(0, 0, 14, 0));
                    paneEncounter.getChildren().add(lootLbl);
                }
            }
        } else if (!alive.isEmpty()) {
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
            double spriteW, spriteH;
            if (DRAGON_NAMES.contains(name)) {
                spriteW = DRAGON_SPRITE_W;
                spriteH = DRAGON_SPRITE_H;
            } else if (MINIBOSS_NAMES.contains(name)) {
                spriteW = MINIBOSS_SPRITE_W;
                spriteH = MINIBOSS_SPRITE_H;
            } else {
                spriteW = ENEMY_SPRITE_W;
                spriteH = ENEMY_SPRITE_H;
            }
            ImageView iv = loadImage(variants.get(used % variants.size()), spriteW, spriteH);
            if (iv != null) row.getChildren().add(iv);
        }
        return row;
    }

    private void buildEnemyStatsPanel() {
        paneEnemyStats.getChildren().clear();
        paneEnemyStats.setAlignment(Pos.CENTER);
        paneEnemyStats.setStyle("-fx-background-color:transparent;");
        Wave wave  = gc.getCurrentRoom().getCurrentWave();
        List<Enemy> alive = wave == null ? List.of() :
            wave.getEnemies().stream().filter(Enemy::isAlive).toList();
        if (alive.isEmpty()) {
            paneEnemyStats.setPadding(new Insets(14, 20, 10, 20));
            paneEnemyStats.setSpacing(12);
            Label empty = new Label(wave != null && wave.isCleared() ? "Ondata completata!" : "Nessun nemico");
            empty.setFont(pixelFontSmall);
            empty.setStyle("-fx-text-fill:" + WHITE + ";");
            paneEnemyStats.getChildren().add(empty);
            return;
        }
        boolean compact = alive.size() > 3;
        double vPad  = compact ? 6  : 14;
        double vGap  = compact ? 5  : 12;
        double hPad  = compact ? 10 : 20;
        paneEnemyStats.setPadding(new Insets(vPad, hPad, vPad, hPad));
        paneEnemyStats.setSpacing(vGap);
        for (Enemy enemy : alive)
            paneEnemyStats.getChildren().add(buildEnemyStatRow(enemy, compact));
    }

    private VBox buildEnemyStatRow(Enemy enemy, boolean compact) {
        double rowW    = ENEMY_ROW_W;
        Font nameFont  = compact ? pixelFontTiny  : pixelFontSmall;
        Font statFont  = compact ? pixelFontTiny  : pixelFontSmall;
        double barH    = compact ? 5  : HP_BAR_H;
        double rowGap  = compact ? 3  : 5;
        double statsGap = compact ? 8 : 16;

        HBox nameRow = new HBox(compact ? 4 : 6);
        nameRow.setAlignment(Pos.CENTER);
        nameRow.setMaxWidth(rowW);
        Label nameLbl = new Label(enemy.getName());
        nameLbl.setFont(nameFont);
        nameLbl.setStyle("-fx-text-fill:" + LABEL_FG + ";");
        nameRow.getChildren().add(nameLbl);
        if (enemy.isBoss())    nameRow.getChildren().add(badge("BOSS",   BADGE_BOSS));
        if (enemy.isEgg())     nameRow.getChildren().add(badge("EGG",    BADGE_EGG));
        if (enemy.isStunned()) nameRow.getChildren().add(badge("STUN",   BADGE_STUN));
        if (enemy.isImmune())  nameRow.getChildren().add(badge("IMMUNE", BADGE_IMMUNE));

        double hpRatio  = (double) enemy.getCurrentHp() / Math.max(1, enemy.getMaxHp());
        String barColor = hpRatio > 0.5 ? HP_BAR_FG_HIGH : hpRatio > 0.25 ? HP_BAR_FG_MID : HP_BAR_FG_LOW;
        StackPane hpBar = buildBar(rowW, barH, Math.max(1, hpRatio * rowW), HP_BAR_BG, barColor);

        HBox statsRow = new HBox(statsGap,
            statChipSmall("ATK",  String.valueOf(enemy.getAttack()),  statFont),
            statChipSmall("DEF",  String.valueOf(enemy.getDefense()), statFont),
            statChipSmall("AGI",  String.valueOf(enemy.getAgility()), statFont),
            statChipSmall("CRIT", String.format("%.0f%%", enemy.getCritChance() * 100), statFont)
        );
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setMaxWidth(rowW);

        VBox row = new VBox(rowGap, nameRow, hpBar, statsRow);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(rowW);
        return row;
    }

    private void buildLogPanel() {
        paneLog.getChildren().clear();
        paneLog.setStyle("-fx-background-color:transparent;");
        paneLog.setAlignment(Pos.TOP_CENTER);
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave != null) {
            Label waveTitle = new Label(wave.getName().toUpperCase());
            waveTitle.setFont(pixelFontTiny);
            waveTitle.setStyle("-fx-text-fill:" + LABEL_FG + ";");
            waveTitle.setWrapText(true);
            waveTitle.setMaxWidth(COL_RIGHT - 24);
            waveTitle.setAlignment(Pos.CENTER);
            waveTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            Label desc = new Label(wave.getDescription());
            desc.setFont(pixelFontTiny);
            desc.setStyle("-fx-text-fill:" + WHITE + ";-fx-line-spacing:3;");
            desc.setWrapText(true);
            desc.setMaxWidth(COL_RIGHT - 24);
            desc.setAlignment(Pos.CENTER);
            desc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            VBox header = new VBox(6, waveTitle, desc);
            header.setPadding(new Insets(10, 12, 8, 12));
            header.setAlignment(Pos.CENTER);
            Region sep = new Region();
            sep.setPrefHeight(1); sep.setMaxWidth(COL_RIGHT - 24);
            sep.setStyle("-fx-background-color:" + BORDER + ";-fx-opacity:0.4;");
            VBox.setMargin(sep, new Insets(0, 12, 0, 12));
            paneLog.getChildren().addAll(header, sep);
        }
        VBox entriesBox = new VBox(4);
        entriesBox.setPadding(new Insets(6, 12, 8, 12));
        entriesBox.setAlignment(Pos.TOP_CENTER);
        if (logEntries.isEmpty()) {
            Label hint = new Label("\u2014 in attesa di azione \u2014");
            hint.setFont(pixelFontTiny);
            hint.setStyle("-fx-text-fill:#555566;");
            hint.setWrapText(true);
            hint.setMaxWidth(COL_RIGHT - 24);
            hint.setAlignment(Pos.CENTER);
            hint.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            entriesBox.getChildren().add(hint);
        } else {
            int start = Math.max(0, logEntries.size() - 12);
            for (int i = start; i < logEntries.size(); i++) {
                String entry = logEntries.get(i);
                String color = entry.startsWith(">") ? "#f44336"
                             : entry.startsWith("\u2726") || entry.startsWith("\u2605") ? LABEL_FG
                             : WHITE;
                Label lbl = new Label(entry);
                lbl.setFont(pixelFontTiny);
                lbl.setStyle("-fx-text-fill:" + color + ";");
                lbl.setWrapText(true);
                lbl.setMaxWidth(COL_RIGHT - 24);
                lbl.setAlignment(Pos.CENTER);
                lbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                entriesBox.getChildren().add(lbl);
            }
        }
        ScrollPane scroll = new ScrollPane(entriesBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-color:transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        entriesBox.heightProperty().addListener((obs, o, n) -> scroll.setVvalue(1.0));
        paneLog.getChildren().add(scroll);
    }

    private void buildMapPanel() {
        paneRightTop.getChildren().clear();
        paneRightTop.setStyle("-fx-background-color:transparent;");
        List<Room> rooms    = gc.getGameState().getDungeonMap().getRooms();
        Room currentRoom    = gc.getCurrentRoom();
        int  currentWaveI   = currentRoom.getWaveIndex();
        double cardW    = COL_RIGHT, cardH = ROW_TOP;
        double lineX    = 28, dotR = 5, waveDotR = 3;
        double textOffX = lineX + dotR + 10;
        double topPad   = 20, areaH = 18, waveH = 14, connH = 10;
        Canvas canvas = new Canvas(cardW, cardH);
        GraphicsContext g = canvas.getGraphicsContext2D();
        Color colGold = Color.web(LABEL_FG), colCleared = Color.web("#447744");
        Color colDim  = Color.web("#555566"), lineColor   = Color.web("#443355");
        double y = topPad;
        for (int ri = 0; ri < rooms.size(); ri++) {
            Room room      = rooms.get(ri);
            boolean isCur  = room.getId().equals(currentRoom.getId());
            boolean isPast = ri < indexOf(rooms, currentRoom);
            if (ri > 0) {
                g.setStroke(lineColor); g.setLineWidth(2);
                g.strokeLine(lineX, y, lineX, y + connH); y += connH;
            }
            Color dotFill = isCur ? colGold : isPast ? colCleared : colDim;
            g.setFill(dotFill);
            g.fillOval(lineX - dotR, y + areaH / 2 - dotR, dotR * 2, dotR * 2);
            g.setFont(pixelFontSmall != null ? pixelFontSmall : Font.font("Courier New", 10));
            g.fillText(ROOM_NAMES_EN.getOrDefault(room.getId(), room.getName()).toUpperCase(), textOffX, y + areaH / 2 + 4);
            y += areaH;
            for (int wi = 0; wi < room.getWaves().size(); wi++) {
                Wave wv          = room.getWaves().get(wi);
                boolean isActive = isCur && wi == currentWaveI;
                Color wc = isActive ? colGold : (wv.isCleared() || isPast) ? colCleared : colDim;
                double indentX = lineX + 12;
                g.setStroke(lineColor); g.setLineWidth(1);
                g.strokeLine(lineX, y + waveH / 2, indentX - waveDotR - 2, y + waveH / 2);
                g.setFill(wc);
                g.fillOval(indentX - waveDotR, y + waveH / 2 - waveDotR, waveDotR * 2, waveDotR * 2);
                g.setFont(pixelFontTiny != null ? pixelFontTiny : Font.font("Courier New", 8));
                g.fillText(waveLabel(wi, room.getWaves().size(), wv.getName()), indentX + waveDotR + 6, y + waveH / 2 + 3);
                y += waveH;
            }
        }
        paneRightTop.getChildren().add(canvas);
        StackPane.setAlignment(canvas, Pos.TOP_LEFT);
    }

    private String waveLabel(int index, int total, String name) {
        String lower = name.toLowerCase();
        if (lower.contains("bastone"))  return "\u2726 Magic Staff";
        if (lower.contains("spadone"))  return "\u2726 Greatsword";
        if (lower.contains("boss") || lower.contains("miniboss")) {
            if (lower.contains("finale") || lower.contains("final")) return "Final Boss";
            if (lower.contains("re goblin") || lower.contains("king")) return "Miniboss: Goblin King";
            if (lower.contains("strega") || lower.contains("witch"))   return "Miniboss: Witch";
            return "Boss";
        }
        return "Wave " + (index + 1);
    }

    private int indexOf(List<Room> rooms, Room target) {
        for (int i = 0; i < rooms.size(); i++)
            if (rooms.get(i).getId().equals(target.getId())) return i;
        return 0;
    }

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
        portraitBox.setStyle("-fx-background-color:#0d0d1f;-fx-border-color:" + BORDER +
            ";-fx-border-width:3;-fx-border-radius:" + PORTRAIT_RADIUS + ";-fx-background-radius:" + PORTRAIT_RADIUS + ";");
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
        if (p instanceof Mage m && m.isMagicShieldActive())
            statsBox.getChildren().add(badge("MAGIC SHIELD", "#2196f3"));
        if (p instanceof Thief t && t.isStealthBonusActive())
            statsBox.getChildren().add(badge("STEALTH", "#a855f7"));
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

    private void buildActionPanel() {
        paneAction.getChildren().clear();
        paneAction.setAlignment(Pos.CENTER);
        paneAction.setStyle("-fx-background-color:transparent;");
        paneAction.setPadding(new Insets(18, 20, 14, 20));
        paneAction.setSpacing(10);
        Room currentRoom = gc.getCurrentRoom();
        Wave currentWave = currentRoom.getCurrentWave();
        boolean isLootWave = currentWave != null
            && currentWave.getEnemies().isEmpty()
            && currentWave.isCleared();
        boolean roomCleared = currentRoom.isCleared();
        if (roomCleared && gc.getGameState().getDungeonMap().hasNextRoom()) {
            Button btnAdvance = makeBorderedButtonWithIcon("AVANZA", "/assets/icons/arrow.svg");
            btnAdvance.setMaxWidth(Double.MAX_VALUE);
            btnAdvance.setOnAction(e -> {
                gc.advanceRoom();
                logEntries.clear();
                addLogEntry("\u2726 Sei entrato in: " + gc.getCurrentRoom().getName());
                refresh();
                handleLootWaveAutoAdvance();
            });
            paneAction.getChildren().add(btnAdvance);
        } else if (isLootWave) {
            Button btnNext = makeBorderedButtonWithIcon("PROCEDI", "/assets/icons/arrow.svg");
            btnNext.setMaxWidth(Double.MAX_VALUE);
            btnNext.setOnAction(e -> {
                gc.checkWaveCleared();
                refresh();
            });
            paneAction.getChildren().add(btnNext);
        } else {
            long potions = gc.countPotions();
            Button btnAttack    = makeTextButtonWithIcon("ATTACK",    "/assets/icons/arrow.svg", false);
            Button btnSAttack   = makeTextButtonWithIcon("S. ATTACK", "/assets/icons/arrow.svg", true);
            Button btnInventory = makeTextButtonWithIcon("INVENTORY (" + potions + ")", "/assets/icons/arrow.svg", false);
            Button btnRun       = makeTextButtonWithIcon("RUN",        "/assets/icons/arrow.svg", true);
            btnRun.setDisable(!gc.canFlee());
            btnAttack   .setOnAction(e -> onAttack());
            btnSAttack  .setOnAction(e -> onSpecialAttack());
            btnInventory.setOnAction(e -> onInventory());
            btnRun      .setOnAction(e -> onRun());
            paneAction.getChildren().addAll(
                makeSplitRow(btnAttack, btnSAttack),
                makeSplitRow(btnInventory, btnRun),
                makeSaveMenuRow()
            );
        }
    }

    // =========================================================================
    // LAYOUT
    // =========================================================================

    private void buildLayout() {
        Canvas bgCanvas = new Canvas(WIN_W, WIN_H);
        drawGrid(bgCanvas);
        bgCanvas.setMouseTransparent(true);
        StackPane cardEncounter  = makeCardWithTitle("ENCOUNTER",   paneEncounter,  COL_LEFT,  ROW_TOP);
        StackPane cardCharacter  = makeCardWithTitle("CHARACTER",   paneCharacter,  COL_MID,   ROW_TOP);
        StackPane cardRightTop   = makeCardWithTitle("MAP",          paneRightTop,   COL_RIGHT, ROW_TOP);
        StackPane cardEnemyStats = makeCardWithTitle("ENEMY STATS",  paneEnemyStats, COL_LEFT,  ROW_BOT);
        StackPane cardAction     = makeCardWithTitle("ACTION",       paneAction,     COL_MID,   ROW_BOT);
        StackPane cardLog        = makeCardWithTitle("COMBAT LOG",   paneLog,        COL_RIGHT, ROW_BOT);
        HBox rowTop = new HBox(GAP, cardEncounter, cardCharacter, cardRightTop);
        rowTop.setAlignment(Pos.BOTTOM_LEFT);
        rowTop.setPrefHeight(ROW_TOP + LABEL_OFFSET);
        rowTop.setMinHeight(ROW_TOP + LABEL_OFFSET);
        rowTop.setMaxHeight(ROW_TOP + LABEL_OFFSET);
        HBox rowBot = new HBox(GAP, cardEnemyStats, cardAction, cardLog);
        rowBot.setAlignment(Pos.BOTTOM_LEFT);
        rowBot.setPrefHeight(ROW_BOT + LABEL_OFFSET);
        rowBot.setMinHeight(ROW_BOT + LABEL_OFFSET);
        rowBot.setMaxHeight(ROW_BOT + LABEL_OFFSET);
        Label sysContent = new Label("DUNGEON RPG  v1.0  \u2014  by Lorenzo Grassi");
        sysContent.setFont(pixelFontSmall);
        sysContent.setStyle("-fx-text-fill:" + SYS_TEXT + ";");
        sysContent.setAlignment(Pos.CENTER); sysContent.setMaxWidth(Double.MAX_VALUE);
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
        Canvas gridOverlay = buildGridOverlay(new double[][]{
            {xOff + COL_LEFT + GAP,                 yTop2, COL_MID,   ROW_TOP},
            {xOff + COL_LEFT + GAP + COL_MID + GAP, yTop2, COL_RIGHT, ROW_TOP},
            {xOff,                                  yBot2, COL_LEFT,  ROW_BOT},
            {xOff + COL_LEFT + GAP,                 yBot2, COL_MID,   ROW_BOT},
            {xOff + COL_LEFT + GAP + COL_MID + GAP, yBot2, COL_RIGHT, ROW_BOT}
        });
        gridOverlay.setMouseTransparent(true);
        StackPane stack = new StackPane(bgCanvas, mainBox, gridOverlay);
        stack.setPrefSize(WIN_W, WIN_H); stack.setMinSize(WIN_W, WIN_H); stack.setMaxSize(WIN_W, WIN_H);
        stack.setAlignment(Pos.TOP_LEFT);
        stack.setStyle("-fx-background-color:" + BG + ";");
        root.setCenter(stack);
    }

    // ── Widget factories ──────────────────────────────────────────────

    private StackPane makeCardWithTitle(String title, Region content, double w, double h) {
        content.setPrefSize(w, h); content.setMinSize(w, h); content.setMaxSize(w, h);
        StackPane card = new StackPane(content);
        card.setPrefSize(w, h); card.setMinSize(w, h); card.setMaxSize(w, h);
        card.setStyle("-fx-background-color:" + CARD_BG + ";-fx-border-color:" + BORDER +
            ";-fx-border-width:" + BORDER_W + ";-fx-border-radius:" + RADIUS + ";-fx-background-radius:" + RADIUS + ";");
        Label lbl = new Label("  " + title + "  ");
        lbl.setFont(pixelFont); lbl.setPrefHeight(LABEL_H);
        lbl.setStyle("-fx-text-fill:" + LABEL_FG + ";-fx-background-color:" + CARD_BG +
            ";-fx-border-color:" + BORDER + ";-fx-border-width:" + BORDER_W +
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

    private HBox makeSplitRow(Button left, Button right) {
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(left, spacer, right);
        row.setAlignment(Pos.CENTER_LEFT); row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private HBox makeSaveMenuRow() {
        Button btnSave = makeBorderedButtonWithIcon("SAVE", "/assets/icons/save.svg");
        Button btnMenu = makeBorderedButtonWithIcon("MENU", "/assets/icons/exit.svg");
        btnSave.setOnAction(e -> onSave());
        btnMenu.setOnAction(e -> app.showMenu(stage));
        btnSave.setMaxWidth(Double.MAX_VALUE); btnMenu.setMaxWidth(Double.MAX_VALUE);
        HBox row = new HBox(10, btnSave, btnMenu);
        row.setAlignment(Pos.CENTER);
        HBox.setHgrow(btnSave, Priority.ALWAYS); HBox.setHgrow(btnMenu, Priority.ALWAYS);
        return row;
    }

    private Button makeTextButtonWithIcon(String text, String iconPath, boolean pushRight) {
        String baseStyle = "-fx-background-color:transparent;-fx-border-color:transparent;" +
            "-fx-border-width:0;-fx-padding:8 4;-fx-cursor:hand;";
        ImageView iconWhite = SvgUtil.load(iconPath, WHITE,    ACTION_ICON_SIZE);
        ImageView iconGold  = SvgUtil.load(iconPath, LABEL_FG, ACTION_ICON_SIZE);
        Label lbl = new Label(text);
        lbl.setFont(pixelFontAction);
        lbl.setStyle("-fx-text-fill:" + WHITE + ";");
        HBox content = new HBox(8);
        content.setAlignment(Pos.CENTER_LEFT);
        if (iconWhite != null) content.getChildren().add(iconWhite);
        content.getChildren().add(lbl);
        Button btn = new Button(); btn.setGraphic(content); btn.setStyle(baseStyle);
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
        String base  = "-fx-background-color:" + CARD_BG + ";-fx-border-color:" + BORDER +
            ";-fx-border-width:2;-fx-border-radius:" + RADIUS + ";-fx-background-radius:" + RADIUS +
            ";-fx-padding:6 10;-fx-cursor:hand;";
        String hover = "-fx-background-color:#1e1640;-fx-border-color:" + BORDER +
            ";-fx-border-width:2;-fx-border-radius:" + RADIUS + ";-fx-background-radius:" + RADIUS +
            ";-fx-padding:6 10;-fx-cursor:hand;";
        ImageView iconGold  = SvgUtil.load(iconPath, LABEL_FG, ICON_SIZE);
        ImageView iconWhite = SvgUtil.load(iconPath, WHITE,    ICON_SIZE);
        Label lbl = new Label(text); lbl.setFont(pixelFontSmall);
        lbl.setStyle("-fx-text-fill:" + LABEL_FG + ";");
        HBox content = new HBox(6); content.setAlignment(Pos.CENTER);
        if (iconGold != null) content.getChildren().add(iconGold);
        content.getChildren().add(lbl);
        Button btn = new Button(); btn.setGraphic(content); btn.setStyle(base); btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hover); lbl.setStyle("-fx-text-fill:" + WHITE + ";");
            if (!content.getChildren().isEmpty() && iconWhite != null) content.getChildren().set(0, iconWhite);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(base); lbl.setStyle("-fx-text-fill:" + LABEL_FG + ";");
            if (!content.getChildren().isEmpty() && iconGold != null) content.getChildren().set(0, iconGold);
        });
        return btn;
    }

    // ── Small helpers ─────────────────────────────────────────────────

    private HBox equipRow(String label, String value) {
        Label lbl = new Label(label + ": "); lbl.setFont(pixelFontSmall); lbl.setStyle("-fx-text-fill:" + LABEL_FG + ";");
        Label val = new Label(value);         val.setFont(pixelFontSmall); val.setStyle("-fx-text-fill:" + WHITE + ";");
        HBox row = new HBox(0, lbl, val); row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label statLine(String text, String color, Font font) {
        Label l = new Label(text); l.setFont(font);
        l.setStyle("-fx-text-fill:" + color + ";"); l.setWrapText(false);
        return l;
    }

    private VBox statChip(String key, String val) {
        Label k = new Label(key); k.setFont(pixelFontTiny); k.setStyle("-fx-text-fill:#888888;");
        Label v = new Label(val); v.setFont(pixelFontSmall); v.setStyle("-fx-text-fill:" + WHITE + ";");
        VBox chip = new VBox(2, k, v); chip.setAlignment(Pos.CENTER);
        return chip;
    }

    private VBox statChipSmall(String key, String val, Font valFont) {
        Label k = new Label(key); k.setFont(pixelFontTiny); k.setStyle("-fx-text-fill:#888888;");
        Label v = new Label(val); v.setFont(valFont);        v.setStyle("-fx-text-fill:" + WHITE + ";");
        VBox chip = new VBox(1, k, v); chip.setAlignment(Pos.CENTER);
        return chip;
    }

    private Label badge(String text, String color) {
        Label b = new Label(text); b.setFont(pixelFontTiny);
        b.setStyle("-fx-text-fill:#ffffff;-fx-background-color:" + color +
            ";-fx-background-radius:3;-fx-padding:1 4;");
        return b;
    }

    private StackPane buildBar(double totalW, double h, double fillW, String bgColor, String fgColor) {
        Rectangle bg   = new Rectangle(totalW, h); bg.setFill(Color.web(bgColor)); bg.setArcWidth(h); bg.setArcHeight(h);
        Rectangle fill = new Rectangle(fillW,  h); fill.setFill(Color.web(fgColor)); fill.setArcWidth(h); fill.setArcHeight(h);
        StackPane sp = new StackPane(bg, fill); sp.setAlignment(Pos.CENTER_LEFT);
        sp.setPrefSize(totalW, h); sp.setMaxSize(totalW, h); sp.setMinSize(totalW, h);
        return sp;
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

    private void drawGrid(Canvas canvas) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web(BG)); g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.setStroke(Color.web("#ffffff", GRID_OPACITY)); g.setLineWidth(1);
        for (double x = 0; x <= canvas.getWidth();  x += GRID_SIZE) g.strokeLine(x, 0, x, canvas.getHeight());
        for (double y = 0; y <= canvas.getHeight(); y += GRID_SIZE) g.strokeLine(0, y, canvas.getWidth(), y);
    }

    private Canvas buildGridOverlay(double[][] cards) {
        Canvas canvas = new Canvas(WIN_W, WIN_H);
        GraphicsContext g = canvas.getGraphicsContext2D();
        for (double[] c : cards) {
            double cx = c[0], cy = c[1], cw = c[2], ch = c[3], inset = BORDER_W + 1;
            g.save(); g.beginPath();
            g.moveTo(cx + inset + RADIUS, cy + inset);
            g.arcTo(cx + cw - inset, cy + inset,      cx + cw - inset, cy + ch - inset, RADIUS);
            g.arcTo(cx + cw - inset, cy + ch - inset, cx + inset,      cy + ch - inset, RADIUS);
            g.arcTo(cx + inset,      cy + ch - inset, cx + inset,      cy + inset,      RADIUS);
            g.arcTo(cx + inset,      cy + inset,      cx + cw - inset, cy + inset,      RADIUS);
            g.closePath(); g.clip();
            g.setStroke(Color.web("#ffffff", GRID_OPACITY)); g.setLineWidth(1);
            for (double x = cx - (cx % GRID_SIZE); x <= cx + cw; x += GRID_SIZE)
                g.strokeLine(x, cy + inset, x, cy + ch - inset);
            for (double y = cy - (cy % GRID_SIZE); y <= cy + ch; y += GRID_SIZE)
                g.strokeLine(cx + inset, y, cx + cw - inset, y);
            g.restore();
        }
        return canvas;
    }

    // ── CombatListener ────────────────────────────────────────────────
    private class CombatListenerImpl implements CombatController.CombatListener {
        @Override public void onEvent(String msg) { addLogEntry(msg); }
        @Override public void onTurnEnd(List<String> log, boolean pd, boolean wc) {}
    }
}
