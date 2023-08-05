package me.cubecrafter.xutils.commands;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.xutils.text.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class CommandExecutor {

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
            } else {
                TextUtil.sendMessage(sender, CommandManager.get().getUnknownCommandMessage());
            }

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
        if (args.length > 1) {
            CommandWrapper subCommand = command.getSubCommand(args[0]);

            if (subCommand != null && subCommand.hasPermission(sender)) {
                return subCommand.tabComplete(sender, null, Arrays.copyOfRange(args, 1, args.length));
            }
        } else if (args.length == 1) {
            List<String> completions = command.getSubCommands().stream()
                    .filter(command -> command.hasPermission(sender))
                    .map(CommandWrapper::getName).collect(Collectors.toList());

            if (command.getTabCompleter() != null) {
                completions.addAll(command.getTabCompleter().apply(sender, args));
            }

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        return Collections.emptyList();
    }

}
