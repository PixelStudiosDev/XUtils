package dev.pixelstudios.xutils.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConfigManager {

    private static ConfigManager instance;

    private final Map<String, Configuration> configurations = new HashMap<>();

    private ConfigManager() {}

    public Configuration load(String name, String destination) {
        name = name.endsWith(".yml") ? name : name + ".yml";

        if (configurations.containsKey(name)) {
            return configurations.get(name);
        }

        Configuration config = new Configuration(name, destination);
        config.load();
        configurations.put(name, config);

        return config;
    }

    public Configuration load(String name) {
        return load(name, null);
    }

    public Configuration update(String name, String destination, List<String> ignoredKeys) {
        Configuration config = load(name, destination);
        config.update(ignoredKeys);

        return config;
    }

    public Configuration update(String name, List<String> ignoredKeys) {
        return update(name, null, ignoredKeys);
    }

    public Configuration update(String name, String destination) {
        return update(name, destination, Collections.emptyList());
    }

    public Configuration update(String name) {
        return update(name, Collections.emptyList());
    }

    public void invalidate(String path) {
        configurations.remove(path);
    }

    public void reloadAll() {
        configurations.values().forEach(Configuration::load);
    }

    public void saveAll() {
        configurations.values().forEach(Configuration::save);
    }

    public void invalidateAll() {
        configurations.clear();
    }

    public static ConfigManager get() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

}
