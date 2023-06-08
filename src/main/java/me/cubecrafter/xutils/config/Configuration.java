package me.cubecrafter.xutils.config;

import me.cubecrafter.xutils.FileUtil;
import me.cubecrafter.xutils.XUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

public class Configuration {

    private static final Plugin plugin = XUtils.getPlugin();

    private final File file;
    private final String path;

    private YamlConfiguration config;

    public Configuration(String path) {
        this.path = path;
        this.file = new File(plugin.getDataFolder(), path);
    }

    public void load() {
        load(false);
    }

    public void load(boolean update) {
        if (!file.exists()) {
            FileUtil.copy(plugin.getResource(path), file);
        }
        config = YamlConfiguration.loadConfiguration(file);
        if (update) {
            update();
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean update() {
        InputStreamReader reader = new InputStreamReader(plugin.getResource(path));
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);

        boolean updated = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
                updated = true;
            }
        }

        if (updated) {
            save();
        }
        return updated;
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public List<Integer> getIntegerList(String path) {
        return config.getIntegerList(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    public Object get(String path) {
        return config.get(path);
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

}
