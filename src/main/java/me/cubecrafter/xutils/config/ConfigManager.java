package me.cubecrafter.xutils.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConfigManager {

    private static ConfigManager instance;

    private final Map<String, Configuration> configurations = new HashMap<>();

    private ConfigManager() {}

    public Configuration load(String path) {
        if (configurations.containsKey(path)) {
            return configurations.get(path);
        }

        Configuration config = new Configuration(path);
        config.load();
        configurations.put(path, config);

        return config;
    }

    public Configuration update(String path, List<String> ignoredKeys) {
        Configuration config = load(path);
        config.update(ignoredKeys);

        return config;
    }

    public Configuration update(String path) {
        return update(path, Collections.emptyList());
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
