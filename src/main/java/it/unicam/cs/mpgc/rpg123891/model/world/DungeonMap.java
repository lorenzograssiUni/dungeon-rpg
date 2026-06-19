package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta la mappa del dungeon come sequenza ordinata di stanze.
 * Costruisce le stanze predefinite del gioco e gestisce la navigazione.
 * Estendibile: per aggiungere nuove stanze basta aggiungere elementi alla lista.
 */
public class DungeonMap {

    private final List<Room> rooms = new ArrayList<>();
    private int currentRoomIndex = 0;

    public DungeonMap() {
        buildDungeon();
    }

    /**
     * Costruisce le stanze del dungeon con nemici e oggetti predefiniti.
     */
    private void buildDungeon() {

        // Stanza 1: Entrata
        Room entrance = new Room("r1", "Entrata del Dungeon",
                "Un corridoio umido e buio. L'aria sa di muffa e pericolo.");
        entrance.addEnemy(EnemyFactory.createGoblin());
        entrance.addEnemy(EnemyFactory.createGoblin());
        rooms.add(entrance);

        // Stanza 2: Sala degli Scheletri
        Room skeletonRoom = new Room("r2", "Sala degli Scheletri",
                "Ossa sparse ovunque. Qualcosa si muove tra le ombre.");
        skeletonRoom.addEnemy(EnemyFactory.createSkeleton());
        skeletonRoom.addItem(new Potion("Pozione di Cura", "Ripristina 30 HP", 30));
        rooms.add(skeletonRoom);

        // Stanza 3: Biblioteca Maledetta
        Room libraryRoom = new Room("r3", "Biblioteca Maledetta",
                "Libri polverosi volteggiano nell'aria. Una strega custodisce un segreto.");
        libraryRoom.addEnemy(EnemyFactory.createWitch());
        libraryRoom.addItem(new Weapon("Spada Arrugginita", "+3 ATK, +2% CRIT", 3, 0.02));
        libraryRoom.addItem(new Weapon("Bastone Antico", "+8 ATK, +5% CRIT (Mago)", 8, 0.05));
        rooms.add(libraryRoom);

        // Stanza 4: Salone dei Guerrieri
        Room warriorHall = new Room("r4", "Salone dei Guerrieri",
                "Statue di antichi guerrieri osservano la tua avanzata.");
        warriorHall.addEnemy(EnemyFactory.createSkeleton());
        warriorHall.addEnemy(EnemyFactory.createGoblin());
        warriorHall.addEnemy(EnemyFactory.createSkeleton());
        warriorHall.addItem(new Weapon("Pugnale Affilato", "+5 ATK, +8% CRIT", 5, 0.08));
        rooms.add(warriorHall);

        // Stanza 5: Sala del Boss
        Room bossRoom = new Room("r5", "Sala del Drago",
                "Il terreno trema. Un ruggito assordante risuona nelle pareti di pietra.");
        bossRoom.addEnemy(EnemyFactory.createDragonBoss());
        rooms.add(bossRoom);
    }

    public Room getCurrentRoom() {
        return rooms.get(currentRoomIndex);
    }

    public boolean hasNextRoom() {
        return currentRoomIndex < rooms.size() - 1;
    }

    public void advanceToNextRoom() {
        if (hasNextRoom()) currentRoomIndex++;
    }

    public List<Room> getRooms() {
        return List.copyOf(rooms);
    }

    public int getCurrentRoomIndex() {
        return currentRoomIndex;
    }

    public int getTotalRooms() {
        return rooms.size();
    }
}
