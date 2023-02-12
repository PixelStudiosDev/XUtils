package me.cubecrafter.xutils.commands.argument.provider;

import me.cubecrafter.xutils.commands.annotations.Sender;
import me.cubecrafter.xutils.commands.argument.ArgumentProvider;
import me.cubecrafter.xutils.commands.argument.CommandArgs;
import me.cubecrafter.xutils.commands.exceptions.CommandArgumentException;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.util.List;

public class CommandSenderProvider extends ArgumentProvider<CommandSender> {

    public CommandSenderProvider() {
        super(CommandSender.class, Sender.class);
    }

    @Override
    public boolean isArgumentRequired() {
        return false;
    }

    @Override
    public CommandSender provide(CommandArgs args, List<? extends Annotation> annotations) throws CommandArgumentException {
        return args.getSender();
    }

}

