package com.example.dungeon.model;

import java.io.Serializable;

public abstract class Entity implements Serializable {
    private String name;
    private int hitPoints;
    private int attackPower;

    public Entity() {
        this.name = "Без имени";
        this.hitPoints = 10;
        this.attackPower = 1;
    }

    public Entity(String name, int hitPoints, int attackPower) {
        this.name = name;
        this.hitPoints = hitPoints;
        this.attackPower = attackPower;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public boolean isAlive() {
        return hitPoints > 0;
    }

    @Override
    public String toString() {
        return String.format("%s (HP: %d, ATK: %d)", name, hitPoints, attackPower);
    }
}