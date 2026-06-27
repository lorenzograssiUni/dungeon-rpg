package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DungeonMap implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Room> rooms = new ArrayList<>();
    private int currentRoomIndex   = 0;
    private boolean treasureRoomCleaned = false;

    public DungeonMap() {
        buildDungeon();
    }

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
                "Alberi contorti ti circondano. Qualcosa si agita tra i cespugli. " +
                "Un vecchio bastone intagliato \u00e8 appoggiato a un tronco \u2014 " +
                "sembra aspettarti.");

        // Wave 0: nessun nemico, loot = Bastone Magico (GAME_SPEC)
        Wave waveBastone = new Wave("Stanza del Bastone", false,
                "Un vecchio bastone intagliato \u00e8 appoggiato a un tronco. " +
                "Sembra aspettarti. Lo raccogli.");
        waveBastone.addLoot(new MagicStaff());
        room.addWave(waveBastone);

        Wave waveA = new Wave("Ondata A", true,
                "Dal fogliame emergono tre cinghiali dagli occhi rossi. Grugniscono e caricano!");
        waveA.addEnemy(EnemyFactory.createCinghiale());
        waveA.addEnemy(EnemyFactory.createCinghiale());
        waveA.addEnemy(EnemyFactory.createCinghiale());
        room.addWave(waveA);

        Wave waveB = new Wave("Ondata B", true,
                "Non hai fatto in tempo a rifiatare. Due cinghiali e un lupo solitario " +
                "sbucano dall'ombra, denti scoperti.");
        waveB.addEnemy(EnemyFactory.createCinghiale());
        waveB.addEnemy(EnemyFactory.createCinghiale());
        waveB.addEnemy(EnemyFactory.createLupo());
        room.addWave(waveB);

        return room;
    }

    /** r2 - Villaggio Goblin */
    private Room buildGoblinVillage() {
        Room room = new Room("r2", "Villaggio Goblin",
                "Capanne di legno bruciate costeggiano il sentiero. " +
                "L'aria puzza di fumo e carne bruciata. " +
                "I goblin ti fissano con odio dagli anfratti.");

        Wave waveA = new Wave("Ondata A", true,
                "Due goblin saltano fuori da dietro una capanna, armati di coltellacci arrugginiti. " +
                "Uno di essi porta con s\u00e9 un fodero con doppie daghe lucenti \u2014 roba rubata.");
        waveA.addEnemy(EnemyFactory.createGoblin());
        waveA.addEnemy(EnemyFactory.createGoblin());
        waveA.addLoot(new DualDaggers());
        room.addWave(waveA);

        Wave waveB = new Wave("Ondata B", true,
                "Un fischio acuto riecheggia nel villaggio. Tre goblin guardia si fanno avanti, " +
                "equipaggiati con armature rozze e spade di ferro.");
        waveB.addEnemy(EnemyFactory.createGoblinGuardia());
        waveB.addEnemy(EnemyFactory.createGoblinGuardia());
        waveB.addEnemy(EnemyFactory.createGoblinGuardia());
        waveB.addLoot(new Sword());
        room.addWave(waveB);

        Wave waveC = new Wave("Miniboss: Re Goblin", false,
                "Il terreno trema. Dal palazzo di fango e sterpi emerge il Re Goblin: " +
                "tozzo, coperto di cicatrici, con una corona storta di ossa. " +
                "\"INTRUSO! Lo stritolo con le mie stesse mani!\"");
        waveC.addEnemy(EnemyFactory.createReGoblin());
        room.addWave(waveC);

        return room;
    }

    /** r3 - Catacombe */
    private Room buildCatacombs() {
        Room room = new Room("r3", "Catacombe",
                "Tunnel stretti illuminati da torce tremolanti. " +
                "L'eco dei tuoi passi si moltiplica nell'oscurit\u00e0. " +
                "L'odore di pietra umida e morte vecchia appesta l'aria.");

        Wave waveA = new Wave("Ondata A", true,
                "Tre scheletri si svegliano dalle nicchie scavate nelle pareti. " +
                "Le loro orbite vuote brillano di luce bluastra. Le ossa scricchiolano mentre avanzano.");
        waveA.addEnemy(EnemyFactory.createScheletro());
        waveA.addEnemy(EnemyFactory.createScheletro());
        waveA.addEnemy(EnemyFactory.createScheletro());
        room.addWave(waveA);

        Wave waveStatua = new Wave("Stanza dello Spadone", true,
                "Arrivi in una camera circolare silenziosa. Al centro, un'imponente statua " +
                "di un guerriero in armatura piena \u2014 alta il doppio di un uomo. " +
                "Tra le sue mani di pietra stringe uno SPADONE enorme. " +
                "Lentamente, le dita si aprono. La spada cade ai tuoi piedi con un rimbombo.");
        waveStatua.addLoot(new Greatsword());
        room.addWave(waveStatua);

        Wave waveB = new Wave("Ondata B", true,
                "Dalle pareti emergono altri scheletri, stavolta tre comuni e uno corazzato " +
                "con scudo e armatura. Si muovono con sincronia innaturale.");
        waveB.addEnemy(EnemyFactory.createScheletro());
        waveB.addEnemy(EnemyFactory.createScheletro());
        waveB.addEnemy(EnemyFactory.createScheletro());
        waveB.addEnemy(EnemyFactory.createScheletroGuardia());
        room.addWave(waveB);

        Wave waveC = new Wave("Miniboss: Strega", false,
                "Un sibilo acuto penetra l'oscurit\u00e0. Da un corridoio laterale emerge la Strega: " +
                "vesti lacere, occhi color sangue, dita adunche che tracciano rune nell'aria.");
        waveC.addEnemy(EnemyFactory.createStrega());
        room.addWave(waveC);

        return room;
    }

    /** r4 - Sala del Tesoro */
    private Room buildTreasureRoom() {
        Room room = new Room("r4", "Sala del Tesoro",
                "Uno scintillio dorato filtra da sotto la porta. " +
                "Entri: scaffali di legno marcio stracolmi di monete e gioielli. " +
                "Al centro, tre forzieri di ferro.");

        room.addEntryLoot(new MagicAmulet());

        Wave wavePozioni = new Wave("Forzieri", true,
                "I forzieri si aprono rivelando provviste e armi abbandonate da avventurieri caduti.");
        wavePozioni.addLoot(new Potion());
        wavePozioni.addLoot(new Potion());
        room.addWave(wavePozioni);

        return room;
    }

    /** r5 - Covo del Drago */
    private Room buildBossRoom() {
        Room room = new Room("r5", "Covo del Drago",
                "Il soffitto si perde nell'oscurit\u00e0. Ossa di avventurieri decorano il pavimento. " +
                "Al centro della caverna, L'Ultimo Drago apre un occhio.");

        Wave waveUova = new Wave("Uova del Drago", true,
                "Prima di raggiungere il drago devi farti largo tra le sue uova " +
                "e i cuccioli gi\u00e0 schiusi. Attento: il drago osserva.");
        waveUova.addEnemy(EnemyFactory.createUovo());
        waveUova.addEnemy(EnemyFactory.createUovo());
        waveUova.addEnemy(EnemyFactory.createCuccioloDrago());
        room.addWave(waveUova);

        Wave waveDrago = new Wave("Boss: L'Ultimo Drago", false,
                "L'Ultimo Drago si alza in piedi. La sua ombra copre l'intera caverna. " +
                "\"Piccolo insetto... osi sfidarmi?\"");
        waveDrago.addEnemy(EnemyFactory.createUltimoDrago());
        room.addWave(waveDrago);

        return room;
    }

    // -------------------------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------------------------

    public Room getCurrentRoom()      { return rooms.get(currentRoomIndex); }
    public int  getCurrentRoomIndex() { return currentRoomIndex; }
    public int  getTotalRooms()       { return rooms.size(); }

    /** Restituisce una vista non modificabile di tutte le stanze (usato da GameScreen). */
    public List<Room> getRooms() { return Collections.unmodifiableList(rooms); }

    public boolean hasNextRoom() {
        return currentRoomIndex < rooms.size() - 1;
    }

    public void advanceToNextRoom() {
        if (hasNextRoom()) currentRoomIndex++;
    }

    public boolean isTreasureRoomCleaned()          { return treasureRoomCleaned; }
    public void    setTreasureRoomCleaned(boolean v) { this.treasureRoomCleaned = v; }
}
