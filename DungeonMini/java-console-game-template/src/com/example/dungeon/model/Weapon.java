package com.example.dungeon.model;

public class Weapon extends Item {
    private int attackBonus;

    public Weapon(String name, int attackBonus) {
        super(name, "Weapon");
        this.attackBonus = attackBonus;
    }

    @Override
    public void apply(Player player) {
        int currentAttack = player.getAttackPower();
        int newAttack = currentAttack + attackBonus;
        player.setAttackPower(newAttack);

        System.out.println("Экипировано оружие: +" + attackBonus + " к атаке. " +
                "Текущая атака: " + newAttack);
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    @Override
    public String toString() {
        return String.format("%s (+%d к атаке)", getName(), attackBonus);
    }
}