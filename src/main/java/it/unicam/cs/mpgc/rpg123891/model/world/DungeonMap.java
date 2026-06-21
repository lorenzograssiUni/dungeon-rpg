package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.item.Meat;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mappa del dungeon: sequenza ordinata di 5 stanze.
 *
 * Distribuzione loot aggiornata per garantire che il boss sia affrontabile:
 *   r1 - Entrata      : 2 Goblin | Carne
 *   r2 - Scheletri    : 1 Scheletro | Pozione, Scudo
 *   r3 - Biblioteca   : 1 Strega | Spada Semplice, Bastone Magico, Doppie Daghe
 *   r4 - Salone       : 1 Scheletro + 1 Goblin + 1 Scheletro | Spadone, Armatura, Carne, Pozione
 *   r5 - Boss         : Drago Boss | Pendente Magico, Pozione (chest pre-boss)
 */
public class DungeonMap implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Room> rooms = new ArrayList<>();
    private int currentRoomIndex = 0;

    public DungeonMap() {
        buildDungeon();
    }

    private void buildDungeon() {

        // r1 - Entrata
        Room entrance = new Room("r1", "Entrata del Dungeon",
                "Un corridoio umido e buio. L'aria sa di muffa e pericolo.");
        entrance.addEnemy(EnemyFactory.createGoblin());
        entrance.addEnemy(EnemyFactory.createGoblin());
        entrance.addItem(new Meat());
        rooms.add(entrance);

        // r2 - Sala degli Scheletri
        Room skeletonRoom = new Room("r2", "Sala degli Scheletri",
                "Ossa sparse ovunque. Qualcosa si muove tra le ombre.");
        skeletonRoom.addEnemy(EnemyFactory.createSkeleton());
        skeletonRoom.addItem(new Potion());
        skeletonRoom.addItem(new Shield());
        rooms.add(skeletonRoom);

        // r3 - Biblioteca Maledetta
        Room libraryRoom = new Room("r3", "Biblioteca Maledetta",
                "Libri polverosi volteggiano nell'aria. Una strega custodisce un segreto.");
        libraryRoom.addEnemy(EnemyFactory.createWitch());
        libraryRoom.addItem(new Sword());
        libraryRoom.addItem(new MagicStaff());
        libraryRoom.addItem(new DualDaggers());
        rooms.add(libraryRoom);

        // r4 - Salone dei Guerrieri
        Room warriorHall = new Room("r4", "Salone dei Guerrieri",
                "Statue di antichi guerrieri osservano la tua avanzata.");
        warriorHall.addEnemy(EnemyFactory.createSkeleton());
        warriorHall.addEnemy(EnemyFactory.createGoblin());
        warriorHall.addEnemy(EnemyFactory.createSkeleton());
        warriorHall.addItem(new Greatsword());
        warriorHall.addItem(new Armor());
        warriorHall.addItem(new Meat());
        warriorHall.addItem(new Potion());
        rooms.add(warriorHall);

        // r5 - Sala del Drago (Boss)
        Room bossRoom = new Room("r5", "Sala del Drago",
                "Il terreno trema. Un ruggito assordante risuona nelle pareti di pietra.");
        bossRoom.addEnemy(EnemyFactory.createDragonBoss());
        bossRoom.addItem(new MagicAmulet());
        bossRoom.addItem(new Potion());
        rooms.add(bossRoom);
    }

    public Room getCurrentRoom()     { return rooms.get(currentRoomIndex); }
    public boolean hasNextRoom()     { return currentRoomIndex < rooms.size() - 1; }
    public void advanceToNextRoom()  { if (hasNextRoom()) currentRoomIndex++; }

    public List<Room> getUnclearedRooms() {
        return rooms.stream().filter(r -> !r.isCleared()).collect(Collectors.toList());
    }

    public long countAliveEnemies() {
        return rooms.stream().flatMap(r -> r.getEnemies().stream())
                .filter(Enemy::isAlive).count();
    }

    public List<String> getVisitedRoomNames() {
        return rooms.stream().filter(Room::isVisited)
                .map(Room::getName).collect(Collectors.toList());
    }

    public boolean areAllRoomsCleared() {
        return rooms.stream().allMatch(Room::isCleared);
    }

    public List<Room> getRooms()         { return List.copyOf(rooms); }
    public int getCurrentRoomIndex()     { return currentRoomIndex; }
    public int getTotalRooms()           { return rooms.size(); }
}
