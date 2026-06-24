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

    private static final String BG_DARK    = "#0d0d1f";
    private static final String BG_PANEL   = "#12122a";
    private static final String BG_PANEL2  = "#0f0f22";
    private static final String BORDER_COL = "#8b6914";
    private static final String GOLD       = "#e0c46c";
    private static final String WHITE      = "#cccccc";
    private static final String RED        = "#e05555";
    private static final String GREEN      = "#55e077";
    private static final String ORANGE     = "#e0a030";

    private Font pixelFont;
    private Font pixelFontSmall;

    private final BorderPane root;
    private final GameController gc;
    private final Stage stage;
    private final FxApp app;
    private final CombatController combatController;
    private final EquipmentManager equipmentManager;

    private final StackPane  encounterPane  = new StackPane();
    private final VBox       characterPanel = new VBox(6);
    // enemyStatsPanel ELIMINATO
    private final TextArea   logArea        = new TextArea();
    // actionPanel ELIMINATO come colonna — sostituito da combatBar orizzontale

    private Enemy  selectedEnemy  = null;
    private String lastLoggedWave = null;

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
    // Layout:
    //   topRow:    [ encounterPane (grow) | characterPanel (fixed) ]
    //   bottomRow: [ logWrapper (grow) ]
    //   combatBar: [ Fight | Skills | Items | Equip | Run ]  — HBox orizzontale
    //   sysBar:    crediti
    //   Save/Menu: in fondo a characterPanel
    // =========================================================================
    private void buildLayout() {
        encounterPane.setPrefSize(500, 320);
        encounterPane.setMinHeight(280);
        encounterPane.setStyle(panelStyle());

        characterPanel.setPadding(new Insets(10));
        characterPanel.setPrefWidth(230);
        characterPanel.setMinWidth(200);
        characterPanel.setStyle(panelStyle());

        HBox topRow = new HBox(4, encounterPane, characterPanel);
        HBox.setHgrow(encounterPane, Priority.ALWAYS);
        topRow.setPadding(new Insets(6));

        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(160);
        logArea.setFont(pixelFontSmall);
        logArea.setStyle(
            "-fx-control-inner-background:" + BG_PANEL2 + ";" +
            "-fx-text-fill:" + WHITE + ";" +
            "-fx-font-size:7px;"
        );
        VBox logWrapper = new VBox(logArea);
        VBox.setVgrow(logArea, Priority.ALWAYS);
        logWrapper.setStyle(panelStyle());
        logWrapper.setPadding(new Insets(8));
        HBox.setHgrow(logWrapper, Priority.ALWAYS);

        HBox bottomRow = new HBox(4, logWrapper);
        bottomRow.setPadding(new Insets(0, 6, 2, 6));
        bottomRow.setPrefHeight(160);

        // combatBar — id usato da getCombatBar() per aggiornamento dinamico
        HBox combatBar = new HBox(6);
        combatBar.setId("combatBar");
        combatBar.setPadding(new Insets(6, 6, 6, 6));
        combatBar.setStyle(
            "-fx-background-color:" + BG_PANEL + ";" +
            "-fx-border-color:" + BORDER_COL + ";" +
            "-fx-border-width:2 0 0 0;"
        );
        combatBar.setAlignment(Pos.CENTER_LEFT);

        Label sysInfo = new Label("DUNGEON RPG  v1.0  -  by Lorenzo Grassi");
        sysInfo.setFont(pixelFontSmall);
        sysInfo.setStyle("-fx-text-fill:" + GOLD + ";-fx-padding:2 8;");
        HBox sysBar = new HBox(sysInfo);
        sysBar.setAlignment(Pos.CENTER);
        sysBar.setStyle("-fx-background-color:#08081a;-fx-border-color:" + BORDER_COL + ";-fx-border-width:1 0 0 0;");

        VBox main = new VBox(0, topRow, bottomRow, combatBar, sysBar);
        VBox.setVgrow(topRow, Priority.ALWAYS);
        root.setCenter(main);
    }

    /** Recupera la combatBar dal VBox principale tramite id. */
    private HBox getCombatBar() {
        VBox main = (VBox) root.getCenter();
        return (HBox) main.getChildren().stream()
            .filter(n -> "combatBar".equals(n.getId()))
            .findFirst().orElseThrow();
    }

    private void refresh() {
        refreshEncounter();
        refreshCharacterPanel();
        refreshCombatBar();
        logCurrentWaveIfNew();
    }

    // =========================================================================
    // refreshCombatBar — Fight/Skills/Items/Equip/Run in HBox orizzontale.
    // Save/Menu sono gestiti da refreshCharacterPanel() in fondo al panel.
    // =========================================================================
    private void refreshCombatBar() {
        HBox bar = getCombatBar();
        bar.getChildren().clear();
        boolean cleared = gc.getCurrentRoom().isCleared();

        if (!cleared) {
            Button btnAtk     = pixelButton("> Fight",  WHITE, "#1a1a3a");
            Button btnSpecial = pixelButton("> Skills", WHITE, "#1a1a3a");
            Button btnInv     = pixelButton("> Items",  WHITE, "#1a1a3a");
            Button btnEquip   = pixelButton("> Equip",  WHITE, "#1a1a3a");
            Button btnFuga    = pixelButton("> Run",    RED,   "#2a0a0a");

            btnAtk.setOnAction(e     -> doNormalAttack());
            btnSpecial.setOnAction(e -> showSpecialAtk());
            btnInv.setOnAction(e     -> showInventario());
            btnEquip.setOnAction(e   -> showEquip());
            btnFuga.setOnAction(e    -> doFlee());

            btnFuga.setDisable(!gc.canFlee());
            if (!gc.canFlee()) btnFuga.setStyle(pixelBtnStyle("#555", "#2a0a0a"));

            // In HBox orizzontale non serve maxWidth=MAX
            for (Button b : new Button[]{btnAtk, btnSpecial, btnInv, btnEquip, btnFuga}) {
                b.setMaxWidth(Region.USE_PREF_SIZE);
            }
            bar.getChildren().addAll(btnAtk, btnSpecial, btnInv, btnEquip, btnFuga);
        } else {
            if (gc.getGameState().getDungeonMap().hasNextRoom()) {
                Button adv = pixelButton("> Avanza nella stanza successiva", GREEN, "#0a2a0a");
                adv.setMaxWidth(Region.USE_PREF_SIZE);
                adv.setOnAction(e -> doAdvanceRoom());
                bar.getChildren().add(adv);
            } else {
                showVictory();
            }
        }
    }

    /** Punto unico di selezione nemico — log scritto una sola volta. */
    private void selectEnemy(Enemy enemy) {
        if (enemy == selectedEnemy) return;
        selectedEnemy = enemy;
        refreshEncounter();
        appendLog("[MIRA] " + enemy.getName() + " selezionato.");
    }

    private void refreshEncounter() {
        encounterPane.getChildren().clear();

        String bgPath = ROOM_BG.getOrDefault(gc.getCurrentRoom().getId(),
                                             "/assets/backgrounds/foresta.png");
        ImageView bg = loadImage(bgPath, 500, 310, true);
        if (bg != null) encounterPane.getChildren().add(bg);

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.15);");
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlay.setMouseTransparent(true);
        encounterPane.getChildren().add(overlay);

        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null || gc.getCurrentRoom().isCleared()) {
            Label cleared = pixelLabel("- STANZA LIBERATA -", GOLD, 9);
            encounterPane.getChildren().add(cleared);
            return;
        }

        List<Enemy> alive = wave.getEnemies().stream().filter(Enemy::isAlive).toList();
        if (alive.isEmpty()) return;

        HBox spritesBox = new HBox(16);
        spritesBox.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(spritesBox, Pos.BOTTOM_CENTER);
        StackPane.setMargin(spritesBox, new Insets(0, 0, 12, 0));

        for (Enemy enemy : alive) {
            spritesBox.getChildren().add(buildEnemySprite(enemy));
        }
        encounterPane.getChildren().add(spritesBox);
    }

    private VBox buildEnemySprite(Enemy enemy) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.BOTTOM_CENTER);

        String spritePath = ENEMY_SPRITE.get(enemy.getName());

        ImageView sprite = loadSprite(spritePath, 130);
        if (sprite == null) {
            Label ph = new Label("[" + enemy.getName().substring(0, Math.min(4, enemy.getName().length())) + "]");
            ph.setStyle("-fx-text-fill:" + RED + ";-fx-font-size:20px;-fx-font-weight:bold;");
            card.getChildren().add(ph);
        } else {
            StackPane spriteWrapper = new StackPane(sprite);
            spriteWrapper.setStyle(enemy == selectedEnemy
                ? "-fx-border-color:" + GOLD + ";-fx-border-width:3;"
                : "-fx-border-color:transparent;-fx-border-width:3;");
            card.getChildren().add(spriteWrapper);
        }

        double hpRatio = (double) enemy.getCurrentHp() / enemy.getMaxHp();
        String hpColor = hpRatio > 0.5 ? GREEN : hpRatio > 0.25 ? ORANGE : RED;
        ProgressBar hpBar = new ProgressBar(hpRatio);
        hpBar.setPrefWidth(100);
        hpBar.setPrefHeight(8);
        hpBar.setStyle("-fx-accent:" + hpColor + ";-fx-background-color:#333;");

        Label nameLabel = pixelLabel(enemy.getName(), enemy == selectedEnemy ? GOLD : WHITE, 7);
        nameLabel.setWrapText(false);
        Label hpLabel = pixelLabel(enemy.getCurrentHp() + "/" + enemy.getMaxHp(), hpColor, 6);

        card.getChildren().addAll(nameLabel, hpBar, hpLabel);
        card.setPadding(new Insets(0, 4, 0, 4));
        card.setCursor(javafx.scene.Cursor.HAND);

        card.setOnMouseClicked(e -> selectEnemy(enemy));
        card.setOnMouseEntered(e -> { if (enemy != selectedEnemy) card.setStyle("-fx-opacity:0.85;"); });
        card.setOnMouseExited(e -> card.setStyle("-fx-opacity:1;"));

        return card;
    }

    // =========================================================================
    // refreshCharacterPanel — aggiunge Save/Menu in fondo con spacer elastico
    // =========================================================================
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
            ImageView portrait = loadImage(classSprite, 64, 64, false);
            if (portrait != null) portraitRow.getChildren().add(portrait);
        }

        VBox nameBox = new VBox(3);
        nameBox.getChildren().addAll(
            pixelLabel(p.getName(), GOLD, 8),
            pixelLabel(p.getCharacterClass().toString(), WHITE, 7)
        );
        portraitRow.getChildren().add(nameBox);
        characterPanel.getChildren().add(portraitRow);
        characterPanel.getChildren().add(pixelSep());

        double hpRatio = (double) p.getCurrentHp() / p.getMaxHp();
        String hpCol = hpRatio > 0.5 ? GREEN : hpRatio > 0.25 ? ORANGE : RED;
        characterPanel.getChildren().addAll(
            statRowPixel("HP",  p.getCurrentHp() + "/" + p.getMaxHp(), hpCol),
            hpProgressBar(hpRatio, hpCol),
            statRowPixel("STA", p.getCurrentStamina() + "/" + p.getMaxStamina(), "#4a9eff"),
            staminaBar(p),
            pixelSep(),
            statRowPixel("ATK", String.valueOf(p.getAttack()),  WHITE),
            statRowPixel("DEF", String.valueOf(p.getDefense()), WHITE),
            statRowPixel("AGI", String.valueOf(p.getAgility()), WHITE),
            statRowPixel("CRI", String.format("%.0f%%", p.getCritChance() * 100), WHITE)
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
            String prefix = switch (slot) {
                case MAIN_HAND -> "W:";
                case OFF_HAND  -> "S:";
                case BODY      -> "A:";
            };
            String val = eq.map(Weapon::getName).orElse("-");
            characterPanel.getChildren().add(statRowPixel(prefix, val, eq.isPresent() ? GOLD : "#555577"));
        }

        // Spacer elastico + Save/Menu in fondo
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        characterPanel.getChildren().add(spacer);
        characterPanel.getChildren().add(pixelSep());

        Button btnSave = pixelButton("> Save", GOLD, "#1a1a3a");
        Button btnMenu = pixelButton("> Menu", RED,  "#2a0a0a");
        btnSave.setOnAction(e -> { gc.saveGame(); appendLog("[SAVE] Partita salvata."); });
        btnMenu.setOnAction(e -> confirmMenu());
        characterPanel.getChildren().addAll(btnSave, btnMenu);
    }

    private void showInventario() {
        characterPanel.getChildren().clear();
        characterPanel.getChildren().add(pixelLabel("INVENTARIO", GOLD, 9));
        characterPanel.getChildren().add(pixelSep());

        List<Item> inv = player().getInventory();
        if (inv.isEmpty()) {
            characterPanel.getChildren().add(pixelLabel("(vuoto)", WHITE, 8));
        } else {
            LinkedHashMap<String, Long> consumabili = new LinkedHashMap<>();
            for (Item item : inv)
                if (!(item instanceof Weapon)) consumabili.merge(item.getName(), 1L, Long::sum);

            consumabili.forEach((nome, count) -> {
                String label = count > 1 ? nome + " x" + count : nome;
                Button b = pixelButton("> " + label, WHITE, "#0d0d25");
                b.setMaxWidth(Double.MAX_VALUE);
                b.setOnAction(e -> {
                    Item found = inv.stream()
                            .filter(i -> !(i instanceof Weapon) && i.getName().equals(nome))
                            .findFirst().orElse(null);
                    if (found == null) return;
                    if (found instanceof Potion) {
                        gc.useFirstPotion();
                        appendLog("[ITEM] Pozione usata! +10 HP, +5 Stamina.");
                    } else if (found instanceof Meat meat) {
                        meat.use(player());
                        player().getInventory().remove(meat);
                        appendLog("[ITEM] Carne mangiata! +10 HP, +2 Stamina.");
                    }
                    showInventario(); refreshCharacterPanel();
                });
                characterPanel.getChildren().add(b);
            });
            for (Item item : inv) {
                if (item instanceof Weapon w) {
                    Button b = pixelButton("> [W] " + w.getName(), GOLD, "#0d0d25");
                    b.setMaxWidth(Double.MAX_VALUE);
                    b.setOnAction(e -> showEquipPreview(w));
                    characterPanel.getChildren().add(b);
                }
            }
        }
        characterPanel.getChildren().add(pixelSep());
        Button back = pixelButton("< Indietro", WHITE, "#1a1a3a");
        back.setOnAction(e -> refreshCharacterPanel());
        characterPanel.getChildren().add(back);
    }

    private void showEquip() {
        characterPanel.getChildren().clear();
        characterPanel.getChildren().add(pixelLabel("EQUIPAGGIAMENTO", GOLD, 9));
        characterPanel.getChildren().add(pixelSep());

        for (EquipSlot slot : EquipSlot.values()) {
            String prefix = switch (slot) {
                case MAIN_HAND -> "W: ";
                case OFF_HAND  -> "S: ";
                case BODY      -> "A: ";
            };
            Optional<Weapon> eq = equipmentManager.getEquipped(slot);
            Label l = pixelLabel(prefix + eq.map(Weapon::getName).orElse("-"), eq.isPresent() ? GOLD : WHITE, 7);
            l.setWrapText(true);
            HBox row = new HBox(6, l);
            if (eq.isPresent()) {
                Button unb = pixelButton("X", RED, "#2a0a0a");
                unb.setOnAction(e -> {
                    equipmentManager.unequip(slot);
                    appendLog("[EQUIP] Rimosso.");
                    showEquip(); refreshCharacterPanel();
                });
                row.getChildren().add(unb);
            }
            characterPanel.getChildren().add(row);
        }
        characterPanel.getChildren().add(pixelSep());
        Button back = pixelButton("< Indietro", WHITE, "#1a1a3a");
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
        if (!canEquip.success()) {
            Label w = pixelLabel(canEquip.message(), RED, 7); w.setWrapText(true);
            characterPanel.getChildren().add(w);
        }
        Button eq = pixelButton("> Equipaggia", canEquip.success() ? GREEN : "#555", "#0a2a0a");
        eq.setDisable(!canEquip.success());
        eq.setOnAction(e -> {
            equipmentManager.equip(weapon);
            appendLog("[EQUIP] Equipaggiato: " + weapon.getName());
            refreshCharacterPanel();
        });
        Button back = pixelButton("< Indietro", WHITE, "#1a1a3a");
        back.setOnAction(e -> showInventario());
        characterPanel.getChildren().addAll(eq, back);
    }

    private Label deltaPx(String name, int before, int delta) {
        Label l = pixelLabel(name + ": " + before + " > " + (before + delta), delta > 0 ? GREEN : RED, 7);
        l.setWrapText(true);
        return l;
    }

    private void showSpecialAtk() {
        characterPanel.getChildren().clear();
        characterPanel.getChildren().add(pixelLabel("SKILLS", GOLD, 9));
        characterPanel.getChildren().add(pixelSep());

        List<SpecialAttack> specials = equipmentManager.getEquippedSpecials();
        boolean staffEquipped  = equipmentManager.getEquipped(EquipSlot.MAIN_HAND)
                                    .map(w -> w instanceof MagicStaff).orElse(false);
        boolean amuletEquipped = equipmentManager.getEquipped(EquipSlot.BODY)
                                    .map(w -> w instanceof MagicAmulet).orElse(false);
        boolean isMage         = player().getCharacterClass() == CharacterClass.MAGE;
        boolean staffLocked    = staffEquipped && !isMage && !amuletEquipped;

        if (specials.isEmpty()) {
            characterPanel.getChildren().add(pixelLabel("Nessuna skill.\nEquipa un'arma.", WHITE, 7));
        } else {
            if (staffLocked) {
                characterPanel.getChildren().add(
                    pixelLabel("[!] Richiede Pendente\nMagico o classe Mago.", RED, 7));
            }
            for (SpecialAttack s : specials) {
                boolean isStaffSpecial = staffEquipped &&
                        (s.getName().equals("Onda Magica") || s.getName().equals("Colpo Vitale"));
                boolean locked = isStaffSpecial && staffLocked;
                boolean canUse = !locked && player().canUseSpecial(s.getStaminaCost());
                String col = locked ? "#555" : canUse ? WHITE : RED;
                Button b = pixelButton("> " + s.getName() + "  [" + s.getStaminaCost() + " STA]", col, "#0d0d25");
                b.setDisable(locked || !canUse);
                b.setMaxWidth(Double.MAX_VALUE);
                b.setOnAction(e -> executeSpecial(s));
                characterPanel.getChildren().add(b);
            }
        }
        characterPanel.getChildren().add(pixelSep());
        Button back = pixelButton("< Indietro", WHITE, "#1a1a3a");
        back.setOnAction(e -> refreshCharacterPanel());
        characterPanel.getChildren().add(back);
    }

    private void logRoomEntry() {
        Room room = gc.getCurrentRoom();
        appendLog("");
        appendLog("===============================");
        appendLog("  " + room.getName().toUpperCase());
        appendLog("===============================");
        appendLog(room.getDescription());
        if (room.getId().equals("r1"))
            appendLog("[*] Raccogli il Bastone Magico.");
    }

    private void logCurrentWaveIfNew() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null) return;
        String key = gc.getCurrentRoom().getId() + "|" + wave.getName();
        if (key.equals(lastLoggedWave)) return;
        lastLoggedWave = key;
        if (!wave.getDescription().isBlank()) {
            appendLog("");
            appendLog("--- " + wave.getName() + " ---");
            appendLog(wave.getDescription());
        }
        if (wave.getEnemies().isEmpty()) {
            wave.setCleared(true);
            gc.checkWaveCleared();
            refresh();
        }
    }

    private void appendLog(String msg) {
        Platform.runLater(() -> {
            logArea.appendText(msg + "\n");
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private void doNormalAttack() {
        if (selectedEnemy == null || !selectedEnemy.isAlive()) {
            appendLog("[!] Clicca un nemico nell'area ENCOUNTER per selezionarlo.");
            return;
        }
        if (selectedEnemy.isImmune()) { appendLog("[!] " + selectedEnemy.getName() + " e' immune!"); return; }
        if (selectedEnemy.isEgg())    { appendLog("[!] Non puoi attaccare un Uovo!"); return; }
        handleTurnResult(combatController.playerNormalAttack(selectedEnemy));
    }

    private void executeSpecial(SpecialAttack special) {
        boolean isAoe = special.getName().equals("Onda Magica") || special.getName().equals("Spazzatutto");
        CombatController.TurnResult result;
        if (isAoe) {
            result = combatController.playerAoeAttack(special);
        } else {
            if (selectedEnemy == null || !selectedEnemy.isAlive()) {
                appendLog("[!] Seleziona un nemico prima di usare uno speciale.");
                return;
            }
            result = combatController.playerSpecialAttack(special, selectedEnemy);
        }
        handleTurnResult(result);
    }

    private void doFlee() {
        handleTurnResult(combatController.playerFlee());
    }

    private void doAdvanceRoom() {
        gc.advanceRoom();
        selectedEnemy  = null;
        lastLoggedWave = null;
        Room newRoom = gc.getCurrentRoom();
        if (newRoom.getId().equals("r5")) {
            Wave w = newRoom.getCurrentWave();
            if (w != null) w.getEnemies().stream().findFirst().ifPresent(
                    d -> combatController.checkAndActivateDragonBuff(d));
        }
        logRoomEntry();
        refresh();
    }

    private void handleTurnResult(CombatController.TurnResult result) {
        if (result == null) return;
        result.log().forEach(this::appendLog);
        if (result.playerDead())  { showGameOver(); return; }
        if (result.fleeSuccess()) { appendLog("[FUGA] Sei fuggito!"); refresh(); return; }
        if (result.waveCleared()) {
            appendLog("[WAVE] Ondata completata!");
            gc.checkWaveCleared();
            if (gc.getGameState().isVictory()) { showVictory(); return; }
            selectedEnemy = null;
        }
        refresh();
        flashEnemy();
    }

    private void flashEnemy() {
        Region flash = new Region();
        flash.setStyle("-fx-background-color:rgba(200,30,30,0.25);");
        flash.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        flash.setMouseTransparent(true);
        encounterPane.getChildren().add(flash);
        Timeline t = new Timeline(
            new KeyFrame(Duration.millis(180), e -> encounterPane.getChildren().remove(flash))
        );
        t.play();
    }

    private void showGameOver() {
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color:" + BG_DARK + ";");
        Label title = pixelLabel("GAME OVER", RED, 28);
        Label sub   = pixelLabel("Sei stato sconfitto.", WHITE, 10);
        Button back = pixelButton("> Torna al Menu", GOLD, "#1a1a3a");
        back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().addAll(title, sub, back);
        root.setCenter(vb);
    }

    private void showVictory() {
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color:" + BG_DARK + ";");
        Label title = pixelLabel("VITTORIA!", GOLD, 24);
        Label sub   = pixelLabel("L'Ultimo Drago e' sconfitto!", WHITE, 10);
        Button back = pixelButton("> Torna al Menu", GOLD, "#1a1a3a");
        back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().addAll(title, sub, back);
        root.setCenter(vb);
    }

    private void confirmMenu() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Menu");
        alert.setHeaderText("Tornare al menu principale?");
        alert.setContentText("I progressi non salvati andranno persi.");
        alert.showAndWait().ifPresent(resp -> { if (resp == ButtonType.OK) app.showMenu(stage); });
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

    private Button pixelButton(String text, String textColor, String bgColor) {
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
               "-fx-padding:5 8;" +
               "-fx-cursor:hand;" +
               "-fx-background-radius:0;" +
               "-fx-border-radius:0;";
    }

    private Separator pixelSep() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color:" + BORDER_COL + ";");
        return s;
    }

    private HBox statRowPixel(String key, String val, String valColor) {
        Label k = new Label(key + " ");
        k.setFont(pixelFontSmall);
        k.setStyle("-fx-text-fill:" + WHITE + ";-fx-font-size:7px;");
        Label v = new Label(val);
        v.setFont(pixelFontSmall);
        v.setStyle("-fx-text-fill:" + valColor + ";-fx-font-size:7px;-fx-font-weight:bold;");
        v.setWrapText(true);
        HBox row = new HBox(2, k, v);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private ProgressBar hpProgressBar(double ratio, String color) {
        ProgressBar pb = new ProgressBar(ratio);
        pb.setPrefHeight(8);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent:" + color + ";-fx-background-color:#222;");
        return pb;
    }

    private ProgressBar staminaBar(GameCharacter p) {
        double r = p.getMaxStamina() > 0 ? (double) p.getCurrentStamina() / p.getMaxStamina() : 0;
        ProgressBar pb = new ProgressBar(r);
        pb.setPrefHeight(8);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent:#4a9eff;-fx-background-color:#222;");
        return pb;
    }

    /**
     * Carica uno sprite nemico con altezza fissa e larghezza proporzionale.
     * fitHeight ONLY evita il collasso quando il PNG ha proporzioni diverse da quelle attese.
     */
    private ImageView loadSprite(String path, double height) {
        if (path == null) return null;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            Image img = new Image(is);
            ImageView iv = new ImageView(img);
            iv.setSmooth(true);
            iv.setPreserveRatio(true);
            iv.setFitHeight(height);
            return iv;
        } catch (Exception e) { return null; }
    }

    /**
     * Carica un'immagine generica con dimensioni esatte (sfondo) o proporzionali (ritratti).
     */
    private ImageView loadImage(String path, double w, double h, boolean cover) {
        if (path == null) return null;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            Image img = new Image(is, w, h, !cover, false);
            ImageView iv = new ImageView(img);
            iv.setSmooth(false);
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            iv.setPreserveRatio(!cover);
            return iv;
        } catch (Exception e) { return null; }
    }

    private String panelStyle() {
        return "-fx-background-color:" + BG_PANEL + ";" +
               "-fx-border-color:" + BORDER_COL + ";" +
               "-fx-border-width:2;" +
               "-fx-border-radius:0;" +
               "-fx-background-radius:0;";
    }

    private GameCharacter player() { return (GameCharacter) gc.getPlayer(); }
}
