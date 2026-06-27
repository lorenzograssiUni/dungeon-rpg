package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.util.List;
import java.util.Random;

/**
 * Abilità speciale de L'Ultimo Drago: "Soffio del Drago".
 *
 * Applica una bruciatura al giocatore:
 *   - Danno per turno: 5-8 HP
 *   - Durata: 3-5 turni
 *   - Il danno ignora la difesa (applicato via applyBurnDamage)
 *
 * Questa abilità non ha cooldown: il drago può usarla ogni turno
 * (isReady() restituisce sempre true).
 */
public class DragonBreathAbility implements EnemyAbility {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Random random;

    public DragonBreathAbility() {
        this.random = new Random();
    }

    public DragonBreathAbility(Random random) {
        this.random = random;
    }

    @Override
    public String getName() { return "Soffio del Drago"; }

    /** Nessun cooldown: il Drago può usare il soffio ogni turno. */
    @Override
    public void tick() { /* nessun contatore */ }

    /** Sempre pronta. */
    @Override
    public boolean isReady() { return true; }

    @Override
    public AbilityResult use(Enemy user, GameCharacter target) {
        BurnEffect burn = new BurnEffect(random);
        String msg = String.format(
                "L'Ultimo Drago usa il Soffio del Drago! " +
                "%s è in fiamme! (%d danno/turno per %d turni)",
                target.getName(),
                burn.getDamagePerTurn(),
                burn.getTurnsRemaining()
        );
        return new AbilityResult(msg, List.of(), 0, burn);
    }
}
