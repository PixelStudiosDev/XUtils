package me.cubecrafter.xutils.config;

import me.cubecrafter.xutils.FileUtil;
import me.cubecrafter.xutils.XUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Configuration {

    private static final Plugin plugin = XUtils.getPlugin();

    private final File file;
    private final String name;

    private YamlConfiguration config;

    public Configuration(String name) {
        this.name = name;
        this.file = new File(plugin.getDataFolder(), name + ".yml");
    }

    public void load() {
        if (!file.exists()) {
            FileUtil.copy(plugin.getResource(name + ".yml"), file);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInt(String path) {
        return config.getInt(path);
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

}
