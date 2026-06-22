# Dungeon RPG — Specifiche di Gioco

> Documento di riferimento per l'implementazione. Ogni sezione descrive una componente del gioco con stats, comportamenti e regole di loot.

---

## Personaggi Giocabili

| Stat | Warrior | Mage | Thief |
|------|---------|------|-------|
| HP | 120 | 75 | 90 |
| Attacco | 22 | 15 | 18 |
| Difesa | 8 | 4 | 6 |
| Agilità | 4 | 6 | 8 |
| Stamina | 8 | 10 | 12 |
| Critico | 15% | 5% | 25% |

> Abbreviazioni usate nelle schede item: **W** = Warrior, **M** = Mage, **T** = Thief

---

## Struttura del Dungeon

Il gioco è composto da **5 stanze** (inizio con 3 pozioni) + Boss Finale.
Ogni turno attacca ogni nemico nell'ordine di agilità (Reminder: bisogna aggiungere delle stats di agilità ad ogni mob).
Implementare la possibilità di scappare (impossibile contro i miniboss o nemici con drop assicurato).

### 1. Foresta
*drop prima stanza prima che arrivino i nemici: Bastone Magico*

| Ondata | Nemici | Loot |
|--------|--------|------|
| A | Cinghiali ×3 | 50% prob. Carne per ogni mob |
| B | Cinghiali ×2 + Lupo | 50% prob. Carne per ogni mob |

---

### 2. Villaggio Goblin

| Ondata | Nemici | Loot |
|--------|--------|------|
| A | Goblin ×2 | Drop **assicurato**: doppie daghe |
| B | Goblin Guardie ×3 | Drop **assicurato**: Spada + Scudo o Armatura |
| Miniboss | Re Goblin | --- |

> Il Re Goblin è un miniboss con abilità speciale.

---

### 3. Catacombe
*Ricompensa esplorazione: Spadone*

| Ondata | Nemici | Loot |
|--------|--------|------|
| A | Scheletri ×3 | — |
| B | Statua Gigante | Drop: **Spadone** |
| C | Scheletri ×3 + Scheletro con Scudo/Armatura | Drop: l'item mancante (Scudo o Armatura) |
| Miniboss | Strega | Drop: Pendente Magico + 3 Pozioni |

> La Strega è un miniboss con abilità speciale.

---

### 4. Sala del Tesoro

| Ondata | Nemici | Loot |
|--------|--------|------|
| A | Uova ×3 | 50% prob. Carne per ogni mob |
| B | Uova ×2 + Cucciolo di Drago | 50% prob. Carne per ogni mob |
| C | Cuccioli di Drago ×3 | 50% prob. Carne per ogni mob |

> Le uova non droppano carne, solo i cuccioli.
---

### 5. Boss Finale — L'Ultimo Drago

Vedi scheda nemico dedicata nella sezione Nemici.

---

## Schede Nemici

### Cinghiale
- **HP:** 36
- **Attacco:** 7–10
- **Loot:** 50% probabilità Carne

### Lupo
- **HP:** 45
- **Attacco:** 10–12
- **Loot:** 50% probabilità Carne

### Goblin
- **HP:** 42
- **Attacco:** 10–15
- **Loot:** —

### Goblin Guardia
- **HP:** come Goblin normale
- **Equipaggiamento:** Spada + Scudo o Armatura
- **Loot drop assicurato:** Spada + Scudo o Armatura

