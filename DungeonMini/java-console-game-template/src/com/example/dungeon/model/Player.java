package com.example.dungeon.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player extends Entity implements Serializable {
    private List<Item> inventory;
    private Room currentRoom;

    public Player() {
        super();
        this.inventory = new ArrayList<>();
    }

    public Player(String name, int hitPoints, int attackPower) {
        super(name, hitPoints, attackPower);
        this.inventory = new ArrayList<>();
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public boolean removeItem(Item item) {
        return inventory.remove(item);
    }

    public Item findItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    @Override
    public boolean isAlive() {
        return getHitPoints() > 0;
    }

    @Override
    public String toString() {
        return String.format("%s (HP: %d/%d, ATK: %d)",
                getName(), getHitPoints(), getHitPoints(), getAttackPower());
    }
}