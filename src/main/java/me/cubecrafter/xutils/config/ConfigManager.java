package me.cubecrafter.xutils.config;

import me.cubecrafter.xutils.commands.CommandWrapper;

import java.util.HashMap;
import java.util.Map;

public final class ConfigManager {

    private static ConfigManager instance;

    private final Map<String, Configuration> configurations = new HashMap<>();

    private ConfigManager() {}

    public Configuration load(String path, boolean update) {
        if (configurations.containsKey(path)) {
            return configurations.get(path);
        }

        Configuration config = new Configuration(path);
        config.load(update);

        configurations.put(path, config);
        return config;
    }

    public Configuration load(String path) {
        return load(path, false);
    }

    public void invalidate(String name) {
        configurations.remove(name);
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
