package me.cubecrafter.xutils.config;

import me.cubecrafter.xutils.FileUtil;
import me.cubecrafter.xutils.ItemBuilder;
import me.cubecrafter.xutils.XUtils;
import me.cubecrafter.xutils.text.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Configuration extends YamlConfiguration {

    private static final Plugin plugin = XUtils.getPlugin();

    private final File file;
    private final String path;

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
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if (update) {
            update();
        }
    }

    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean update() {
        InputStreamReader reader = new InputStreamReader(plugin.getResource(path));
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);

        boolean updated = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!contains(key)) {
                set(key, defaultConfig.get(key));
                updated = true;
            }
        }

        if (updated) {
            save();
        }
        return updated;
    }

    public Location getLocation(String path) {
        return TextUtil.parseLocation(getString(path));
    }

    public ItemBuilder getItem(String path) {
        return ItemBuilder.fromConfig(getConfigurationSection(path));
    }

    @Override
    public void set(String path, Object value) {
        if (value instanceof Location) {
            Location location = (Location) value;
            set(path, TextUtil.fromLocation(location));
            return;
        }

        super.set(path, value);
    }

}
