package it.unicam.cs.mpgc.rpg123891.model.combat;

import it.unicam.cs.mpgc.rpg123891.controller.CombatController;
import it.unicam.cs.mpgc.rpg123891.controller.GameController;
import it.unicam.cs.mpgc.rpg123891.model.character.Warrior;
import it.unicam.cs.mpgc.rpg123891.model.world.DungeonMap;
import it.unicam.cs.mpgc.rpg123891.persistence.JsonPersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StunAndImmunityTest {

    private GameController gc;
    private CombatController cc;
    private Warrior player;

    @BeforeEach
    void setUp() {
        player = new Warrior("G");
        gc = new GameController(new JsonPersistenceManager());
        gc.startNewGame(player);
        DungeonMap map = gc.getGameState().getDungeonMap();
        cc = new CombatController(gc, map);
    }

    @Test
    void stun_clearStun_cycle() {
        Enemy e = new Enemy("E", 30, 8, 2, AttackType.PHYSICAL, 0.0);
        assertFalse(e.isStunned());
        e.stun();
        assertTrue(e.isStunned());
        e.clearStun();
        assertFalse(e.isStunned());
    }

    @Test
    void stunnedEnemy_flagBehavior() {
        Enemy goblin = EnemyFactory.createGoblin();
        goblin.stun();
        assertTrue(goblin.isStunned());
        goblin.clearStun();
        assertFalse(goblin.isStunned());
    }

    @Test
    void immunity_preventsFlag() {
        Enemy e = new Enemy("E", 30, 8, 2, AttackType.PHYSICAL, 0.0);
        assertFalse(e.isImmune());
        e.setImmune(true);
        assertTrue(e.isImmune());
    }

    @Test
    void immuneEnemy_canStillTakeDamage() {
        Enemy e = new Enemy("E", 30, 8, 2, AttackType.PHYSICAL, 0.0);
        e.setImmune(true);
        e.takeDamage(20); // 20-2=18
        assertEquals(12, e.getCurrentHp());
    }

    @Test
    void enemy_deadAfterLethalDamage() {
        Enemy e = new Enemy("E", 10, 5, 0, AttackType.PHYSICAL, 0.0);
        e.takeDamage(999);
        assertFalse(e.isAlive());
        assertEquals(0, e.getCurrentHp());
    }
}
