# 📌 Dungeon RPG

Gioco di ruolo a turni sviluppato in Java come progetto per il corso di **Metodologie di Programmazione e Modellazione e Gestione della Conoscenza** (AA 2025/26), Università di Camerino.  
Il giocatore sceglie una classe (Guerriero, Mago, Ladro), esplora stanze di un dungeon fantasy, combatte nemici a turni, raccoglie oggetti e salva i progressi su file JSON.

---

## 🚀 Come eseguire il progetto

### Prerequisiti
- Java 17 o superiore (testato con OpenJDK 19)
- Gradle (incluso tramite wrapper — non richiede installazione)

### Istruzioni

```bash
git clone https://github.com/lorenzograssiUni/dungeon-rpg.git
cd dungeon-rpg
```

### Build del progetto
```bash
./gradlew build
```
Su Windows:
```bat
gradlew.bat build
```

### Esecuzione
```bash
./gradlew run
```
Su Windows:
```bat
gradlew.bat run
```

> Se il JDK sul sistema è diverso da quello atteso, impostare il path in `gradle.properties` alla voce `org.gradle.java.home`.

---

## 🤖 Uso di strumenti di AI

Nella realizzazione di questo progetto sono stati utilizzati i seguenti strumenti di AI come **supporto**, non come sostituto del lavoro personale:

* Utilizzato **Perplexity AI** per:
  * progettare l'architettura del sistema (package, interfacce, pattern)
  * applicare i principi SOLID alla struttura delle classi
  * risolvere problemi di configurazione Gradle e compatibilità JavaFX
  * generare bozze di codice successivamente lette, comprese e modificate
  * redigere la documentazione nella Wiki del repository

* Utilizzato **GitHub Copilot** per:
  * autocompletamento di metodi semplici (getter/setter)
  * generazione di codice ripetitivo (costruttori, pattern boilerplate)

Tutto il codice è stato **compreso, verificato e testato** personalmente prima dell'integrazione. Le scelte architetturali significative sono state prese in modo autonomo.

---

📌 Per una descrizione più dettagliata dell'uso dell'AI, consultare la **[Wiki — Dichiarazione Uso AI](../../wiki/Dichiarazione-Uso-AI)**.
