package it.unicam.cs.mpgc.rpg123891.model.character;

import java.io.Serial;
import java.util.Random;

/**
 * Guerriero: alta vita e difesa, attacco solido.
 * Stat base: HP 120, ATK 22, DEF 8, AGI 4, STA 8, CRIT 15%
 *
 * Blocco passivo (GAME_SPEC):
 *   20% chance per attacco fisico ricevuto.
 *   Il contatore (blockStreak) si incrementa ad ogni attacco fisico NON bloccato.
 *   Al 5o attacco senza blocco il blocco e' garantito.
 *   Il contatore si azzera ogni volta che il blocco si attiva.
 *   NON si azzera al cambio wave.
 */
public class Warrior extends GameCharacter implements PlayerCharacter {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final double BLOCK_CHANCE    = 0.20;
    private static final int    BLOCK_GUARANTEE = 5;

    /** Numero di attacchi fisici consecutivi NON bloccati. */
    private int blockStreak = 0;

    public Warrior(String name) {
        super(name, 120, 22, 8, 4, 8, 0.15);
    }

    @Override
    public void applyPassiveBonus() {
        restoreStamina(2);
    }

    /**
     * Testa se il blocco si attiva per l'attacco fisico corrente.
     * Aggiorna il contatore blockStreak di conseguenza.
     * Usa il Random iniettato da CombatSystem per garantire determinismo nei test.
     *
     * @param random il generatore casuale passato da CombatSystem
     * @return true se l'attacco viene bloccato
     */
    public boolean testBlock(Random random) {
        if (blockStreak >= BLOCK_GUARANTEE - 1) {
            blockStreak = 0;
            return true;
        }
        if (random.nextDouble() < BLOCK_CHANCE) {
            blockStreak = 0;
            return true;
        }
        blockStreak++;
        return false;
    }

    public double getBlockChance()  { return BLOCK_CHANCE; }
    public int    getBlockStreak()  { return blockStreak; }

    @Override
    public CharacterClass getCharacterClass() { return CharacterClass.WARRIOR; }
}
