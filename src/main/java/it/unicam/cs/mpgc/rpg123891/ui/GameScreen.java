package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.CombatController;
import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.*;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * Schermata principale di gioco.
 *
 * Layout 3 colonne:
 *   LEFT   = scheda giocatore (o inventario / equip / special in base al bottone)
 *   CENTER = log degli eventi
 *   RIGHT  = stanza corrente + lista nemici cliccabili
 *   BOTTOM = barra azioni
 *
 * Drop probabilistici (50% Carne) gestiti qui in rollDrops(),
 * seguendo esattamente GAME_SPEC.md:
 *   - Cinghiale, Lupo, Cucciolo di Drago → 50% Carne
 *   - Uovo → NO drop (diventa Cucciolo, non droppa carne)
 *   - Goblin, Scheletro, Re Goblin, Strega → drop fissi già in Wave.loot
 */
public class GameScreen {

    private static final String DARK       = "-fx-background-color:#1a1a2e;";
    private static final String PANEL_BG   = "-fx-background-color:#16213e;-fx-border-color:#3a3a6e;-fx-border-width:1;";
    private static final String TEXT_GOLD  = "-fx-text-fill:#e0c46c;";
    private static final String TEXT_WHITE = "-fx-text-fill:#cccccc;";
    private static final String TEXT_RED   = "-fx-text-fill:#e05555;";
    private static final String TEXT_GREEN = "-fx-text-fill:#55e077;";
    private static final String BTN_STYLE  = "-fx-background-color:#2a2a4e;-fx-text-fill:#e0c46c;" +
                                              "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:6 12;";
    private static final String BTN_DANGER = "-fx-background-color:#4e2a2a;-fx-text-fill:#e06c6c;" +
                                              "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:6 12;";

    private final BorderPane root;
    private final GameController gc;
    private final Stage stage;
    private final FxApp app;
    private final CombatController combatController;
    private final EquipmentManager equipmentManager;

    private final VBox     leftPanel = new VBox(8);
    private final TextArea logArea   = new TextArea();
    private final VBox     rightPanel= new VBox(8);
    private final HBox     bottomBar = new HBox(8);

    private Enemy selectedEnemy = null;

    public GameScreen(GameController gc, Stage stage, FxApp app) {
        this.gc    = gc;
        this.stage = stage;
        this.app   = app;
        this.root  = new BorderPane();
        root.setStyle(DARK);

        this.equipmentManager = new EquipmentManager((GameCharacter) gc.getPlayer());

        combatController = new CombatController(
                gc, gc.getGameState().getDungeonMap());
        combatController.setListener(new CombatController.CombatListener() {
            public void onEvent(String msg) { appendLog(msg); }
            public void onTurnEnd(List<String> log, boolean dead, boolean cleared) {}
        });

        buildLayout();
        refresh();

        // Log di benvenuto con inventario iniziale
        appendLog("=== DUNGEON RPG ===");
        appendLog("Benvenuto, " + gc.getPlayer().getName() + "!");
        appendLog("Hai ricevuto: 3 Pozioni + Bastone Magico");
        appendLog("Sei nella " + gc.getCurrentRoom().getName() + ". Buona fortuna!");
        appendLog("----------------------------------");
    }

    public BorderPane getRoot() { return root; }

    // =========================================================================
    // Layout base
    // =========================================================================

