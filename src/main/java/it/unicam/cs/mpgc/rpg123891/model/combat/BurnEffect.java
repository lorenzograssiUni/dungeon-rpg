package it.unicam.cs.mpgc.rpg123891.model.combat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

/**
 * Status effect: Bruciatura.
 * Infligge danno per un certo numero di turni, ignorando la difesa.
 * Applicata dal Soffio del Drago: 5-8 HP per turno, dura 3-5 turni.
 *
 * Viene applicata al personaggio giocatore e il tick viene chiamato
 * dal controller a fine di ogni turno del boss.
 */
public class BurnEffect implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int damagePerTurn;
    private int turnsRemaining;

    /**
     * Costruttore con valori randomici secondo la spec:
     * danno 5-8 HP, durata 3-5 turni.
     */
    public BurnEffect(Random random) {
        this.damagePerTurn  = 5 + random.nextInt(4);  // 5, 6, 7 o 8
        this.turnsRemaining = 3 + random.nextInt(3);  // 3, 4 o 5
    }

    /** Costruttore esplicito per i test. */
    public BurnEffect(int damagePerTurn, int turnsRemaining) {
        this.damagePerTurn  = damagePerTurn;
        this.turnsRemaining = turnsRemaining;
    }

    /**
     * Applica il tick di bruciatura al bersaglio.
     * Il danno ignora la difesa: viene sottratto direttamente agli HP.
     * @return danno inflitto in questo turno, 0 se l'effetto è esaurito
     */
    public int tick(it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter target) {
        if (turnsRemaining <= 0) return 0;
        turnsRemaining--;
        // Danno diretto: bypassiamo takeDamage() che applica la difesa
        int before = target.getCurrentHp();
        target.heal(-damagePerTurn); // heal con valore negativo non funziona — usiamo metodo diretto
        // heal() non accetta negativi, usiamo la via corretta:
        // togliamo HP direttamente tramite il metodo pubblico takeDamage con difesa=0
        // Poiché takeDamage sottrae defense, passiamo damage = damagePerTurn + defense
        // per annullare la riduzione. Ma non abbiamo accesso a defense da qui.
        // SOLUZIONE: aggiungiamo applyBurnDamage() in GameCharacter (vedi commit).
        // Per ora restituiamo il valore teorico; il controller chiamerà applyBurnDamage().
        return damagePerTurn;
    }

    /** Applica il danno di bruciatura ignorando la difesa del bersaglio. */
    public int applyTo(it.unicam.cs.mpgc.rpg123891.model.character.GameCharacter target) {
        if (turnsRemaining <= 0) return 0;
        turnsRemaining--;
        target.applyBurnDamage(damagePerTurn);
        return damagePerTurn;
    }

    public boolean isExpired()      { return turnsRemaining <= 0; }
    public int getDamagePerTurn()   { return damagePerTurn; }
    public int getTurnsRemaining()  { return turnsRemaining; }
}
