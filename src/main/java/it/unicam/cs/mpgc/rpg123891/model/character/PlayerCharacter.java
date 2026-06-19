package it.unicam.cs.mpgc.rpg123891.model.character;

import it.unicam.cs.mpgc.rpg123891.model.item.Item;

import java.io.Serializable;
import java.util.List;

/**
 * Interfaccia marcatore per i personaggi controllati dal giocatore.
 * Non ridefinisce metodi gia' presenti in GameCharacter: i player sono
 * sempre istanze di GameCharacter, quindi il cast e' sicuro.
 * Estende Serializable per garantire la serializzazione dell'intera gerarchia.
 */
public interface PlayerCharacter extends Serializable {
    // Interfaccia marcatore: tutti i metodi sono ereditati da GameCharacter.
    // Serve per distinguere i personaggi giocatore dai nemici a livello di tipo.
}
