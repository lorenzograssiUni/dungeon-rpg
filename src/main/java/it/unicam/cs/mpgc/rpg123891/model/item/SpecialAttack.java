package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serializable;

/**
 * Rappresenta un attacco speciale associato a un'arma.
 *
 * Ogni SpecialAttack ha:
 *   - name        : nome visualizzato al giocatore
 *   - description : testo descrittivo
 *   - staminaCost : stamina consumata all'uso
 *   - effect      : logica dell'effetto (attaccante, difensore)
 *
 * L'effect e' una lambda SpecialEffect (interfaccia funzionale Serializable)
 * per permettere la serializzazione dell'oggetto.
 */
public class SpecialAttack implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final int staminaCost;
    private final SpecialEffect effect;

    public SpecialAttack(String name, String description,
                         int staminaCost, SpecialEffect effect) {
        this.name        = name;
        this.description = description;
        this.staminaCost = staminaCost;
        this.effect      = effect;
    }

    /**
     * Esegue l'effetto speciale.
     * @param attacker il personaggio che usa l'attacco
     * @param defender il bersaglio
     * @return danno netto inflitto (0 se l'effetto non e' offensivo)
     */
    public int execute(GameCharacter attacker, GameCharacter defender) {
        return effect.apply(attacker, defender);
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public int getStaminaCost()    { return staminaCost; }

    // -------------------------------------------------------------------------
    // Interfaccia funzionale per l'effetto
    // -------------------------------------------------------------------------

    @FunctionalInterface
    public interface SpecialEffect extends Serializable {
        /**
         * Applica l'effetto speciale.
         * @return danno netto inflitto al difensore
         */
        int apply(GameCharacter attacker, GameCharacter defender);
    }
}
