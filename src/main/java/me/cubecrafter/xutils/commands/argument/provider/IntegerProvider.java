package me.cubecrafter.xutils.commands.argument.provider;

import me.cubecrafter.xutils.commands.annotations.Optional;
import me.cubecrafter.xutils.commands.argument.ArgumentProvider;
import me.cubecrafter.xutils.commands.argument.CommandArgs;
import me.cubecrafter.xutils.commands.exceptions.CommandArgumentException;

import java.lang.annotation.Annotation;
import java.util.List;

public class IntegerProvider extends ArgumentProvider<Integer> {

    public IntegerProvider() {
        super(int.class);
    }

    @Override
    public boolean isArgumentRequired() {
        return true;
    }

    @Override
    public Integer provide(CommandArgs args, List<? extends Annotation> annotations) throws CommandArgumentException {
        Optional optional = annotations.stream().filter(annotation -> annotation instanceof Optional).map(annotation -> (Optional) annotation).findAny().orElse(null);
        String arg;
        if (optional != null && !args.hasNext()) {
            arg = optional.value();
        } else {
            arg = args.next();
        }
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("Invalid number: " + arg);
        }
    }

}
