package dev.pixelstudios.xutils.placeholder;

import lombok.Value;

import java.util.function.Function;

@Value
public class Argument {

    String id;
    Function<String, Object> function;
    boolean required;

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
