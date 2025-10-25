package dev.pixelstudios.xutils.text.placeholder.papi;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public class Argument {

    private final String id;
    private final Function<String, Object> function;
    private final boolean required;

    public static Argument required(String id) {
        return new Argument(id, string -> string, true);
    }

    public static Argument required(String id, Function<String, Object> function) {
        return new Argument(id, function, true);
    }

    public static Argument optional(String id) {
        return new Argument(id, string -> string, false);
    }

    public static Argument optional(String id, Function<String, Object> function) {
        return new Argument(id, function, false);
    }

}
