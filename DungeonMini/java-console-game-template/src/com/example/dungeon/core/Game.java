package com.example.dungeon.core;

import com.example.dungeon.model.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final GameState state = new GameState();
    private final Map<String, Command> commands = new LinkedHashMap<>();

    static {
        WorldInfo.touch("Game");
    }

    public Game() {
        registerCommands();
        bootstrapWorld();
    }

    private void registerCommands() {
        commands.put("help", (ctx, a) -> System.out.println("Команды: " + String.join(", ", commands.keySet())));
        commands.put("gc-stats", (ctx, a) -> {
            Runtime rt = Runtime.getRuntime();
            long free = rt.freeMemory(), total = rt.totalMemory(), used = total - free;
            System.out.println("Память: used=" + used + " free=" + free + " total=" + total);
        });
        commands.put("look", (ctx, a) -> System.out.println(ctx.getCurrent().describe()));

        commands.put("move", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите направление: move <north|south|east|west>");
            }
            String direction = a.get(0).toLowerCase(Locale.ROOT);
            Room currentRoom = ctx.getCurrent();
            Room nextRoom = currentRoom.getNeighbors().get(direction);

            if (nextRoom == null) {
                throw new InvalidCommandException("Нет пути в направлении: " + direction);
            }

            if (currentRoom.isExitLocked(direction)) {
                throw new InvalidCommandException("Выход в направлении " + direction + " заперт. Найдите ключ чтобы открыть его.");
            }

            ctx.setCurrent(nextRoom);
            System.out.println("Вы перешли в: " + nextRoom.getName());
            System.out.println(nextRoom.describe());
        });

        commands.put("take", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите предмет: take <название>");
            }
            String itemName = String.join(" ", a);
            Room currentRoom = ctx.getCurrent();
            Player player = ctx.getPlayer();

            Item foundItem = null;
            for (Item item : currentRoom.getItems()) {
                if (item.getName().equalsIgnoreCase(itemName)) {
                    foundItem = item;
                    break;
                }
            }

            if (foundItem == null) {
                throw new InvalidCommandException("Предмет не найден: " + itemName);
            }

            currentRoom.getItems().remove(foundItem);
            player.getInventory().add(foundItem);
            System.out.println("Взято: " + foundItem.getName());
        });

        commands.put("inventory", (ctx, a) -> {
            Player player = ctx.getPlayer();
            List<Item> inventory = player.getInventory();

            if (inventory.isEmpty()) {
                System.out.println("Инвентарь пуст.");
                return;
            }

            System.out.println("Ваш инвентарь:");
            inventory.stream()
                    .collect(Collectors.groupingBy(Item::getType, TreeMap::new, Collectors.toList()))
                    .forEach((type, items) -> {
                        System.out.println("- " + type + " (" + items.size() + "): " +
                                items.stream()
                                        .map(Item::getName)
                                        .sorted()
                                        .collect(Collectors.joining(", ")));
                    });
        });

        commands.put("use", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите предмет: use <название>");
            }
            String itemName = String.join(" ", a);
            Player player = ctx.getPlayer();
            List<Item> inventory = player.getInventory();
            Room currentRoom = ctx.getCurrent();

            Item foundItem = null;
            for (Item item : inventory) {
                if (item.getName().equalsIgnoreCase(itemName)) {
                    foundItem = item;
                    break;
                }
            }

            if (foundItem == null) {
                throw new InvalidCommandException("В инвентаре нет предмета: " + itemName);
            }

            if (foundItem instanceof Key) {
                Key key = (Key) foundItem;
                boolean doorUnlocked = false;

                for (String direction : currentRoom.getNeighbors().keySet()) {
                    if (currentRoom.isExitLocked(direction)) {
                        if (currentRoom.unlockExit(direction, key)) {
                            System.out.println("Щелк! Вы использовали " + key.getName() +
                                    " чтобы открыть дверь на " + direction + ".");
                            inventory.remove(foundItem);
                            doorUnlocked = true;
                            break;
                        }
                    }
                }

                if (!doorUnlocked) {
                    throw new InvalidCommandException("Ключ " + key.getName() +
                            " не подходит ни к одной двери в этой комнате.");
                }
            } else {
                foundItem.apply(player);
                System.out.println("Использован: " + foundItem.getName());
                inventory.remove(foundItem);
            }
        });

        commands.put("fight", (ctx, a) -> {
            Room currentRoom = ctx.getCurrent();
            Monster monster = currentRoom.getMonster();
            Player player = ctx.getPlayer();

            if (monster == null) {
                throw new InvalidCommandException("В этой комнате нет монстров");
            }

            System.out.println("Начинается бой с " + monster.getName());

            while (player.getHitPoints() > 0 && monster.getHitPoints() > 0) {
                int playerDamage = player.getAttackPower();
                monster.setHitPoints(monster.getHitPoints() - playerDamage);
                System.out.println("Вы бьёте " + monster.getName() + " на " +
                        playerDamage + ". HP монстра: " + Math.max(0, monster.getHitPoints()));

                if (monster.getHitPoints() <= 0) {
                    System.out.println("Победа! " + monster.getName() + " повержен!");
                    List<Item> loot = monster.dropLoot();
                    if (!loot.isEmpty()) {
                        currentRoom.getItems().addAll(loot);
                        System.out.println("Монстр выбросил: " +
                                loot.stream()
                                        .map(Item::getName)
                                        .collect(Collectors.joining(", ")));
                    }

                    currentRoom.setMonster(null);
                    break;
                }

                int monsterDamage = monster.getAttackPower();
                player.setHitPoints(player.getHitPoints() - monsterDamage);
                System.out.println(monster.getName() + " бьёт вас на " +
                        monsterDamage + ". Ваше HP: " + Math.max(0, player.getHitPoints()));

                if (player.getHitPoints() <= 0) {
                    System.out.println("Вы погибли! Игра окончена.");
                    System.exit(0);
                }
            }
        });

        commands.put("save", (ctx, a) -> SaveLoad.save(ctx));
        commands.put("load", (ctx, a) -> {
            GameState loadedState = SaveLoad.load(ctx);
            state.setPlayer(loadedState.getPlayer());
            state.setCurrent(loadedState.getCurrent());
            state.setScore(loadedState.getScore());
            System.out.println("Игра загружена. Текущая комната: " + state.getCurrent().getName());
        });
        commands.put("scores", (ctx, a) -> SaveLoad.printScores());
        commands.put("exit", (ctx, a) -> {
            // Сохраняем счет перед выходом
            SaveLoad.saveScore(ctx.getPlayer().getName(), ctx.getScore());
            System.out.println("Ваш счет сохранен. Пока!");
            System.exit(0);
        });
    }

    private void bootstrapWorld() {
        Player hero = new Player("Герой", 30, 5);
        state.setPlayer(hero);

        Room square = new Room("Площадь", "Каменная площадь с фонтаном. Сердце города.");
        Room forest = new Room("Лес", "Густой лес. Шелест листвы навевает спокойствие.");
        Room cave = new Room("Пещера", "Вход в пещеру. Из глубины доносится эхо.");
        Room deepCave = new Room("Глубокие пещеры", "Мрачные тоннели, подсвечиваемые грибами.");
        Room abandonedHouse = new Room("Заброшенный дом", "Дом, полный пыли и старых вещей.");
        Room basement = new Room("Подвал", "Темное и сырое помещение.");

        square.getNeighbors().put("north", forest);
        forest.getNeighbors().put("south", square);
        forest.getNeighbors().put("east", cave);
        forest.getNeighbors().put("west", abandonedHouse);
        cave.getNeighbors().put("west", forest);
        cave.getNeighbors().put("east", deepCave);
        deepCave.getNeighbors().put("west", cave);
        abandonedHouse.getNeighbors().put("east", forest);
        abandonedHouse.getNeighbors().put("down", basement);
        basement.getNeighbors().put("up", abandonedHouse);

        forest.lockExit("west", "house_front_door_lock");
        Key houseKey = new Key("Ржавый ключ", "house_front_door_lock");
        cave.getItems().add(houseKey);

        cave.lockExit("east", "deep_cave_lock");
        Key deepCaveKey = new Key("Каменный ключ", "deep_cave_lock");
        basement.getItems().add(deepCaveKey);

        forest.getItems().add(new Potion("Малое зелье здоровья", 10));
        Monster wolf = new Monster("Волк", 1, 8);
        wolf.addDrop(new Potion("Малое зелье здоровья", 10));
        forest.setMonster(wolf);

        deepCave.getItems().add(new Weapon("Зачарованный меч", 10));
        Monster troll = new Monster("Пещерный тролль", 3, 10);
        troll.addDrop(new Potion("Большое зелье здоровья", 10));
        deepCave.setMonster(troll);

        basement.getItems().add(new Potion("Большое зелье здоровья", 20));

        state.setCurrent(square);
        hero.setCurrentRoom(square);
    }

    public void run() {
        System.out.println("DungeonMini (TEMPLATE). 'help' — команды.");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("> ");
                String line = in.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> parts = Arrays.asList(line.split("\\s+"));
                String cmd = parts.getFirst().toLowerCase(Locale.ROOT);
                List<String> args = parts.subList(1, parts.size());
                Command c = commands.get(cmd);
                try {
                    if (c == null) throw new InvalidCommandException("Неизвестная команда: " + cmd);
                    c.execute(state, args);
                    state.addScore(1);
                } catch (InvalidCommandException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Непредвиденная ошибка: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода: " + e.getMessage());
        }
    }
}