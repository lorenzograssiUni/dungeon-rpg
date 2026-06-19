package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una stanza del dungeon.
 * Serializable: lo stato (nemici HP, cleared, visited) viene salvato fedelmente.
 */
public class Room implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

    /**
     * Una stanza senza nemici e' considerata liberata automaticamente.
     */
    public boolean isCleared() {
        if (enemies.isEmpty()) return true;
        return cleared;
    }

    public void setCleared(boolean cleared) { this.cleared = cleared; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Item> getItems() { return items; }
}
