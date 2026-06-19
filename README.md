# Dungeon RPG

Gioco di ruolo a turni sviluppato in Java come progetto per il corso di **Metodologie di Programmazione e Modellazione e Gestione della Conoscenza** (AA 2025/26), Università di Camerino.

## Descrizione

Dungeon RPG è un gioco di ruolo a turni ambientato in un dungeon fantasy. Il giocatore sceglie una classe (Guerriero, Mago, Ladro), esplora stanze collegate tra loro, combatte nemici, raccoglie oggetti e gestisce il proprio inventario. Il progetto è sviluppato in Java con interfaccia grafica JavaFX e persistenza tramite file JSON.

## Requisiti

- **JDK 17 o superiore** (testato con OpenJDK 19)
- Il Gradle Wrapper è incluso nel repository — non è necessario installare Gradle

## Compilazione ed Esecuzione

Su Linux / macOS:
```bash
./gradlew build
./gradlew run
```

Su Windows:
```bat
gradlew.bat build
gradlew.bat run
```

> Se sul sistema è installato un JDK diverso da quello richiesto, impostare il path corretto in `gradle.properties` alla voce `org.gradle.java.home`.

## Struttura del Progetto

```
src/main/java/it/unicam/cs/mpgc/rpg123891/
├── Main.java                  # Entry point
├── model/
│   ├── character/             # Character (astratta), Warrior, Mage, Thief
│   ├── combat/                # Combatable (interfaccia), CombatSystem, Enemy
│   ├── world/                 # Room, DungeonMap, Direction
│   ├── item/                  # Item (interfaccia), Weapon, Potion
│   └── game/                  # GameState
├── controller/                # GameController
├── persistence/               # Saveable (interfaccia), JsonPersistenceManager
└── ui/                        # UIInterface (interfaccia), JavaFXUI
```

## Funzionalità Principali

- Creazione personaggio con scelta della classe (Warrior / Mage / Thief)
- Esplorazione dungeon con stanze collegate in direzioni cardinali
- Sistema di combattimento a turni con calcolo danni e level-up
- Inventario con armi e pozioni
- Salvataggio e caricamento partita tramite file JSON (`savegame.json`)
- Interfaccia grafica desktop con JavaFX 21

## Documentazione

La documentazione completa del progetto (funzionalità, responsabilità delle classi, persistenza, estendibilità) è disponibile nella **[Wiki del repository](../../wiki)**.

## Dichiarazione Uso di Strumenti di AI

Nella realizzazione di questo progetto sono stati utilizzati i seguenti strumenti di intelligenza artificiale:

| Strumento | Scopo |
|---|---|
| **Perplexity AI** | Progettazione architettura, definizione interfacce e classi, risoluzione problemi di configurazione Gradle/JavaFX, generazione bozze di codice, redazione della Wiki |
| **GitHub Copilot** | Suggerimenti inline durante la scrittura del codice (getter/setter, pattern boilerplate) |

Tutto il codice generato è stato letto, compreso e verificato dallo studente prima dell'integrazione. Le decisioni architetturali significative (scelta del tipo di gioco, meccaniche, struttura della mappa) sono state prese autonomamente. La dichiarazione dettagliata è disponibile nella [Wiki — Dichiarazione Uso AI](../../wiki/Dichiarazione-Uso-AI).
