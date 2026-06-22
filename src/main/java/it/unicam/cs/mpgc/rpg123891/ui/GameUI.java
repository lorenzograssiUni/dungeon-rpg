package it.unicam.cs.mpgc.rpg123891.ui;

import it.unicam.cs.mpgc.rpg123891.controller.CombatController;
import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.*;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyAbility;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;

import java.util.List;
import java.util.Scanner;

/**
 * UI testuale a console per Dungeon RPG.
 *
 * Flusso:
 *   main menu -> scelta classe -> loop stanze -> per ogni stanza: loop ondate
 *     -> per ogni ondata: loop turni combattimento
 *       -> per ogni turno: scelta azione (attacca, speciale, pozione, fuggi)
 *
 * Mostra sempre lo stato del giocatore (HP, stamina, agilita') prima di ogni
 * azione, e la lista nemici vivi con HP.
 */
public class GameUI {

    private static final String LINE  = "=" .repeat(60);
    private static final String DLINE = "-" .repeat(60);

    private final GameController    gameController;
    private CombatController        combatController;
    private final Scanner           scanner;

    public GameUI(GameController gameController) {
        this.gameController = gameController;
        this.scanner        = new Scanner(System.in);
    }

    // =========================================================================
    // Entry point
    // =========================================================================

    public void run() {
        printBanner();
        mainMenu();
    }

    // =========================================================================
    // Menu principale
    // =========================================================================

    private void mainMenu() {
        while (true) {
            println(LINE);
            println("  MENU PRINCIPALE");
            println(LINE);
            println("  1. Nuova Partita");
            println("  2. Carica Partita");
            println("  0. Esci");
            print("  Scelta: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> startNewGame();
                case "2" -> loadGame();
                case "0" -> { println("Arrivederci!"); return; }
                default  -> println("[!] Scelta non valida.");
            }
        }
    }

    // =========================================================================
    // Nuova partita
    // =========================================================================

    private void startNewGame() {
        println(LINE);
        println("  SCEGLI LA TUA CLASSE");
        println(LINE);
        println("  1. Guerriero  (HP:120 ATK:22 DEF:8  AGI:4  STA:8  CRIT:5%)");
        println("     Passive: blocco 20% fisici, +5DEF +20MaxHP a ogni stanza");
        println("  2. Mago       (HP:75  ATK:15 DEF:4  AGI:6  STA:10 CRIT:5%)");
        println("     Passive: scudo magico assorbe 1 fisico, +30% danno magico subito");
        println("  3. Ladro      (HP:90  ATK:18 DEF:6  AGI:8  STA:12 CRIT:25%)");
        println("     Passive: 1° attacco sempre critico, +2% crit per attacco (max 50%)");
        print("  Scelta: ");
        String choice = scanner.nextLine().trim();

        String name = askName();
        PlayerCharacter player = switch (choice) {
            case "2" -> new Mage(name);
            case "3" -> new Thief(name);
            default  -> new Warrior(name);
        };

        gameController.startNewGame(player);
        this.combatController = new CombatController(
                gameController, gameController.getGameState().getDungeonMap());
        combatController.setListener(new LogListener());

        println("\nBenvenuto, " + name + " (" + player.getCharacterClass() + ")!");
        gameLoop();
    }

    private String askName() {
        print("  Nome personaggio (invio = Eroe): ");
        String n = scanner.nextLine().trim();
        return n.isEmpty() ? "Eroe" : n;
    }

    // =========================================================================
    // Carica partita
    // =========================================================================

    private void loadGame() {
        if (!gameController.hasSavedGame()) {
            println("[!] Nessun salvataggio trovato.");
            return;
        }
        gameController.loadGame();
        this.combatController = new CombatController(
                gameController, gameController.getGameState().getDungeonMap());
        combatController.setListener(new LogListener());
        println("\n[SAVE] Partita caricata!");
        gameLoop();
    }

    // =========================================================================
    // Game loop principale (stanze)
    // =========================================================================

    private void gameLoop() {
        while (!gameController.getGameState().isGameOver()) {
            Room room = gameController.getCurrentRoom();
            printRoomHeader(room);

            // Mostra entry loot se presente (gia' raccolto da advanceRoom)
            // Il loot viene distribuito automaticamente da GameController

            // Loop ondate
            roomLoop(room);

            if (gameController.getGameState().isGameOver()) break;

            // Stanza liberata: chiedi se avanzare
            if (room.isCleared()) {
                if (!gameController.getGameState().getDungeonMap().hasNextRoom()) {
                    printVictory();
                    return;
                }
                println(DLINE);
                println("  Stanza liberata! Premi INVIO per avanzare...");
                scanner.nextLine();
                gameController.advanceRoom();
            }
        }

        if (gameController.getGameState().isVictory()) {
            printVictory();
        } else {
            printGameOver();
        }
    }

    // =========================================================================
    // Room loop (ondate)
    // =========================================================================

