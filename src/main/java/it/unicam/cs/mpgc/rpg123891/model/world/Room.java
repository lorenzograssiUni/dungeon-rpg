package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una stanza del dungeon.
 *
 * Struttura aggiornata per supportare le ondate sequenziali:
 *   - entryLoot     : item droppati appena si entra nella stanza,
 *                     prima che arrivino i nemici (es. Bastone Magico in Foresta,
 *                     Spadone in Catacombe).
 *   - waves         : lista ordinata di Wave; il controller avanza
 *                     alla prossima solo quando quella corrente e' cleared.
 *   - waveIndex     : indice dell'ondata corrente
 *
 * La stanza e' considerata cleared quando tutte le ondate sono cleared.
 */
public class Room implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String     id;
    private final String     name;
    private final String     description;
    private final List<Item> entryLoot = new ArrayList<>();
    private final List<Wave> waves     = new ArrayList<>();
    private int              waveIndex = 0;
    private boolean          visited   = false;

    public Room(String id, String name, String description) {
        this.id          = id;
        this.name        = name;
        this.description = description;
    }

    // -------------------------------------------------------------------------
    // Entry loot (drop prima dei nemici)
    // -------------------------------------------------------------------------

    public void addEntryLoot(Item item)   { entryLoot.add(item); }
    public List<Item> getEntryLoot()      { return entryLoot; }

    // -------------------------------------------------------------------------
    // Ondate
    // -------------------------------------------------------------------------

    public void addWave(Wave wave)        { waves.add(wave); }

    /** Ondata attualmente in corso. null se la stanza non ha ondate. */
    public Wave getCurrentWave() {
        if (waves.isEmpty() || waveIndex >= waves.size()) return null;
        return waves.get(waveIndex);
    }

    /**
     * Avanza all'ondata successiva.
     * @return true se c'e' un'altra ondata, false se la stanza e' finita.
     */
    public boolean advanceWave() {
        if (waveIndex < waves.size() - 1) {
            waveIndex++;
            return true;
        }
        return false;
    }

    public boolean hasMoreWaves() {
        return waveIndex < waves.size() - 1;
    }

    public int getWaveIndex()             { return waveIndex; }
    public int getTotalWaves()            { return waves.size(); }
    public List<Wave> getWaves()          { return waves; }

    // -------------------------------------------------------------------------
    // Stato stanza
    // -------------------------------------------------------------------------

    /**
     * La stanza e' cleared quando tutte le sue ondate sono cleared.
     * Una stanza senza ondate e' immediatamente cleared.
     */
    public boolean isCleared() {
        if (waves.isEmpty()) return true;
        return waves.stream().allMatch(Wave::isCleared);
    }

    public boolean isVisited()            { return visited; }
    public void setVisited(boolean v)     { this.visited = v; }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /** Tutti i nemici di tutte le ondate (utile per conteggi). */
    public List<Enemy> getAllEnemies() {
        List<Enemy> all = new ArrayList<>();
        waves.forEach(w -> all.addAll(w.getEnemies()));
        return all;
    }

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
}
