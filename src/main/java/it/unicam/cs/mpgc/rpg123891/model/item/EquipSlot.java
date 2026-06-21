package it.unicam.cs.mpgc.rpg123891.model.item;

/**
 * Slot di equipaggiamento disponibili per un personaggio.
 *
 *   MAIN_HAND : arma principale (Sword, MagicStaff, DualDaggers, Greatsword)
 *   OFF_HAND  : mano sinistra (Shield); bloccato se MAIN_HAND ha un'arma a 2 mani
 *   BODY      : corpo (Armor o MagicAmulet, si escludono a vicenda)
 */
public enum EquipSlot {
    MAIN_HAND,
    OFF_HAND,
    BODY
}
