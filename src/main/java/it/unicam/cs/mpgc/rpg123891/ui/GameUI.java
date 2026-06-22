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
 */
public class GameUI {

    private static final String LINE  = "=".repeat(60);
    private static final String DLINE = "-".repeat(60);

    private final GameController gameController;
    private CombatController     combatController;
    private final Scanner        scanner;

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
            String choice = prompt("  Scelta: ");
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
        println("     Passive: 1' attacco sempre critico, +2% crit per attacco (max 50%)");
        String choice = prompt("  Scelta: ");
        String name   = prompt("  Nome personaggio (invio = Eroe): ");
        if (name.isEmpty()) name = "Eroe";

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

    // =========================================================================
    // Carica partita
    // =========================================================================

    private void loadGame() {
        if (!gameController.hasSavedGame()) { println("[!] Nessun salvataggio trovato."); return; }
        gameController.loadGame();
        this.combatController = new CombatController(
                gameController, gameController.getGameState().getDungeonMap());
        combatController.setListener(new LogListener());
        println("\n[SAVE] Partita caricata!");
        gameLoop();
    }

    // =========================================================================
    // Game loop
    // =========================================================================

    private void gameLoop() {
        while (!gameController.getGameState().isGameOver()) {
            Room room = gameController.getCurrentRoom();
            printRoomHeader(room);
            roomLoop(room);
            if (gameController.getGameState().isGameOver()) break;
            if (room.isCleared()) {
                if (!gameController.getGameState().getDungeonMap().hasNextRoom()) {
                    printVictory(); return;
                }
                println(DLINE);
                prompt("  Stanza liberata! Premi INVIO per avanzare...");
                gameController.advanceRoom();
            }
        }
        if (gameController.getGameState().isVictory()) printVictory();
        else printGameOver();
    }

    private void roomLoop(Room room) {
        while (!room.isCleared() && !gameController.getGameState().isGameOver()) {
            Wave wave = room.getCurrentWave();
            if (wave == null) break;
            println(DLINE);
            println("  " + wave.getLabel().toUpperCase());
            println(DLINE);
            if (room.getId().equals("r5")) {
                Enemy dragon = wave.getEnemies().stream().findFirst().orElse(null);
                if (dragon != null) combatController.checkAndActivateDragonBuff(dragon);
            }
            combatLoop(wave);
            if (gameController.getGameState().isGameOver()) return;
        }
    }

