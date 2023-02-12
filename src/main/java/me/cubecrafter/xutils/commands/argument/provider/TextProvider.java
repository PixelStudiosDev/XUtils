package me.cubecrafter.xutils.commands.argument.provider;

import me.cubecrafter.xutils.commands.annotations.Optional;
import me.cubecrafter.xutils.commands.annotations.Text;
import me.cubecrafter.xutils.commands.argument.ArgumentProvider;
import me.cubecrafter.xutils.commands.argument.CommandArgs;

import java.lang.annotation.Annotation;
import java.util.List;

public class TextProvider extends ArgumentProvider<String> {

    public TextProvider() {
        super(String.class, Text.class);
    }

    @Override
    public boolean isArgumentRequired() {
        return true;
    }

    @Override
    public String provide(CommandArgs args, List<? extends Annotation> annotations) {
        Optional optional = annotations.stream().filter(annotation -> annotation instanceof Optional).map(annotation -> (Optional) annotation).findAny().orElse(null);
        if (optional != null && !args.hasNext()) {
            return optional.value();
        }
        StringBuilder builder = new StringBuilder();
        while (args.hasNext()) {
            builder.append(args.next()).append(" ");
        }
        return builder.toString().trim();
    }

}

