package dev.pixelstudios.xutils.text.placeholder;

import dev.pixelstudios.xutils.text.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderMap implements Cloneable {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(.*?)}");

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
        if (text == null) {
            return null;
        }

        if (placeholders.isEmpty()) {
            return text;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer builder = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = placeholders.containsKey(key) ? placeholders.get(key).get() : matcher.group();
            matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(builder);
        return TextUtil.color(builder.toString());
    }

    public List<String> parse(List<String> text) {
        if (text == null) {
            return null;
        }

        if (placeholders.isEmpty() && multiLinePlaceholders.isEmpty()) {
            return text;
        }

        List<String> parsed = new ArrayList<>();

        for (String line : text) {
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(line);

            if (matcher.find()) {
                String key = matcher.group(1);

                if (multiLinePlaceholders.containsKey(key)) {
                    parsed.addAll(multiLinePlaceholders.get(key).get());
                    continue;
                }
            }

            parsed.add(parse(line));
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
