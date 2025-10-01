package com.example.dungeon.core;

import com.example.dungeon.model.GameState;
import java.util.List;

@FunctionalInterface
public interface Command {
    void execute(GameState state, List<String> args) throws InvalidCommandException;
}