package com.example.dungeon.model;

public class Potion extends Item {
    private int healAmount;

    public Potion(String name, int healAmount) {
        super(name, "Potion");
        this.healAmount = healAmount;
    }

    @Override
    public void apply(Player player) {
        int currentHp = player.getHitPoints();
        int newHp = currentHp + healAmount;
        player.setHitPoints(newHp);

        System.out.println("Выпито зелье: +" + healAmount + " HP. " +
                "Текущее HP: " + newHp);
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(int healAmount) {
        this.healAmount = healAmount;
    }

    @Override
    public String toString() {
        return String.format("%s (Восстанавливает %d HP)", getName(), healAmount);
    }
}