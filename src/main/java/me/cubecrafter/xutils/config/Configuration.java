package me.cubecrafter.xutils.config;

import me.cubecrafter.xutils.FileUtil;
import me.cubecrafter.xutils.item.ItemBuilder;
import me.cubecrafter.xutils.XUtils;
import me.cubecrafter.xutils.text.TextUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class Configuration extends YamlConfiguration {

    private final File file;
    private final String name;

    public Configuration(String name, String destination) {
        this.name = name.endsWith(".yml") ? name : name + ".yml";
        if (destination == null) {
            destination = XUtils.getPlugin().getDataFolder().getAbsolutePath();
        }
        this.file = new File(destination, this.name);
    }

    public Configuration(String name) {
        this(name, null);
    }

    public void load() {
        if (!file.exists()) {
            FileUtil.copy(XUtils.getPlugin().getResource(name), file);
        }
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
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
        return update(Collections.emptyList());
    }

    public boolean update(List<String> ignoredKeys) {
        InputStreamReader reader = new InputStreamReader(XUtils.getPlugin().getResource(name));
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);

        boolean updated = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!contains(key)) {
                if (ignoredKeys.stream().anyMatch(key::startsWith)) continue;
                set(key, defaultConfig.get(key));
                updated = true;
            }
        }

        if (updated) {
            save();
        }
        return updated;
    }

    public void invalidate() {
        ConfigManager.get().invalidate(name);
    }

    public Location getLocation(String path) {
        return TextUtil.parseLocation(getString(path));
    }

    public ItemBuilder getItem(String path) {
        return ItemBuilder.fromConfig(getConfigurationSection(path));
    }

    public Color getColor(String path) {
        return TextUtil.parseColor(getString(path));
    }

}
