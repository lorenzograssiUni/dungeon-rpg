package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serial;
import java.util.List;

/**
 * Abilità speciale del Re Goblin: "3 Lanci".
 *
 * Infligge 3 attacchi separati da 20 ATK ciascuno al giocatore.
 * Ogni lancio bypassa la difesa (danno diretto) secondo la spec.
 * Il danno totale è 3 × 20 = 60 HP (al lordo delle protezioni speciali
 * di classe come blocco Warrior o scudo Mage, gestite nel CombatSystem).
 *
 * NOTA: i 3 lanci sono attacchi fisici separati — il blocco Warrior
 * e lo scudo Mage si applicano solo al PRIMO lancio che li attiva.
 */
public class ReGoblinThrowAbility implements EnemyAbility {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int THROWS    = 3;
    private static final int DMG_EACH  = 20;

    @Override
    public String getName() { return "3 Lanci"; }

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
