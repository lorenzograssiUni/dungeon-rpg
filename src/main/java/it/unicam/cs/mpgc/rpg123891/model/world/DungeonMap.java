package it.unicam.cs.mpgc.rpg123891.model.world;

import it.unicam.cs.mpgc.rpg123891.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg123891.model.item.Potion;
import it.unicam.cs.mpgc.rpg123891.model.item.weapons.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        // GAME_SPEC: drop prima stanza prima che arrivino i nemici: Bastone Magico
        room.addEntryLoot(new MagicStaff());

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

        // Ondata A: Goblin x2, drop assicurato Doppie Daghe
        Wave waveA = new Wave("Ondata A", true,
                "Due goblin saltano fuori da dietro una capanna, armati di coltellacci arrugginiti. " +
                "Uno di essi porta con s\u00e9 un fodero con doppie daghe lucenti \u2014 roba rubata.");
        waveA.addEnemy(EnemyFactory.createGoblin());
        waveA.addEnemy(EnemyFactory.createGoblin());
        waveA.addLoot(new DualDaggers());
        room.addWave(waveA);

        // Ondata B: Goblin Guardie x3, drop assicurato Spada + (Scudo o Armatura)
        Wave waveB = new Wave("Ondata B", true,
                "Un fischio acuto riecheggia nel villaggio. Tre goblin guardia si fanno avanti, " +
                "equipaggiati con armature rozze e spade di ferro.");
        waveB.addEnemy(EnemyFactory.createGoblinGuardia());
        waveB.addEnemy(EnemyFactory.createGoblinGuardia());
        waveB.addEnemy(EnemyFactory.createGoblinGuardia());
        waveB.addLoot(new Sword());
        // Scudo o Armatura aggiunto dinamicamente da addConditionalLoot() in GameController
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

        // Ondata A: Scheletri x3
        Wave waveA = new Wave("Ondata A", true,
                "Tre scheletri si svegliano dalle nicchie scavate nelle pareti. " +
                "Le loro orbite vuote brillano di luce bluastra. Le ossa scricchiolano mentre avanzano.");
        waveA.addEnemy(EnemyFactory.createScheletro());
        waveA.addEnemy(EnemyFactory.createScheletro());
        waveA.addEnemy(EnemyFactory.createScheletro());
        room.addWave(waveA);

        // Wave ricompensa esplorazione: Spadone (loot senza nemici)
        Wave waveStatua = new Wave("Stanza dello Spadone", true,
                "Arrivi in una camera circolare silenziosa. Al centro, un'imponente statua " +
                "di un guerriero in armatura piena \u2014 alta il doppio di un uomo. " +
                "Tra le sue mani di pietra stringe uno SPADONE enorme. " +
                "Lentamente, le dita si aprono. La spada cade ai tuoi piedi con un rimbombo.");
        waveStatua.addLoot(new Greatsword());
        room.addWave(waveStatua);

        // Ondata B: Statua Gigante -> Scheletri x3 + Scheletro Guardia; drop Spadone
        Wave waveB = new Wave("Ondata B", true,
                "Dalle pareti emergono altri scheletri, stavolta tre comuni e uno corazzato " +
                "con scudo e armatura. Si muovono con sincronia innaturale.");
        waveB.addEnemy(EnemyFactory.createScheletro());
        waveB.addEnemy(EnemyFactory.createScheletro());
        waveB.addEnemy(EnemyFactory.createScheletro());
        waveB.addEnemy(EnemyFactory.createScheletroGuardia());
        room.addWave(waveB);

        // Ondata C: Scheletri x3 + Scheletro Guardia; drop item mancante (gestito da controller)
        Wave waveC_skeletons = new Wave("Ondata C", true,
                "Un'altra ondata di scheletri emerge dall'oscurita'. Questa volta uno di loro " +
                "porta l'equipaggiamento che ti manca.");
        waveC_skeletons.addEnemy(EnemyFactory.createScheletro());
        waveC_skeletons.addEnemy(EnemyFactory.createScheletro());
        waveC_skeletons.addEnemy(EnemyFactory.createScheletro());
        waveC_skeletons.addEnemy(EnemyFactory.createScheletroGuardia());
        room.addWave(waveC_skeletons);

        // Miniboss: Strega; drop assicurato Pendente Magico + 3 Pozioni
        Wave waveD = new Wave("Miniboss: Strega", false,
                "Una risata acuta echeggia dalle volte. Candele si accendono da sole. " +
                "La Strega scende lentamente dal soffitto, circondata da un alone verdastro. " +
                "\"Benvenuto, sciocco eroe. I miei figli ti accoglieranno.\"");
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
                "Oro e gemme scintillano ovunque, ammassati in pile impossibili. " +
                "Ma il pavimento trema. Qualcosa di enorme respira nell'ombra in fondo alla sala.");

        Wave waveA = new Wave("Ondata A", true,
                "Tra i mucchi d'oro, tre grosse uova di drago pulsano di calore. " +
                "Crepe compaiono sui gusci \u2014 stanno per schiudersi!");
        waveA.addEnemy(EnemyFactory.createUovo());
        waveA.addEnemy(EnemyFactory.createUovo());
        waveA.addEnemy(EnemyFactory.createUovo());
        room.addWave(waveA);

        Wave waveB = new Wave("Ondata B", true,
                "Altre due uova emergono dall'ombra. Accanto a esse, un cucciolo gi\u00e0 schiuso " +
                "spalanca le fauci e lancia fiammate basse.");
        waveB.addEnemy(EnemyFactory.createUovo());
        waveB.addEnemy(EnemyFactory.createUovo());
        waveB.addEnemy(EnemyFactory.createCuccioloDrago());
        room.addWave(waveB);

        Wave waveC = new Wave("Ondata C", true,
                "Tre cuccioli di drago si avventano su di te, artigli lucidi e occhi di brace. " +
                "Sono pi\u00f9 veloci di quanto sembrino.");
        waveC.addEnemy(EnemyFactory.createCuccioloDrago());
        waveC.addEnemy(EnemyFactory.createCuccioloDrago());
        waveC.addEnemy(EnemyFactory.createCuccioloDrago());
        room.addWave(waveC);

        return room;
    }

    /** r5 - Boss Finale */
    private Room buildBossRoom() {
        Room room = new Room("r5", "Sala del Drago",
                "Il soffitto \u00e8 altissimo e buio. Le pareti sono annerite dalle fiamme. " +
                "Al centro della sala, una sagoma immensa apre lentamente un occhio dorato.");

        Wave boss = new Wave("Boss Finale: L'Ultimo Drago", false,
                "L'Ultimo Drago si alza in tutta la sua maestosit\u00e0, le ali scagliate che " +
                "proiettano ombre enormi sulle pareti. Un ruggito fa tremare le pietre. " +
                "\"PICCOLO INSETTO... sei venuto a morire?\"");
        boss.addEnemy(EnemyFactory.createUltimoDrago());
        room.addWave(boss);

        return room;
    }

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

    public boolean isTreasureRoomCleaned()      { return treasureRoomCleaned; }
    public void setTreasureRoomCleaned(boolean v){ this.treasureRoomCleaned = v; }
}
