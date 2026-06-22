package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.CombatController;
import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.Mage;
import it.unicam.cs.mpgc.rpg123891.model.character.Thief;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
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
 * Layout:
 *   ┌──────────────┬──────────────────────┬──────────────┐
 *   │  LEFT PANEL  │     LOG / CENTER     │  RIGHT PANEL │
 *   │  (scheda /   │  (cosa succede)      │  (stanza +   │
 *   │  inventario/ │                      │   nemici)    │
 *   │  equip /     │                      │              │
 *   │  special)    │                      │              │
 *   ├──────────────┴──────────────────────┴──────────────┤
 *   │  [Inventario] [Equip.] [ATK] [Special ATK] [Fuga]  │
 *   └─────────────────────────────────────────────────────┘
 */
public class GameScreen {

    private static final String DARK      = "-fx-background-color:#1a1a2e;";
    private static final String PANEL_BG  = "-fx-background-color:#16213e;-fx-border-color:#3a3a6e;-fx-border-width:1;";
    private static final String TEXT_GOLD = "-fx-text-fill:#e0c46c;";
    private static final String TEXT_WHITE= "-fx-text-fill:#cccccc;";
    private static final String TEXT_RED  = "-fx-text-fill:#e05555;";
    private static final String TEXT_GREEN= "-fx-text-fill:#55e077;";
    private static final String BTN_STYLE = "-fx-background-color:#2a2a4e;-fx-text-fill:#e0c46c;" +
                                             "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:6 12;";
    private static final String BTN_DANGER= "-fx-background-color:#4e2a2a;-fx-text-fill:#e06c6c;" +
                                             "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:6 12;";

    private final BorderPane root;
    private final GameController gc;
    private final Stage stage;
    private final FxApp app;
    private CombatController combatController;

    // --- Colonna sinistra ---
    private final VBox leftPanel   = new VBox(8);
    // --- Centro ---
    private final TextArea logArea = new TextArea();
    // --- Colonna destra ---
    private final VBox rightPanel  = new VBox(8);
    // --- Bottoni inferiori ---
    private final HBox bottomBar   = new HBox(8);

    // Nemico selezionato (click nella lista destra)
    private Enemy selectedEnemy = null;
    // Item selezionato in inventario per equip
    private Item  selectedItem  = null;

    public GameScreen(GameController gc, Stage stage, FxApp app) {
        this.gc    = gc;
        this.stage = stage;
        this.app   = app;
        this.root  = new BorderPane();
        root.setStyle(DARK);

        combatController = new CombatController(
                gc, gc.getGameState().getDungeonMap());
        combatController.setListener(new CombatController.CombatListener() {
            public void onEvent(String msg)  { appendLog(msg); }
            public void onTurnEnd(List<String> log, boolean dead, boolean cleared) {}
        });

        buildLayout();
        refresh();
    }

    public BorderPane getRoot() { return root; }

    // =========================================================================
    // Layout
    // =========================================================================

    private void buildLayout() {
        // --- Log centrale ---
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle(PANEL_BG + "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        logArea.setPrefHeight(9999);
        VBox center = new VBox(logArea);
        VBox.setVgrow(logArea, Priority.ALWAYS);
        center.setPadding(new Insets(8));
        center.setStyle(PANEL_BG);

        // --- Pannelli laterali ---
        leftPanel.setPadding(new Insets(8));
        leftPanel.setStyle(PANEL_BG);
        leftPanel.setPrefWidth(230);

        rightPanel.setPadding(new Insets(8));
        rightPanel.setStyle(PANEL_BG);
        rightPanel.setPrefWidth(220);

        // --- Barra inferiore ---
        bottomBar.setPadding(new Insets(8));
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setStyle("-fx-background-color:#0f0f23;-fx-border-color:#3a3a6e;-fx-border-width:1 0 0 0;");

        root.setLeft(leftPanel);
        root.setCenter(center);
        root.setRight(rightPanel);
        root.setBottom(bottomBar);
    }

    // =========================================================================
    // Refresh
    // =========================================================================

    private void refresh() {
        showScheda();
        refreshRight();
        buildBottomBar();
    }

    // --- LEFT: Scheda giocatore ---
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
            statRow("HP",     p.getCurrentHp() + " / " + p.getMaxHp(), hpColor(p)),
            statRow("Difesa", String.valueOf(p.getDefense()),            TEXT_WHITE),
            statRow("Attacco",String.valueOf(p.getAttack()),             TEXT_WHITE),
            statRow("Agil.",  String.valueOf(p.getAgility()),            TEXT_WHITE),
            statRow("Stamina",p.getCurrentStamina()+" / "+p.getMaxStamina(), TEXT_WHITE),
            statRow("Crit",   String.format("%.0f%%", p.getCritChance()*100), TEXT_WHITE)
        );

