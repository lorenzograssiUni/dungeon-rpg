package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serializable;
import java.util.List;

/**
 * Interfaccia che rappresenta un'abilità speciale di un nemico.
 *
 * A differenza di SpecialAttack (usata dai giocatori tramite armi),
 * le abilità nemico possono avere effetti complessi che coinvolgono
 * più bersagli, evocazione di nuovi nemici o status effect.
 *
 * Il metodo use() restituisce un AbilityResult con:
 *   - messaggi da mostrare all'utente
 *   - eventuali nuovi nemici evocati (lista vuota se nessuno)
 *   - danno totale inflitto al giocatore (0 se nessuno)
 *   - eventuale BurnEffect applicato (null se nessuno)
 */
public interface EnemyAbility extends Serializable {

    /**
     * Esegue l'abilità speciale.
     * @param user    il nemico che usa l'abilità
     * @param target  il personaggio giocatore bersaglio
     * @return        risultato dell'abilità
     */
    AbilityResult use(Enemy user, GameCharacter target);

    /** Nome dell'abilità (mostrato nella UI). */
    String getName();

    // -------------------------------------------------------------------------
    // Result record
    // -------------------------------------------------------------------------

    /**
     * Contiene il risultato dell'esecuzione di un'abilità nemica.
     *
     * @param message        testo descrittivo da mostrare nella UI
     * @param summonedEnemies lista di nemici evocati (vuota se nessuno)
     * @param totalDamage    danno totale inflitto al giocatore
     * @param burnEffect     BurnEffect applicato (null se nessuno)
     */
    record AbilityResult(
            String message,
            List<Enemy> summonedEnemies,
            int totalDamage,
            BurnEffect burnEffect
    ) {
        /** Costruttore rapido per abilità senza evocazioni né bruciatura. */
        public static AbilityResult of(String message, int damage) {
            return new AbilityResult(message, List.of(), damage, null);
        }
    }
}
