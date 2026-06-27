package it.unicam.cs.mpgc.rpg123891.controller;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg123891.model.combat.AttackType;
import it.unicam.cs.mpgc.rpg123891.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.game.GameState;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipSlot;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Armor;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.MagicAmulet;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.MagicStaff;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Shield;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.Sword;
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

    /**
     * caricaTurnsRemaining: numero di turni in cui il giocatore subisce 0 danno
     * per effetto di Carica! (turno corrente + successivo = 2 turni).
     */
    private int caricaTurnsRemaining = 0;

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
        if (caricaTurnsRemaining > 0) return 0;
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
        if (special.getName().equals("Carica!")) caricaTurnsRemaining = 2;
        return damage;
    }

    public Map<Enemy, Integer> executeAoeSpecial(SpecialAttack special) {
        GameCharacter player = asGameCharacter(gameState.getPlayer());
        player.consumeStaminaForSpecial(special.getStaminaCost());

        Wave wave = getCurrentRoom().getCurrentWave();
        List<Enemy> aliveEnemies = wave == null ? List.of() :
                wave.getEnemies().stream().filter(Enemy::isAlive).toList();

        Map<Enemy, Integer> results = new java.util.LinkedHashMap<>();

        boolean isOndaMagica = special.getName().equals("Onda Magica");
        int bonusOndaMagica = isOndaMagica ? 3 * aliveEnemies.size() : 0;

        for (Enemy enemy : aliveEnemies) {
            if (isOndaMagica) {
                int damage = player.getAttack() + bonusOndaMagica;
                int hpBefore = enemy.getCurrentHp();
                enemy.applyBurnDamage(damage);
                results.put(enemy, hpBefore - enemy.getCurrentHp());
            } else {
                results.put(enemy, special.execute(player, enemy));
            }
        }
        return results;
    }

    public Map<Enemy, Integer> executeIra(SpecialAttack special) {
        GameCharacter player = asGameCharacter(gameState.getPlayer());
        player.consumeStaminaForSpecial(special.getStaminaCost());

        Wave wave = getCurrentRoom().getCurrentWave();
        List<Enemy> aliveEnemies = wave == null ? List.of() :
                wave.getEnemies().stream().filter(Enemy::isAlive).toList();

        player.increaseCritChance(0.25);
        Map<Enemy, Integer> results = new java.util.LinkedHashMap<>();
        for (Enemy enemy : aliveEnemies) {
            results.put(enemy, special.execute(player, enemy));
        }
        player.increaseCritChance(-0.25);
        return results;
    }

    // -------------------------------------------------------------------------
    // Carica! tick / rollback
    // -------------------------------------------------------------------------

    /** Decrementa il contatore di Carica! di un turno. */
    public void tickCarica() {
        if (caricaTurnsRemaining > 0) caricaTurnsRemaining--;
    }

    /**
     * Alias di tickCarica() usato dai test.
     * Decrementa il contatore di Carica! di un turno.
     */
    public void rollbackCarica() {
        tickCarica();
    }

    public boolean isCaricaActive() { return caricaTurnsRemaining > 0; }

    public boolean canUseCarica() {
        GameCharacter player = asGameCharacter(gameState.getPlayer());
        boolean hasSword  = player.getEquipmentManager()
                .getEquipped(EquipSlot.MAIN_HAND)
                .map(w -> w instanceof Sword)
                .orElse(false);
        boolean hasShield = player.getEquipmentManager()
                .getEquipped(EquipSlot.OFF_HAND)
                .map(w -> w instanceof Shield)
                .orElse(false);
        return hasSword && hasShield;
    }

    // -------------------------------------------------------------------------
    // Fuga (GAME_SPEC)
    // -------------------------------------------------------------------------

    public boolean canFlee() {
        Wave wave = getCurrentRoom().getCurrentWave();
        if (wave == null) return false;
        if (!wave.canFlee()) return false;

        List<Enemy> alive = wave.getEnemies().stream()
                .filter(Enemy::isAlive).toList();
        if (alive.isEmpty()) return false;

        boolean isFinalRoom = getCurrentRoom().getId().equals("r5");
        if (isFinalRoom) {
            boolean onlyEggsAndCubs = alive.stream().allMatch(
                e -> e.isEgg() || e.getName().equals("Cucciolo di Drago"));
            if (onlyEggsAndCubs) return true;
        }

        double avgEnemyAgi = alive.stream()
                .mapToInt(Enemy::getAgility).average().orElse(0);
        int playerAgi = asGameCharacter(gameState.getPlayer()).getAgility();
        return playerAgi < avgEnemyAgi;
    }

    public boolean flee() {
        if (!canFlee()) return false;
        getCurrentRoom().resetToFirstWave();
        return true;
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
        collectEntryLoot();
        return true;
    }

    public boolean checkWaveCleared() {
        Room room = getCurrentRoom();
        Wave wave = room.getCurrentWave();
        if (wave == null || !wave.isCleared()) return false;

        addConditionalLoot(room, wave);
        collectWaveLoot(wave);

        asGameCharacter(gameState.getPlayer()).restoreStamina(2);

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
                if (wave.getName().equals("Ondata B")) {
                    if (!hasShield)     wave.addLoot(new Shield());
                    else if (!hasArmor) wave.addLoot(new Armor());
                }
            }
            case "r3" -> {
                if (wave.getName().equals("Ondata C")) {
                    if (!hasShield)     wave.addLoot(new Shield());
                    else if (!hasArmor) wave.addLoot(new Armor());
                }
            }
        }
    }

    public boolean canUseColpoVitale() {
        GameCharacter player = asGameCharacter(gameState.getPlayer());
        boolean staffEquipped = player.getEquipmentManager()
                .getEquipped(EquipSlot.MAIN_HAND)
                .map(w -> w instanceof MagicStaff)
                .orElse(false);
        boolean amuletEquipped = player.getEquipmentManager()
                .getEquipped(EquipSlot.BODY)
                .map(w -> w instanceof MagicAmulet)
                .orElse(false);
        return staffEquipped && amuletEquipped;
    }

    public boolean canUseStaffSpecials() {
        it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass cls =
            asGameCharacter(gameState.getPlayer()).getCharacterClass();
        if (cls == it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass.MAGE) return true;
        GameCharacter player = asGameCharacter(gameState.getPlayer());
        return player.getEquipmentManager()
                .getEquipped(EquipSlot.BODY)
                .map(w -> w instanceof MagicAmulet)
                .orElse(false);
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
