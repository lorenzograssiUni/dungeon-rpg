package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.game.GameState;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Armor;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Shield;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import it.unicam.cs.mpgc.rpg123891.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller principale del gioco.
 */
public class GameController {

    private GameState gameState;
    private final CombatSystem combatSystem = new CombatSystem();
    private final PersistenceManager persistenceManager;
    private boolean caricaActive = false;

    public GameController(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    // -------------------------------------------------------------------------
    // Nuova partita
    // -------------------------------------------------------------------------

    public void startNewGame(PlayerCharacter player) {
        this.gameState = new GameState(player);
        getCurrentRoom().setVisited(true);
        asGameCharacter(player).applyPassiveBonus();

        GameCharacter gc = asGameCharacter(player);
        gc.addItem(new Potion());
        gc.addItem(new Potion());
        gc.addItem(new Potion());
        // NON chiamiamo collectEntryLoot() all'avvio: la prima wave della foresta
        // (Stanza del Bastone) viene mostrata in UI e il loot viene consegnato
        // tramite handleLootWaveAutoAdvance() in GameScreen.
    }

    // -------------------------------------------------------------------------
    // Combattimento — attacchi normali
    // -------------------------------------------------------------------------

    public int playerAttack(Enemy enemy) {
        return combatSystem.executeAttack(
                asGameCharacter(gameState.getPlayer()), enemy,
                AttackType.PHYSICAL,
                enemy.getCritModifierOnPlayer()
        );
    }

    public int enemyAttack(Enemy enemy) {
        return combatSystem.executeAttack(
                enemy, asGameCharacter(gameState.getPlayer()),
                enemy.getAttackType(),
                0
        );
    }

    // -------------------------------------------------------------------------
    // Combattimento — attacchi speciali
    // -------------------------------------------------------------------------

    public int executeSpecial(SpecialAttack special, Enemy target) {
        GameCharacter player = asGameCharacter(gameState.getPlayer());
        player.consumeStaminaForSpecial(special.getStaminaCost());
        int damage = special.execute(player, target);
        if (special.getName().equals("Carica!")) caricaActive = true;
        return damage;
    }

    public Map<Enemy, Integer> executeAoeSpecial(SpecialAttack special) {
        GameCharacter player = asGameCharacter(gameState.getPlayer());
        player.consumeStaminaForSpecial(special.getStaminaCost());

        Wave wave = getCurrentRoom().getCurrentWave();
        List<Enemy> aliveEnemies = wave == null ? List.of() :
                wave.getEnemies().stream().filter(Enemy::isAlive).toList();

        Map<Enemy, Integer> results = new java.util.LinkedHashMap<>();
        for (Enemy enemy : aliveEnemies) {
            results.put(enemy, special.execute(player, enemy));
        }
        return results;
    }

    public void rollbackCarica() {
        if (caricaActive) {
            asGameCharacter(gameState.getPlayer()).increaseDefense(-3);
            caricaActive = false;
        }
    }

    public boolean isCaricaActive() { return caricaActive; }

    // -------------------------------------------------------------------------
    // Fuga
    // -------------------------------------------------------------------------

    public boolean canFlee() {
        Wave wave = getCurrentRoom().getCurrentWave();
        if (wave == null) return false;
        if (!wave.canFlee()) return false;
        return wave.getEnemies().stream().anyMatch(Enemy::isAlive);
    }

    public boolean flee() {
        return canFlee();
    }

    // -------------------------------------------------------------------------
    // Inventario e consumabili
    // -------------------------------------------------------------------------

    public boolean useFirstPotion() {
        List<Item> inventory = gameState.getPlayer().getInventory();
        Optional<Item> potionOpt = inventory.stream()
                .filter(item -> item instanceof Potion).findFirst();
        if (potionOpt.isEmpty()) return false;
        Potion potion = (Potion) potionOpt.get();
        potion.use(asGameCharacter(gameState.getPlayer()));
        inventory.remove(potion);
        return true;
    }

    public void collectEntryLoot() {
        Room room = getCurrentRoom();
        for (Item item : new ArrayList<>(room.getEntryLoot())) {
            asGameCharacter(gameState.getPlayer()).addItem(item);
        }
        room.getEntryLoot().clear();
    }

    public void collectWaveLoot(Wave wave) {
        for (Item item : new ArrayList<>(wave.getLoot())) {
            asGameCharacter(gameState.getPlayer()).addItem(item);
        }
        wave.getLoot().clear();
    }

    // -------------------------------------------------------------------------
    // Avanzamento stanza / ondata
    // -------------------------------------------------------------------------

    public boolean advanceRoom() {
        Room current = getCurrentRoom();
        if (!current.isCleared()) return false;
        if (!gameState.getDungeonMap().hasNextRoom()) return false;
        gameState.getDungeonMap().advanceToNextRoom();
        Room next = getCurrentRoom();
        next.setVisited(true);
        next.getAllEnemies().forEach(Enemy::applyPassiveBonus);
        asGameCharacter(gameState.getPlayer()).applyPassiveBonus();
        collectEntryLoot();
        return true;
    }

    public boolean checkWaveCleared() {
        Room room = getCurrentRoom();
        Wave wave = room.getCurrentWave();
        if (wave == null || !wave.isCleared()) return false;

        addConditionalLoot(room, wave);
        collectWaveLoot(wave);

        if (room.getId().equals("r4") && !room.hasMoreWaves()) {
            gameState.getDungeonMap().setTreasureRoomCleaned(true);
        }

        if (room.hasMoreWaves()) {
            room.advanceWave();
            return false;
        }

        if (!gameState.getDungeonMap().hasNextRoom()) {
            gameState.setVictory(true);
            gameState.setGameOver(true);
        }
        return true;
    }

    private void addConditionalLoot(Room room, Wave wave) {
        List<Item> inv = gameState.getPlayer().getInventory();
        boolean hasShield = inv.stream().anyMatch(i -> i instanceof Shield);
        boolean hasArmor  = inv.stream().anyMatch(i -> i instanceof Armor);

        switch (room.getId()) {
            case "r2" -> {
                if (wave.getName().equals("Ondata B") && !hasArmor) {
                    wave.addLoot(new Armor());
                }
            }
            case "r3" -> {
                if (wave.getName().equals("Ondata B") && !hasShield) {
                    wave.addLoot(new Shield());
                }
                if (wave.getName().equals("Ondata C")) {
                    if (!hasShield)     wave.addLoot(new Shield());
                    else if (!hasArmor) wave.addLoot(new Armor());
                }
            }
        }
    }

    public boolean checkPlayerDead() {
        if (!asGameCharacter(gameState.getPlayer()).isAlive()) {
            gameState.setGameOver(true);
            gameState.setVictory(false);
            return true;
        }
        return false;
    }

    public long countPotions() {
        return gameState.getPlayer().getInventory().stream()
                .filter(item -> item instanceof Potion).count();
    }

    // -------------------------------------------------------------------------
    // Persistenza
    // -------------------------------------------------------------------------

    public void saveGame()        { persistenceManager.save(gameState); }
    public GameState loadGame()   { this.gameState = persistenceManager.load(); return this.gameState; }
    public boolean hasSavedGame() { return persistenceManager.hasSave(); }

    // -------------------------------------------------------------------------
    // Getter
    // -------------------------------------------------------------------------

    public GameState getGameState()    { return gameState; }
    public Room getCurrentRoom()       { return gameState.getDungeonMap().getCurrentRoom(); }
    public PlayerCharacter getPlayer() { return gameState.getPlayer(); }

    private GameCharacter asGameCharacter(PlayerCharacter player) {
        return (GameCharacter) player;
    }
}
