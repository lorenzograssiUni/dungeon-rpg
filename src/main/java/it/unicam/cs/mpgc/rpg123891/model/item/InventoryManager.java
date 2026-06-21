package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gestisce le operazioni sull'inventario di un personaggio (SRP).
 * Usa la Stream API per filtrare, trasformare e aggregare gli oggetti.
 *
 * Nuove operazioni rispetto alla versione precedente:
 *   - usePotion()         : usa e rimuove la prima pozione disponibile
 *   - useMeat()           : usa e rimuove la prima carne disponibile
 *   - equipWeapon(Weapon) : applica i bonus dell'arma al personaggio
 *   - getWeapons()        : lista di tutte le armi (ora Weapon astratta)
 *   - getAllSpecials()     : raccoglie tutti gli attacchi speciali disponibili
 */
public class InventoryManager {

    private final GameCharacter character;

    public InventoryManager(GameCharacter character) {
        this.character = character;
    }

    // -------------------------------------------------------------------------
    // Query
    // -------------------------------------------------------------------------

    public List<Potion> getPotions() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Potion)
                .map(item -> (Potion) item)
                .collect(Collectors.toList());
    }

    public List<Weapon> getWeapons() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Weapon)
                .map(item -> (Weapon) item)
                .collect(Collectors.toList());
    }

    public List<Meat> getMeats() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Meat)
                .map(item -> (Meat) item)
                .collect(Collectors.toList());
    }

    /** Raccoglie tutti gli attacchi speciali di tutte le armi nell'inventario. */
    public List<SpecialAttack> getAllSpecials() {
        return getWeapons().stream()
                .flatMap(w -> w.getSpecialAttacks().stream())
                .collect(Collectors.toList());
    }

    public long countPotions() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Potion).count();
    }

    public long countMeats() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Meat).count();
    }

    public Optional<Potion> getFirstPotion() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Potion)
                .map(item -> (Potion) item)
                .findFirst();
    }

    public List<String> getItemNames() {
        return character.getInventory().stream()
                .map(Item::getName)
                .collect(Collectors.toList());
    }

    public boolean hasItem(String itemName) {
        return character.getInventory().stream()
                .anyMatch(item -> item.getName().equals(itemName));
    }

    // -------------------------------------------------------------------------
    // Azioni
    // -------------------------------------------------------------------------

    /**
     * Usa la prima pozione disponibile nell'inventario e la rimuove.
     * @return true se una pozione e' stata usata, false se l'inventario e' vuoto
     */
    public boolean usePotion() {
        Optional<Potion> potion = getFirstPotion();
        potion.ifPresent(p -> {
            p.use(character);
            character.removeItem(p);
        });
        return potion.isPresent();
    }

    /**
     * Usa la prima carne disponibile nell'inventario e la rimuove.
     * Ripristina 2 stamina al personaggio.
     * @return true se la carne e' stata usata
     */
    public boolean useMeat() {
        Optional<Meat> meat = character.getInventory().stream()
                .filter(item -> item instanceof Meat)
                .map(item -> (Meat) item)
                .findFirst();
        meat.ifPresent(m -> {
            m.use(character);
            character.removeItem(m);
        });
        return meat.isPresent();
    }

    /**
     * Equipaggia un'arma: applica i bonus stat al personaggio in base alla sua classe.
     * L'arma rimane nell'inventario dopo l'equip.
     */
    public void equipWeapon(Weapon weapon) {
        weapon.use(character);
    }
}
