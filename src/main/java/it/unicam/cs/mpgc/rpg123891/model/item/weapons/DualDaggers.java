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
 * Bonus: T: ATK+5 AGI+2 STA+2 | W: DEF-2 AGI+2 | M: ATK-2 AGI+2 STA-1
 *
 * Speciali:
 *   Sfuriata (costo 5) — 3 colpi separati: 100% + 50% + 50% dell'ATK base.
 *                        Ogni colpo applica takeDamage() separatamente
 *                        (la difesa viene sottratta 3 volte, come da spec).
 *
 *   Ira (costo 3)      — Primo colpo normale. Il secondo colpo (25% + crit%)
 *                        ignora la difesa (danno diretto via applyBurnDamage).
 */
public class DualDaggers extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public DualDaggers() {
        super("Doppie Daghe",
              "[Mano DX+SX] T:+5ATK +2AGI +2STA | W:-2DEF +2AGI | M:-2ATK +2AGI -1STA",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier( 0, -2,  2, 0,  0, 0.0),
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
                "3 colpi separati: 100% + 50% + 50% ATK (difesa applicata ad ogni colpo)",
                5,
                (attacker, defender) -> {
                    int atk      = attacker.getAttack();
                    int total    = 0;
                    int hpBefore;

                    // Colpo 1: 100% ATK
                    hpBefore = defender.getCurrentHp();
                    defender.takeDamage(atk);
                    total += hpBefore - defender.getCurrentHp();

                    // Colpo 2: 50% ATK (solo se ancora vivo)
                    if (defender.isAlive()) {
                        hpBefore = defender.getCurrentHp();
                        defender.takeDamage(atk / 2);
                        total += hpBefore - defender.getCurrentHp();
                    }

                    // Colpo 3: 50% ATK (solo se ancora vivo)
                    if (defender.isAlive()) {
                        hpBefore = defender.getCurrentHp();
                        defender.takeDamage(atk / 2);
                        total += hpBefore - defender.getCurrentHp();
                    }

                    return total;
                }),

            new SpecialAttack(
                "Ira",
                "Attacco base + 25%+crit% chance secondo colpo che ignora la difesa",
                3,
                (attacker, defender) -> {
                    int total    = 0;
                    int hpBefore;

                    // Primo colpo: normale (ridotto dalla difesa)
                    hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    total += hpBefore - defender.getCurrentHp();

                    // Secondo colpo: probabilistico, ignora la difesa
                    if (defender.isAlive() &&
                        Math.random() < 0.25 + attacker.getCritChance()) {
                        int dmg = attacker.getAttack();
                        defender.applyBurnDamage(dmg); // ignora difesa
                        total += dmg;
                    }

                    return total;
                })
        );
    }
}
