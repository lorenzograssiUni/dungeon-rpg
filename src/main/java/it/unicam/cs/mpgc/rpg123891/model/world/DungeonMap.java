package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mappa del dungeon: 5 stanze + Boss Finale, costruite esattamente
 * secondo GAME_SPEC.md.
 *
 * Struttura:
 *   r1 - Foresta        : entry drop Bastone Magico
 *                         Ondata A: Cinghiali x3 (50% Carne ciascuno)
 *                         Ondata B: Cinghiali x2 + Lupo (50% Carne ciascuno)
 *
 *   r2 - Villaggio Goblin:
 *                         Ondata A: Goblin x2          (drop assicurato: Doppie Daghe)
 *                         Ondata B: Goblin Guardie x3  (drop assicurato: Spada + Scudo o Armatura)
 *                         Ondata C: Re Goblin          (miniboss, fuga impossibile)
 *
 *   r3 - Catacombe      : entry drop Spadone
 *                         Ondata A: Scheletri x3
 *                         Ondata B: Statua Gigante     (drop: Spadone extra — gia' in entryLoot;
 *                                                        qui drop Scudo se mancante)
 *                         Ondata C: Scheletri x3 + Scheletro Guardia (drop: item mancante)
 *                         Ondata D: Strega             (miniboss, fuga impossibile;
 *                                                        drop: Pendente Magico + 3 Pozioni)
 *
 *   r4 - Sala del Tesoro:
 *                         Ondata A: Uova x3            (50% Carne — solo cuccioli in spec;
 *                                                        uova non droppano)
 *                         Ondata B: Uova x2 + Cucciolo (50% Carne dal cucciolo)
 *                         Ondata C: Cuccioli x3        (50% Carne ciascuno)
 *
 *   r5 - Boss Finale    :
 *                         Boss: L'Ultimo Drago         (fuga impossibile)
 *
 * NOTA sui drop probabilistici (50% Carne):
 *   Non sono pre-generati qui. Il controller, dopo ogni nemico sconfitto
 *   che prevede 50% Carne, lancia Random e aggiunge la Carne all'inventario.
 *
 * NOTA sul drop condizionale (item mancante in Catacombe):
 *   Il loot della Wave C e della Wave B di Catacombe viene costruito
 *   dal controller in base all'inventario del giocatore al momento del drop.
 *   Le Wave relative contengono una flag isMissingItemWave = true.
 */
