package me.cubecrafter.xutils.commands.argument.provider;

import me.cubecrafter.xutils.commands.annotations.Sender;
import me.cubecrafter.xutils.commands.argument.ArgumentProvider;
import me.cubecrafter.xutils.commands.argument.CommandArgs;
import me.cubecrafter.xutils.commands.exceptions.CommandArgumentException;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.util.List;

public class PlayerSenderProvider extends ArgumentProvider<Player> {

    public PlayerSenderProvider() {
        super(Player.class, Sender.class);
    }

    @Override
    public boolean isArgumentRequired() {
        return false;
    }

    @Override
    public Player provide(CommandArgs args, List<? extends Annotation> annotations) throws CommandArgumentException {
        if (args.isPlayer()) {
            return (Player) args.getSender();
        } else {
            throw new CommandArgumentException("&cYou must be a player to use this command!");
        }
    }

}

