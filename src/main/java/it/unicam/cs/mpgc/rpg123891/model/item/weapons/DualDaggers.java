package it.unicam.cs.mpgc.rpg123891.model.item.weapons;

import it.unicam.cs.mpgc.rpg123891.model.character.CharacterClass;
import it.unicam.cs.mpgc.rpg123891.model.item.SpecialAttack;
import it.unicam.cs.mpgc.rpg123891.model.item.StatModifier;
import it.unicam.cs.mpgc.rpg123891.model.item.Weapon;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * Doppie Daghe (2 mani).
 *
 * Bonus stat:
 *   Warrior : ATK 0, DEF-2, AGI+2, STA 0
 *   Mage    : ATK-2, DEF 0, AGI+2, STA-1
 *   Thief   : ATK+5, DEF 0, AGI+2, STA+2
 *
 * Attacchi speciali:
 *   - Sfuriata : 3 colpi in sequenza (100% / 50% / 50% danno base) | costo 5
 *   - Ira      : 25% + critChance di attaccare tante volte quanti i nemici in stanza | costo 3
 *     (Ira colpisce lo stesso difensore N volte; il controller gestisce N)
 */
public class DualDaggers extends Weapon {

    @Serial
    private static final long serialVersionUID = 1L;

    public DualDaggers() {
        super("Doppie Daghe", "T: +5 ATK +2 AGI +2 STA | W: -2 DEF +2 AGI | M: -2 ATK +2 AGI -1 STA",
                Map.of(
                        CharacterClass.WARRIOR, new StatModifier(0, -2, 2, 0, 0, 0.0),
                        CharacterClass.MAGE,    new StatModifier(-2, 0, 2, 0, -1, 0.0),
                        CharacterClass.THIEF,   new StatModifier(5, 0, 2, 0, 2, 0.0)
                ));
    }

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return List.of(
            new SpecialAttack(
                "Sfuriata",
                "3 colpi: 100% + 50% + 50% del danno base",
                5,
                (attacker, defender) -> {
                    int atk = attacker.getAttack();
                    int totalDamage = atk + (atk / 2) + (atk / 2);
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(totalDamage);
                    return hpBefore - defender.getCurrentHp();
                }
            ),
            new SpecialAttack(
                "Ira",
                "25% + critChance probabilita' di attaccare due volte di fila",
                3,
                (attacker, defender) -> {
                    int hpBefore = defender.getCurrentHp();
                    defender.takeDamage(attacker.getAttack());
                    // Secondo colpo probabilistico
                    double chance = 0.25 + attacker.getCritChance();
                    if (Math.random() < chance) {
                        defender.takeDamage(attacker.getAttack());
                    }
                    return hpBefore - defender.getCurrentHp();
                }
            )
        );
    }
}
