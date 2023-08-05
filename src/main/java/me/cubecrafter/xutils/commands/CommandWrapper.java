package me.cubecrafter.xutils.commands;

import lombok.Getter;
import me.cubecrafter.xutils.XUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Getter
public final class CommandWrapper extends Command implements PluginIdentifiableCommand {

    private final CommandHandler commandHandler = new CommandHandler(this);

    private final Map<String, CommandWrapper> subCommands = new HashMap<>();
    private final Map<String, CommandWrapper> commandAliases = new HashMap<>();

    private final BiConsumer<CommandSender, String[]> executor;
    private final BiConsumer<ConsoleCommandSender, String[]> consoleExecutor;
    private final BiConsumer<Player, String[]> playerExecutor;
    private final BiFunction<CommandSender, String[], List<String>> tabCompleter;

    CommandWrapper(
            String name,
            String permission,
            String description,
            List<String> aliases,
            BiConsumer<CommandSender, String[]> executor,
            BiConsumer<ConsoleCommandSender, String[]> consoleExecutor,
            BiConsumer<Player, String[]> playerExecutor,
            BiFunction<CommandSender, String[], List<String>> tabCompleter,
            List<CommandWrapper> subCommands
    ) {
        super(name.toLowerCase());

        setPermission(permission);
        setDescription(description);
        setAliases(aliases);

        this.executor = executor;
        this.consoleExecutor = consoleExecutor;
        this.playerExecutor = playerExecutor;
        this.tabCompleter = tabCompleter;

        for (CommandWrapper command : subCommands) {
            this.subCommands.put(command.getName(), command);

            for (String alias : command.getAliases()) {
                this.commandAliases.put(alias.toLowerCase(), command);
            }
        }
    }

    public CommandWrapper getSubCommand(String name) {
        CommandWrapper command = this.subCommands.get(name.toLowerCase());
        if (command == null) {
            command = this.commandAliases.get(name.toLowerCase());
        }
        return command;
    }

    public Collection<CommandWrapper> getSubCommands() {
        return this.subCommands.values();
    }

    public boolean hasPermission(CommandSender sender) {
        return getPermission() == null || sender.hasPermission(getPermission());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return this.commandHandler.execute(sender, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return this.commandHandler.tabComplete(sender, args);
    }

    @Override
    public Plugin getPlugin() {
        return XUtils.getPlugin();
    }

}
