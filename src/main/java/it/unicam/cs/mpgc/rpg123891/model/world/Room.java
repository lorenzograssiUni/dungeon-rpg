package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una singola stanza del dungeon.
 * Ogni stanza può contenere nemici, oggetti e avere una descrizione narrativa.
 * La stanza sa se è stata già visitata e se i nemici sono stati sconfitti.
 */
public class Room {

    private final String id;
    private final String name;
    private final String description;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private boolean visited = false;
    private boolean cleared = false;

    public Room(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void addEnemy(Enemy enemy) { enemies.add(enemy); }
    public void addItem(Item item) { items.add(item); }

    public List<Enemy> getEnemies() { return List.copyOf(enemies); }
    public List<Item> getItems() { return List.copyOf(items); }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }

    public boolean isCleared() { return cleared; }
    public void setCleared(boolean cleared) { this.cleared = cleared; }

    /** Rimuove un oggetto dalla stanza quando viene raccolta dal giocatore. */
    public void removeItem(Item item) { items.remove(item); }

    /** Rimuove un nemico dalla stanza quando viene sconfitto. */
    public void removeEnemy(Enemy enemy) { enemies.remove(enemy); }
}
