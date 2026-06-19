package it.unicam.cs.mpgc.rpg123891.controller;

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

/**
 * Controller principale del gioco.
 * Fa da ponte tra la UI e il model, coordinando le azioni del giocatore.
 * La UI chiama i metodi di questo controller; il controller aggiorna il model.
 * Rispetta il principio di separazione delle responsabilità:
 * - il model non sa nulla della UI
 * - la UI non accede direttamente al model
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
        player.applyPassiveBonus();
    }

    /** Esegue un attacco del giocatore al nemico corrente. Restituisce il danno inflitto. */
    public int playerAttack(Enemy enemy) {
        return combatSystem.executeAttack(
                gameState.getPlayer(), enemy,
                AttackType.PHYSICAL,
                enemy.getCritModifierOnPlayer()
        );
    }

    /** Esegue un attacco del nemico al giocatore. Restituisce il danno inflitto. */
    public int enemyAttack(Enemy enemy) {
        return combatSystem.executeAttack(
                enemy, gameState.getPlayer(),
                enemy.getAttackType(),
                0
        );
    }

    /** Usa una pozione dell'inventario. */
    public void usePotion(Potion potion) {
        potion.use(gameState.getPlayer());
        gameState.getPlayer().getInventory();
    }

    /** Raccoglie tutti gli oggetti della stanza corrente nell'inventario. */
    public void collectItemsInRoom() {
        Room room = getCurrentRoom();
        for (Item item : room.getItems()) {
            gameState.getPlayer().addItem(item);
        }
    }

    /** Avanza alla prossima stanza se la corrente è stata liberata. */
    public boolean advanceRoom() {
        Room current = getCurrentRoom();
        if (!current.isCleared()) return false;
        if (!gameState.getDungeonMap().hasNextRoom()) return false;
        gameState.getDungeonMap().advanceToNextRoom();
        Room next = getCurrentRoom();
        next.setVisited(true);
        next.getEnemies().forEach(e -> e.applyPassiveBonus());
        gameState.getPlayer().applyPassiveBonus();
        return true;
    }

    /** Segna la stanza come liberata se non ci sono più nemici vivi. */
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

    public void saveGame() {
        persistenceManager.save(gameState);
    }

    public GameState loadGame() {
        this.gameState = persistenceManager.load();
        return this.gameState;
    }

    public GameState getGameState() { return gameState; }
    public Room getCurrentRoom() { return gameState.getDungeonMap().getCurrentRoom(); }
    public PlayerCharacter getPlayer() { return gameState.getPlayer(); }
}
