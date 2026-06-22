package it.unicam.cs.mpgc.rpg123891.model.item.weapons;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg123891.model.item.EquipSlot;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.StatModifier;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * Spadone — MAIN_HAND, 2 mani (blocca OFF_HAND).
 *
 * Bonus: W: ATK+5 STA+3 | M: ATK-2 AGI-4 STA-2 | T: ATK+1 AGI-4
 *
 * Speciali:
 *   Spazzatutto (costo 4) — colpisce un singolo bersaglio e lo stordisce 1 turno.
 *     Il controller è responsabile di chiamare Spazzatutto su ogni nemico
 *     della stanza (multi-target è gestito al punto 7).
 *     Lo stordimento è applicato qui: se il defender è un Enemy, viene stordito.
 *
 *   Taglio Profondo (costo 7) — danno = metà degli HP correnti del bersaglio.
 *     Il danno bypassa la difesa (danno diretto basato sugli HP, non sull'ATK).
 */
public class Greatsword extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public Greatsword() {
        super("Spadone",
              "[Mano DX+SX] W:+5ATK +3STA | M:-2ATK -4AGI -2STA | T:+1ATK -4AGI",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier( 5, 0,  0, 0,  3, 0.0),
                  CharacterClass.MAGE,    new StatModifier(-2, 0, -4, 0, -2, 0.0),
                  CharacterClass.THIEF,   new StatModifier( 1, 0, -4, 0,  0, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()   { return EquipSlot.MAIN_HAND; }
    @Override public boolean isTwoHanded() { return true; }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(

            new SpecialAttack(
                "Spazzatutto",
                "Colpisce tutti i nemici e li stordisce per 1 turno (costo: 4 stamina)",
                4,
                (attacker, defender) -> {
                    // Danno normale al bersaglio
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    int dealt = hpBefore - defender.getCurrentHp();

                    // Applica stordimento se il bersaglio è un Enemy ancora vivo
                    if (defender instanceof Enemy enemy && enemy.isAlive()) {
                        enemy.stun();
                    }

                    return dealt;
                }),

            new SpecialAttack(
                "Taglio Profondo",
                "Infligge danno pari alla metà degli HP correnti del nemico (costo: 7 stamina)",
                7,
                (attacker, defender) -> {
                    // Danno diretto = metà HP correnti, bypassa la difesa
                    int damage = defender.getCurrentHp() / 2;
                    defender.applyBurnDamage(damage);
                    return damage;
                })
        );
    }
}
