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
 * Spada Semplice — MAIN_HAND, 1 mano.
 *
 * Bonus: W: ATK+2 AGI+2 | M: ATK+2 STA-3 | T: ATK+2
 *
 * Speciali:
 *   Fendente (costo 2) — +25% ATK e +5% crit per questo attacco
 *   Carica!  (costo 4) — attacco base + +3 DEF per il turno corrente;
 *                        la DEF temporanea dura fino al prossimo attacco
 *                        nemico. Il controller chiama removeCaricaBuff()
 *                        dopo che il nemico ha attaccato.
 */
public class Sword extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public Sword() {
        super("Spada Semplice",
              "[Mano DX] +2 ATK | W:+2 AGI | M:-3 STA",
              Map.of(
                  CharacterClass.WARRIOR, new StatModifier(2, 0,  2, 0,  0, 0.0),
                  CharacterClass.MAGE,    new StatModifier(2, 0,  0, 0, -3, 0.0),
                  CharacterClass.THIEF,   new StatModifier(2, 0,  0, 0,  0, 0.0)
              ));
    }

    @Override public EquipSlot getSlot()   { return EquipSlot.MAIN_HAND; }
    @Override public boolean isTwoHanded() { return false; }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(

            new SpecialAttack(
                "Fendente",
                "+25% ATK e +5% crit per questo attacco (costo: 2 stamina)",
                2,
                (attacker, defender) -> {
                    int boostedAtk = (int)(attacker.getAttack() * 1.25);
                    boolean crit   = Math.random() < attacker.getCritChance() + 0.05;
                    int damage     = crit ? boostedAtk * 2 : boostedAtk;
                    int hpBefore   = defender.getCurrentHp();
                    defender.takeDamage(damage);
                    return hpBefore - defender.getCurrentHp();
                }),

            new SpecialAttack(
                "Carica!",
                "Attacchi e guadagni +3 DEF per il turno (rimossa dopo l'attacco nemico)",
                4,
                (attacker, defender) -> {
                    // Applica danno normale
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    int dealt = hpBefore - defender.getCurrentHp();

                    // Applica buff difensivo temporaneo: dura fino al turno nemico.
                    // Il controller deve chiamare attacker.increaseDefense(-3)
                    // dopo che il nemico ha attaccato in questo round.
                    attacker.increaseDefense(3);

                    return dealt;
                })
        );
    }
}
