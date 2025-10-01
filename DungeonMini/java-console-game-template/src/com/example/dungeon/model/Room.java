package com.example.dungeon.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Room implements Serializable {
    private String name;
    private String description;
    private List<Item> items;
    private Monster monster;
    private HashMap<String, Room> neighbors;
    private HashMap<String, String> lockedExits;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.items = new ArrayList<>();
        this.neighbors = new HashMap<>();
        this.lockedExits = new HashMap<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
    public Monster getMonster() { return monster; }
    public void setMonster(Monster monster) { this.monster = monster; }
    public HashMap<String, Room> getNeighbors() { return neighbors; }
    public void setNeighbors(HashMap<String, Room> neighbors) { this.neighbors = neighbors; }
    public HashMap<String, String> getLockedExits() { return lockedExits; }

    public void lockExit(String direction, String keyId) {
        lockedExits.put(direction, keyId);
    }

    public boolean isExitLocked(String direction) {
        return lockedExits.containsKey(direction);
    }

    public boolean unlockExit(String direction, Key key) {
        String requiredKeyId = lockedExits.get(direction);
        if (requiredKeyId != null && requiredKeyId.equals(key.getDoorId())) {
            lockedExits.remove(direction);
            return true;
        }
        return false;
    }

    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ").append(description).append("\n");

        if (!items.isEmpty()) {
            sb.append("Предметы: ");
            for (int i = 0; i < items.size(); i++) {
                sb.append(items.get(i).getName());
                if (i < items.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        if (monster != null) {
            sb.append("В комнате монстр: ").append(monster.getName())
                    .append(" (ур. ").append((monster.getHitPoints() - 20) / 10).append(")\n");
        }

        if (!lockedExits.isEmpty()) {
            sb.append("Запертые выходы: ").append(String.join(", ", lockedExits.keySet())).append("\n");
        }

        if (!neighbors.isEmpty()) {
            List<String> availableExits = new ArrayList<>();
            for (String exit : neighbors.keySet()) {
                if (!isExitLocked(exit)) {
                    availableExits.add(exit);
                }
            }
            if (!availableExits.isEmpty()) {
                sb.append("Открытые выходы: ").append(String.join(", ", availableExits));
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return name;
    }
}