package com.example.dungeon.core;

import com.example.dungeon.model.GameState;
import java.io.*;

public class SaveLoad {

    public static void save(GameState state) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("save.dat"))) {
            oos.writeObject(state);
            System.out.println("Игра сохранена.");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения игры: " + e.getMessage(), e);
        }
    }

    public static GameState load(GameState ctx) {
        GameState loadedState = null;
        File saveFile = new File("save.dat");

        if (!saveFile.exists()) {
            throw new RuntimeException("Файл сохранения не найден.");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            loadedState = (GameState) ois.readObject();
            System.out.println("Игра загружена.");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка загрузки игры: " + e.getMessage(), e);
        }
        return loadedState;
    }

    public static void saveScore(String playerName, int score) {
        try (FileWriter fw = new FileWriter("scores.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(playerName + "," + score);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения счета: " + e.getMessage());
        }
    }

    public static void printScores() {
        File scoresFile = new File("scores.csv");
        if (!scoresFile.exists()) {
            System.out.println("Таблица лидеров пуста.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(scoresFile))) {
            System.out.println("Таблица лидеров:");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    System.out.println("Игрок: " + parts[0] + " | Счет: " + parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения таблицы лидеров: " + e.getMessage());
        }
    }
}