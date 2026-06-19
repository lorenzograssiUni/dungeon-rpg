package it.unicam.cs.mpgc.rpg123891.model.item;

import it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter;

import java.io.Serializable;

/**
 * Interfaccia per tutti gli oggetti del gioco.
 * Estende Serializable per garantire la serializzazione dell'inventario.
 */
public interface Item extends Serializable {
    String getName();
    String getDescription();
    void use(GameCharacter character);
}