        // Passive badge
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

    // --- LEFT: Inventario ---
    private void showInventario() {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("INVENTARIO", 14));
        leftPanel.getChildren().add(sep());

        List<Item> inv = player().getInventory();
        if (inv.isEmpty()) {
            leftPanel.getChildren().add(lbl("(vuoto)"));
        } else {
            for (Item item : inv) {
                Button btn = itemBtn(item.getName());
                btn.setOnAction(e -> {
                    selectedItem = item;
                    highlightSelected(btn);
                    // Se è una Pozione: usala subito
                    if (item instanceof Potion) {
                        gc.useFirstPotion();
                        appendLog("Hai usato una Pozione!");
                        showInventario();
                        refreshRight();
                        showScheda();
                    }
                    // Se è un'arma: mostra panel equip
                    else if (item instanceof Weapon) {
                        showEquipPreview((Weapon) item);
                    }
                });
                leftPanel.getChildren().add(btn);
            }
        }

        leftPanel.getChildren().add(sep());
        Button back = btn("Indietro");
        back.setOnAction(e -> showScheda());
        leftPanel.getChildren().add(back);
    }

    // --- LEFT: Equipaggiamento ---
    private void showEquip() {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("EQUIPAGGIAMENTO", 13));
        leftPanel.getChildren().add(sep());

        EquipmentManager em = gc.getGameState().getEquipmentManager();

        for (EquipSlot slot : EquipSlot.values()) {
            Label slotLbl = bold(slotName(slot) + ":", 12);
            slotLbl.setStyle(TEXT_GOLD);
            Optional<Weapon> eq = em.getEquipped(slot);
            Label itemLbl = lbl(eq.map(Weapon::getName).orElse("(vuoto)"));

            HBox row = new HBox(6, slotLbl, itemLbl);
            if (eq.isPresent()) {
                Button unequipBtn = smallBtn("Rimuovi");
                unequipBtn.setOnAction(e -> {
                    em.unequip(slot);
                    appendLog("Rimosso: " + eq.get().getName());
                    showEquip();
                    showScheda();
                });
                row.getChildren().add(unequipBtn);
            }
            leftPanel.getChildren().add(row);
        }

        leftPanel.getChildren().add(sep());
        Label hint = lbl("Seleziona un'arma dall'Inventario per equipaggiarla.");
        hint.setWrapText(true);
        hint.setStyle(TEXT_WHITE + "-fx-font-size:11px;");
        leftPanel.getChildren().add(hint);

        Button back = btn("Indietro");
        back.setOnAction(e -> showScheda());
        leftPanel.getChildren().add(back);
    }

    // --- LEFT: Preview equip arma ---
    private void showEquipPreview(Weapon weapon) {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("EQUIPAGGIA", 13));
        leftPanel.getChildren().add(sep());
        leftPanel.getChildren().add(bold(weapon.getName(), 12));
        leftPanel.getChildren().add(lbl(weapon.getDescription()));
        leftPanel.getChildren().add(sep());

        GameCharacter p = player();
        EquipmentManager em = gc.getGameState().getEquipmentManager();
        EquipSlot slot = weapon.getSlot();

        // Mostra stat prima -> dopo
        for (StatModifier mod : weapon.getStatModifiers()) {
            int before = getStatValue(p, mod.stat());
            int after  = before + mod.amount();
            Label row  = lbl(statLabel(mod.stat()) + ": " + before + " → " + after);
            row.setStyle(mod.amount() > 0 ? TEXT_GREEN : TEXT_RED);
            leftPanel.getChildren().add(row);
        }
        leftPanel.getChildren().add(sep());

        EquipmentManager.EquipResult canEquip = em.canEquip(weapon);
        Button equipBtn = btn("Equipaggia in " + slotName(slot));
        equipBtn.setDisable(!canEquip.success());
        equipBtn.setOnAction(e -> {
            EquipmentManager.EquipResult r = em.equip(weapon);
            appendLog(r.message());
            showScheda();
        });

        if (!canEquip.success()) {
            Label warn = lbl(canEquip.message());
            warn.setStyle(TEXT_RED);
            leftPanel.getChildren().add(warn);
        }

        Button back = btn("Indietro");
        back.setOnAction(e -> showInventario());
        leftPanel.getChildren().addAll(equipBtn, back);
    }

    // --- LEFT: Special ATK ---
    private void showSpecialAtk() {
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(bold("ATTACCHI SPECIALI", 13));
        leftPanel.getChildren().add(sep());

        List<SpecialAttack> specials = gc.getGameState().getEquipmentManager().getEquippedSpecials();
        if (specials.isEmpty()) {
            leftPanel.getChildren().add(lbl("Nessun attacco speciale.\nEquipa un'arma prima."));
        } else {
            GameCharacter p = player();
            for (SpecialAttack s : specials) {
                boolean canUse = p.canUseSpecial(s.getStaminaCost());
                String label   = s.getName() + "  costo:" + s.getStaminaCost();
                Button btn     = btn(label);
                if (!canUse) {
                    btn.setStyle(BTN_DANGER);
                    btn.setTooltip(new Tooltip("Stamina insufficiente"));
                }
                btn.setDisable(!canUse);
                btn.setOnAction(e -> executeSpecial(s));
                leftPanel.getChildren().add(btn);
            }
        }

        leftPanel.getChildren().add(sep());
        Button back = btn("Indietro");
        back.setOnAction(e -> showScheda());
        leftPanel.getChildren().add(back);
    }

    // --- RIGHT: Stanza + nemici ---
    private void refreshRight() {
        rightPanel.getChildren().clear();
        Room room = gc.getCurrentRoom();

        Label stanzaLbl = bold("Stanza:", 12);
        stanzaLbl.setStyle(TEXT_GOLD);
        Label stanzaName = lbl(room.getName());
        rightPanel.getChildren().addAll(stanzaLbl, stanzaName, sep());

        Wave wave = room.getCurrentWave();
        if (wave != null) {
            Label nemiciLbl = bold("Nemici:", 12);
            nemiciLbl.setStyle(TEXT_GOLD);
            rightPanel.getChildren().add(nemiciLbl);

            List<Enemy> alive = wave.getEnemies().stream().filter(Enemy::isAlive).toList();
            for (Enemy e : alive) {
                String tags = (e.isBoss() ? "[BOSS] " : "") +
                              (e.isImmune() ? "[IMM] " : "") +
                              (e.isStunned() ? "[STORD] " : "");
                Button eb = enemyBtn(e.getName() + " " + tags +
                                     "\nHP: " + e.getCurrentHp() + "/" + e.getMaxHp());
                if (e == selectedEnemy) {
                    eb.setStyle(eb.getStyle() + "-fx-border-color:#e0c46c;-fx-border-width:2;");
                }
                eb.setOnAction(ev -> {
                    selectedEnemy = e;
                    refreshRight();
                });
                rightPanel.getChildren().add(eb);
            }
        } else if (room.isCleared()) {
            Label cleared = lbl("Stanza liberata!");
            cleared.setStyle(TEXT_GREEN);
            rightPanel.getChildren().add(cleared);

            if (gc.getGameState().getDungeonMap().hasNextRoom()) {
                Button advBtn = btn("Avanza →");
                advBtn.setOnAction(e -> {
                    gc.advanceRoom();
                    selectedEnemy = null;
                    appendLog("--- Nuova stanza: " + gc.getCurrentRoom().getName() + " ---");
                    refresh();
                });
                rightPanel.getChildren().add(advBtn);
            } else {
                showVictory();
            }
        }
    }

    // --- BOTTOM BAR ---
    private void buildBottomBar() {
        bottomBar.getChildren().clear();

        Button btnInv     = btn("Inventario");
        Button btnEquip   = btn("Equip.");
        Button btnAtk     = btn("ATK");
        Button btnSpecial = btn("Special ATK");
        Button btnFuga    = dangerBtn("Fuga");
        Button btnSave    = btn("Salva");

        btnInv.setOnAction(e -> showInventario());
        btnEquip.setOnAction(e -> showEquip());
        btnAtk.setOnAction(e -> doNormalAttack());
        btnSpecial.setOnAction(e -> showSpecialAtk());
        btnFuga.setOnAction(e -> doFlee());
        btnSave.setOnAction(e -> { gc.saveGame(); appendLog("[SAVE] Partita salvata."); });

        btnFuga.setDisable(!gc.canFlee());

        bottomBar.getChildren().addAll(btnInv, btnEquip, btnAtk, btnSpecial, btnFuga, btnSave);
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
            appendLog("[!] " + selectedEnemy.getName() + " e' immune!");
            return;
        }
        CombatController.TurnResult result = combatController.playerNormalAttack(selectedEnemy);
        handleTurnResult(result);
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
        CombatController.TurnResult result = combatController.playerFlee();
        handleTurnResult(result);
    }

    private void handleTurnResult(CombatController.TurnResult result) {
        if (result == null) return;
        result.log().forEach(this::appendLog);
        refresh();

        if (result.playerDead()) {
            showGameOver();
            return;
        }
        if (result.fleeSuccess()) {
            appendLog("Sei fuggito!");
        }
        if (result.waveCleared()) {
            appendLog("[WAVE CLEARED] Ondata completata!");
            rollDrops();
            // Controlla se la stanza è completata
            gc.checkWaveCleared();
            if (gc.getGameState().isVictory()) {
                showVictory();
                return;
            }
            selectedEnemy = null;
            refresh();
        }
    }

    private void rollDrops() {
        Wave wave = gc.getCurrentRoom().getCurrentWave();
        if (wave == null) return;
        wave.getEnemies().stream().filter(e -> !e.isAlive()).forEach(e -> {
            if (dropsMe(e) && Math.random() < 0.5) {
                player().addItem(new Meat());
                appendLog("[DROP] " + e.getName() + " ha lasciato Carne!");
            }
        });
    }

    private boolean dropsMe(Enemy e) {
        return switch (e.getName()) {
            case "Cinghiale", "Lupo", "Cucciolo di Drago" -> true;
            default -> false;
        };
    }

    // =========================================================================
    // Game Over / Victory
    // =========================================================================

    private void showGameOver() {
        root.getChildren().clear();
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle(DARK);
        Label l = bold("SEI MORTO.  GAME OVER.", 28);
        l.setStyle(TEXT_RED);
        Button back = btn("Torna al Menu");
        back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().addAll(l, back);
        root.setCenter(vb);
    }

    private void showVictory() {
        root.getChildren().clear();
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle(DARK);
        Label l = bold("HAI VINTO!  L'Ultimo Drago e' stato sconfitto!", 22);
        l.setStyle(TEXT_GOLD);
        Button back = btn("Torna al Menu");
        back.setOnAction(e -> app.showMenu(stage));
        vb.getChildren().addAll(l, back);
        root.setCenter(vb);
    }

    // =========================================================================
    // Helpers UI
    // =========================================================================

    private void appendLog(String msg) {
        Platform.runLater(() -> {
            logArea.appendText(msg + "\n");
        });
    }

    private GameCharacter player() {
        return (GameCharacter) gc.getPlayer();
    }

    private String hpColor(GameCharacter p) {
        double ratio = (double) p.getCurrentHp() / p.getMaxHp();
        if (ratio > 0.5) return TEXT_GREEN;
        if (ratio > 0.25) return "-fx-text-fill:#e0a030;";
        return TEXT_RED;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setStyle(TEXT_WHITE);
        l.setWrapText(true);
        return l;
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

    private HBox statRow(String label, String value, String valueColor) {
        Label k = new Label(label + ": ");
        k.setStyle(TEXT_WHITE + "-fx-font-size:12px;");
        Label v = new Label(value);
        v.setStyle(valueColor + "-fx-font-size:12px;-fx-font-weight:bold;");
        return new HBox(4, k, v);
    }

    private Separator sep() { return new Separator(); }

    private Button btn(String text) {
        Button b = new Button(text);
        b.setStyle(BTN_STYLE);
        return b;
    }

    private Button dangerBtn(String text) {
        Button b = new Button(text);
        b.setStyle(BTN_DANGER);
        return b;
    }

    private Button smallBtn(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:#2a2a4e;-fx-text-fill:#e0c46c;-fx-font-size:10px;-fx-cursor:hand;");
        return b;
    }

    private Button itemBtn(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:#1e1e3e;-fx-text-fill:#cccccc;" +
                   "-fx-font-size:12px;-fx-cursor:hand;-fx-padding:4 8;");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private Button enemyBtn(String text) {
        Button b = new Button(text);
        b.setWrapText(true);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color:#1e1e3e;-fx-text-fill:#e05555;" +
                   "-fx-font-size:11px;-fx-cursor:hand;-fx-padding:4 6;-fx-alignment:left;");
        return b;
    }

    private void highlightSelected(Button b) {
        b.setStyle(b.getStyle() + "-fx-border-color:#e0c46c;-fx-border-width:2;");
    }

    private String slotName(EquipSlot slot) {
        return switch (slot) {
            case MAIN_HAND -> "Arma (Mano DX)";
            case OFF_HAND  -> "Scudo (Mano SX)";
            case BODY      -> "Armatura / Pendente";
        };
    }

    private String statLabel(String stat) {
        return switch (stat.toUpperCase()) {
            case "ATTACK"   -> "Attacco";
            case "DEFENSE"  -> "Difesa";
            case "AGILITY"  -> "Agilità";
            case "MAX_HP"   -> "HP Max";
            case "STAMINA"  -> "Stamina";
            case "CRIT"     -> "Crit%";
            default         -> stat;
        };
    }

    private int getStatValue(GameCharacter p, String stat) {
        return switch (stat.toUpperCase()) {
            case "ATTACK"   -> p.getAttack();
            case "DEFENSE"  -> p.getDefense();
            case "AGILITY"  -> p.getAgility();
            case "MAX_HP"   -> p.getMaxHp();
            case "STAMINA"  -> p.getMaxStamina();
            default         -> 0;
        };
    }
}
