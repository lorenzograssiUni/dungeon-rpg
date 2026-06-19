package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

/**
 * Rappresenta una pozione di cura utilizzabile dal personaggio.
 * Una pozione ripristina una quantità fissa di HP.
 */
public class Potion implements Item {

    private final String name;
    private final String description;
    private final int healAmount;

    public Potion(String name, String description, int healAmount) {
        this.name = name;
        this.description = description;
        this.healAmount = healAmount;
    }

    /**
     * Usa la pozione sul personaggio specificato.
     * @param character il personaggio che riceve la cura
     */
    public void use(GameCharacter character) {
        character.heal(healAmount);
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    public int getHealAmount() { return healAmount; }
}
