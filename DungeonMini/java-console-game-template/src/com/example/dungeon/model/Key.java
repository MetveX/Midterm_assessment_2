package com.example.dungeon.model;

public class Key extends Item {
    private String doorId;

    public Key(String name, String doorId) {
        super(name, "Key");
        this.doorId = doorId;
    }

    public String getDoorId() {
        return doorId;
    }

    @Override
    public void apply(Player player) {
        System.out.println("Это ключ от какой-то двери. Используйте команду 'use <название ключа>'.");
    }
}