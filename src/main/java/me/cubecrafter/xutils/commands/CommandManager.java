package me.cubecrafter.xutils.commands;

import lombok.Getter;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.XUtils;
import me.cubecrafter.xutils.commands.annotations.Command;
import me.cubecrafter.xutils.commands.annotations.SubCommand;
import me.cubecrafter.xutils.commands.annotations.Usage;
import me.cubecrafter.xutils.commands.argument.ArgumentParser;
import me.cubecrafter.xutils.commands.argument.ArgumentProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public final class CommandManager {

    private static CommandManager instance;

    @Getter
    private final ArgumentParser parser = new ArgumentParser();
    private final CommandMap commandMap = (CommandMap) ReflectionUtil.getFieldValue(Bukkit.getServer().getClass(), "commandMap", Bukkit.getServer());
    private final Map<String, CommandWrapper> commands = new HashMap<>();

    private CommandManager() {}

    public CommandManager registerCommand(Object handler) {
        Class<?> clazz = handler.getClass();
        if (!clazz.isAnnotationPresent(Command.class)) {
            return null;
        }
        Command command = clazz.getAnnotation(Command.class);
        Method baseMethod = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(SubCommand.class))
                .filter(m -> m.getAnnotation(SubCommand.class).name().isEmpty())
                .findAny().orElse(null);
        CommandWrapper container = new CommandWrapper(handler, baseMethod, command.name(), command.permission(), command.aliases());

        commands.put(command.name().toLowerCase(), container);

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubCommand.class)) continue;
            SubCommand subCommand = method.getAnnotation(SubCommand.class);
            CommandWrapper wrapper = new CommandWrapper(handler, method, subCommand.name(), subCommand.permission());
            if (method.isAnnotationPresent(Usage.class)) {
                wrapper.setUsage(method.getAnnotation(Usage.class).value());
            }
            container.registerSub(wrapper);
        }

        if (commandMap.getCommand(command.name()) != null) {
            commandMap.getCommand(command.name()).unregister(commandMap);
        }
        for (String alias : command.aliases()) {
            if (commandMap.getCommand(alias) != null) {
                commandMap.getCommand(alias).unregister(commandMap);
            }
        }
        commandMap.register(XUtils.getPlugin().getName().toLowerCase(), container);

        return this;
    }

    public CommandManager registerCommands(Object... handlers) {
        for (Object handler : handlers) {
            registerCommand(handler);
        }
        return this;
    }

    public CommandManager registerTabCompleter(String command, BiFunction<CommandSender, String[], List<String>> tabCompleter) {
        CommandWrapper container = getCommand(command);
        if (container != null) {
            container.setTabCompleter(tabCompleter);
        }
        return this;
    }

    public void registerProvider(ArgumentProvider<?> provider) {
        parser.getProviders().add(provider);
    }

    public CommandWrapper getCommand(String command) {
        String[] split = command.split(" ");
        CommandWrapper container = commands.get(split[0].toLowerCase());
        if (container == null) return null;
        for (int i = 1; i < split.length; i++) {
            CommandWrapper subCommand = container.getSubCommand(split[i]);
            if (subCommand == null) return container;
            container = subCommand;
        }
        return container;
    }

    public static CommandManager get() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

}
