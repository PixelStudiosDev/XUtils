package me.cubecrafter.xutils.commands;

import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.XUtils;
import me.cubecrafter.xutils.commands.argument.CommandArgs;
import me.cubecrafter.xutils.commands.exceptions.UnknownProviderException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Getter
public class CommandWrapper extends Command implements PluginIdentifiableCommand {

    private static final CommandManager manager = CommandManager.get();

    private final Map<String, CommandWrapper> subCommands = new HashMap<>();
    private final Object handler;
    private final Method method;

    @Setter
    private BiFunction<CommandSender, String[], List<String>> tabCompleter;

    public CommandWrapper(Object handler, Method method, String name, String permission) {
        super(name);
        this.handler = handler;
        this.method = method;
        setPermission(permission);
        setUsage("&cInvalid usage!");
        setPermissionMessage("&cYou don't have permission to do that!");
    }

    public CommandWrapper(Object handler, Method method, String name, String permission, String[] aliases) {
        this(handler, method, name, permission);
        setAliases(Arrays.asList(aliases));
    }

    public void registerSub(CommandWrapper container) {
        subCommands.put(container.getName().toLowerCase(), container);
    }

    public CommandWrapper getSubCommand(String name) {
        return subCommands.get(name.toLowerCase());
    }

    public void sendUsage(CommandSender sender) {
        TextUtil.sendMessage(sender, getUsage());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (!getPermission().isEmpty() && !sender.hasPermission(getPermission())) {
            return true;
        }

        if (args.length > 0) {
            CommandWrapper subCommand = getSubCommand(args[0]);
            if (subCommand != null) {
                subCommand.execute(sender, commandLabel, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        if (method != null) {
            try {
                Object[] parameters = manager.getParser().parseArguments(this, sender, new CommandArgs(sender, args));
                if (parameters == null) return true;
                ReflectionUtil.invokeMethod(method, handler, parameters);
                return true;
            } catch (UnknownProviderException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {

        if (args.length > 1) {
            CommandWrapper subCommand = getSubCommand(args[0]);
            if (subCommand != null && (subCommand.getPermission().isEmpty() || sender.hasPermission(subCommand.getPermission()))) {
                return subCommand.tabComplete(sender, alias, Arrays.copyOfRange(args, 1, args.length));
            }
        } else if (args.length == 1) {
            List<String> completions = subCommands.values().stream().filter(subCommand -> subCommand.getPermission().isEmpty() || sender.hasPermission(subCommand.getPermission())).map(Command::getName).collect(Collectors.toList());
            if (tabCompleter != null) {
                completions.addAll(tabCompleter.apply(sender, args));
            }
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        return Collections.emptyList();
    }

    @Override
    public Plugin getPlugin() {
        return XUtils.getPlugin();
    }

}
