package me.cubecrafter.xutils.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PlaceholderMap {

    private final Map<String, Supplier<String>> placeholders = new HashMap<>();

    public void put(String key, Supplier<String> value) {
        placeholders.put(key, value);
    }

    public void put(String key, String value) {
        placeholders.put(key, () -> value);
    }

    public String parse(String text) {
        for (Map.Entry<String, Supplier<String>> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue().get());
        }
        return text;
    }

    public List<String> parse(List<String> text) {
        return text.stream().map(this::parse).collect(Collectors.toList());
    }

}
