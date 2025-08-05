package dev.pixelstudios.xutils.placeholder.papi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class PAPIPlaceholder {

    @Getter
    private final String id;

    private Argument[] arguments = new Argument[0];

    private BiFunction<Player, Map<String, Object>, String> parser;

    public PAPIPlaceholder arguments(Argument... arguments) {
        this.arguments = arguments;
        return this;
    }

    public PAPIPlaceholder parseString(BiFunction<Player, Map<String, Object>, String> parser) {
        this.parser = parser;
        return this;
    }

    public PAPIPlaceholder parseNumber(BiFunction<Player, Map<String, Object>, Number> parser) {
        this.parser = (player, args) -> String.valueOf(parser.apply(player, args));
        return this;
    }

    public String parse(Player player, String input) {
        if (parser == null) return null;

        Map<String, Object> args = parseArguments(input);
        return args == null ? null : parser.apply(player, args);
    }

    private Map<String, Object> parseArguments(String input) {
        if (!input.startsWith(id)) return null;

        input = input.substring(id.length());

        String[] split = Arrays.stream(input.split("_"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        Map<String, Object> arguments = new HashMap<>();

        for (int i = 0; i < this.arguments.length; i++) {
            Argument argument = this.arguments[i];

            if (argument.isRequired()) {
                if (split.length <= i) {
                    return null;
                }

                Object value = argument.getFunction().apply(split[i]);

                if (value == null) {
                    return null;
                }

                arguments.put(argument.getId(), value);
            } else {
                if (split.length <= i) {
                    break;
                }
                arguments.put(argument.getId(), argument.getFunction().apply(split[i]));
            }
        }

        return arguments;
    }

}
