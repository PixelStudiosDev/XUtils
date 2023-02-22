package me.cubecrafter.xutils.commands;

import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.XUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import javax.swing.event.MenuDragMouseEvent;
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

    private static final ArgumentParser parser = CommandManager.get().getParser();

    private final Map<String, CommandWrapper> commands = new HashMap<>();
    private final Object handler;
    private final Method method;

    @Setter
    private BiFunction<CommandSender, String[], List<String>> tabCompleter;

    public CommandWrapper(String name, Object handler, Method method) {
        super(name);
        this.handler = handler;
        this.method = method;

        setUsage("&cInvalid usage!");
        setPermissionMessage("&cYou don't have permission to do that!");
    }

    public void registerSub(CommandWrapper container) {
        commands.put(container.getName().toLowerCase(), container);
    }

    public CommandWrapper getSubCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public void sendUsage(CommandSender sender) {
        TextUtil.sendMessage(sender, getUsage());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (getPermission() != null && !sender.hasPermission(getPermission())) {
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
            Object[] parameters = parser.parseArguments(this, sender, Arrays.asList(args).iterator());
            if (parameters == null) return true;
            ReflectionUtil.invokeMethod(method, handler, parameters);
            return true;
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {

        if (args.length > 1) {
            CommandWrapper subCommand = getSubCommand(args[0]);
            if (subCommand != null && (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission()))) {
                return subCommand.tabComplete(sender, alias, Arrays.copyOfRange(args, 1, args.length));
            }
        } else if (args.length == 1) {
            List<String> completions = commands.values().stream().filter(subCommand -> subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())).map(Command::getName).collect(Collectors.toList());
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
