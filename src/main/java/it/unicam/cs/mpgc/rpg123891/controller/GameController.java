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
import it.unicam.cs.mpgc.rpg123891.model.world.Room;
import it.unicam.cs.mpgc.rpg123891.model.world.Wave;
import it.unicam.cs.mpgc.rpg123891.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller principale del gioco.
 * Fa da ponte tra la UI e il model, coordinando le azioni del giocatore.
 */
public class GameController {

    private GameState gameState;
    private final CombatSystem combatSystem = new CombatSystem();
    private final PersistenceManager persistenceManager;

    /**
     * Flag per il buff temporaneo di Carica! (+3 DEF).
     * Impostato a true da executeSpecial() quando viene usata Carica!.
     * Il controller lo azzera chiamando rollbackCarica() dopo che
     * il nemico ha attaccato nel turno corrente.
     */
    private boolean caricaActive = false;

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
    // Combattimento — attacchi normali
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
    // Combattimento — attacchi speciali
    // -------------------------------------------------------------------------

    /**
     * Esegue un attacco speciale SINGLE-TARGET del giocatore su un nemico.
     * Consuma la stamina del costo dello speciale.
     * Rileva automaticamente se lo speciale è "Carica!" e imposta caricaActive.
     *
     * @param special l'attacco speciale da eseguire
     * @param target  il nemico bersaglio
     * @return danno inflitto al bersaglio
     */
    public int executeSpecial(SpecialAttack special, Enemy target) {
        GameCharacter player = asGameCharacter(gameState.getPlayer());
        player.consumeStaminaForSpecial(special.getStaminaCost());
        int damage = special.execute(player, target);
        if (special.getName().equals("Carica!")) {
            caricaActive = true;
        }
        return damage;
    }

    /**
     * Esegue un attacco speciale AOE (Area Of Effect) su tutti i nemici vivi
     * dell'ondata corrente.
     *
     * Usato da:
     *   - "Onda Magica"  (Bastone Magico): ATK base su ogni nemico vivo
     *   - "Spazzatutto" (Spadone):         ATK base + stun su ogni nemico vivo
     *
     * La stamina viene consumata UNA SOLA VOLTA (non per ogni bersaglio).
     *
     * @param special lo speciale AOE da eseguire
     * @return Map<Enemy, Integer> danno inflitto a ciascun nemico colpito
     */
    public Map<Enemy, Integer> executeAoeSpecial(SpecialAttack special) {
        GameCharacter player = asGameCharacter(gameState.getPlayer());
        player.consumeStaminaForSpecial(special.getStaminaCost());

        Wave wave = getCurrentRoom().getCurrentWave();
        List<Enemy> aliveEnemies = wave == null ? List.of() :
                wave.getEnemies().stream().filter(Enemy::isAlive).toList();

        Map<Enemy, Integer> results = new java.util.LinkedHashMap<>();
        for (Enemy enemy : aliveEnemies) {
            int damage = special.execute(player, enemy);
            results.put(enemy, damage);
        }
        return results;
    }

    /**
     * Rimuove il buff temporaneo di Carica! (+3 DEF).
     * Chiamare DOPO che il nemico ha attaccato nel turno in cui era attivo.
     * Se caricaActive è false, non fa nulla.
     */
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

    /**
     * Verifica se il giocatore può fuggire dall'ondata corrente.
     *
     * Regole (GAME_SPEC.md):
     *   1. wave.canFlee() == false → fuga impossibile
     *   2. Negli altri casi: fuga possibile solo se
     *      agilità giocatore < agilità media nemici vivi
     */
    public boolean canFlee() {
        Wave wave = getCurrentRoom().getCurrentWave();
        if (wave == null) return false;
        if (!wave.canFlee()) return false;

        List<Enemy> aliveEnemies = wave.getEnemies().stream()
                .filter(Enemy::isAlive).toList();
        if (aliveEnemies.isEmpty()) return false;

        double avgEnemyAgility = aliveEnemies.stream()
                .mapToInt(Enemy::getAgility).average().orElse(0);
        int playerAgility = asGameCharacter(gameState.getPlayer()).getAgility();
        return playerAgility < avgEnemyAgility;
    }

    /**
     * Esegue la fuga. La stanza rimane non cleared.
     * @return true se la fuga è avvenuta con successo
     */
    public boolean flee() {
        if (!canFlee()) return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // Inventario e consumabili
    // -------------------------------------------------------------------------

    /** Usa la prima pozione disponibile e la rimuove dall'inventario. */
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

    /** Raccoglie l'entry loot della stanza corrente nell'inventario del giocatore. */
    public void collectEntryLoot() {
        Room room = getCurrentRoom();
        for (Item item : new ArrayList<>(room.getEntryLoot())) {
            asGameCharacter(gameState.getPlayer()).addItem(item);
        }
        room.getEntryLoot().clear();
    }

    /** Raccoglie il loot garantito di un'ondata nell'inventario. */
    public void collectWaveLoot(Wave wave) {
        for (Item item : new ArrayList<>(wave.getLoot())) {
            asGameCharacter(gameState.getPlayer()).addItem(item);
        }
        wave.getLoot().clear();
    }

    // -------------------------------------------------------------------------
    // Avanzamento stanza / ondata
    // -------------------------------------------------------------------------

    /** Avanza alla prossima stanza se la corrente è stata liberata. */
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
     * Controlla se l'ondata corrente è terminata, raccoglie il loot
     * e avanza alla prossima ondata (o segna la stanza come cleared).
     *
     * @return true se la stanza è stata completamente liberata
     */
    public boolean checkWaveCleared() {
        Room room = getCurrentRoom();
        Wave wave = room.getCurrentWave();
        if (wave == null || !wave.isCleared()) return false;

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

    /** Verifica se il giocatore è morto e imposta il game over. */
    public boolean checkPlayerDead() {
        if (!asGameCharacter(gameState.getPlayer()).isAlive()) {
            gameState.setGameOver(true);
            gameState.setVictory(false);
            return true;
        }
        return false;
    }

    /** Numero di pozioni nell'inventario. */
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
