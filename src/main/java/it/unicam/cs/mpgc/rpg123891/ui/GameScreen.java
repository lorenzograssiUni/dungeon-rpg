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
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameScreen {

    private static final String DARK       = "-fx-background-color:#1a1a2e;";
    private static final String PANEL_BG   = "-fx-background-color:#16213e;-fx-border-color:#3a3a6e;-fx-border-width:1;";
    private static final String LOG_STYLE  = "-fx-background-color:#16213e;-fx-border-color:#3a3a6e;-fx-border-width:1;" +
                                              "-fx-text-fill:#cccccc;-fx-font-size:12px;" +
                                              "-fx-control-inner-background:#16213e;";
    private static final String TEXT_GOLD  = "-fx-text-fill:#e0c46c;";
    private static final String TEXT_WHITE = "-fx-text-fill:#cccccc;";
    private static final String TEXT_RED   = "-fx-text-fill:#e05555;";
    private static final String TEXT_GREEN = "-fx-text-fill:#55e077;";
    private static final String BTN_STYLE  = "-fx-background-color:#2a2a4e;-fx-text-fill:#e0c46c;" +
                                              "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:6 12;";
    private static final String BTN_DANGER = "-fx-background-color:#4e2a2a;-fx-text-fill:#e06c6c;" +
                                              "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:6 12;";
    private static final String BTN_GREEN  = "-fx-background-color:#1e4e2a;-fx-text-fill:#55e077;" +
                                              "-fx-font-size:14px;-fx-font-weight:bold;-fx-cursor:hand;-fx-padding:8 18;";
    private static final String BTN_GRAY   = "-fx-background-color:#2e2e2e;-fx-text-fill:#888888;" +
                                              "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:6 12;";

    private final BorderPane root;
    private final GameController gc;
    private final Stage stage;
    private final FxApp app;
    private final CombatController combatController;
    private final EquipmentManager equipmentManager;

    private final VBox     leftPanel  = new VBox(8);
    private final TextArea logArea    = new TextArea();
    private final VBox     rightPanel = new VBox(8);
    private final HBox     bottomBar  = new HBox(8);

    private Enemy  selectedEnemy  = null;
    private String lastLoggedWave = null;

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
        logRoomEntry();
    }

    public BorderPane getRoot() { return root; }

    // =========================================================================
    // Layout
    // =========================================================================

    private void buildLayout() {
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle(LOG_STYLE);
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
        logCurrentWaveIfNew();
    }

    // =========================================================================
    // Log narrativo
    // =========================================================================

    private void logRoomEntry() {
        Room room = gc.getCurrentRoom();
        appendLog("");
        appendLog("=====================================================");
        appendLog("  " + room.getName().toUpperCase());
        appendLog("=====================================================");
        appendLog(room.getDescription());
        appendLog("");
        if (room.getId().equals("r1"))
            appendLog("[\u2605] Raccogli il Bastone Magico. Senti il potere scorrere tra le dita.");
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
            appendLog("");
        }

        if (wave.getEnemies().isEmpty()) {
            wave.setCleared(true);
            gc.checkWaveCleared();
            showScheda();
            refreshRight();
            buildBottomBar();
        }
    }

    // =========================================================================
    // Pannello LEFT — Scheda
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
            statRow("HP",     p.getCurrentHp() + " / " + p.getMaxHp(),         hpColor(p)),
            statRow("Difesa", String.valueOf(p.getDefense()),                    TEXT_WHITE),
            statRow("Attacco",String.valueOf(p.getAttack()),                     TEXT_WHITE),
            statRow("Agil.",  String.valueOf(p.getAgility()),                    TEXT_WHITE),
            statRow("Stamina",p.getCurrentStamina() + "/" + p.getMaxStamina(),  TEXT_WHITE),
            statRow("Crit",   String.format("%.0f%%", p.getCritChance() * 100), TEXT_WHITE)
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

    // =========================================================================
    // Pannello LEFT — Inventario (raggruppato)
    // =========================================================================

    private void showInventario() {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("INVENTARIO", 14));
        leftPanel.getChildren().add(sep());

        List<Item> inv = player().getInventory();
        if (inv.isEmpty()) {
            leftPanel.getChildren().add(lbl("(vuoto)"));
        } else {
            // Raggruppa consumabili per nome (Carne x4, Pozione x3...)
            // Le armi rimangono singole (per equipaggiarle)
            LinkedHashMap<String, Long> consumabili = new LinkedHashMap<>();
            for (Item item : inv) {
                if (!(item instanceof Weapon)) {
                    consumabili.merge(item.getName(), 1L, Long::sum);
                }
            }

            // Bottoni consumabili raggruppati
            consumabili.forEach((nome, count) -> {
                String label = count > 1 ? nome + " (x" + count + ")" : nome;
                Button b = itemBtn(label);
                b.setOnAction(e -> {
                    // Trova la prima istanza nell'inventario con quel nome
                    Item found = inv.stream()
                            .filter(i -> !(i instanceof Weapon) && i.getName().equals(nome))
                            .findFirst().orElse(null);
                    if (found == null) return;

                    if (found instanceof Potion) {
                        gc.useFirstPotion();
                        appendLog("[ITEM] Hai usato una Pozione! +10 HP, +5 Stamina.");
                    } else if (found instanceof Meat meat) {
                        meat.use(player());
                        player().getInventory().remove(meat);
                        appendLog("[ITEM] Hai mangiato Carne! +10 HP, +2 Stamina.");
                    }
                    showInventario(); showScheda();
                });
                leftPanel.getChildren().add(b);
            });

            // Bottoni armi (singoli, per equipaggiarle)
            for (Item item : inv) {
                if (item instanceof Weapon w) {
                    Button b = itemBtn("\u2694 " + w.getName());
                    b.setOnAction(e -> showEquipPreview(w));
                    leftPanel.getChildren().add(b);
                }
            }
        }

        leftPanel.getChildren().add(sep());
        Button back = btn("Indietro");
        back.setOnAction(e -> showScheda());
        leftPanel.getChildren().add(back);
    }

    // =========================================================================
    // Pannello LEFT — Equipaggiamento
    // =========================================================================

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
                    showEquip(); showScheda();
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
            Label r = lbl("Crit: " + String.format("%.0f%%", p.getCritChance() * 100)
                    + " \u2192 " + String.format("%.0f%%", (p.getCritChance() + mod.critDelta()) * 100));
            r.setStyle(mod.critDelta() > 0 ? TEXT_GREEN : TEXT_RED);
            leftPanel.getChildren().add(r);
        }
        leftPanel.getChildren().add(sep());

        EquipmentManager.EquipResult canEquip = equipmentManager.canEquip(weapon);
        if (!canEquip.success()) {
            Label warn = lbl(canEquip.message()); warn.setStyle(TEXT_RED);
            leftPanel.getChildren().add(warn);
        }
        Button equipBtn = btn("Equipaggia  [" + slotName(weapon.getSlot()) + "]");
        equipBtn.setDisable(!canEquip.success());
        equipBtn.setOnAction(e -> {
            equipmentManager.equip(weapon);
            appendLog("[EQUIP] Hai equipaggiato: " + weapon.getName());
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

    // =========================================================================
    // Pannello LEFT — Speciali
    // Fix: blocca speciali del Bastone Magico se non Mago e senza Pendente
    // =========================================================================

    private void showSpecialAtk() {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("ATTACCHI SPECIALI", 13));
        leftPanel.getChildren().add(sep());

        List<SpecialAttack> specials = equipmentManager.getEquippedSpecials();

        // Controlla se il Bastone Magico è equipaggiato
        boolean staffEquipped = equipmentManager.getEquipped(EquipSlot.MAIN_HAND)
                .map(w -> w instanceof MagicStaff).orElse(false);
        // Il Pendente Magico è nel slot BODY
        boolean amuletEquipped = equipmentManager.getEquipped(EquipSlot.BODY)
                .map(w -> w instanceof MagicAmulet).orElse(false);
        boolean isMage = player().getCharacterClass() == CharacterClass.MAGE;
        // Blocco speciali bastone: richiede classe Mago OPPURE Pendente equipaggiato
        boolean staffSpecialsLocked = staffEquipped && !isMage && !amuletEquipped;

        if (specials.isEmpty()) {
            leftPanel.getChildren().add(lbl("Nessun attacco speciale.\nEquipa un'arma prima."));
        } else {
            if (staffSpecialsLocked) {
                Label warn = lbl("[!] Gli speciali del Bastone Magico\nrichiedono il Pendente Magico\nequipaggiato (o classe Mago).");
                warn.setStyle(TEXT_RED);
                leftPanel.getChildren().add(warn);
            }
            for (SpecialAttack s : specials) {
                boolean isStaffSpecial = staffEquipped &&
                        (s.getName().equals("Onda Magica") || s.getName().equals("Colpo Vitale"));
                boolean locked = isStaffSpecial && staffSpecialsLocked;
                boolean canUse = !locked && player().canUseSpecial(s.getStaminaCost());

                Button b = btn(s.getName() + "  costo:" + s.getStaminaCost());
                if (locked) {
                    b.setStyle(BTN_GRAY);
                    b.setTooltip(new Tooltip("Richiede Pendente Magico o classe Mago"));
                    b.setDisable(true);
                } else if (!canUse) {
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

        Label stanzaLbl = bold(room.getName(), 13);
        stanzaLbl.setStyle(TEXT_GOLD);
        rightPanel.getChildren().addAll(stanzaLbl, sep());

        Wave wave = room.getCurrentWave();
        boolean roomCleared = room.isCleared();

        if (!roomCleared && wave != null && !wave.getEnemies().isEmpty()) {
            Label wLbl = bold(wave.getName(), 11);
            wLbl.setStyle(TEXT_WHITE);
            rightPanel.getChildren().add(wLbl);
            rightPanel.getChildren().add(sep());

            List<Enemy> alive = wave.getEnemies().stream().filter(Enemy::isAlive).toList();
            for (Enemy e : alive) {
                String tags = (e.isBoss()    ? "[BOSS] " : "")
                            + (e.isImmune()  ? "[IMM] "  : "")
                            + (e.isStunned() ? "[STORD]" : "")
                            + (e.isEgg()     ? "[UOVO]"  : "");
                String label = e.getName() + (tags.isBlank() ? "" : " " + tags.strip())
                             + "\nHP: " + e.getCurrentHp() + "/" + e.getMaxHp();
                Button eb = enemyBtn(label);
                if (e == selectedEnemy)
                    eb.setStyle(eb.getStyle() + "-fx-border-color:#e0c46c;-fx-border-width:2;");
                eb.setOnAction(ev -> { selectedEnemy = e; refreshRight(); });
                rightPanel.getChildren().add(eb);
            }
        } else if (roomCleared) {
            Label cleared = lbl("Stanza liberata!");
            cleared.setStyle(TEXT_GREEN);
            rightPanel.getChildren().add(cleared);

            if (gc.getGameState().getDungeonMap().hasNextRoom()) {
                Button adv = new Button("Avanza \u2192");
                adv.setStyle(BTN_GREEN);
                adv.setMaxWidth(Double.MAX_VALUE);
                adv.setOnAction(e -> doAdvanceRoom());
                rightPanel.getChildren().add(adv);
            } else {
                showVictory();
            }
        }
    }

    // =========================================================================
    // Bottom bar — con bottone Menu sempre visibile
    // =========================================================================

    private void buildBottomBar() {
        bottomBar.getChildren().clear();
        Room room = gc.getCurrentRoom();
        boolean roomCleared = room.isCleared();

        Button btnMenu  = dangerBtn("Menu");
        Button btnInv   = btn("Inventario");
        Button btnEquip = btn("Equip.");
        Button btnSave  = btn("Salva");

        btnMenu.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Torna al Menu");
            alert.setHeaderText("Vuoi tornare al menu principale?");
            alert.setContentText("I progressi non salvati andranno persi.");
            alert.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.OK) app.showMenu(stage);
            });
        });
        btnInv.setOnAction(e   -> showInventario());
        btnEquip.setOnAction(e -> showEquip());
        btnSave.setOnAction(e  -> { gc.saveGame(); appendLog("[SAVE] Partita salvata."); });

        bottomBar.getChildren().addAll(btnMenu, btnInv, btnEquip);

        if (!roomCleared) {
            Button btnAtk     = btn("ATK");
            Button btnSpecial = btn("Special ATK");
            Button btnPotion  = btn("Pozione (x" + gc.countPotions() + ")");
            Button btnFuga    = dangerBtn("Fuga");

            btnAtk.setOnAction(e     -> doNormalAttack());
            btnSpecial.setOnAction(e -> showSpecialAtk());
            btnPotion.setOnAction(e  -> doUsePotion());
            btnFuga.setOnAction(e    -> doFlee());

            btnFuga.setDisable(!gc.canFlee());
            bottomBar.getChildren().addAll(btnAtk, btnSpecial, btnPotion, btnFuga);
        } else if (gc.getGameState().getDungeonMap().hasNextRoom()) {
            Button btnAdv = new Button("Avanza \u2192");
            btnAdv.setStyle(BTN_GREEN);
            btnAdv.setOnAction(e -> doAdvanceRoom());
            bottomBar.getChildren().add(btnAdv);
        }

        bottomBar.getChildren().add(btnSave);
    }

    // =========================================================================
    // Azioni
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
        if (selectedEnemy.isEgg()) {
            appendLog("[!] Non puoi attaccare un Uovo direttamente — aspetta che si schiuda!");
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

    private void doUsePotion() {
        CombatController.TurnResult result = combatController.playerUsePotion();
        result.log().forEach(this::appendLog);
        showScheda();
        buildBottomBar();
    }

    private void doAdvanceRoom() {
        gc.advanceRoom();
        selectedEnemy  = null;
        lastLoggedWave = null;
        Room newRoom = gc.getCurrentRoom();
        if (newRoom.getId().equals("r5")) {
            Wave w = newRoom.getCurrentWave();
            if (w != null) {
                w.getEnemies().stream().findFirst().ifPresent(
                    d -> combatController.checkAndActivateDragonBuff(d));
            }
        }
        logRoomEntry();
        refresh();
    }

    private void handleTurnResult(CombatController.TurnResult result) {
        if (result == null) return;
        result.log().forEach(this::appendLog);

        if (result.playerDead()) {
            showGameOver();
            return;
        }

        if (result.fleeSuccess()) {
            appendLog("[FUGA] Sei fuggito dalla stanza!");
            refresh();
            return;
        }

        if (result.waveCleared()) {
            appendLog("[WAVE] Ondata completata!");
            gc.checkWaveCleared();
            if (gc.getGameState().isVictory()) {
                showVictory();
                return;
            }
            selectedEnemy = null;
        }

        refresh();
    }

    // =========================================================================
    // Game over / vittoria
    // =========================================================================

    private void showGameOver() {
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER); vb.setStyle(DARK);
        Label l = bold("SEI MORTO.  GAME OVER.", 28); l.setStyle(TEXT_RED);
        Button back = btn("Torna al Menu");
        back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().addAll(l, back);
        root.setCenter(vb); root.setLeft(null); root.setRight(null); root.setBottom(null);
    }

    private void showVictory() {
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER); vb.setStyle(DARK);
        Label l = bold("HAI VINTO!  L'Ultimo Drago e' stato sconfitto!", 22); l.setStyle(TEXT_GOLD);
        Button back = btn("Torna al Menu");
        back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().addAll(l, back);
        root.setCenter(vb); root.setLeft(null); root.setRight(null); root.setBottom(null);
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
        l.setStyle(TEXT_WHITE); return l;
    }
    private Label badge(String text, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 11));
        l.setStyle(color); return l;
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
        b.setMaxWidth(Double.MAX_VALUE); return b;
    }
    private Button enemyBtn(String t) {
        Button b = new Button(t); b.setWrapText(true); b.setMaxWidth(Double.MAX_VALUE);
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