    private void combatLoop(Wave wave) {
        while (!wave.isCleared() && !gameController.getGameState().isGameOver()) {
            printCombatStatus(wave);
            String action = askCombatAction(wave);
            CombatController.TurnResult result = executeCombatAction(action, wave);
            if (result == null) continue;
            println("");
            result.log().forEach(line -> println("  " + line));
            if (result.isCombatOver()) {
                if (result.playerDead())   { printGameOver(); return; }
                if (result.fleeSuccess())  { println("  Sei fuggito!"); return; }
                if (result.waveCleared())  { println("  [WAVE CLEARED] Ondata completata!"); rollDrops(wave); return; }
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
        println("  5. Inventario");
        println("  S. Salva");
        return prompt("  Scelta: ").toUpperCase();
    }

    private CombatController.TurnResult executeCombatAction(String action, Wave wave) {
        return switch (action) {
            case "1" -> doNormalAttack(wave);
            case "2" -> doSpecialAttack(wave);
            case "3" -> combatController.playerUsePotion();
            case "4" -> {
                if (!gameController.canFlee()) { println("  [!] Non puoi fuggire!"); yield null; }
                yield combatController.playerFlee();
            }
            case "5" -> { showInventory(); yield null; }
            case "S" -> { gameController.saveGame(); println("  [SAVE] Partita salvata."); yield null; }
            default  -> { println("  [!] Scelta non valida."); yield null; }
        };
    }

    private CombatController.TurnResult doNormalAttack(Wave wave) {
        Enemy target = selectTarget(wave);
        return target == null ? null : combatController.playerNormalAttack(target);
    }

    private CombatController.TurnResult doSpecialAttack(Wave wave) {
        GameCharacter player = (GameCharacter) gameController.getPlayer();
        List<SpecialAttack> specials = getEquippedSpecials(player);
        if (specials.isEmpty()) { println("  [!] Nessun attacco speciale (nessuna arma equipaggiata)."); return null; }

        println("  Attacchi speciali:");
        for (int i = 0; i < specials.size(); i++) {
            SpecialAttack s = specials.get(i);
            String warn = player.canUseSpecial(s.getStaminaCost()) ? "" : " [STAMINA INSUFF.]";
            println(String.format("  %d. %s (STA:%d) - %s%s", i+1, s.getName(), s.getStaminaCost(), s.getDescription(), warn));
        }
        String input = prompt("  Scelta (0=annulla): ");
        if (input.equals("0")) return null;
        int idx;
        try { idx = Integer.parseInt(input) - 1; } catch (NumberFormatException e) { return null; }
        if (idx < 0 || idx >= specials.size()) return null;

        SpecialAttack chosen = specials.get(idx);
        if (!player.canUseSpecial(chosen.getStaminaCost())) { println("  [!] Stamina insufficiente!"); return null; }
        if (isAoe(chosen)) return combatController.playerAoeAttack(chosen);
        Enemy target = selectTarget(wave);
        return target == null ? null : combatController.playerSpecialAttack(chosen, target);
    }

    private boolean isAoe(SpecialAttack s) {
        return s.getName().equals("Onda Magica") || s.getName().equals("Spazzatutto");
    }

    private Enemy selectTarget(Wave wave) {
        List<Enemy> alive = wave.getEnemies().stream()
                .filter(e -> e.isAlive() && !e.isImmune()).toList();
        if (alive.isEmpty()) { println("  [!] Nessun bersaglio valido."); return null; }
        if (alive.size() == 1) return alive.get(0);
        println("  Scegli bersaglio:");
        for (int i = 0; i < alive.size(); i++) {
            Enemy e = alive.get(i);
            println(String.format("  %d. %s (HP: %d/%d)", i+1, e.getName(), e.getCurrentHp(), e.getMaxHp()));
        }
        try {
            int idx = Integer.parseInt(prompt("  Scelta: ").trim()) - 1;
            if (idx >= 0 && idx < alive.size()) return alive.get(idx);
        } catch (NumberFormatException ignored) {}
        return alive.get(0);
    }

    // =========================================================================
    // Drop Carne
    // =========================================================================

    private void rollDrops(Wave wave) {
        wave.getEnemies().stream().filter(e -> !e.isAlive() && dropsMeat(e)).forEach(e -> {
            if (Math.random() < 0.50) {
                ((GameCharacter) gameController.getPlayer())
                        .addItem(new it.unicam.cs.mpgc.rpg123891.model.item.Meat());
                println("  [DROP] " + e.getName() + " ha lasciato Carne!");
            }
        });
    }

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
        GameCharacter p = (GameCharacter) gameController.getPlayer();
        println(String.format("  %s  HP: %s  STA: %d/%d  AGI: %d  CRIT: %.0f%%",
                p.getName(), hpBar(p.getCurrentHp(), p.getMaxHp()),
                p.getCurrentStamina(), p.getMaxStamina(),
                p.getAgility(), p.getCritChance() * 100));
        if (p instanceof Mage m && m.isMagicShieldActive())    println("  [SCUDO MAGICO ATTIVO]");
        if (p instanceof Thief t && t.isStealthBonusActive())  println("  [STEALTH: critico garantito]");
        if (combatController.getActiveBurn() != null) {
            println(String.format("  [BRUCIATURA: %d dmg/t, %d turni]",
                    combatController.getActiveBurn().getDamagePerTurn(),
                    combatController.getActiveBurn().getTurnsRemaining()));
        }
        if (combatController.isCaricaActive()) println("  [CARICA! +3 DEF attivo]");
        println("");
        println("  NEMICI:");
        wave.getEnemies().stream().filter(Enemy::isAlive).forEach(e -> {
            String tags = (e.isBoss() ? " [BOSS]" : "") +
                          (e.isImmune() ? " [IMMUNE]" : "") +
                          (e.isStunned() ? " [STORDITO]" : "");
            println(String.format("    - %s%s  HP: %s", e.getName(), tags, hpBar(e.getCurrentHp(), e.getMaxHp())));
        });
    }

    private String hpBar(int current, int max) {
        int bars  = max > 0 ? (int)(10.0 * current / max) : 0;
        String ok = "#".repeat(Math.max(0, bars));
        String ko = ".".repeat(Math.max(0, 10 - bars));
        return String.format("[%s%s] %d/%d", ok, ko, current, max);
    }

    private void showInventory() {
        println(DLINE);
        println("  INVENTARIO:");
        List<Item> inv = gameController.getPlayer().getInventory();
        if (inv.isEmpty()) { println("  (vuoto)"); return; }
        inv.forEach(item -> println("  - " + item.getName() + ": " + item.getDescription()));
    }

    private List<SpecialAttack> getEquippedSpecials(GameCharacter player) {
        return player.getInventory().stream()
                .filter(i -> i instanceof Weapon)
                .flatMap(i -> ((Weapon) i).getSpecialAttacks().stream())
                .toList();
    }

    private void printVictory() {
        println("\n" + LINE);
        println("  *** HAI VINTO! L'Ultimo Drago e' stato sconfitto! ***");
        println(LINE);
    }

    private void printGameOver() {
        println("\n" + LINE);
        println("  *** SEI MORTO. GAME OVER. ***");
        println(LINE);
    }

    // =========================================================================
    // I/O helpers
    // =========================================================================

    /**
     * Stampa msg senza newline, svuota il buffer, poi legge la riga.
     * Unico punto di ingresso per tutta la lettura da stdin.
     */
    private String prompt(String msg) {
        System.out.print(msg);
        System.out.flush();
        return scanner.nextLine().trim();
    }

    private void println(String s) {
        System.out.println(s);
        System.out.flush();
    }

    // =========================================================================
    // CombatListener
    // =========================================================================

    private class LogListener implements CombatController.CombatListener {
        @Override public void onEvent(String message) { println("  " + message); }
        @Override public void onTurnEnd(List<String> log, boolean pd, boolean wc) {}
    }
}
