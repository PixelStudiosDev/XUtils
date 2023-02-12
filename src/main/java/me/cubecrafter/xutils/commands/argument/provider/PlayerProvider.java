package me.cubecrafter.xutils.commands.argument.provider;

import me.cubecrafter.xutils.commands.annotations.Optional;
import me.cubecrafter.xutils.commands.argument.ArgumentProvider;
import me.cubecrafter.xutils.commands.argument.CommandArgs;
import me.cubecrafter.xutils.commands.exceptions.CommandArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.util.List;

public class PlayerProvider extends ArgumentProvider<Player> {

    public PlayerProvider() {
        super(Player.class);
    }

    @Override
    public boolean isArgumentRequired() {
        return true;
    }

    @Override
    public Player provide(CommandArgs args, List<? extends Annotation> annotations) throws CommandArgumentException {
        Optional optional = annotations.stream().filter(annotation -> annotation instanceof Optional).map(annotation -> (Optional) annotation).findAny().orElse(null);
        String arg;
        if (optional != null && !args.hasNext()) {
            arg = optional.value();
        } else {
            arg = args.next();
        }
        Player player = Bukkit.getPlayer(arg);
        if (player == null) {
            throw new CommandArgumentException("Player not found: " + arg);
        }
        return player;
    }

}
