package dev.pixelstudios.xutils.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Getter
@RequiredArgsConstructor
public class ConfigMap {

    private final Map<String, Object> map;

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public boolean isString(String key) {
        return map.get(key) instanceof String;
    }

    public boolean isInt(String key) {
        return map.get(key) instanceof Integer;
    }

    public boolean isBoolean(String key) {
        return map.get(key) instanceof Boolean;
    }

    public boolean isDouble(String key) {
        return map.get(key) instanceof Double;
    }

    public boolean isList(String key) {
        return map.get(key) instanceof List;
    }

    public String getString(String key, String def) {
        if (!contains(key)) return def;

        return (String) map.get(key);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public List<String> getStringList(String key, List<String> def) {
        if (!contains(key)) return def;

        return (List<String>) map.get(key);
    }

    public List<String> getStringList(String key) {
        return getStringList(key, null);
    }

    public int getInt(String key, int def) {
        if (!contains(key)) return def;

        return (int) map.get(key);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public boolean getBoolean(String key, boolean def) {
        if (!contains(key)) return def;

        return (boolean) map.get(key);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public double getDouble(String key, double def) {
        if (!contains(key)) return def;

        return (double) map.get(key);
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public ConfigMap set(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public static List<ConfigMap> loadList(ConfigurationSection section , String key) {
        if (!section.contains(key)) return null;

        return section.getMapList(key).stream()
                .map(map -> new ConfigMap((Map<String, Object>) map))
                .collect(Collectors.toList());
    }

}


