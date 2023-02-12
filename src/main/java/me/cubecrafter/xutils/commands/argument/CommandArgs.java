package me.cubecrafter.xutils.commands.argument;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;

public class CommandArgs {

    @Getter
    private final CommandSender sender;
    private final String[] args;
    private final Iterator<String> iterator;

    public CommandArgs(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
        this.iterator = Arrays.asList(args).iterator();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public String next() {
        return iterator.next();
    }

    public int count() {
        return args.length;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

}
