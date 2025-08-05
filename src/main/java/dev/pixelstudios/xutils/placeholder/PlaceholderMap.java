package dev.pixelstudios.xutils.placeholder;

import dev.pixelstudios.xutils.text.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PlaceholderMap implements Cloneable {

    private final Map<String, Supplier<String>> placeholders = new HashMap<>();
    private final Map<String, Supplier<List<String>>> multiLinePlaceholders = new HashMap<>();

    public PlaceholderMap add(String key, Supplier<String> value) {
        placeholders.put(key, value);
        return this;
    }

    public PlaceholderMap add(String key, String value) {
        placeholders.put(key, () -> value);
        return this;
    }

    public PlaceholderMap addNumber(String key, Supplier<Number> value) {
        placeholders.put(key, () -> String.valueOf(value.get()));
        return this;
    }

    public PlaceholderMap addNumber(String key, Number value) {
        placeholders.put(key, () -> String.valueOf(value));
        return this;
    }

    public PlaceholderMap addMultiple(String key, Supplier<List<String>> value) {
        multiLinePlaceholders.put(key, value);
        return this;
    }

    public PlaceholderMap addMultiple(String key, List<String> value) {
        multiLinePlaceholders.put(key, () -> value);
        return this;
    }

    public PlaceholderMap clear() {
        placeholders.clear();
        multiLinePlaceholders.clear();
        return this;
    }

    public String parse(String text) {
        return parse(text, '{', '}');
    }

    public String parse(String text, char open, char close) {
        if (text == null) {
            return null;
        }

        for (Map.Entry<String, Supplier<String>> entry : placeholders.entrySet()) {
            text = text.replace(open + entry.getKey() + close, entry.getValue().get());
        }

        return TextUtil.color(text);
    }

    public List<String> parse(List<String> text) {
        return parse(text, '{', '}');
    }

    public List<String> parse(List<String> text, char open, char close) {
        if (text == null) {
            return null;
        }

        List<String> parsed = new ArrayList<>();

        for (String line : text) {
            boolean found = false;

            for (Map.Entry<String, Supplier<List<String>>> entry : multiLinePlaceholders.entrySet()) {
                if (line.contains(open + entry.getKey() + close)) {
                    parsed.addAll(entry.getValue().get());

                    found = true;
                    break;
                }
            }

            if (!found) {
                parsed.add(parse(line, open, close));
            }
        }

        return TextUtil.color(parsed);
    }

    public PlaceholderMap merge(PlaceholderMap map) {
        placeholders.putAll(map.placeholders);
        multiLinePlaceholders.putAll(map.multiLinePlaceholders);

        return this;
    }

    @Override
    public PlaceholderMap clone() {
        PlaceholderMap map = new PlaceholderMap();

        map.placeholders.putAll(this.placeholders);
        map.multiLinePlaceholders.putAll(this.multiLinePlaceholders);

        return map;
    }

}
