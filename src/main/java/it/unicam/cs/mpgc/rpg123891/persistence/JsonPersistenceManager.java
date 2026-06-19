package it.unicam.cs.mpgc.rpg123891.persistence;

import it.unicam.cs.mpgc.rpg123891.model.game.GameState;

import java.io.*;
import java.nio.file.*;

/**
 * Implementazione della persistenza tramite Java Object Serialization (file binario .sav).
 * Sostituisce la precedente implementazione Gson che non ricostruiva correttamente
 * le liste di oggetti polimorfici (Enemy, Item) ne' rispettava lo stato dei nemici.
 *
 * Java Object Serialization garantisce:
 * - Ricostruzione fedele dell'intero grafo di oggetti
 * - Nessuna chiamata ai costruttori (DungeonMap non riesegue buildDungeon)
 * - Conservazione di HP, cleared, visited, inventario esattamente come al momento del salvataggio
 *
 * Nota: il nome della classe rimane JsonPersistenceManager per non modificare
 * i riferimenti esistenti nel codice, ma il formato e' ora binario (.sav).
 */
public class JsonPersistenceManager implements PersistenceManager {

    private static final String SAVE_FILE = "savegame.sav";

    @Override
    public void save(GameState gameState) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(Path.of(SAVE_FILE))))) {
            oos.writeObject(gameState);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio: " + e.getMessage());
        }
    }

    @Override
    public GameState load() {
        if (!hasSave()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(Path.of(SAVE_FILE))))) {
            return (GameState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore nel caricamento: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean hasSave() {
        return Files.exists(Path.of(SAVE_FILE));
    }
}
