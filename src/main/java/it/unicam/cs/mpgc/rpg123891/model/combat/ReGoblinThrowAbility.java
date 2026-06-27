package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.util.List;

/**
 * Abilità speciale del Re Goblin: "3 Lanci".
 *
 * Infligge 3 attacchi separati da 20 ATK ciascuno al giocatore.
 * Ogni lancio bypassa la difesa (danno diretto).
 *
 * Nessun cooldown: il Re Goblin può usarla ogni turno
 * (isReady() restituisce sempre true).
 */
public class ReGoblinThrowAbility implements EnemyAbility {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int THROWS    = 3;
    private static final int DMG_EACH  = 20;

    @Override
    public String getName() { return "3 Lanci"; }

    /** Nessun cooldown. */
    @Override
    public void tick() { /* nessun contatore */ }

    /** Sempre pronta. */
    @Override
    public boolean isReady() { return true; }

    @Override
    public AbilityResult use(Enemy user, GameCharacter target) {
        int totalDamage = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("Il Re Goblin lancia 3 proiettili!\n");

        for (int i = 1; i <= THROWS; i++) {
            int before = target.getCurrentHp();
            target.applyBurnDamage(DMG_EACH); // danno diretto, ignora difesa
            int dealt = before - target.getCurrentHp();
            totalDamage += dealt;
            sb.append(String.format("  Lancio %d: %d danno", i, dealt));
            if (!target.isAlive()) {
                sb.append(" — il giocatore è caduto!");
                break;
            }
            sb.append("\n");
        }

        return new AbilityResult(sb.toString().trim(), List.of(), totalDamage, null);
    }
}