### Re Goblin *(Miniboss)*
- **HP:** 55
- **Attacco:** 15–20
- **Abilità Speciale:** 3 Lanci (20 ATK l'uno)

### Scheletro
- **HP:** 50
- **Attacco:** 12–17
- **Loot:** —

### Scheletro Guardia
- **HP:** come Scheletro normale
- **Equipaggiamento:** Spada + Scudo/Armatura
- **Abilità Speciale:** Carica!
- **Loot drop assicurato:** l'item mancante al giocatore (Scudo o Armatura)

### Strega *(Miniboss)*
- **HP:** 80
- **Attacco:** 15–25
- **Abilità Speciale:** Evoca 3 scheletri — immune agli attacchi durante finchè gli scheletri sono in vita.
- **Loot drop assicurato:** Pendente Magico + 3 Pozioni

### Uovo
- **HP:** 20
- **Attacco:** 1 (danno sicuro, ignora difesa)
- **Meccanica:** diventa Cucciolo di Drago entro 3 turni se non sconfitto
- **Loot:** 50% probabilità Carne

### Cucciolo di Drago
- **HP:** 65
- **Attacco:** 20–25
- **Loot:** 50% probabilità Carne

### L'Ultimo Drago *(Boss Finale)*
- **HP:** 140
- **Attacco:** 30–60
- **Buff passivo:** se ha ucciso tutti i Cuccioli e le Uova nella stanza → +20% danno
- **Abilità Speciale — Soffio del Drago:** infligge bruciatura — 5–8 HP di danno per turno, dura 3–5 turni

---

## Oggetti & Equipaggiamento

### Armi

#### Spada Semplice
| Stat | Warrior | Mage | Thief |
|------|---------|------|-------|
| Attacco | +2 | +2 | +2 |
| Agilità | +2 | 0 | +2 |
| Stamina | 0 | -3 | 0 |

**Attacchi Speciali:**
- **Fendente** — +25% attacco, +5% Critico. Costo: 2 stamina
- **Carica!** *(richiede Spada + Scudo)* — equivale al Fendente + ricevi 0 danno nel turno corrente. Costo: 4 stamina

---

#### Bastone Magico
| Stat | Warrior | Mage | Thief |
|------|---------|------|-------|
| Attacco | -3 | +5 | 0 |
| Agilità | 0 | +2 | 0 |
| Stamina | -3 | +3 | -3 |

**Attacchi Speciali:**
- **Onda Magica** — colpisce tutti i nemici nella stanza; danno = ATK + (3 di danno × numero nemici). Costo: 4 stamina
- **Colpo Vitale** *(richiede Bastone + Pendente Magico)* — infligge danni pari alla vita attuale del personaggio. Malus: perdi metà vita Costo: 6 stamina

---

#### Doppie Daghe *(2 mani)*
| Stat | Warrior | Mage | Thief |
|------|---------|------|-------|
| Attacco | 0 | -2 | +5 |
| Agilità | +2 | +2 | +2 |
| Stamina | 0 | -1 | +2 |

**Attacchi Speciali:**
- **Sfuriata** — 3 attacchi: 1° 100% danno, 2° 50% danno, 3° 50% danno + 25% prob. Critico. Costo: 5 stamina
- **Ira** — +25% prob. Critico; attacca tante volte quanti nemici nella stanza. Costo: 3 stamina

---

#### Spadone *(2 mani)*
| Stat | Warrior | Mage | Thief |
|------|---------|------|-------|
| Attacco | +5 | -2 | +1 |
| Agilità | 0 | -4 | -4 |
| Costo Stamina | +3 | -2 | 0 |

**Attacchi Speciali:**
- **Spazzatutto** — colpisce tutti i nemici e li stordisce per 1 turno. Costo: 4 stamina
- **Taglio Profondo** — danno pari alla metà degli HP attuali del nemico. Costo: 7 stamina

---

### Armature & Accessori

#### Scudo
| Stat | Warrior | Mage | Thief |
|------|---------|------|-------|
| Difesa | +2 | +2 | +2 |
| Agilità | -2 | -2 | -2 |

> Occupa slot OFF_HAND. Incompatibile con armi a 2 mani (Spadone, Doppie Daghe).

---

#### Armatura
| Stat | Warrior | Mage | Thief |
|------|---------|------|-------|
| Difesa | +4 | +4 | +4 |
| Agilità | -4 | -4 | -4 |

> Occupa slot BODY. Incompatibile con Pendente Magico (stesso slot).

---

#### Pendente Magico
| Stat | Warrior | Mage | Thief |
|------|---------|------|-------|
| Difesa | -3 | -3 | -3 |
| HP | +2 | +5 | +2 |

> Occupa slot BODY. Incompatibile con Armatura (stesso slot).  
> Abilita l'attacco speciale **Colpo Vitale** del Bastone Magico.

---

### Consumabili

#### Carne
- **Effetto:** +40 HP

#### Pozione
- **Effetto:** Stamina +3 (per tutti i personaggi)

---

## Regole di Combattimento (Riepilogo)

- **Iniziativa:** chi ha Agilità maggiore attacca per primo; parità → priorità al giocatore.
- **Stamina:** consumata ad ogni attacco normale (1 stamina). Gli attacchi speciali hanno costo variabile. I **nemici non consumano stamina**.
- **Critico:** danno × 2. Il Thief ha critico garantito al primo attacco se `stealthBonusActive = true`.
- **Blocco Warrior:** chance di annullare completamente un attacco **fisico** in arrivo.
- **Scudo Magico Mage:** blocca 1 attacco **fisico** in arrivo, poi si disattiva.
- **Vulnerabilità Mage:** subisce +30% danno da attacchi magici.
- **Danno minimo:** 0 (il danno non può essere negativo).
- **HP minimo:** 0 (non scendono sotto zero).
- **Bruciatura (Soffio del Drago):** 5–8 HP per turno per 3–5 turni. Applicata a fine turno del boss.
- **Stordimento (Spazzatutto):** il nemico stordito salta il proprio turno.
- **Uova:** se un Uovo non viene sconfitto entro 3 turni, si trasforma in Cucciolo di Drago.

---

## Note di Implementazione

- Il loot "item mancante" nella stanza Catacombe deve verificare cosa il giocatore **non ha ancora** nell'inventario.
- **Colpo Vitale** richiede entrambi gli slot: Bastone Magico in MAIN_HAND e Pendente Magico in BODY.
- **Carica!** richiede Spada Semplice in MAIN_HAND e Scudo in OFF_HAND.
- Il **buff passivo del Drago** si attiva solo se nella Sala del Tesoro il giocatore ha ucciso tutti i cuccioli/uova prima di affrontare il Boss.
- La **fuga** è possibile solo se l'agilità del giocatore è minore a l'agilità media dei nemici all'interno della stanza. Nella stanza finale può fuggire sempre per uova e cuccioli di drago