    private void roomLoop(Room room) {
        while (!room.isCleared() && !gameController.getGameState().isGameOver()) {
            Wave wave = room.getCurrentWave();
            if (wave == null) break;

            println(DLINE);
            println("  " + wave.getLabel().toUpperCase());
            println(DLINE);

            // Buff passivo Drago (solo boss)
            if (room.getId().equals("r5")) {
                Enemy dragon = wave.getEnemies().stream().findFirst().orElse(null);
                if (dragon != null) combatController.checkAndActivateDragonBuff(dragon);
            }

            // Loop combattimento per questa ondata
            combatLoop(wave);

            if (gameController.getGameState().isGameOver()) return;
        }
    }

    // =========================================================================
    // Combat loop (turni)
    // =========================================================================

    private void combatLoop(Wave wave) {
        while (!wave.isCleared() && !gameController.getGameState().isGameOver()) {
            printCombatStatus(wave);
            String action = askCombatAction(wave);
            CombatController.TurnResult result = executeCombatAction(action, wave);

            if (result == null) continue;

            // Stampa log del turno
            println("");
            result.log().forEach(line -> println("  " + line));

            if (result.isCombatOver()) {
                if (result.playerDead()) {
                    printGameOver();
                    return;
                }
                if (result.fleeSuccess()) {
                    println("  Sei fuggito!");
                    return;
                }
                if (result.waveCleared()) {
                    println("  [WAVE CLEARED] Ondata completata!");
                    // Drop probabilistici 50% Carne per nemici che lo prevedono
                    rollDrops(wave);
                    return;
                }
            }
        }
    }

    // =========================================================================
    // Azioni combattimento
    // =========================================================================

    private String askCombatAction(Wave wave) {
        println("");
        println("  AZIONI:");
        println("  1. Attacco normale");
        println("  2. Attacco speciale");
        println("  3. Usa Pozione  (x" + gameController.countPotions() + ")");
        if (gameController.canFlee()) println("  4. Fuggi");
        println("  5. Inventario / Equipaggiamento");
        println("  S. Salva");
        print("  Scelta: ");
        return scanner.nextLine().trim().toUpperCase();
    }

    private CombatController.TurnResult executeCombatAction(String action, Wave wave) {
        return switch (action) {
            case "1" -> doNormalAttack(wave);
            case "2" -> doSpecialAttack(wave);
            case "3" -> combatController.playerUsePotion();
            case "4" -> {
                if (!gameController.canFlee()) {
                    println("  [!] Non puoi fuggire!"); yield null;
                }
                yield combatController.playerFlee();
            }
            case "5" -> { showInventory(); yield null; }
            case "S" -> { gameController.saveGame(); println("  [SAVE] Partita salvata."); yield null; }
            default  -> { println("  [!] Scelta non valida."); yield null; }
        };
    }

    private CombatController.TurnResult doNormalAttack(Wave wave) {
        Enemy target = selectTarget(wave);
        if (target == null) return null;
        return combatController.playerNormalAttack(target);
    }

