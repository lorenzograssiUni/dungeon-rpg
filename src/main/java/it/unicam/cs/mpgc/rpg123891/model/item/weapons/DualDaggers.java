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
 * Doppie Daghe — MAIN_HAND, 2 mani (blocca OFF_HAND).
 *
 * Bonus (GAME_SPEC):
 *   W: AGI+2
 *   M: ATK-2 AGI+2 STA-1
 *   T: ATK+5 AGI+2 STA+2
 *
 * Speciali:
 *   Sfuriata (costo 5) — 3 colpi: 100% + 50% + 50% ATK.
 *                         Il 3° colpo ha 25% probabilita' di critico sul danno.
 *
 *   Ira (costo 3)      — +25% prob. Critico; attacca tante volte quanti
 *                         sono i nemici VIVI nella stanza.
 *                         Il controller gestisce il multi-target chiamando
 *                         executeIra() per ogni nemico vivo.
 */
public class DualDaggers extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public DualDaggers() {
        super("Doppie Daghe",
              "[Mano DX+SX] T:+5ATK +2AGI +2STA | W:+2AGI | M:-2ATK +2AGI -1STA",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier( 0,  0,  2, 0,  0, 0.0),
                  CharacterClass.MAGE,    new StatModifier(-2,  0,  2, 0, -1, 0.0),
                  CharacterClass.THIEF,   new StatModifier( 5,  0,  2, 0,  2, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()   { return EquipSlot.MAIN_HAND; }
    @Override public boolean isTwoHanded() { return true; }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(

            new SpecialAttack(
                "Sfuriata",
                "3 colpi: 100%+50%+50% ATK, 3° con 25% critico (costo: 5 stamina)",
                5,
                (attacker, defender) -> {
                    int atk   = attacker.getAttack();
                    int total = 0;
                    int hpBefore;

                    // Colpo 1: 100% ATK
                    hpBefore = defender.getCurrentHp();
                    defender.takeDamage(atk);
                    total += hpBefore - defender.getCurrentHp();

                    // Colpo 2: 50% ATK
                    if (defender.isAlive()) {
                        hpBefore = defender.getCurrentHp();
                        defender.takeDamage(atk / 2);
                        total += hpBefore - defender.getCurrentHp();
                    }

                    // Colpo 3: 50% ATK + 25% critico
                    if (defender.isAlive()) {
                        int dmg3 = atk / 2;
                        boolean crit3 = Math.random() < 0.25;
                        if (crit3) dmg3 *= 2;
                        hpBefore = defender.getCurrentHp();
                        defender.takeDamage(dmg3);
                        total += hpBefore - defender.getCurrentHp();
                    }

                    return total;
                }),

            new SpecialAttack(
                "Ira",
                "+25% crit; attacca tutti i nemici vivi (uno per uno) (costo: 3 stamina)",
                3,
                (attacker, defender) -> {
                    // Questo lambda viene chiamato dal controller per OGNI nemico vivo.
                    // Il controller imposta temporaneamente crit+25% prima di ogni chiamata.
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    return hpBefore - defender.getCurrentHp();
                })
        );
    }
}
