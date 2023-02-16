package me.cubecrafter.xutils.commands;

import lombok.Getter;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.XUtils;
import me.cubecrafter.xutils.commands.annotations.Command;
import me.cubecrafter.xutils.commands.annotations.Default;
import me.cubecrafter.xutils.commands.annotations.Permission;
import me.cubecrafter.xutils.commands.annotations.SubCommand;
import me.cubecrafter.xutils.commands.annotations.Usage;
import me.cubecrafter.xutils.objects.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

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
            return this;
        }

        Command command = clazz.getAnnotation(Command.class);
        Method defaultMethod = Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(Default.class)).findAny().orElse(null);

        CommandWrapper container = new CommandWrapper(command.name(), handler, defaultMethod);
        container.setAliases(Arrays.asList(command.aliases()));

        if (defaultMethod != null) {
            if (defaultMethod.isAnnotationPresent(Usage.class)) {
                container.setUsage(defaultMethod.getAnnotation(Usage.class).value());
            }
            if (defaultMethod.isAnnotationPresent(Permission.class)) {
                container.setPermission(defaultMethod.getAnnotation(Permission.class).value());
            }
        }

        commands.put(command.name().toLowerCase(), container);

        Map<Integer, List<Pair<String, CommandWrapper>>> subCommands = new TreeMap<>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubCommand.class)) continue;
            SubCommand subCommand = method.getAnnotation(SubCommand.class);

            String[] split = subCommand.value().toLowerCase().split(" ");
            String commandName = split[split.length - 1];

            CommandWrapper wrapper = new CommandWrapper(commandName, handler, method);
            if (method.isAnnotationPresent(Usage.class)) {
                wrapper.setUsage(method.getAnnotation(Usage.class).value());
            }
            if (method.isAnnotationPresent(Permission.class)) {
                wrapper.setPermission(method.getAnnotation(Permission.class).value());
            }

            List<Pair<String, CommandWrapper>> list = subCommands.getOrDefault(split.length, new ArrayList<>());
            list.add(new Pair<>(subCommand.value(), wrapper));
            subCommands.put(split.length, list);
        }

        for (List<Pair<String, CommandWrapper>> list : subCommands.values()) {
            for (Pair<String, CommandWrapper> pair : list) {
                String commandPath = pair.getFirst();
                if (!commandPath.contains(" ")) {
                    container.registerSub(pair.getSecond());
                    continue;
                }
                CommandWrapper parent = getCommand(command.name() + " " + commandPath.substring(0, commandPath.lastIndexOf(" ")));
                if (parent != null) {
                    parent.registerSub(pair.getSecond());
                } else {
                    TextUtil.error("Could not register sub command '" + commandPath + "' to the command '" + command.name() + "'");
                }
            }
        }
        /*
        if (commandMap.getCommand(command.name()) != null) {
            commandMap.getCommand(command.name()).unregister(commandMap);
        }
        for (String alias : command.aliases()) {
            if (commandMap.getCommand(alias) != null) {
                commandMap.getCommand(alias).unregister(commandMap);
            }
        }
        */
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

    public <T> CommandManager registerProvider(Class<T> type, Function<String, T> provider) {
        parser.getProviders().put(type, provider);
        return this;
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
