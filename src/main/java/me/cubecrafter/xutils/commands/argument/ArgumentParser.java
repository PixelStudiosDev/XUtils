package me.cubecrafter.xutils.commands.argument;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.xutils.commands.CommandWrapper;
import me.cubecrafter.xutils.commands.annotations.Optional;
import me.cubecrafter.xutils.commands.argument.provider.BooleanProvider;
import me.cubecrafter.xutils.commands.argument.provider.CommandSenderProvider;
import me.cubecrafter.xutils.commands.argument.provider.DoubleProvider;
import me.cubecrafter.xutils.commands.argument.provider.FloatProvider;
import me.cubecrafter.xutils.commands.argument.provider.IntegerProvider;
import me.cubecrafter.xutils.commands.argument.provider.PlayerProvider;
import me.cubecrafter.xutils.commands.argument.provider.PlayerSenderProvider;
import me.cubecrafter.xutils.commands.argument.provider.StringProvider;
import me.cubecrafter.xutils.commands.argument.provider.TextProvider;
import me.cubecrafter.xutils.commands.exceptions.CommandArgumentException;
import me.cubecrafter.xutils.commands.exceptions.UnknownProviderException;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ArgumentParser {

    @Getter
    private final List<ArgumentProvider<?>> providers = Arrays.asList(
            new BooleanProvider(),
            new CommandSenderProvider(),
            new DoubleProvider(),
            new FloatProvider(),
            new IntegerProvider(),
            new PlayerProvider(),
            new PlayerSenderProvider(),
            new StringProvider(),
            new TextProvider()
    );

    public Object[] parseArguments(CommandWrapper command, CommandSender sender, CommandArgs args) throws UnknownProviderException {

        Method method = command.getMethod();

        Object[] parameters = new Object[method.getParameterCount()];

        for (int i = 0; i < method.getParameterCount(); i++) {

            Parameter parameter = method.getParameters()[i];
            Class<?> type = parameter.getType();

            Set<Class<? extends Annotation>> annotations = Arrays.stream(parameter.getAnnotations()).map(Annotation::annotationType).collect(Collectors.toSet());
            ArgumentProvider<?> provider = getProvider(type, annotations);

            if (provider == null) {
                throw new UnknownProviderException(type);
            }

            if (provider.isArgumentRequired() && !args.hasNext() && !annotations.contains(Optional.class)) {
                command.sendUsage(sender);
                return null;
            }

            try {
                parameters[i] = provider.provide(args, Arrays.asList(parameter.getAnnotations()));
            } catch (CommandArgumentException e) {
                command.sendUsage(sender);
                return null;
            }

        }

        return parameters;
    }

    private static final Set<Class<? extends Annotation>> IGNORED_ANNOTATIONS = new HashSet<>(Collections.singletonList(Optional.class));

    public ArgumentProvider<?> getProvider(Class<?> clazz, Set<Class<? extends Annotation>> annotations) {
        for (ArgumentProvider<?> provider : providers) {
            if (provider.getType() != clazz) continue;
            if (annotations.stream().allMatch(annotation -> provider.getAnnotations().contains(annotation) || IGNORED_ANNOTATIONS.contains(annotation))) {
                return provider;
            }
        }
        return null;
    }

}