    private void buildLayout() {
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle(PANEL_BG + "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        VBox center = new VBox(logArea);
        VBox.setVgrow(logArea, Priority.ALWAYS);
        center.setPadding(new Insets(8));
        center.setStyle(PANEL_BG);

        leftPanel.setPadding(new Insets(8));
        leftPanel.setStyle(PANEL_BG);
        leftPanel.setPrefWidth(235);

        rightPanel.setPadding(new Insets(8));
        rightPanel.setStyle(PANEL_BG);
        rightPanel.setPrefWidth(220);

        bottomBar.setPadding(new Insets(8));
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setStyle("-fx-background-color:#0f0f23;-fx-border-color:#3a3a6e;-fx-border-width:1 0 0 0;");

        root.setLeft(leftPanel);
        root.setCenter(center);
        root.setRight(rightPanel);
        root.setBottom(bottomBar);
    }

    private void refresh() {
        showScheda();
        refreshRight();
        buildBottomBar();
    }

    // =========================================================================
    // Pannelli LEFT
    // =========================================================================

    private void showScheda() {
        leftPanel.getChildren().clear();
        GameCharacter p = player();

        Label title = bold("SCHEDA GIOCATORE", 14);
        title.setStyle(TEXT_GOLD);
        Label classe = lbl("CLASSE: " + p.getCharacterClass());
        classe.setStyle(TEXT_GOLD);

        leftPanel.getChildren().addAll(
            title, sep(),
            classe, sep(),
            bold("Stats:", 12),
            statRow("HP",     p.getCurrentHp() + " / " + p.getMaxHp(),          hpColor(p)),
            statRow("Difesa", String.valueOf(p.getDefense()),                     TEXT_WHITE),
            statRow("Attacco",String.valueOf(p.getAttack()),                      TEXT_WHITE),
            statRow("Agil.",  String.valueOf(p.getAgility()),                     TEXT_WHITE),
            statRow("Stamina",p.getCurrentStamina() + "/" + p.getMaxStamina(),   TEXT_WHITE),
            statRow("Crit",   String.format("%.0f%%", p.getCritChance() * 100),  TEXT_WHITE)
        );

        if (p instanceof Mage m && m.isMagicShieldActive())
            leftPanel.getChildren().add(badge("[SCUDO MAGICO]", TEXT_GREEN));
        if (p instanceof Thief t && t.isStealthBonusActive())
            leftPanel.getChildren().add(badge("[STEALTH]", TEXT_GREEN));
        if (combatController.isCaricaActive())
            leftPanel.getChildren().add(badge("[CARICA! +3 DEF]", TEXT_GREEN));
        if (combatController.getActiveBurn() != null)
            leftPanel.getChildren().add(badge(
                "[BRUCIATURA " + combatController.getActiveBurn().getDamagePerTurn() + "/t]", TEXT_RED));
    }

    private void showInventario() {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("INVENTARIO", 14));
        leftPanel.getChildren().add(sep());

        List<Item> inv = player().getInventory();
        if (inv.isEmpty()) {
            leftPanel.getChildren().add(lbl("(vuoto)"));
        } else {
            for (Item item : inv) {
                Button b = itemBtn(item.getName());
                b.setOnAction(e -> {
                    if (item instanceof Potion) {
                        boolean used = gc.useFirstPotion();
                        if (used) {
                            appendLog("[ITEM] Hai usato una Pozione! Stamina +3.");
                            showInventario();
                            showScheda();
                        }
                    } else if (item instanceof Weapon w) {
                        showEquipPreview(w);
                    } else if (item instanceof Meat meat) {
                        // Usa la Carne: +40 HP
                        meat.use((GameCharacter) gc.getPlayer());
                        gc.getPlayer().removeItem(meat);
                        appendLog("[ITEM] Hai mangiato Carne! +40 HP.");
                        showInventario();
                        showScheda();
                    }
                });
                leftPanel.getChildren().add(b);
            }
        }
        leftPanel.getChildren().add(sep());
        Button back = btn("Indietro");
        back.setOnAction(e -> showScheda());
        leftPanel.getChildren().add(back);
    }

    private void showEquip() {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("EQUIPAGGIAMENTO", 13));
        leftPanel.getChildren().add(sep());

        for (EquipSlot slot : EquipSlot.values()) {
            Label slotLbl = bold(slotName(slot) + ":", 12);
            slotLbl.setStyle(TEXT_GOLD);
            Optional<Weapon> eq = equipmentManager.getEquipped(slot);
            Label itemLbl = lbl(eq.map(Weapon::getName).orElse("(vuoto)"));

            HBox row = new HBox(6, slotLbl, itemLbl);
            if (eq.isPresent()) {
                Weapon w = eq.get();
                Button unb = smallBtn("Rimuovi");
                unb.setOnAction(e -> {
                    equipmentManager.unequip(slot);
                    appendLog("[EQUIP] Rimosso: " + w.getName());
                    showEquip();
                    showScheda();
                });
                row.getChildren().add(unb);
            }
            leftPanel.getChildren().add(row);
        }

