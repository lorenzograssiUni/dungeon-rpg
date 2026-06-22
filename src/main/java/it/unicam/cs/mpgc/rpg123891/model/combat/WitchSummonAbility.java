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
 * La logica dell'immunità è gestita nel CombatController tramite
 * il flag witchImmune che viene impostato dall'AbilityResult.
 * Il controller deve rimuovere l'immunità quando tutti gli scheletri
 * evocati (tracciati tramite la lista summonedEnemies) sono morti.
 *
 * Questa abilità si attiva una sola volta per combattimento
 * (hasActivated previene riattivazioni).
 */
public class WitchSummonAbility implements EnemyAbility {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean hasActivated = false;

    @Override
    public String getName() { return "Evocazione Scheletri"; }

    @Override
    public AbilityResult use(Enemy user, GameCharacter target) {
        if (hasActivated) {
            return AbilityResult.of("La Strega tenta di evocare, ma non ha più energia!", 0);
        }

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
