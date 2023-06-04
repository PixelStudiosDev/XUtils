package me.cubecrafter.xutils.commands;

import me.cubecrafter.xutils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.util.HashMap;
import java.util.Map;

public final class CommandManager {

    private static CommandManager instance;

    private final Map<String, BaseCommand> commands = new HashMap<>();
    private final CommandMap commandMap = (CommandMap) ReflectionUtil.getFieldValue(Bukkit.getServer().getClass(), "commandMap", Bukkit.getServer());

    private CommandManager() {}

    public BaseCommand register(BaseCommand command) {
        commands.put(command.getName(), command);
        commandMap.register(command.getPlugin().getName(), command);
        return command;
    }

    public BaseCommand getCommand(String name) {
        return commands.get(name);
    }

    public static CommandManager get() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

}
