package it.unicam.cs.mpgc.rpg123891.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unicam.cs.mpgc.rpg123891.model.game.GameState;

import java.io.*;
import java.nio.file.*;

/**
 * Implementazione della persistenza tramite file JSON.
 * Usa la libreria Gson per serializzare e deserializzare lo stato della partita.
 *
 * Implementazione concreta di PersistenceManager.
 * Per cambiare il meccanismo di persistenza (es. database) basta creare
 * una nuova classe che implementa PersistenceManager.
 */
public class JsonPersistenceManager implements PersistenceManager {

    private static final String SAVE_FILE = "savegame.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void save(GameState gameState) {
        try (Writer writer = Files.newBufferedWriter(Path.of(SAVE_FILE))) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio: " + e.getMessage());
        }
    }

    @Override
    public GameState load() {
        if (!hasSave()) return null;
        try (Reader reader = Files.newBufferedReader(Path.of(SAVE_FILE))) {
            return gson.fromJson(reader, GameState.class);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean hasSave() {
        return Files.exists(Path.of(SAVE_FILE));
    }
}
