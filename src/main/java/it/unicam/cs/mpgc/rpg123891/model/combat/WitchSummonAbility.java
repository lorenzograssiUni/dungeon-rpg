package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.util.List;

/**
 * Abilità speciale della Strega: "Evocazione Scheletri".
 *
 * Evoca 3 Scheletri nel combattimento e rende la Strega immune
 * a tutti gli attacchi finché almeno uno degli scheletri evocati
 * è ancora vivo.
 *
 * Questa abilità si attiva una sola volta per combattimento:
 * dopo la prima attivazione isReady() restituisce false.
 */
public class WitchSummonAbility implements EnemyAbility {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean hasActivated = false;

    @Override
    public String getName() { return "Evocazione Scheletri"; }

    /** Nessun contatore da incrementare: la logica è in hasActivated. */
    @Override
    public void tick() { /* nessun contatore */ }

    /** Pronta solo se non è ancora stata usata. */
    @Override
    public boolean isReady() { return !hasActivated; }

    @Override
    public AbilityResult use(Enemy user, GameCharacter target) {
        hasActivated = true;

        List<Enemy> summoned = List.of(
                EnemyFactory.createScheletro(),
                EnemyFactory.createScheletro(),
                EnemyFactory.createScheletro()
        );

        String msg = "La Strega evoca 3 Scheletri! È immune finché sono in vita!";
        return new AbilityResult(msg, summoned, 0, null);
    }

    public boolean hasActivated() { return hasActivated; }
}
