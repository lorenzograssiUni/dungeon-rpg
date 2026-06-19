package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.item.Item;

import java.io.Serializable;
import java.util.List;

/**
 * Interfaccia per i personaggi controllati dal giocatore.
 * Dichiara tutti i metodi usati da GameController e GameUI.
 * Warrior, Mage e Thief la implementano estendendo GameCharacter,
 * quindi il cast a GameCharacter e' sempre sicuro.
 * Estende Serializable per la persistenza dell'intera gerarchia.
 */
public interface PlayerCharacter extends Serializable {

    String getName();
    int getCurrentHp();
    int getMaxHp();
    int getAttack();
    int getDefense();
    int getCurrentStamina();
    int getStamina();
    double getCritChance();
    boolean isAlive();
    CharacterClass getCharacterClass();
    void applyPassiveBonus();
    List<Item> getInventory();
    void addItem(Item item);
    void takeDamage(int damage);
    void heal(int amount);
}
