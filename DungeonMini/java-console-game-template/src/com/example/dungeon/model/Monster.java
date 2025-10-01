package com.example.dungeon.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Monster extends Entity implements Serializable {
    private List<Item> drops;

    public Monster() {
        super();
        this.drops = new ArrayList<>();
    }

    public Monster(String name, int level, int attackPower) {
        super(name, level * 10 + 20, attackPower);
        this.drops = new ArrayList<>();
    }

    public void addDrop(Item item) {
        this.drops.add(item);
    }

    public List<Item> dropLoot() {
        List<Item> droppedItems = new ArrayList<>(this.drops);
        this.drops.clear();
        return droppedItems;
    }

    public List<Item> getDrops() {
        return drops;
    }

    @Override
    public boolean isAlive() {
        return getHitPoints() > 0;
    }

    @Override
    public String toString() {
        return String.format("%s (Ур. %d, HP: %d, ATK: %d)",
                getName(),
                (getHitPoints() - 20) / 10,
                getHitPoints(),
                getAttackPower());
    }
}