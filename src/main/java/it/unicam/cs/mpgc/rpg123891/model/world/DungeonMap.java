package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rappresenta la mappa del dungeon come sequenza ordinata di stanze.
 * Implementa Serializable: la lista rooms viene serializzata con tutto lo stato
 * (nemici, HP, cleared, items), quindi il caricamento ripristina esattamente
 * la partita salvata senza ricostruire il dungeon da zero.
 */
public class DungeonMap implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Room> rooms = new ArrayList<>();
    private int currentRoomIndex = 0;

    /**
     * Costruisce il dungeon con le stanze predefinite.
     * Chiamato SOLO alla creazione di una nuova partita.
     * Durante la deserializzazione questo costruttore NON viene chiamato:
     * Java Object Serialization ripristina i campi direttamente.
     */
    public DungeonMap() {
        buildDungeon();
    }

    private void buildDungeon() {
        Room entrance = new Room("r1", "Entrata del Dungeon",
                "Un corridoio umido e buio. L'aria sa di muffa e pericolo.");
        entrance.addEnemy(EnemyFactory.createGoblin());
        entrance.addEnemy(EnemyFactory.createGoblin());
        rooms.add(entrance);

        Room skeletonRoom = new Room("r2", "Sala degli Scheletri",
                "Ossa sparse ovunque. Qualcosa si muove tra le ombre.");
        skeletonRoom.addEnemy(EnemyFactory.createSkeleton());
        skeletonRoom.addItem(new Potion("Pozione di Cura", "Ripristina 30 HP", 30));
        rooms.add(skeletonRoom);

        Room libraryRoom = new Room("r3", "Biblioteca Maledetta",
                "Libri polverosi volteggiano nell'aria. Una strega custodisce un segreto.");
        libraryRoom.addEnemy(EnemyFactory.createWitch());
        libraryRoom.addItem(new Weapon("Spada Arrugginita", "+3 ATK, +2% CRIT", 3, 0.02));
        libraryRoom.addItem(new Weapon("Bastone Antico", "+8 ATK, +5% CRIT (Mago)", 8, 0.05));
        rooms.add(libraryRoom);

        Room warriorHall = new Room("r4", "Salone dei Guerrieri",
                "Statue di antichi guerrieri osservano la tua avanzata.");
        warriorHall.addEnemy(EnemyFactory.createSkeleton());
        warriorHall.addEnemy(EnemyFactory.createGoblin());
        warriorHall.addEnemy(EnemyFactory.createSkeleton());
        warriorHall.addItem(new Weapon("Pugnale Affilato", "+5 ATK, +8% CRIT", 5, 0.08));
        rooms.add(warriorHall);

        Room bossRoom = new Room("r5", "Sala del Drago",
                "Il terreno trema. Un ruggito assordante risuona nelle pareti di pietra.");
        bossRoom.addEnemy(EnemyFactory.createDragonBoss());
        rooms.add(bossRoom);
    }

    public Room getCurrentRoom() { return rooms.get(currentRoomIndex); }
    public boolean hasNextRoom() { return currentRoomIndex < rooms.size() - 1; }
    public void advanceToNextRoom() { if (hasNextRoom()) currentRoomIndex++; }

    public List<Room> getUnclearedRooms() {
        return rooms.stream().filter(r -> !r.isCleared()).collect(Collectors.toList());
    }

    public long countAliveEnemies() {
        return rooms.stream().flatMap(r -> r.getEnemies().stream()).filter(Enemy::isAlive).count();
    }

    public List<String> getVisitedRoomNames() {
        return rooms.stream().filter(Room::isVisited).map(Room::getName).collect(Collectors.toList());
    }

    public boolean areAllRoomsCleared() {
        return rooms.stream().allMatch(Room::isCleared);
    }

    public List<Room> getRooms() { return List.copyOf(rooms); }
    public int getCurrentRoomIndex() { return currentRoomIndex; }
    public int getTotalRooms() { return rooms.size(); }
}
