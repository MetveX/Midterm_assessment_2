package com.example.dungeon.model;

import java.io.Serializable;

public abstract class Item implements Serializable {
    private String name;
    private String type;

    public Item(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract void apply(Player player);

    @Override
    public String toString() {
        return name;
    }
}