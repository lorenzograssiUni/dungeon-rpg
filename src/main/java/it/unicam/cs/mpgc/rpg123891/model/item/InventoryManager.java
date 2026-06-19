package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gestisce le operazioni sull'inventario di un personaggio.
 * Usa la Stream API (lezione 15) per filtrare, trasformare e aggregare
 * la lista di oggetti in modo dichiarativo.
 *
 * Responsabilità separata da GameCharacter (SRP): il personaggio possiede
 * l'inventario, InventoryManager lo interroga e lo manipola.
 */
public class InventoryManager {

    private final GameCharacter character;

    public InventoryManager(GameCharacter character) {
        this.character = character;
    }

    /**
     * Restituisce tutte le pozioni nell'inventario.
     * Usa filter() per selezionare solo gli Item di tipo Potion.
     */
    public List<Potion> getPotions() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Potion)
                .map(item -> (Potion) item)
                .collect(Collectors.toList());
    }

    /**
     * Restituisce tutte le armi nell'inventario.
     * Usa filter() + map() per trasformare Item in Weapon.
     */
    public List<Weapon> getWeapons() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Weapon)
                .map(item -> (Weapon) item)
                .collect(Collectors.toList());
    }

    /**
     * Restituisce il numero di pozioni nell'inventario.
     * Usa filter() + count().
     */
    public long countPotions() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Potion)
                .count();
    }

    /**
     * Restituisce il totale dei bonus ATK sommando tutte le armi nell'inventario.
     * Usa filter() + map() + mapToInt() + sum().
     */
    public int getTotalAttackBonus() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Weapon)
                .map(item -> (Weapon) item)
                .mapToInt(Weapon::getAttackBonus)
                .sum();
    }

    /**
     * Restituisce l'arma con il bonus ATK più alto.
     * Usa filter() + map() + max() con Comparator.
     */
    public Optional<Weapon> getBestWeapon() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Weapon)
                .map(item -> (Weapon) item)
                .max((w1, w2) -> Integer.compare(w1.getAttackBonus(), w2.getAttackBonus()));
    }

    /**
     * Restituisce la prima pozione disponibile nell'inventario.
     * Usa filter() + findFirst() che restituisce un Optional.
     */
    public Optional<Potion> getFirstPotion() {
        return character.getInventory().stream()
                .filter(item -> item instanceof Potion)
                .map(item -> (Potion) item)
                .findFirst();
    }

    /**
     * Restituisce i nomi di tutti gli oggetti nell'inventario come lista di stringhe.
     * Usa map() per estrarre solo il nome da ogni Item.
     */
    public List<String> getItemNames() {
        return character.getInventory().stream()
                .map(Item::getName)
                .collect(Collectors.toList());
    }

    /**
     * Verifica se l'inventario contiene almeno un oggetto con il nome specificato.
     * Usa anyMatch() per una verifica booleana senza raccogliere risultati.
     */
    public boolean hasItem(String itemName) {
        return character.getInventory().stream()
                .anyMatch(item -> item.getName().equals(itemName));
    }
}