public class DungeonMap implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Room> rooms = new ArrayList<>();
    private int currentRoomIndex   = 0;

    /** Traccia se nella Sala del Tesoro tutti cuccioli/uova sono stati uccisi. */
    private boolean treasureRoomCleaned = false;

    public DungeonMap() {
        buildDungeon();
    }

    // -------------------------------------------------------------------------
    // Costruzione stanze
    // -------------------------------------------------------------------------

    private void buildDungeon() {
        rooms.add(buildForest());
        rooms.add(buildGoblinVillage());
        rooms.add(buildCatacombs());
        rooms.add(buildTreasureRoom());
        rooms.add(buildBossRoom());
    }

    /** r1 - Foresta */
    private Room buildForest() {
        Room room = new Room("r1", "Foresta",
                "Alberi contorti ti circondano. Qualcosa si agita tra i cespugli.");

        // Drop prima dei nemici
        room.addEntryLoot(new MagicStaff());

        // Ondata A: Cinghiali x3
        Wave waveA = new Wave("Ondata A");
        waveA.addEnemy(EnemyFactory.createCinghiale());
        waveA.addEnemy(EnemyFactory.createCinghiale());
        waveA.addEnemy(EnemyFactory.createCinghiale());
        room.addWave(waveA);

        // Ondata B: Cinghiali x2 + Lupo
        Wave waveB = new Wave("Ondata B");
        waveB.addEnemy(EnemyFactory.createCinghiale());
        waveB.addEnemy(EnemyFactory.createCinghiale());
        waveB.addEnemy(EnemyFactory.createLupo());
        room.addWave(waveB);

        return room;
    }

    /** r2 - Villaggio Goblin */
    private Room buildGoblinVillage() {
        Room room = new Room("r2", "Villaggio Goblin",
                "Capanne di legno bruciate. I Goblin ti fissano con odio.");

        // Ondata A: Goblin x2 — drop assicurato Doppie Daghe
        Wave waveA = new Wave("Ondata A");
        waveA.addEnemy(EnemyFactory.createGoblin());
        waveA.addEnemy(EnemyFactory.createGoblin());
        waveA.addLoot(new DualDaggers());
        room.addWave(waveA);

        // Ondata B: Goblin Guardie x3 — drop assicurato Spada + (Scudo o Armatura)
        // Scudo e Armatura: il controller decide quale manca al giocatore
        Wave waveB = new Wave("Ondata B");
        waveB.addEnemy(EnemyFactory.createGoblinGuardia());
        waveB.addEnemy(EnemyFactory.createGoblinGuardia());
        waveB.addEnemy(EnemyFactory.createGoblinGuardia());
        waveB.addLoot(new Sword());
        // Scudo/Armatura condizionale: il controller aggiunge l'item mancante
        room.addWave(waveB);

        // Ondata C: Re Goblin — miniboss, fuga impossibile
        Wave waveC = new Wave("Miniboss: Re Goblin", false);
        waveC.addEnemy(EnemyFactory.createReGoblin());
        room.addWave(waveC);

        return room;
    }

    /** r3 - Catacombe */
    private Room buildCatacombs() {
        Room room = new Room("r3", "Catacombe",
                "Tunnel stretti illuminati da torce tremolanti. L'eco dei passi ti segue.");

        // Drop esplorazione prima dei nemici
        room.addEntryLoot(new Greatsword());

        // Ondata A: Scheletri x3
        Wave waveA = new Wave("Ondata A");
        waveA.addEnemy(EnemyFactory.createScheletro());
        waveA.addEnemy(EnemyFactory.createScheletro());
        waveA.addEnemy(EnemyFactory.createScheletro());
        room.addWave(waveA);

        // Ondata B: Statua Gigante — usata come Enemy con stat di Scheletro Guardia
        // drop: Scudo (se il giocatore non ce l'ha gia') — condizionale, gestito dal controller
        Wave waveB = new Wave("Ondata B");
        waveB.addEnemy(EnemyFactory.createScheletroGuardia()); // placeholder Statua Gigante
        room.addWave(waveB);

        // Ondata C: Scheletri x3 + Scheletro Guardia — drop: item mancante (Scudo o Armatura)
        Wave waveC = new Wave("Ondata C");
        waveC.addEnemy(EnemyFactory.createScheletro());
        waveC.addEnemy(EnemyFactory.createScheletro());
        waveC.addEnemy(EnemyFactory.createScheletro());
        waveC.addEnemy(EnemyFactory.createScheletroGuardia());
        room.addWave(waveC);

        // Ondata D: Strega — miniboss, fuga impossibile
        // drop assicurato: Pendente Magico + 3 Pozioni (in Wave.loot)
        Wave waveD = new Wave("Miniboss: Strega", false);
        waveD.addEnemy(EnemyFactory.createStrega());
        waveD.addLoot(new MagicAmulet());
        waveD.addLoot(new Potion());
        waveD.addLoot(new Potion());
        waveD.addLoot(new Potion());
        room.addWave(waveD);

        return room;
    }

    /** r4 - Sala del Tesoro */
    private Room buildTreasureRoom() {
        Room room = new Room("r4", "Sala del Tesoro",
                "Oro e gemme scintillano ovunque. Ma qualcosa di enorme si muove nell'ombra.");

        // Ondata A: Uova x3 (le uova non droppano Carne — solo i cuccioli)
        Wave waveA = new Wave("Ondata A");
        waveA.addEnemy(EnemyFactory.createUovo());
        waveA.addEnemy(EnemyFactory.createUovo());
        waveA.addEnemy(EnemyFactory.createUovo());
        room.addWave(waveA);

        // Ondata B: Uova x2 + Cucciolo di Drago
        Wave waveB = new Wave("Ondata B");
        waveB.addEnemy(EnemyFactory.createUovo());
        waveB.addEnemy(EnemyFactory.createUovo());
        waveB.addEnemy(EnemyFactory.createCuccioloDrago());
        room.addWave(waveB);

        // Ondata C: Cuccioli di Drago x3
        Wave waveC = new Wave("Ondata C");
        waveC.addEnemy(EnemyFactory.createCuccioloDrago());
        waveC.addEnemy(EnemyFactory.createCuccioloDrago());
        waveC.addEnemy(EnemyFactory.createCuccioloDrago());
        room.addWave(waveC);

        return room;
    }

    /** r5 - Boss Finale */
    private Room buildBossRoom() {
        Room room = new Room("r5", "Sala del Drago",
                "Il terreno trema. Fiamme lambiscono le pareti. L'Ultimo Drago ti aspetta.");

        Wave boss = new Wave("Boss Finale: L'Ultimo Drago", false);
        boss.addEnemy(EnemyFactory.createUltimoDrago());
        room.addWave(boss);

        return room;
    }

    // -------------------------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------------------------

    public Room getCurrentRoom()    { return rooms.get(currentRoomIndex); }
    public boolean hasNextRoom()    { return currentRoomIndex < rooms.size() - 1; }
    public void advanceToNextRoom() { if (hasNextRoom()) currentRoomIndex++; }
    public int getCurrentRoomIndex(){ return currentRoomIndex; }
    public int getTotalRooms()      { return rooms.size(); }
    public List<Room> getRooms()    { return List.copyOf(rooms); }

    public boolean areAllRoomsCleared() {
        return rooms.stream().allMatch(Room::isCleared);
    }

    public List<String> getVisitedRoomNames() {
        return rooms.stream().filter(Room::isVisited)
                .map(Room::getName).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Flag Sala del Tesoro (per buff passivo del Drago)
    // -------------------------------------------------------------------------

    public boolean isTreasureRoomCleaned()      { return treasureRoomCleaned; }
    public void setTreasureRoomCleaned(boolean v){ this.treasureRoomCleaned = v; }
}
