package me.cubecrafter.xutils.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public interface SubCommand {

    String getLabel();
    void execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    default String getPermission() {
        return null;
    }

    default String getDescription() {
        return "";
    }

    default boolean isPlayerOnly() {
        return false;
    }

}
