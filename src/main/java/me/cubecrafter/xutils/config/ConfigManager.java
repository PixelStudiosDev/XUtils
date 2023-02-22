package me.cubecrafter.xutils.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static ConfigManager instance;

    private final Map<String, Configuration> configurations = new HashMap<>();

    private ConfigManager() {}

    public Configuration load(String name) {
        if (configurations.containsKey(name)) {
            return configurations.get(name);
        }
        Configuration config = new Configuration(name);
        config.load();
        configurations.put(name, config);
        return config;
    }

    public void reloadAll() {
        configurations.values().forEach(Configuration::load);
    }

    public void saveAll() {
        configurations.values().forEach(Configuration::save);
    }

    public void invalidate(String name) {
        configurations.remove(name);
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
