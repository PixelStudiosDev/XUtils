package me.cubecrafter.xutils.commands.argument.provider;

import me.cubecrafter.xutils.commands.annotations.Optional;
import me.cubecrafter.xutils.commands.argument.ArgumentProvider;
import me.cubecrafter.xutils.commands.argument.CommandArgs;
import me.cubecrafter.xutils.commands.exceptions.CommandArgumentException;

import java.lang.annotation.Annotation;
import java.util.List;

public class BooleanProvider extends ArgumentProvider<Boolean> {

    public BooleanProvider() {
        super(boolean.class);
    }

    @Override
    public boolean isArgumentRequired() {
        return true;
    }

    @Override
    public Boolean provide(CommandArgs args, List<? extends Annotation> annotations) throws CommandArgumentException {
        Optional optional = annotations.stream().filter(annotation -> annotation instanceof Optional).map(annotation -> (Optional) annotation).findAny().orElse(null);
        String arg;
        if (optional != null && !args.hasNext()) {
            arg = optional.value();
        } else {
            arg = args.next().toLowerCase();
        }
        if (arg.equals("true") || arg.equals("false")) {
            return Boolean.parseBoolean(arg);
        } else {
            throw new CommandArgumentException("You must specify a boolean value!");
        }
    }

}
