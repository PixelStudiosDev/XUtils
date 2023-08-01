package me.cubecrafter.xutils.commands;

import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.xutils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public final class CommandManager {

    private static CommandManager instance;

    private final Map<String, BaseCommand> commands = new HashMap<>();
    private final CommandMap commandMap = (CommandMap) ReflectionUtil.getFieldValue(Bukkit.getServer().getClass(), "commandMap", Bukkit.getServer());

    private String playerOnlyMessage = "&cThis command can be executed only by players!";
    private String permissionMessage = "&cYou don't have the permission to do this!";
    private String unknownCommandMessage = "&cUnknown command!";

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
