package it.unicam.cs.mpgc.rpg123891.model.game;

import it.unicam.cs.mpgc.rpg123891.model.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;

import java.io.Serializable;

/**
 * Rappresenta lo stato completo di una partita in corso.
 * Contiene il personaggio del giocatore, la mappa del dungeon e lo stato della sessione.
 * Implementa Serializable per consentire la persistenza tramite Java Object Serialization,
 * che ricostruisce fedelmente l'oggetto senza chiamare costruttori.
 */
public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private PlayerCharacter player;
    private DungeonMap dungeonMap;
    private boolean gameOver = false;
    private boolean victory = false;

    public GameState(PlayerCharacter player) {
        this.player = player;
        this.dungeonMap = new DungeonMap();
    }

    public PlayerCharacter getPlayer() { return player; }
    public DungeonMap getDungeonMap() { return dungeonMap; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public boolean isVictory() { return victory; }
    public void setVictory(boolean victory) { this.victory = victory; }
}
