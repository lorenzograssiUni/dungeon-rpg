# Dungeon RPG

Gioco di ruolo a turni sviluppato in Java come progetto per il corso di Metodologie di Programmazione e Modellazione e Gestione della Conoscenza (AA 2025/26).

## Descrizione

Dungeon RPG è un gioco di ruolo a turni ambientato in un dungeon fantasy.
Il giocatore sceglie una classe (Guerriero, Mago, Ladro), esplora stanze,
combatte nemici, raccoglie oggetti e affronta un boss finale.

## Requisiti

- Java 17 o superiore
- Gradle (incluso tramite wrapper)

## Compilazione

```bash
./gradlew build
```

## Esecuzione

```bash
./gradlew run
```

## Uso di strumenti AI

Nella realizzazione di questo progetto sono stati utilizzati strumenti di intelligenza artificiale (Perplexity AI) come supporto per la progettazione architetturale, la definizione della struttura del progetto e la generazione di parti del codice. Tutti i contenuti sono stati revisionati e integrati dallo studente.

## Struttura del progetto

```
src/main/java/it/unicam/cs/mpgc/rpg123891/
├── Main.java
├── model/
│   ├── character/
│   ├── combat/
│   ├── world/
│   ├── item/
│   └── game/
├── controller/
├── persistence/
└── ui/
```
