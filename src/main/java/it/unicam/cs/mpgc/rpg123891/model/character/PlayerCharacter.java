package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serializable;

/**
 * Interfaccia marcatore per i personaggi controllati dal giocatore.
 * Estende Serializable per garantire la serializzazione dell'intera gerarchia.
 */
public interface PlayerCharacter extends Serializable {
    String getName();
    int getCurrentHp();
    int getMaxHp();
    int getAttack();
    int getDefense();
    int getCurrentStamina();
    int getStamina();
    boolean isAlive();
    CharacterClass getCharacterClass();
    void applyPassiveBonus();
    java.util.List<it.unicam.cs.mpgc.rpg123891.model.item.Item> getInventory();
    void addItem(it.unicam.cs.mpgc.rpg123891.model.item.Item item);
    void takeDamage(int damage);
    void heal(int amount);
}