        leftPanel.getChildren().add(sep());
        Label hint = lbl("Apri l'Inventario e clicca un'arma per equipaggiarla.");
        hint.setWrapText(true);
        hint.setStyle(TEXT_WHITE + "-fx-font-size:11px;");
        leftPanel.getChildren().add(hint);

        Button back = btn("Indietro");
        back.setOnAction(e -> showScheda());
        leftPanel.getChildren().add(back);
    }

    private void showEquipPreview(Weapon weapon) {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("EQUIPAGGIA", 13));
        leftPanel.getChildren().add(sep());
        leftPanel.getChildren().add(bold(weapon.getName(), 12));
        leftPanel.getChildren().add(lbl(weapon.getDescription()));
        leftPanel.getChildren().add(sep());

        GameCharacter p = player();
        StatModifier mod = weapon.getModifierFor(p.getCharacterClass());

        addStatDeltaRow("Attacco",  p.getAttack(),      mod.attackDelta());
        addStatDeltaRow("Difesa",   p.getDefense(),     mod.defenseDelta());
        addStatDeltaRow("Agilit\u00e0", p.getAgility(), mod.agilityDelta());
        addStatDeltaRow("HP Max",   p.getMaxHp(),       mod.maxHpDelta());
        addStatDeltaRow("Stamina",  p.getMaxStamina(),  mod.maxStaminaDelta());
        if (mod.critDelta() != 0) {
            Label row = lbl("Crit: " + String.format("%.0f%%", p.getCritChance() * 100)
                    + " \u2192 " + String.format("%.0f%%", (p.getCritChance() + mod.critDelta()) * 100));
            row.setStyle(mod.critDelta() > 0 ? TEXT_GREEN : TEXT_RED);
            leftPanel.getChildren().add(row);
        }

        leftPanel.getChildren().add(sep());

        EquipmentManager.EquipResult canEquip = equipmentManager.canEquip(weapon);
        if (!canEquip.success()) {
            Label warn = lbl(canEquip.message());
            warn.setStyle(TEXT_RED);
            leftPanel.getChildren().add(warn);
        }

        Button equipBtn = btn("Equipaggia  [" + slotName(weapon.getSlot()) + "]");
        equipBtn.setDisable(!canEquip.success());
        equipBtn.setOnAction(e -> {
            EquipmentManager.EquipResult r = equipmentManager.equip(weapon);
            appendLog("[EQUIP] " + r.message());
            showScheda();
        });

        Button back = btn("Indietro");
        back.setOnAction(e -> showInventario());
        leftPanel.getChildren().addAll(equipBtn, back);
    }

    private void addStatDeltaRow(String name, int before, int delta) {
        if (delta == 0) return;
        Label row = lbl(name + ": " + before + " \u2192 " + (before + delta));
        row.setStyle(delta > 0 ? TEXT_GREEN : TEXT_RED);
        leftPanel.getChildren().add(row);
    }

    private void showSpecialAtk() {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("ATTACCHI SPECIALI", 13));
        leftPanel.getChildren().add(sep());

        List<SpecialAttack> specials = equipmentManager.getEquippedSpecials();
        if (specials.isEmpty()) {
            leftPanel.getChildren().add(lbl("Nessun attacco speciale.\nEquipa un'arma prima."));
        } else {
            GameCharacter p = player();
            for (SpecialAttack s : specials) {
                boolean canUse = p.canUseSpecial(s.getStaminaCost());
                Button b = btn(s.getName() + "  costo:" + s.getStaminaCost());
                if (!canUse) {
                    b.setStyle(BTN_DANGER);
                    b.setTooltip(new Tooltip("Stamina insufficiente"));
                    b.setDisable(true);
                }
                b.setOnAction(e -> executeSpecial(s));
                leftPanel.getChildren().add(b);
            }
        }
        leftPanel.getChildren().add(sep());
        Button back = btn("Indietro");
        back.setOnAction(e -> showScheda());
        leftPanel.getChildren().add(back);
    }

    // =========================================================================
    // Pannello RIGHT
    // =========================================================================

    private void refreshRight() {
        rightPanel.getChildren().clear();
        Room room = gc.getCurrentRoom();

        Label stanzaLbl = bold("Stanza:", 12);
        stanzaLbl.setStyle(TEXT_GOLD);
        rightPanel.getChildren().addAll(stanzaLbl, lbl(room.getName()), sep());

        Wave wave = room.getCurrentWave();
        if (wave != null) {
            Label nemLbl = bold("Nemici:", 12);
            nemLbl.setStyle(TEXT_GOLD);
            rightPanel.getChildren().add(nemLbl);

            List<Enemy> alive = wave.getEnemies().stream().filter(Enemy::isAlive).toList();
            for (Enemy e : alive) {
                String tags = (e.isBoss()    ? "[BOSS] " : "")
                            + (e.isImmune()  ? "[IMM] "  : "")
                            + (e.isStunned() ? "[STORD]" : "");
                String label = e.getName() + (tags.isBlank() ? "" : " " + tags.strip())
                             + "\nHP: " + e.getCurrentHp() + "/" + e.getMaxHp();
                Button eb = enemyBtn(label);
                if (e == selectedEnemy)
                    eb.setStyle(eb.getStyle() + "-fx-border-color:#e0c46c;-fx-border-width:2;");
                eb.setOnAction(ev -> { selectedEnemy = e; refreshRight(); });
                rightPanel.getChildren().add(eb);
            }
        } else if (room.isCleared()) {
            Label cleared = lbl("Stanza liberata!");
            cleared.setStyle(TEXT_GREEN);
            rightPanel.getChildren().add(cleared);

            if (gc.getGameState().getDungeonMap().hasNextRoom()) {
                Button adv = btn("Avanza \u2192");
                adv.setOnAction(e -> {
                    gc.advanceRoom();
                    selectedEnemy = null;
                    appendLog("=== Nuova stanza: " + gc.getCurrentRoom().getName() + " ===");
                    refresh();
                });
                rightPanel.getChildren().add(adv);
            } else {
                showVictory();
            }
        }
    }

    // =========================================================================
    // Bottom bar
    // =========================================================================

    private void buildBottomBar() {
        bottomBar.getChildren().clear();

        Button btnInv     = btn("Inventario");
        Button btnEquip   = btn("Equip.");
        Button btnAtk     = btn("ATK");
        Button btnSpecial = btn("Special ATK");
        Button btnFuga    = dangerBtn("Fuga");
        Button btnSave    = btn("Salva");

        btnInv.setOnAction(e     -> showInventario());
        btnEquip.setOnAction(e   -> showEquip());
        btnAtk.setOnAction(e     -> doNormalAttack());
        btnSpecial.setOnAction(e -> showSpecialAtk());
        btnFuga.setOnAction(e    -> doFlee());
        btnSave.setOnAction(e    -> { gc.saveGame(); appendLog("[SAVE] Partita salvata."); });

        btnFuga.setDisable(!gc.canFlee());
        bottomBar.getChildren().addAll(btnInv, btnEquip, btnAtk, btnSpecial, btnFuga, btnSave);
    }

    // =========================================================================
    // Azioni combattimento
    // =========================================================================

    private void doNormalAttack() {
        if (selectedEnemy == null || !selectedEnemy.isAlive()) {
            appendLog("[!] Seleziona un nemico nella colonna destra prima di attaccare.");
            return;
        }
        if (selectedEnemy.isImmune()) {
            appendLog("[!] " + selectedEnemy.getName() + " e' immune agli attacchi!");
            return;
        }
        CombatController.TurnResult result = combatController.playerNormalAttack(selectedEnemy);
        handleTurnResult(result);
    }

    private void executeSpecial(SpecialAttack special) {
        boolean isAoe = special.getName().equals("Onda Magica") ||
                        special.getName().equals("Spazzatutto");
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
        CombatController.TurnResult result = combatController.playerFlee();
        handleTurnResult(result);
    }

    private void handleTurnResult(CombatController.TurnResult result) {
        if (result == null) return;
        result.log().forEach(this::appendLog);
        refresh();
        if (result.playerDead()) { showGameOver(); return; }
        if (result.fleeSuccess()) appendLog("[FUGA] Sei fuggito dalla stanza!");
        if (result.waveCleared()) {
            appendLog("[WAVE] Ondata completata!");
            rollDrops();
            gc.checkWaveCleared();
            if (gc.getGameState().isVictory()) { showVictory(); return; }
            selectedEnemy = null;
            refresh();
        }
    }

    /**
     * Drop probabilistici 50% Carne da GAME_SPEC.md:
     *   Cinghiale, Lupo, Cucciolo di Drago → 50% ciascuno
     *   Uovo                               → NO drop
     *   Tutti gli altri                    → drop fissi già in Wave.loot, non qui
     */
    private void rollDrops() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null) return;
        wave.getEnemies().stream()
            .filter(e -> !e.isAlive())
            .filter(e -> meatDropper(e.getName()))
            .forEach(e -> {
                if (Math.random() < 0.5) {
                    player().addItem(new Meat());
                    appendLog("[DROP] " + e.getName() + " ha lasciato Carne! (+40 HP se usata)");
                }
            });
    }

    /**
     * Solo questi nemici possono droppare Carne con probabilità 50%.
     * Le Uova NON droppano (si trasformano in Cuccioli).
     */
    private boolean meatDropper(String name) {
        return switch (name) {
            case "Cinghiale", "Lupo", "Cucciolo di Drago" -> true;
            default -> false;
        };
    }

    // =========================================================================
    // Game over / vittoria
    // =========================================================================

    private void showGameOver() {
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle(DARK);
        Label l = bold("SEI MORTO.  GAME OVER.", 28);
        l.setStyle(TEXT_RED);
        Button back = btn("Torna al Menu");
        back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().addAll(l, back);
        root.setCenter(vb);
        root.setLeft(null);
        root.setRight(null);
        root.setBottom(null);
    }

    private void showVictory() {
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle(DARK);
        Label l = bold("HAI VINTO!  L'Ultimo Drago e' stato sconfitto!", 22);
        l.setStyle(TEXT_GOLD);
        Button back = btn("Torna al Menu");
        back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().addAll(l, back);
        root.setCenter(vb);
        root.setLeft(null);
        root.setRight(null);
        root.setBottom(null);
    }

    // =========================================================================
    // Helpers UI
    // =========================================================================

    private void appendLog(String msg) {
        Platform.runLater(() -> logArea.appendText(msg + "\n"));
    }

    private GameCharacter player() { return (GameCharacter) gc.getPlayer(); }

    private String hpColor(GameCharacter p) {
        double r = (double) p.getCurrentHp() / p.getMaxHp();
        return r > 0.5 ? TEXT_GREEN : r > 0.25 ? "-fx-text-fill:#e0a030;" : TEXT_RED;
    }

    private Label lbl(String text) {
        Label l = new Label(text); l.setStyle(TEXT_WHITE); l.setWrapText(true); return l;
    }
    private Label bold(String text, int size) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, size));
        l.setStyle(TEXT_WHITE);
        return l;
    }
    private Label badge(String text, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 11));
        l.setStyle(color);
        return l;
    }
    private HBox statRow(String label, String value, String color) {
        Label k = new Label(label + ": "); k.setStyle(TEXT_WHITE + "-fx-font-size:12px;");
        Label v = new Label(value);        v.setStyle(color + "-fx-font-size:12px;-fx-font-weight:bold;");
        return new HBox(4, k, v);
    }
    private Separator sep() { return new Separator(); }
    private Button btn(String t)       { Button b = new Button(t); b.setStyle(BTN_STYLE);  return b; }
    private Button dangerBtn(String t) { Button b = new Button(t); b.setStyle(BTN_DANGER); return b; }
    private Button smallBtn(String t) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:#2a2a4e;-fx-text-fill:#e0c46c;-fx-font-size:10px;-fx-cursor:hand;");
        return b;
    }
    private Button itemBtn(String t) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:#1e1e3e;-fx-text-fill:#cccccc;" +
                   "-fx-font-size:12px;-fx-cursor:hand;-fx-padding:4 8;");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }
    private Button enemyBtn(String t) {
        Button b = new Button(t);
        b.setWrapText(true); b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color:#1e1e3e;-fx-text-fill:#e05555;" +
                   "-fx-font-size:11px;-fx-cursor:hand;-fx-padding:4 6;-fx-alignment:left;");
        return b;
    }
    private String slotName(EquipSlot slot) {
        return switch (slot) {
            case MAIN_HAND -> "Arma (Mano DX)";
            case OFF_HAND  -> "Scudo (Mano SX)";
            case BODY      -> "Armatura / Pendente";
        };
    }
}
