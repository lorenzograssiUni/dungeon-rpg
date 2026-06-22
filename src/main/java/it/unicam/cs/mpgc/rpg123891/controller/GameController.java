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
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import it.unicam.cs.mpgc.rpg123891.persistence.PersistenceManager;

import java.util.List;
import java.util.Optional;

/**
 * Controller principale del gioco.
 * Fa da ponte tra la UI e il model, coordinando le azioni del giocatore.
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

    // -------------------------------------------------------------------------
    // Combattimento
    // -------------------------------------------------------------------------

    /** Esegue un attacco normale del giocatore al nemico. Restituisce il danno inflitto. */
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

    // -------------------------------------------------------------------------
    // Fuga
    // -------------------------------------------------------------------------

    /**
     * Verifica se il giocatore può fuggire dall'ondata corrente.
     *
     * Regole (GAME_SPEC.md):
     *   1. Se wave.canFlee() == false → fuga impossibile (miniboss / drop assicurato)
     *   2. Nella Sala del Tesoro (r4) la fuga è sempre permessa per Uova e Cuccioli
     *      (tutti i nemici di r4 hanno canFlee=true per costruzione in DungeonMap)
     *   3. Negli altri casi: fuga possibile solo se
     *      agilità giocatore < agilità media dei nemici vivi nell'ondata
     *
     * @return true se la fuga è consentita, false altrimenti
     */
    public boolean canFlee() {
        Wave wave = getCurrentRoom().getCurrentWave();
        if (wave == null) return false;

        // Regola 1: miniboss o drop assicurato — fuga sempre impossibile
        if (!wave.canFlee()) return false;

        // Regola 2 + 3: controlla l'agilità
        List<Enemy> aliveEnemies = wave.getEnemies().stream()
                .filter(Enemy::isAlive)
                .toList();

        if (aliveEnemies.isEmpty()) return false;

        double avgEnemyAgility = aliveEnemies.stream()
                .mapToInt(Enemy::getAgility)
                .average()
                .orElse(0);

        int playerAgility = asGameCharacter(gameState.getPlayer()).getAgility();

        // Fuga possibile se l'agilità del giocatore è STRETTAMENTE MINORE
        // dell'agilità media dei nemici vivi
        return playerAgility < avgEnemyAgility;
    }

    /**
     * Esegue la fuga: il giocatore abbandona l'ondata corrente senza loot.
     * Chiamare solo dopo aver verificato canFlee() == true.
     * La stanza NON viene marcata come cleared — il giocatore dovrà
     * affrontare di nuovo l'ondata se vi ritorna (gestito dalla UI).
     *
     * @return true se la fuga è avvenuta con successo
     */
    public boolean flee() {
        if (!canFlee()) return false;
        // La fuga riporta il giocatore fuori dalla stanza corrente.
        // Il controller non fa avanzare né retrocedere: sarà la UI
        // a decidere cosa mostrare (es. "sei fuggito, torna al menu stanza").
        return true;
    }

    // -------------------------------------------------------------------------
    // Inventario e consumabili
    // -------------------------------------------------------------------------

    /**
     * Usa la prima pozione disponibile nell'inventario e la rimuove.
     * Restituisce true se una pozione è stata usata.
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

    /** Raccoglie l'entry loot della stanza corrente nell'inventario del giocatore. */
    public void collectEntryLoot() {
        Room room = getCurrentRoom();
        for (Item item : room.getEntryLoot()) {
            asGameCharacter(gameState.getPlayer()).addItem(item);
        }
        room.getEntryLoot().clear();
    }

    /** Raccoglie il loot dell'ondata appena completata nell'inventario. */
    public void collectWaveLoot(Wave wave) {
        for (Item item : wave.getLoot()) {
            asGameCharacter(gameState.getPlayer()).addItem(item);
        }
        wave.getLoot().clear();
    }

    // -------------------------------------------------------------------------
    // Avanzamento stanza
    // -------------------------------------------------------------------------

    /**
     * Avanza alla prossima stanza se la corrente è stata liberata.
     * Applica il passive bonus del giocatore alla nuova stanza.
     */
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

    /**
     * Controlla se l'ondata corrente è terminata.
     * Se sì, raccoglie il loot garantito e avanza alla prossima ondata.
     * Se era l'ultima ondata, marca la stanza come cleared e
     * controlla la vittoria finale.
     *
     * @return true se la stanza è stata completamente liberata
     */
    public boolean checkWaveCleared() {
        Room room = getCurrentRoom();
        Wave wave = room.getCurrentWave();
        if (wave == null || !wave.isCleared()) return false;

        // Raccogli loot garantito dell'ondata
        collectWaveLoot(wave);

        // Aggiorna flag Sala del Tesoro per buff passivo del Drago
        if (room.getId().equals("r4") && !room.hasMoreWaves()) {
            gameState.getDungeonMap().setTreasureRoomCleaned(true);
        }

        if (room.hasMoreWaves()) {
            room.advanceWave();
            return false;
        }

        // Ultima ondata completata: stanza liberata
        if (!gameState.getDungeonMap().hasNextRoom()) {
            gameState.setVictory(true);
            gameState.setGameOver(true);
        }
        return true;
    }

    /** Verifica se il giocatore è morto e imposta il game over. */
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

    // -------------------------------------------------------------------------
    // Persistenza
    // -------------------------------------------------------------------------

    public void saveGame()           { persistenceManager.save(gameState); }
    public GameState loadGame()      { this.gameState = persistenceManager.load(); return this.gameState; }
    public boolean hasSavedGame()    { return persistenceManager.hasSave(); }

    // -------------------------------------------------------------------------
    // Getter
    // -------------------------------------------------------------------------

    public GameState getGameState()  { return gameState; }
    public Room getCurrentRoom()     { return gameState.getDungeonMap().getCurrentRoom(); }
    public PlayerCharacter getPlayer() { return gameState.getPlayer(); }

    private GameCharacter asGameCharacter(PlayerCharacter player) {
        return (GameCharacter) player;
    }
}
