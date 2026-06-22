package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.Item;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una singola ondata di nemici all'interno di una stanza.
 *
 * Ogni Wave ha:
 *   - label       : etichetta (es. "Ondata A", "Miniboss")
 *   - description : testo narrativo mostrato nel log all'inizio dell'ondata
 *   - enemies     : nemici che appaiono in questa ondata
 *   - loot        : item droppati a fine ondata (drop assicurati)
 *   - canFlee     : se false il giocatore non puo' fuggire
 *
 * I drop probabilistici (50% Carne) sono gestiti dal CombatController
 * al momento dell'uccisione di ciascun nemico.
 */
public class Wave implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String      label;
    private final String      description;
    private final List<Enemy> enemies  = new ArrayList<>();
    private final List<Item>  loot     = new ArrayList<>();
    private final boolean     canFlee;
    private boolean           cleared  = false;

    public Wave(String label, boolean canFlee, String description) {
        this.label       = label;
        this.canFlee     = canFlee;
        this.description = description != null ? description : "";
    }

    public Wave(String label, boolean canFlee) {
        this(label, canFlee, "");
    }

    public Wave(String label, String description) {
        this(label, true, description);
    }

    public Wave(String label) {
        this(label, true, "");
    }

    public void addEnemy(Enemy e) { enemies.add(e); }
    public void addLoot(Item i)   { loot.add(i); }

    public boolean isCleared() {
        if (cleared) return true;
        boolean allDead = enemies.stream().noneMatch(Enemy::isAlive);
        if (allDead) cleared = true;
        return cleared;
    }

    public void setCleared(boolean v)  { this.cleared = v; }
    public String getLabel()           { return label; }
    /** Alias getName() usato dalla UI come wave.getName() */
    public String getName()            { return label; }
    public String getDescription()     { return description; }
    public List<Enemy> getEnemies()    { return enemies; }
    public List<Item>  getLoot()       { return loot; }
    public boolean canFlee()           { return canFlee; }
}
