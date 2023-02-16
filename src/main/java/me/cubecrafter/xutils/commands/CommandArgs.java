package me.cubecrafter.xutils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;

public class CommandArgs {

    @Getter
    private final CommandSender sender;
    private final Iterator<String> iterator;

    public CommandArgs(CommandSender sender, String[] args) {
        this.sender = sender;
        this.iterator = Arrays.asList(args).iterator();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public String next() {
        return iterator.next();
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

}
