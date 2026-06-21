package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Gestisce gli slot di equipaggiamento di un personaggio.
 *
 * Slot disponibili:
 *   MAIN_HAND : arma principale
 *   OFF_HAND  : scudo (bloccato se MAIN_HAND e' a 2 mani)
 *   BODY      : armatura o pendente (si escludono)
 *
 * Regole:
 *   - Equipaggiare una nuova arma in uno slot gia' occupato rimuove
 *     automaticamente quella precedente (con rollback dei bonus stat).
 *   - Un'arma a 2 mani (isTwoHanded) occupa MAIN_HAND e svuota OFF_HAND.
 *   - Non e' possibile equipaggiare Shield se MAIN_HAND ha un'arma a 2 mani.
 *   - BODY accetta solo Armor o MagicAmulet; equipaggiarne uno rimuove l'altro.
 */
public class EquipmentManager {

    private final GameCharacter character;
    private final Map<EquipSlot, Weapon> equipped = new EnumMap<>(EquipSlot.class);

    public EquipmentManager(GameCharacter character) {
        this.character = character;
    }

    // -------------------------------------------------------------------------
    // Equip
    // -------------------------------------------------------------------------

    /**
     * Tenta di equipaggiare un'arma.
     *
     * @param weapon l'item da equipaggiare
     * @return EquipResult con esito e messaggio leggibile
     */
    public EquipResult equip(Weapon weapon) {
        EquipSlot slot = weapon.getSlot();

        // Controllo arma a 2 mani in MAIN_HAND -> blocca OFF_HAND
        if (slot == EquipSlot.OFF_HAND) {
            Weapon mainHand = equipped.get(EquipSlot.MAIN_HAND);
            if (mainHand != null && mainHand.isTwoHanded()) {
                return EquipResult.failure(
                    "Non puoi equipaggiare lo Scudo: " + mainHand.getName() +
                    " richiede entrambe le mani.");
            }
        }

        // Se sto equipaggiando un'arma a 2 mani, rimuovo OFF_HAND
        if (weapon.isTwoHanded() && equipped.containsKey(EquipSlot.OFF_HAND)) {
            unequip(EquipSlot.OFF_HAND);
        }

        // Rimuovi l'eventuale item precedente nello stesso slot
        if (equipped.containsKey(slot)) {
            unequip(slot);
        }

        // Applica bonus e registra
        weapon.use(character);
        equipped.put(slot, weapon);
        return EquipResult.success("Equipaggiato: " + weapon.getName());
    }

    // -------------------------------------------------------------------------
    // Unequip
    // -------------------------------------------------------------------------

    /**
     * Rimuove l'item equipaggiato nello slot indicato e fa rollback dei bonus.
     *
     * @return l'item rimosso, o empty se lo slot era vuoto
     */
    public Optional<Weapon> unequip(EquipSlot slot) {
        Weapon old = equipped.remove(slot);
        if (old != null) {
            old.remove(character);
            return Optional.of(old);
        }
        return Optional.empty();
    }

    // -------------------------------------------------------------------------
    // Query
    // -------------------------------------------------------------------------

    /** Restituisce l'item equipaggiato nello slot, se presente. */
    public Optional<Weapon> getEquipped(EquipSlot slot) {
        return Optional.ofNullable(equipped.get(slot));
    }

    /** True se lo slot e' occupato. */
    public boolean isSlotOccupied(EquipSlot slot) {
        return equipped.containsKey(slot);
    }

    /**
     * Verifica se e' possibile equipaggiare l'arma senza farlo davvero.
     * Utile per la UI per mostrare eventuali blocchi.
     */
    public EquipResult canEquip(Weapon weapon) {
        if (weapon.getSlot() == EquipSlot.OFF_HAND) {
            Weapon mainHand = equipped.get(EquipSlot.MAIN_HAND);
            if (mainHand != null && mainHand.isTwoHanded()) {
                return EquipResult.failure(
                    "Non puoi equipaggiare lo Scudo: " + mainHand.getName() +
                    " richiede entrambe le mani.");
            }
        }
        return EquipResult.success("Puoi equipaggiare " + weapon.getName());
    }

    /** Lista degli attacchi speciali di tutte le armi equipaggiate. */
    public List<SpecialAttack> getEquippedSpecials() {
        return equipped.values().stream()
                .flatMap(w -> w.getSpecialAttacks().stream())
                .toList();
    }

    /** Rappresentazione testuale dell'equipaggiamento corrente. */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Mano DX] ").append(slotLabel(EquipSlot.MAIN_HAND)).append("\n");
        sb.append("[Mano SX] ").append(slotLabel(EquipSlot.OFF_HAND)).append("\n");
        sb.append("[Corpo  ] ").append(slotLabel(EquipSlot.BODY));
        return sb.toString();
    }

    private String slotLabel(EquipSlot slot) {
        return equipped.containsKey(slot) ? equipped.get(slot).getName() : "(vuoto)";
    }

    // -------------------------------------------------------------------------
    // Risultato operazione equip
    // -------------------------------------------------------------------------

    /**
     * Incapsula il risultato di un'operazione equip/canEquip.
     * Permette alla UI di mostrare messaggi chiari senza eccezioni.
     */
    public record EquipResult(boolean success, String message) {
        public static EquipResult success(String msg) { return new EquipResult(true, msg); }
        public static EquipResult failure(String msg) { return new EquipResult(false, msg); }
    }
}
