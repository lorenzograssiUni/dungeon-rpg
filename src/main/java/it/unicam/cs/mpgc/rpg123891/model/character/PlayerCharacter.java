package it.unicam.cs.mpgc.rpg123891.model.character;

/**
 * Classe astratta che rappresenta il personaggio controllato dal giocatore.
 * Estende GameCharacter aggiungendo la classe del personaggio.
 * Le classi concrete (Warrior, Mage, Thief) estendono questa classe.
 */
public abstract class PlayerCharacter extends GameCharacter {

    protected PlayerCharacter(String name, int maxHp, int attack, int defense,
                               int stamina, double critChance, CharacterClass characterClass) {
        super(name, maxHp, attack, defense, stamina, critChance);
    }

    /**
     * Restituisce la classe del personaggio.
     */
    public abstract CharacterClass getCharacterClass();
}
