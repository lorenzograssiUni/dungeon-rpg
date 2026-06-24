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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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

    // Dimensioni fisse delle card — non cambiano al ridimensionamento della finestra
    private static final double ENCOUNTER_W = 640;
    private static final double ENCOUNTER_H = 320;
    private static final double LOG_H       = 170;
    private static final double RIGHT_W     = 260;
    private static final double ACTION_H    = 220;

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final String BG_DARK    = "#0d0d1f";
    private static final String BG_PANEL   = "#12122a";
    private static final String BG_PANEL2  = "#0a0a1a";
    private static final String BORDER_COL = "#8b6914";
    private static final String GOLD       = "#e0c46c";
    private static final String WHITE      = "#cccccc";
    private static final String RED        = "#e05555";
    private static final String GREEN      = "#55e077";
    private static final String ORANGE     = "#e0a030";
    private static final String BLUE       = "#4a9eff";

    private Font pixelFont;
    private Font pixelFontSmall;

    private final BorderPane    root;
    private final GameController gc;
    private final Stage          stage;
    private final FxApp          app;
    private final CombatController  combatController;
    private final EquipmentManager  equipmentManager;

    private final StackPane encounterPane  = new StackPane();
    private final VBox      characterPanel = new VBox(5);
    private final VBox      actionPanel    = new VBox(4);
    private final TextArea  logArea        = new TextArea();

    private Enemy  selectedEnemy  = null;
    private String lastLoggedWave = null;

    // ── Mappe sprite ──────────────────────────────────────────────────────────
    private static final Map<String, String> ENEMY_SPRITE = Map.ofEntries(
        Map.entry("Cinghiale",           "/assets/enemies/Cinghiale1.png"),
        Map.entry("Cinghiale Feroce",    "/assets/enemies/cinghiale2.png"),
        Map.entry("Lupo",                "/assets/enemies/lupo.png"),
        Map.entry("Goblin",              "/assets/enemies/Goblin.png"),
        Map.entry("Goblin Guerriero",    "/assets/enemies/Goblin2.png"),
        Map.entry("Goblin Guardia",      "/assets/enemies/goblinGuard.png"),
        Map.entry("Re Goblin",           "/assets/enemies/regoblin.png"),
        Map.entry("Scheletro",           "/assets/enemies/scheletro.png"),
        Map.entry("Scheletro Antico",    "/assets/enemies/scheletro2.png"),
        Map.entry("Scheletro Guardia",   "/assets/enemies/scheletroGuardia.png"),
        Map.entry("Strega",              "/assets/enemies/Strega.png"),
        Map.entry("Uovo",                "/assets/enemies/uovo1.png"),
        Map.entry("Uovo del Drago",      "/assets/enemies/uovo2.png"),
        Map.entry("Cucciolo di Drago",   "/assets/enemies/cucciolo1.png"),
        Map.entry("L'Ultimo Drago",      "/assets/enemies/UltimoDrago.png")
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
        root.setStyle("-fx-background-color:" + BG_DARK + ";");

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
    // LAYOUT — tutte le card hanno dimensioni FISSE (pref=min=max)
    //
    //  HBox center (padding 6)
    //   ├─ leftCol  VBox  w=ENCOUNTER_W
    //   │   ├─ encounterCard  h=ENCOUNTER_H  (sfondo fisso)
    //   │   └─ logCard        h=LOG_H
    //   └─ rightCol VBox  w=RIGHT_W
    //       ├─ charCard   h=fill (cresce per riempire)
    //       └─ actionCard h=ACTION_H
    // =========================================================================
    private void buildLayout() {

        // ── Encounter ─────────────────────────────────────────────────────────
        encounterPane.setPrefSize(ENCOUNTER_W, ENCOUNTER_H);
        encounterPane.setMinSize(ENCOUNTER_W, ENCOUNTER_H);
        encounterPane.setMaxSize(ENCOUNTER_W, ENCOUNTER_H);  // BLOCCATO
        encounterPane.setStyle(innerStyle());
        VBox encounterCard = wrapInCard("ENCOUNTER", encounterPane);
        encounterCard.setPrefSize(ENCOUNTER_W + 4, ENCOUNTER_H + 26);
        encounterCard.setMinSize(ENCOUNTER_W + 4, ENCOUNTER_H + 26);
        encounterCard.setMaxSize(ENCOUNTER_W + 4, ENCOUNTER_H + 26);

        // ── Log ───────────────────────────────────────────────────────────────
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setFont(pixelFontSmall);
        logArea.setStyle(
            "-fx-control-inner-background:" + BG_PANEL2 + ";" +
            "-fx-text-fill:" + WHITE + ";" +
            "-fx-font-size:7px;"
        );
        logArea.setPrefSize(ENCOUNTER_W, LOG_H - 26);
        logArea.setMinSize(ENCOUNTER_W, LOG_H - 26);
        logArea.setMaxSize(ENCOUNTER_W, LOG_H - 26);

        VBox logInner = new VBox(logArea);
        logInner.setStyle("-fx-background-color:" + BG_PANEL2 + ";");
        VBox logCard = wrapInCard("COMBAT LOG / MESSAGES", logInner);
        logCard.setPrefSize(ENCOUNTER_W + 4, LOG_H);
        logCard.setMinSize(ENCOUNTER_W + 4, LOG_H);
        logCard.setMaxSize(ENCOUNTER_W + 4, LOG_H);

        // ── Left column ───────────────────────────────────────────────────────
        VBox leftCol = new VBox(6, encounterCard, logCard);
        leftCol.setPrefWidth(ENCOUNTER_W + 4);
        leftCol.setMinWidth(ENCOUNTER_W + 4);
        leftCol.setMaxWidth(ENCOUNTER_W + 4);

        // ── Character panel ───────────────────────────────────────────────────
        characterPanel.setPadding(new Insets(8));
        characterPanel.setStyle("-fx-background-color:transparent;");
        VBox charCard = wrapInCard("CHARACTER", characterPanel);
        charCard.setPrefWidth(RIGHT_W + 4);
        charCard.setMinWidth(RIGHT_W + 4);
        charCard.setMaxWidth(RIGHT_W + 4);
        // altezza: tutto lo spazio non occupato da actionCard e gap
        double charH = ENCOUNTER_H + LOG_H + 26 * 2 + 6 - ACTION_H - 6;
        charCard.setPrefHeight(charH);
        charCard.setMinHeight(charH);
        charCard.setMaxHeight(charH);

        // ── Action panel ──────────────────────────────────────────────────────
        actionPanel.setPadding(new Insets(8));
        actionPanel.setStyle("-fx-background-color:transparent;");
        VBox actionCard = wrapInCard("ACTION", actionPanel);
        actionCard.setPrefSize(RIGHT_W + 4, ACTION_H);
        actionCard.setMinSize(RIGHT_W + 4, ACTION_H);
        actionCard.setMaxSize(RIGHT_W + 4, ACTION_H);

        // ── Right column ──────────────────────────────────────────────────────
        VBox rightCol = new VBox(6, charCard, actionCard);
        rightCol.setPrefWidth(RIGHT_W + 4);
        rightCol.setMinWidth(RIGHT_W + 4);
        rightCol.setMaxWidth(RIGHT_W + 4);

        // ── Main HBox ─────────────────────────────────────────────────────────
        HBox center = new HBox(6, leftCol, rightCol);
        center.setPadding(new Insets(6));
        center.setAlignment(Pos.TOP_LEFT);

        // ── SysBar ────────────────────────────────────────────────────────────
        Label sysInfo = new Label("DUNGEON RPG  v1.0  -  by Lorenzo Grassi");
        sysInfo.setFont(pixelFontSmall);
        sysInfo.setStyle("-fx-text-fill:" + GOLD + ";-fx-padding:2 8;");
        HBox sysBar = new HBox(sysInfo);
        sysBar.setAlignment(Pos.CENTER);
        sysBar.setStyle("-fx-background-color:#08081a;-fx-border-color:" + BORDER_COL + ";-fx-border-width:1 0 0 0;");

        VBox main = new VBox(0, center, sysBar);
        root.setCenter(main);
    }

    /**
     * Card con titolo centrato stile pixel-RPG.
     * Il contenuto non viene forzato a crescere (VGrow=NEVER)
     * così non altera le dimensioni della card.
     */
    private VBox wrapInCard(String title, Region content) {
        Label titleLabel = new Label("  " + title + "  ");
        titleLabel.setFont(pixelFont);
        titleLabel.setStyle(
            "-fx-text-fill:" + GOLD + ";" +
            "-fx-background-color:" + BG_PANEL + ";" +
            "-fx-border-color:" + BORDER_COL + ";" +
            "-fx-border-width:0 1 1 1;" +
            "-fx-padding:3 10;"
        );
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);

        VBox card = new VBox(0, titleLabel, content);
        card.setStyle(
            "-fx-background-color:" + BG_PANEL + ";" +
            "-fx-border-color:" + BORDER_COL + ";" +
            "-fx-border-width:2;" +
            "-fx-border-radius:0;" +
            "-fx-background-radius:0;"
        );
        // NON impostiamo VGrow sul content: la card mantiene la propria dimensione fissa
        return card;
    }

    // ── REFRESH ───────────────────────────────────────────────────────────────
    private void refresh() {
        refreshEncounter();
        refreshCharacterPanel();
        refreshActionPanel();
        logCurrentWaveIfNew();
    }

    private void selectEnemy(Enemy enemy) {
        if (enemy == selectedEnemy) return;
        selectedEnemy = enemy;
        refreshEncounter();
        appendLog("[MIRA] " + enemy.getName() + " selezionato.");
    }

    // ── Encounter ─────────────────────────────────────────────────────────────
    private void refreshEncounter() {
        encounterPane.getChildren().clear();

        String bgPath = ROOM_BG.getOrDefault(gc.getCurrentRoom().getId(),
                                             "/assets/backgrounds/foresta.png");
        // Sfondo caricato con dimensioni fisse — NON usa bind()
        ImageView bg = loadBg(bgPath);
        if (bg != null) encounterPane.getChildren().add(bg);

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color:rgba(0,0,0,0.15);");
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlay.setMouseTransparent(true);
        encounterPane.getChildren().add(overlay);

        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null || gc.getCurrentRoom().isCleared()) {
            encounterPane.getChildren().add(pixelLabel("- STANZA LIBERATA -", GOLD, 9));
            return;
        }
        List<Enemy> alive = wave.getEnemies().stream().filter(Enemy::isAlive).toList();
        if (alive.isEmpty()) return;

        HBox spritesBox = new HBox(20);
        spritesBox.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(spritesBox, Pos.BOTTOM_CENTER);
        StackPane.setMargin(spritesBox, new Insets(0, 0, 14, 0));
        for (Enemy e : alive) spritesBox.getChildren().add(buildEnemySprite(e));
        encounterPane.getChildren().add(spritesBox);
    }

    private VBox buildEnemySprite(Enemy enemy) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.BOTTOM_CENTER);

        ImageView sprite = loadSprite(ENEMY_SPRITE.get(enemy.getName()), 140);
        if (sprite == null) {
            Label ph = new Label("[" + enemy.getName().substring(0, Math.min(4, enemy.getName().length())) + "]");
            ph.setStyle("-fx-text-fill:" + RED + ";-fx-font-size:20px;-fx-font-weight:bold;");
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
        hpBar.setPrefWidth(110); hpBar.setPrefHeight(8);
        hpBar.setStyle("-fx-accent:" + hpC + ";-fx-background-color:#333;");

        Label nameL = pixelLabel(enemy.getName(), enemy == selectedEnemy ? GOLD : WHITE, 7);
        nameL.setWrapText(false);
        Label hpL = pixelLabel(enemy.getCurrentHp() + "/" + enemy.getMaxHp(), hpC, 6);

        card.getChildren().addAll(nameL, hpBar, hpL);
        card.setPadding(new Insets(0, 4, 0, 4));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> selectEnemy(enemy));
        card.setOnMouseEntered(e -> { if (enemy != selectedEnemy) card.setStyle("-fx-opacity:0.85;"); });
        card.setOnMouseExited(e -> card.setStyle("-fx-opacity:1;"));
        return card;
    }

    // ── Character Panel ───────────────────────────────────────────────────────
    private void refreshCharacterPanel() {
        characterPanel.getChildren().clear();
        GameCharacter p = player();

        String classSprite = switch (p.getCharacterClass()) {
            case WARRIOR -> "/assets/classes/warrior.png";
            case MAGE    -> "/assets/classes/mage.png";
            case THIEF   -> "/assets/classes/thief.png";
            default      -> null;
        };
        HBox portraitRow = new HBox(8);
        portraitRow.setAlignment(Pos.CENTER_LEFT);
        if (classSprite != null) {
            ImageView portrait = loadImage(classSprite, 56, 56, false);
            if (portrait != null) {
                portrait.setStyle("-fx-border-color:" + BORDER_COL + ";-fx-border-width:2;");
                portraitRow.getChildren().add(portrait);
            }
        }
        VBox nameBox = new VBox(3,
            pixelLabel(p.getName(), GOLD, 8),
            pixelLabel(p.getCharacterClass().toString(), WHITE, 7)
        );
        portraitRow.getChildren().add(nameBox);
        characterPanel.getChildren().add(portraitRow);
        characterPanel.getChildren().add(pixelSep());

        double hpRatio = (double) p.getCurrentHp() / p.getMaxHp();
        String hpCol   = hpRatio > 0.5 ? GREEN : hpRatio > 0.25 ? ORANGE : RED;
        characterPanel.getChildren().addAll(
            statRow("HP",  p.getCurrentHp() + "/" + p.getMaxHp(), hpCol),
            progressBar(hpRatio, hpCol),
            statRow("STA", p.getCurrentStamina() + "/" + p.getMaxStamina(), BLUE),
            progressBar(p.getMaxStamina() > 0 ? (double) p.getCurrentStamina() / p.getMaxStamina() : 0, BLUE),
            pixelSep(),
            statRow("ATK", String.valueOf(p.getAttack()),  WHITE),
            statRow("DEF", String.valueOf(p.getDefense()), WHITE),
            statRow("AGI", String.valueOf(p.getAgility()), WHITE),
            statRow("CRI", String.format("%.0f%%", p.getCritChance() * 100), WHITE)
        );

        characterPanel.getChildren().add(pixelSep());
        if (p instanceof Mage m && m.isMagicShieldActive())
            characterPanel.getChildren().add(pixelLabel("[SCUDO MAGICO]", GREEN, 7));
        if (p instanceof Thief t && t.isStealthBonusActive())
            characterPanel.getChildren().add(pixelLabel("[STEALTH]", GREEN, 7));
        if (combatController.isCaricaActive())
            characterPanel.getChildren().add(pixelLabel("[CARICA +3DEF]", GREEN, 7));
        if (combatController.getActiveBurn() != null)
            characterPanel.getChildren().add(pixelLabel(
                "[BURN " + combatController.getActiveBurn().getDamagePerTurn() + "/t]", RED, 7));

        characterPanel.getChildren().add(pixelSep());
        for (EquipSlot slot : EquipSlot.values()) {
            Optional<Weapon> eq = equipmentManager.getEquipped(slot);
            String prefix = switch (slot) { case MAIN_HAND -> "W:"; case OFF_HAND -> "S:"; case BODY -> "A:"; };
            characterPanel.getChildren().add(
                statRow(prefix, eq.map(Weapon::getName).orElse("-"), eq.isPresent() ? GOLD : "#555577"));
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        characterPanel.getChildren().add(spacer);
        characterPanel.getChildren().add(pixelSep());
        Button btnSave = actionBtn("> Save", GOLD, "#1a1a3a");
        Button btnMenu = actionBtn("> Menu", RED,  "#2a0a0a");
        btnSave.setOnAction(e -> { gc.saveGame(); appendLog("[SAVE] Partita salvata."); });
        btnMenu.setOnAction(e -> confirmMenu());
        characterPanel.getChildren().addAll(btnSave, btnMenu);
    }

    // ── Action Panel ──────────────────────────────────────────────────────────
    private void refreshActionPanel() {
        actionPanel.getChildren().clear();
        boolean cleared = gc.getCurrentRoom().isCleared();

        if (!cleared) {
            String[][] defs = {
                {"> Fight",  WHITE, "#1a1a3a"},
                {"> Skills", WHITE, "#1a1a3a"},
                {"> Items",  WHITE, "#1a1a3a"},
                {"> Equip",  WHITE, "#1a1a3a"},
                {"> Run",    RED,   "#2a0a0a"}
            };
            Button[] btns = new Button[defs.length];
            for (int i = 0; i < defs.length; i++) btns[i] = actionBtn(defs[i][0], defs[i][1], defs[i][2]);
            btns[0].setOnAction(e -> doNormalAttack());
            btns[1].setOnAction(e -> showSpecialAtk());
            btns[2].setOnAction(e -> showInventario());
            btns[3].setOnAction(e -> showEquip());
            btns[4].setOnAction(e -> doFlee());
            btns[4].setDisable(!gc.canFlee());
            if (!gc.canFlee()) btns[4].setStyle(pixelBtnStyle("#555", "#2a0a0a"));
            for (Button b : btns) actionPanel.getChildren().add(b);
        } else {
            if (gc.getGameState().getDungeonMap().hasNextRoom()) {
                Button adv = actionBtn("> Avanza >", GREEN, "#0a2a0a");
                adv.setOnAction(e -> doAdvanceRoom());
                actionPanel.getChildren().add(adv);
            } else { showVictory(); }
        }
    }

    // ── Sub-screens ───────────────────────────────────────────────────────────
    private void showInventario() {
        characterPanel.getChildren().clear();
        characterPanel.getChildren().add(pixelLabel("INVENTARIO", GOLD, 9));
        characterPanel.getChildren().add(pixelSep());
        List<Item> inv = player().getInventory();
        if (inv.isEmpty()) {
            characterPanel.getChildren().add(pixelLabel("(vuoto)", WHITE, 8));
        } else {
            LinkedHashMap<String, Long> cons = new LinkedHashMap<>();
            for (Item item : inv) if (!(item instanceof Weapon)) cons.merge(item.getName(), 1L, Long::sum);
            cons.forEach((nome, count) -> {
                Button b = actionBtn("> " + nome + (count > 1 ? " x" + count : ""), WHITE, "#0d0d25");
                b.setOnAction(e -> {
                    Item found = inv.stream().filter(i -> !(i instanceof Weapon) && i.getName().equals(nome)).findFirst().orElse(null);
                    if (found == null) return;
                    if (found instanceof Potion) { gc.useFirstPotion(); appendLog("[ITEM] Pozione usata! +10 HP, +5 Stamina."); }
                    else if (found instanceof Meat meat) { meat.use(player()); player().getInventory().remove(meat); appendLog("[ITEM] Carne mangiata! +10 HP, +2 Stamina."); }
                    showInventario(); refreshCharacterPanel();
                });
                characterPanel.getChildren().add(b);
            });
            for (Item item : inv) {
                if (item instanceof Weapon w) {
                    Button b = actionBtn("> [W] " + w.getName(), GOLD, "#0d0d25");
                    b.setOnAction(e -> showEquipPreview(w));
                    characterPanel.getChildren().add(b);
                }
            }
        }
        characterPanel.getChildren().add(pixelSep());
        Button back = actionBtn("< Indietro", WHITE, "#1a1a3a");
        back.setOnAction(e -> refreshCharacterPanel());
        characterPanel.getChildren().add(back);
    }

    private void showEquip() {
        characterPanel.getChildren().clear();
        characterPanel.getChildren().add(pixelLabel("EQUIPAGGIAMENTO", GOLD, 9));
        characterPanel.getChildren().add(pixelSep());
        for (EquipSlot slot : EquipSlot.values()) {
            String prefix = switch (slot) { case MAIN_HAND -> "W: "; case OFF_HAND -> "S: "; case BODY -> "A: "; };
            Optional<Weapon> eq = equipmentManager.getEquipped(slot);
            Label l = pixelLabel(prefix + eq.map(Weapon::getName).orElse("-"), eq.isPresent() ? GOLD : WHITE, 7);
            l.setWrapText(true);
            HBox row = new HBox(6, l);
            if (eq.isPresent()) {
                Button unb = actionBtn("X", RED, "#2a0a0a"); unb.setMaxWidth(28);
                unb.setOnAction(e -> { equipmentManager.unequip(slot); appendLog("[EQUIP] Rimosso."); showEquip(); refreshCharacterPanel(); });
                row.getChildren().add(unb);
            }
            characterPanel.getChildren().add(row);
        }
        characterPanel.getChildren().add(pixelSep());
        Button back = actionBtn("< Indietro", WHITE, "#1a1a3a");
        back.setOnAction(e -> refreshCharacterPanel());
        characterPanel.getChildren().add(back);
    }

    private void showEquipPreview(Weapon weapon) {
        characterPanel.getChildren().clear();
        characterPanel.getChildren().add(pixelLabel("EQUIPAGGIA", GOLD, 9));
        characterPanel.getChildren().add(pixelSep());
        characterPanel.getChildren().add(pixelLabel(weapon.getName(), GOLD, 8));
        GameCharacter p = player();
        StatModifier mod = weapon.getModifierFor(p.getCharacterClass());
        if (mod.attackDelta()    != 0) characterPanel.getChildren().add(deltaPx("ATK", p.getAttack(),     mod.attackDelta()));
        if (mod.defenseDelta()   != 0) characterPanel.getChildren().add(deltaPx("DEF", p.getDefense(),    mod.defenseDelta()));
        if (mod.agilityDelta()   != 0) characterPanel.getChildren().add(deltaPx("AGI", p.getAgility(),    mod.agilityDelta()));
        if (mod.maxHpDelta()     != 0) characterPanel.getChildren().add(deltaPx("HP+", p.getMaxHp(),       mod.maxHpDelta()));
        if (mod.maxStaminaDelta()!= 0) characterPanel.getChildren().add(deltaPx("STA", p.getMaxStamina(), mod.maxStaminaDelta()));
        characterPanel.getChildren().add(pixelSep());
        EquipmentManager.EquipResult canEquip = equipmentManager.canEquip(weapon);
        if (!canEquip.success()) { Label w = pixelLabel(canEquip.message(), RED, 7); w.setWrapText(true); characterPanel.getChildren().add(w); }
        Button eq  = actionBtn("> Equipaggia", canEquip.success() ? GREEN : "#555", "#0a2a0a");
        eq.setDisable(!canEquip.success());
        eq.setOnAction(e -> { equipmentManager.equip(weapon); appendLog("[EQUIP] Equipaggiato: " + weapon.getName()); refreshCharacterPanel(); });
        Button back = actionBtn("< Indietro", WHITE, "#1a1a3a"); back.setOnAction(e -> showInventario());
        characterPanel.getChildren().addAll(eq, back);
    }

    private Label deltaPx(String name, int before, int delta) {
        Label l = pixelLabel(name + ": " + before + " > " + (before + delta), delta > 0 ? GREEN : RED, 7);
        l.setWrapText(true); return l;
    }

    private void showSpecialAtk() {
        characterPanel.getChildren().clear();
        characterPanel.getChildren().add(pixelLabel("SKILLS", GOLD, 9));
        characterPanel.getChildren().add(pixelSep());
        List<SpecialAttack> specials = equipmentManager.getEquippedSpecials();
        boolean staffEquipped  = equipmentManager.getEquipped(EquipSlot.MAIN_HAND).map(w -> w instanceof MagicStaff).orElse(false);
        boolean amuletEquipped = equipmentManager.getEquipped(EquipSlot.BODY).map(w -> w instanceof MagicAmulet).orElse(false);
        boolean isMage         = player().getCharacterClass() == CharacterClass.MAGE;
        boolean staffLocked    = staffEquipped && !isMage && !amuletEquipped;
        if (specials.isEmpty()) {
            characterPanel.getChildren().add(pixelLabel("Nessuna skill.\nEquipa un'arma.", WHITE, 7));
        } else {
            if (staffLocked) characterPanel.getChildren().add(pixelLabel("[!] Richiede Pendente\nMagico o classe Mago.", RED, 7));
            for (SpecialAttack s : specials) {
                boolean isStaffSpecial = staffEquipped && (s.getName().equals("Onda Magica") || s.getName().equals("Colpo Vitale"));
                boolean locked = isStaffSpecial && staffLocked;
                boolean canUse = !locked && player().canUseSpecial(s.getStaminaCost());
                Button b = actionBtn("> " + s.getName() + "  [" + s.getStaminaCost() + " STA]",
                    locked ? "#555" : canUse ? WHITE : RED, "#0d0d25");
                b.setDisable(locked || !canUse);
                b.setOnAction(e -> executeSpecial(s));
                characterPanel.getChildren().add(b);
            }
        }
        characterPanel.getChildren().add(pixelSep());
        Button back = actionBtn("< Indietro", WHITE, "#1a1a3a"); back.setOnAction(e -> refreshCharacterPanel());
        characterPanel.getChildren().add(back);
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
            if (selectedEnemy == null || !selectedEnemy.isAlive()) { appendLog("[!] Seleziona un nemico prima di usare uno speciale."); return; }
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
        refresh(); flashEnemy();
    }

    private void flashEnemy() {
        Region flash = new Region();
        flash.setStyle("-fx-background-color:rgba(200,30,30,0.25);");
        flash.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        flash.setMouseTransparent(true);
        encounterPane.getChildren().add(flash);
        new Timeline(new KeyFrame(Duration.millis(180), e -> encounterPane.getChildren().remove(flash))).play();
    }

    // ── End screens ───────────────────────────────────────────────────────────
    private void showGameOver() {
        VBox vb = new VBox(20); vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color:" + BG_DARK + ";");
        vb.getChildren().addAll(pixelLabel("GAME OVER", RED, 28), pixelLabel("Sei stato sconfitto.", WHITE, 10));
        Button back = actionBtn("> Torna al Menu", GOLD, "#1a1a3a"); back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().add(back); root.setCenter(vb);
    }

    private void showVictory() {
        VBox vb = new VBox(20); vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color:" + BG_DARK + ";");
        vb.getChildren().addAll(pixelLabel("VITTORIA!", GOLD, 24), pixelLabel("L'Ultimo Drago e' sconfitto!", WHITE, 10));
        Button back = actionBtn("> Torna al Menu", GOLD, "#1a1a3a"); back.setOnAction(e -> app.showMenu(stage));
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

    private Label pixelLabel(String text, String color, int size) {
        Label l = new Label(text);
        l.setFont(size >= 9 ? pixelFont : pixelFontSmall);
        l.setStyle("-fx-text-fill:" + color + ";-fx-font-size:" + size + "px;");
        l.setWrapText(true);
        return l;
    }

    private Button actionBtn(String text, String textColor, String bgColor) {
        Button b = new Button(text);
        b.setStyle(pixelBtnStyle(textColor, bgColor));
        b.setFont(pixelFontSmall);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private String pixelBtnStyle(String textColor, String bgColor) {
        return "-fx-text-fill:" + textColor + ";" +
               "-fx-background-color:" + bgColor + ";" +
               "-fx-border-color:" + BORDER_COL + ";" +
               "-fx-border-width:1;" +
               "-fx-padding:6 10;" +
               "-fx-cursor:hand;" +
               "-fx-background-radius:0;" +
               "-fx-border-radius:0;";
    }

    private Separator pixelSep() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color:" + BORDER_COL + ";");
        return s;
    }

    private HBox statRow(String key, String val, String valColor) {
        Label k = new Label(key + " ");
        k.setFont(pixelFontSmall); k.setStyle("-fx-text-fill:" + WHITE + ";-fx-font-size:7px;");
        Label v = new Label(val);
        v.setFont(pixelFontSmall); v.setStyle("-fx-text-fill:" + valColor + ";-fx-font-size:7px;-fx-font-weight:bold;");
        v.setWrapText(true);
        HBox row = new HBox(2, k, v); row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private ProgressBar progressBar(double ratio, String color) {
        ProgressBar pb = new ProgressBar(ratio);
        pb.setPrefHeight(8); pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent:" + color + ";-fx-background-color:#222;");
        return pb;
    }

    /**
     * Sfondo encounter con dimensioni FISSE (ENCOUNTER_W x ENCOUNTER_H).
     * Non usa bind() — non si ingrandisce mai.
     */
    private ImageView loadBg(String path) {
        if (path == null) return null;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            Image img = new Image(is, ENCOUNTER_W, ENCOUNTER_H, false, false);
            ImageView iv = new ImageView(img);
            iv.setSmooth(false);
            iv.setFitWidth(ENCOUNTER_W);
            iv.setFitHeight(ENCOUNTER_H);
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

    private ImageView loadImage(String path, double w, double h, boolean cover) {
        if (path == null) return null;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            Image img = new Image(is, w, h, !cover, false);
            ImageView iv = new ImageView(img);
            iv.setSmooth(false); iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(!cover);
            return iv;
        } catch (Exception e) { return null; }
    }

    private String innerStyle() {
        return "-fx-background-color:" + BG_PANEL + ";" +
               "-fx-border-color:" + BORDER_COL + ";" +
               "-fx-border-width:0;";
    }

    private GameCharacter player() { return (GameCharacter) gc.getPlayer(); }
}
