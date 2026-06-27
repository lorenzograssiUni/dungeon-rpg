package it.unicam.cs.mpgc.rpg123891.model.item.weapons;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipSlot;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.StatModifier;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * Bastone Magico — MAIN_HAND, 1 mano.
 *
 * Bonus (GAME_SPEC):
 *   W: ATK-3 STA-3
 *   M: ATK+5 AGI+2 STA+3
 *   T: STA-3
 *
 * Speciali:
 *   Onda Magica  (costo 4) — colpisce tutti i nemici nella stanza;
 *                             danno = ATK + (3 * numero nemici vivi).
 *                             Il controller itera su tutti i nemici e chiama
 *                             questo speciale passando il numero nemici.
 *                             Se non si e' il Mago e' richiesto il Pendente Magico.
 *
 *   Colpo Vitale (costo 6) — danno = HP correnti del PERSONAGGIO.
 *                             Malus: il personaggio perde meta' vita.
 *                             Bypassa la difesa.
 *                             Richiede Bastone in MAIN_HAND + Pendente in BODY.
 */
public class MagicStaff extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public MagicStaff() {
        super("Bastone Magico",
              "[Mano DX] M:+5ATK +2AGI +3STA | W:-3ATK -3STA | T:-3STA",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier(-3, 0,  0, 0, -3, 0.0),
                  CharacterClass.MAGE,    new StatModifier( 5, 0,  2, 0,  3, 0.0),
                  CharacterClass.THIEF,   new StatModifier( 0, 0,  0, 0, -3, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()   { return EquipSlot.MAIN_HAND; }
    @Override public boolean isTwoHanded() { return false; }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(

            new SpecialAttack(
                "Onda Magica",
                "Colpisce tutti i nemici: danno ATK + (3 x num. nemici vivi) (costo: 4 stamina)",
                4,
                (attacker, defender) -> {
                    // Il controller passa un defender virtuale che porta
                    // il numero di nemici vivi nel campo. Il danno reale
                    // viene calcolato e applicato direttamente dal controller
                    // tramite executeAoeSpecialStaff().
                    // Questo lambda viene chiamato per ogni singolo nemico.
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    return hpBefore - defender.getCurrentHp();
                }),

            new SpecialAttack(
                "Colpo Vitale",
                "Danno = HP correnti personaggio, perdi meta' vita, ignora difesa (costo: 6 stamina)",
                6,
                (attacker, defender) -> {
                    int damage = attacker.getCurrentHp();
                    // Il personaggio perde meta' vita
                    attacker.applyBurnDamage(damage / 2);
                    // Danno al nemico ignora difesa
                    defender.applyBurnDamage(damage);
                    return damage;
                })
        );
    }
}
