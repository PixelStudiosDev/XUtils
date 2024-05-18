package dev.pixelstudios.xutils.commands;

import lombok.RequiredArgsConstructor;
import dev.pixelstudios.xutils.text.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public final class CommandHandler {

    private final CommandWrapper command;

    public boolean execute(CommandSender sender, String[] args) {
        if (!command.hasPermission(sender)) {
            TextUtil.sendMessage(sender, CommandManager.get().getPermissionMessage());
            return true;
        }

        if (args.length > 0) {
            CommandWrapper subCommand = command.getSubCommand(args[0]);

            if (subCommand != null) {
                subCommand.execute(sender, null, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        if (!(sender instanceof Player) && command.isPlayerOnly()) {
            TextUtil.sendMessage(sender, CommandManager.get().getPlayerOnlyMessage());
            return true;
        }

        if (!(sender instanceof ConsoleCommandSender) && command.isConsoleOnly()) {
            TextUtil.sendMessage(sender, CommandManager.get().getConsoleOnlyMessage());
            return true;
        }

        if (command.getExecutor() != null) {
            command.getExecutor().accept(sender, args);
        }
        if (sender instanceof ConsoleCommandSender && command.getConsoleExecutor() != null) {
            command.getConsoleExecutor().accept((ConsoleCommandSender) sender, args);
        }
        if (sender instanceof Player && command.getPlayerExecutor() != null) {
            command.getPlayerExecutor().accept((Player) sender, args);
        }

        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getTabCompleter() != null) {
            List<String> commandCompletions = command.getTabCompleter().apply(sender, args);

            if (commandCompletions != null) {
                completions.addAll(commandCompletions);
            }
        }

        if (args.length > 1) {
            CommandWrapper subCommand = command.getSubCommand(args[0]);

            if (subCommand != null && subCommand.hasPermission(sender)) {
                completions.addAll(subCommand.tabComplete(sender, null, Arrays.copyOfRange(args, 1, args.length)));
            }
        } else if (args.length == 1) {
            for (CommandWrapper subCommand : command.getSubCommands()) {
                if (!subCommand.hasPermission(sender)) continue;

                completions.add(subCommand.getName());
                completions.addAll(subCommand.getAliases());
            }
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }

}
