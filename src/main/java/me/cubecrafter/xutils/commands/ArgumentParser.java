package me.cubecrafter.xutils.commands;

import lombok.Getter;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.commands.annotations.Optional;
import me.cubecrafter.xutils.commands.annotations.Sender;
import me.cubecrafter.xutils.commands.annotations.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class ArgumentParser {

    @Getter
    private final Map<Class<?>, Function<String, ?>> providers = new HashMap<>();

    public ArgumentParser() {
        providers.put(boolean.class, arg -> {
            arg = arg.toLowerCase();
            if (arg.equals("true") || arg.equals("false")) {
                return Boolean.parseBoolean(arg);
            }
            return null;
        });
        providers.put(int.class, arg -> {
            try {
                return Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                return null;
            }
        });
        providers.put(float.class, arg -> {
            try {
                return Float.parseFloat(arg);
            } catch (NumberFormatException e) {
                return null;
            }
        });
        providers.put(long.class, arg -> {
            try {
                return Long.parseLong(arg);
            } catch (NumberFormatException e) {
                return null;
            }
        });
        providers.put(String.class, arg -> arg);
        providers.put(Player.class, Bukkit::getPlayer);
    }

    public Object[] parseArguments(CommandWrapper command, CommandSender sender, Iterator<String> args) {
        Method method = command.getMethod();
        Object[] parameters = new Object[method.getParameterCount()];

        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = method.getParameters()[i];
            Class<?> type = parameter.getType();

            if (parameter.isAnnotationPresent(Sender.class)) {
                if (type == CommandSender.class) {
                    parameters[i] = sender;
                } else if (type == Player.class) {
                    if (sender instanceof Player) {
                        parameters[i] = sender;
                    } else {
                        TextUtil.info("You must be a player to use this command!");
                        return null;
                    }
                }
                continue;
            }

            if (parameter.isAnnotationPresent(Text.class)) {
                StringBuilder builder = new StringBuilder();
                while (args.hasNext()) {
                    builder.append(args.next()).append(" ");
                }
                parameters[i] = builder.toString().trim();
                continue;
            }

            if (type.isEnum()) {
                Object[] constants = type.getEnumConstants();
                for (Object constant : constants) {
                    if (constant.toString().equalsIgnoreCase(args.next())) {
                        parameters[i] = constant;
                    }
                }
                continue;
            }

            Function<String, ?> provider = providers.get(type);
            if (provider == null) {
                TextUtil.error("Unknown argument provider for class " + type.getName() + "!");
                return null;
            }

            String arg = null;
            if (!args.hasNext()) {
                if (parameter.isAnnotationPresent(Optional.class)) {
                    arg = parameter.getAnnotation(Optional.class).value();
                    if (arg.isEmpty()) {
                        parameters[i] = null;
                        continue;
                    }
                } else {
                    command.sendUsage(sender);
                    return null;
                }
            }

            Object value = provider.apply(arg != null ? arg : args.next());
            if (value == null) {
                command.sendUsage(sender);
                return null;
            }
            parameters[i] = value;
        }
        return parameters;
    }

}
