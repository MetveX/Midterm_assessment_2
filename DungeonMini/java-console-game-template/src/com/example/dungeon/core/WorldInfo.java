package com.example.dungeon.core;

public class WorldInfo {

    public static void touch(String componentName) {
        System.out.println("WorldInfo: Инициализирован компонент -> " + componentName);
    }

    public static String getInfo() {
        return "DungeonMini World v1.0";
    }
}