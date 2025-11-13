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
    private final Object defaultValue;

    public static Argument required(String id) {
        return new Argument(id, string -> string, true, null);
    }

    public static Argument required(String id, Function<String, Object> function) {
        return new Argument(id, function, true, null);
    }

    public static Argument optional(String id) {
        return new Argument(id, string -> string, false, null);
    }

    public static Argument optional(String id, Function<String, Object> function) {
        return new Argument(id, function, false, null);
    }

    public static Argument optional(String id, Object defaultValue) {
        return new Argument(id, string -> string, false, defaultValue);
    }

    public static Argument optional(String id, Function<String, Object> function, Object defaultValue) {
        return new Argument(id, function, false, defaultValue);
    }

}