    private CombatController.TurnResult doSpecialAttack(Wave wave) {
        GameCharacter player = (GameCharacter) gameController.getPlayer();
        List<SpecialAttack> specials = getEquippedSpecials(player);

        if (specials.isEmpty()) {
            println("  [!] Nessun attacco speciale disponibile (equipa un'arma).");
            return null;
        }

        println("  Attacchi speciali:");
        for (int i = 0; i < specials.size(); i++) {
            SpecialAttack s = specials.get(i);
            String affordable = player.canUseSpecial(s.getStaminaCost()) ? "" : " [STAMINA INSUFFICIENTE]";
            println(String.format("  %d. %s (costo: %d STA) - %s%s",
                    i + 1, s.getName(), s.getStaminaCost(), s.getDescription(), affordable));
        }
        println("  0. Annulla");
        print("  Scelta: ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return null;

        int idx;
        try { idx = Integer.parseInt(input) - 1; } catch (NumberFormatException e) { return null; }
        if (idx < 0 || idx >= specials.size()) return null;

        SpecialAttack chosen = specials.get(idx);
        if (!player.canUseSpecial(chosen.getStaminaCost())) {
            println("  [!] Stamina insufficiente!");
            return null;
        }

        // AOE: Onda Magica, Spazzatutto
        if (isAoe(chosen)) {
            return combatController.playerAoeAttack(chosen);
        }

        // Single target
        Enemy target = selectTarget(wave);
        if (target == null) return null;
        return combatController.playerSpecialAttack(chosen, target);
    }

    /** Onda Magica e Spazzatutto sono AOE. */
    private boolean isAoe(SpecialAttack s) {
        return s.getName().equals("Onda Magica") || s.getName().equals("Spazzatutto");
    }

    private Enemy selectTarget(Wave wave) {
        List<Enemy> alive = wave.getEnemies().stream()
                .filter(e -> e.isAlive() && !e.isImmune()).toList();
        if (alive.isEmpty()) {
            println("  [!] Nessun bersaglio valido.");
            return null;
        }
        if (alive.size() == 1) return alive.get(0);

        println("  Scegli bersaglio:");
        for (int i = 0; i < alive.size(); i++) {
            Enemy e = alive.get(i);
            println(String.format("  %d. %s (HP: %d/%d)",
                    i + 1, e.getName(), e.getCurrentHp(), e.getMaxHp()));
        }
        print("  Scelta: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < alive.size()) return alive.get(idx);
        } catch (NumberFormatException ignored) {}
        return alive.get(0);
    }

    // =========================================================================
    // Drop 50% Carne
    // =========================================================================

    private void rollDrops(Wave wave) {
        wave.getEnemies().stream()
                .filter(e -> !e.isAlive() && dropsMeat(e))
                .forEach(e -> {
                    if (Math.random() < 0.50) {
                        ((GameCharacter) gameController.getPlayer()).addItem(new it.unicam.cs.mpgc.rpg123891.model.item.Meat());
                        println("  [DROP] " + e.getName() + " ha lasciato Carne!");
                    }
                });
    }

    /** Nemici che possono droppare Carne al 50%. */
    private boolean dropsMeat(Enemy e) {
        return switch (e.getName()) {
            case "Cinghiale", "Lupo", "Cucciolo di Drago" -> true;
            default -> false;
        };
    }

    // =========================================================================
    // Display
    // =========================================================================

    private void printBanner() {
        println(LINE);
        println("         DUNGEON RPG");
        println(LINE);
    }

    private void printRoomHeader(Room room) {
        println("\n" + LINE);
        println("  STANZA: " + room.getName().toUpperCase());
        println("  " + room.getDescription());
        println(LINE);
    }

    private void printCombatStatus(Wave wave) {
        println(DLINE);
        // Giocatore
        GameCharacter p = (GameCharacter) gameController.getPlayer();
        println(String.format("  %s  HP: %s  STA: %d/%d  AGI: %d  CRIT: %.0f%%",
                p.getName(),
                hpBar(p.getCurrentHp(), p.getMaxHp()),
                p.getCurrentStamina(), p.getMaxStamina(),
                p.getAgility(),
                p.getCritChance() * 100));

        // Indicatori passive attive
        if (p instanceof Warrior w && w.getBlockChance() > 0)
            print("  [BLOCCO:20%]");
        if (p instanceof Mage m && m.isMagicShieldActive())
            print("  [SCUDO MAGICO ATTIVO]");
        if (p instanceof Thief t && t.isStealthBonusActive())
            print("  [STEALTH: prossimo attacco critico]");

        // BurnEffect attivo
        if (combatController.getActiveBurn() != null) {
            println(String.format("  [BRUCIATURA: %d dmg/turno, %d turni rimasti]",
                    combatController.getActiveBurn().getDamagePerTurn(),
                    combatController.getActiveBurn().getTurnsRemaining()));
        }
        println("");

        // Nemici
        println("  NEMICI:");
        wave.getEnemies().stream().filter(Enemy::isAlive).forEach(e -> {
            String immune  = e.isImmune()  ? " [IMMUNE]"  : "";
            String stunned = e.isStunned() ? " [STORDITO]" : "";
            String boss    = e.isBoss()    ? " [BOSS]"     : "";
            println(String.format("    - %s%s%s%s  HP: %s",
                    e.getName(), boss, immune, stunned,
                    hpBar(e.getCurrentHp(), e.getMaxHp())));
        });
    }

    private String hpBar(int current, int max) {
        int bars = max > 0 ? (int)(10.0 * current / max) : 0;
        String filled = "#".repeat(Math.max(0, bars));
        String empty  = ".".repeat(Math.max(0, 10 - bars));
        return String.format("[%s%s] %d/%d", filled, empty, current, max);
    }

    private void showInventory() {
        println(DLINE);
        println("  INVENTARIO:");
        List<Item> inv = gameController.getPlayer().getInventory();
        if (inv.isEmpty()) { println("  (vuoto)"); return; }
        inv.forEach(item -> println("  - " + item.getName() + ": " + item.getDescription()));
    }

    private List<SpecialAttack> getEquippedSpecials(GameCharacter player) {
        // Raccoglie tutti gli speciali dalle armi nell'inventario
        // (semplificazione: include tutte le armi, non solo quelle equipaggiate)
        return player.getInventory().stream()
                .filter(i -> i instanceof Weapon)
                .flatMap(i -> ((Weapon) i).getSpecialAttacks().stream())
                .toList();
    }

    private void printVictory() {
        println("\n" + LINE);
        println("  *** HAI VINTO! L'Ultimo Drago e' stato sconfitto! ***");
        println("  Il dungeon e' stato liberato. Sei un eroe leggendario!");
        println(LINE);
    }

    private void printGameOver() {
        println("\n" + LINE);
        println("  *** SEI MORTO. GAME OVER. ***");
        println(LINE);
    }

    // =========================================================================
    // Helpers I/O
    // =========================================================================

    private void println(String s) { System.out.println(s); }
    private void print(String s)   { System.out.print(s); System.out.flush(); }

    // =========================================================================
    // CombatListener per log inline
    // =========================================================================

    private class LogListener implements CombatController.CombatListener {
        @Override
        public void onEvent(String message) { println("  " + message); }
        @Override
        public void onTurnEnd(List<String> log, boolean playerDead, boolean waveCleared) {}
    }
}
