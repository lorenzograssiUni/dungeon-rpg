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
 *   - label    : etichetta (es. "Ondata A", "Miniboss")
 *   - enemies  : nemici che appaiono in questa ondata
 *   - loot     : item droppati a fine ondata (drop assicurati)
 *   - canFlee  : se false il giocatore non puo' fuggire (miniboss / drop assicurato)
 *
 * I drop probabilistici (50% Carne) sono gestiti dal controller
 * tramite la logica di loot, non qui.
 */
public class Wave implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String label;
    private final List<Enemy> enemies  = new ArrayList<>();
    private final List<Item>  loot     = new ArrayList<>();
    private final boolean     canFlee;
    private boolean           cleared  = false;

    public Wave(String label, boolean canFlee) {
        this.label   = label;
        this.canFlee = canFlee;
    }

    /** Costruttore rapido per ondate normali (fuga permessa). */
    public Wave(String label) {
        this(label, true);
    }

    public void addEnemy(Enemy e) { enemies.add(e); }
    public void addLoot(Item i)   { loot.add(i); }

    /** True se tutti i nemici dell'ondata sono stati sconfitti. */
    public boolean isCleared() {
        if (cleared) return true;
        boolean allDead = enemies.stream().noneMatch(Enemy::isAlive);
        if (allDead) cleared = true;
        return cleared;
    }

    public void setCleared(boolean v)  { this.cleared = v; }
    public String getLabel()           { return label; }
    public List<Enemy> getEnemies()    { return enemies; }
    public List<Item>  getLoot()       { return loot; }
    public boolean canFlee()           { return canFlee; }
}
