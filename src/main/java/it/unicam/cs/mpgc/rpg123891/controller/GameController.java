package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.game.GameState;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.persistence.PersistenceManager;

import java.util.List;
import java.util.Optional;

/**
 * Controller principale del gioco.
 * Fa da ponte tra la UI e il model, coordinando le azioni del giocatore.
 * Il cast (GameCharacter) player e' sicuro perche' Warrior, Mage e Thief
 * estendono sempre GameCharacter e implementano PlayerCharacter.
 */
public class GameController {

    private GameState gameState;
    private final CombatSystem combatSystem = new CombatSystem();
    private final PersistenceManager persistenceManager;

    public GameController(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    /** Inizia una nuova partita con il personaggio scelto. */
    public void startNewGame(PlayerCharacter player) {
        this.gameState = new GameState(player);
        getCurrentRoom().setVisited(true);
        asGameCharacter(player).applyPassiveBonus();
    }

    /** Esegue un attacco del giocatore al nemico corrente. Restituisce il danno inflitto. */
    public int playerAttack(Enemy enemy) {
        return combatSystem.executeAttack(
                asGameCharacter(gameState.getPlayer()), enemy,
                AttackType.PHYSICAL,
                enemy.getCritModifierOnPlayer()
        );
    }

    /** Esegue un attacco del nemico al giocatore. Restituisce il danno inflitto. */
    public int enemyAttack(Enemy enemy) {
        return combatSystem.executeAttack(
                enemy, asGameCharacter(gameState.getPlayer()),
                enemy.getAttackType(),
                0
        );
    }

    /**
     * Usa la prima pozione disponibile nell'inventario e la rimuove.
     * Restituisce true se una pozione e' stata usata, false se l'inventario non ne ha.
     */
    public boolean useFirstPotion() {
        List<Item> inventory = gameState.getPlayer().getInventory();
        Optional<Item> potionOpt = inventory.stream()
                .filter(item -> item instanceof Potion)
                .findFirst();
        if (potionOpt.isEmpty()) return false;
        Potion potion = (Potion) potionOpt.get();
        potion.use(asGameCharacter(gameState.getPlayer()));
        inventory.remove(potion);
        return true;
    }

    /** Raccoglie tutti gli oggetti della stanza corrente nell'inventario. */
    public void collectItemsInRoom() {
        Room room = getCurrentRoom();
        for (Item item : room.getItems()) {
            asGameCharacter(gameState.getPlayer()).addItem(item);
        }
        room.getItems().clear();
    }

    /** Avanza alla prossima stanza se la corrente e' stata liberata. */
    public boolean advanceRoom() {
        Room current = getCurrentRoom();
        if (!current.isCleared()) return false;
        if (!gameState.getDungeonMap().hasNextRoom()) return false;
        gameState.getDungeonMap().advanceToNextRoom();
        Room next = getCurrentRoom();
        next.setVisited(true);
        next.getEnemies().forEach(e -> e.applyPassiveBonus());
        asGameCharacter(gameState.getPlayer()).applyPassiveBonus();
        return true;
    }

    /** Segna la stanza come liberata se non ci sono piu' nemici vivi. */
    public void checkRoomCleared() {
        Room room = getCurrentRoom();
        boolean allDead = room.getEnemies().stream().noneMatch(Enemy::isAlive);
        if (allDead) {
            room.setCleared(true);
            collectItemsInRoom();
            if (!gameState.getDungeonMap().hasNextRoom()) {
                gameState.setVictory(true);
                gameState.setGameOver(true);
            }
        }
    }

    /** Verifica se il giocatore e' morto e imposta il game over. */
    public boolean checkPlayerDead() {
        if (!asGameCharacter(gameState.getPlayer()).isAlive()) {
            gameState.setGameOver(true);
            gameState.setVictory(false);
            return true;
        }
        return false;
    }

    /** Restituisce il numero di pozioni nell'inventario. */
    public long countPotions() {
        return gameState.getPlayer().getInventory().stream()
                .filter(item -> item instanceof Potion)
                .count();
    }

    public void saveGame() { persistenceManager.save(gameState); }

    public GameState loadGame() {
        this.gameState = persistenceManager.load();
        return this.gameState;
    }

    public boolean hasSavedGame() { return persistenceManager.hasSave(); }

    public GameState getGameState() { return gameState; }
    public Room getCurrentRoom() { return gameState.getDungeonMap().getCurrentRoom(); }
    public PlayerCharacter getPlayer() { return gameState.getPlayer(); }

    /**
     * Cast sicuro da PlayerCharacter a GameCharacter.
     * Warrior, Mage e Thief estendono sempre GameCharacter,
     * quindi questo cast non puo' mai lanciare ClassCastException.
     */
    private GameCharacter asGameCharacter(PlayerCharacter player) {
        return (GameCharacter) player;
    }
}
