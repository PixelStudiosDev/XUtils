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
    private final String path;

    public Configuration(String path) {
        this.path = path;
        this.file = new File(XUtils.getPlugin().getDataFolder(), path);
    }

    public void load() {
        if (!file.exists()) {
            FileUtil.copy(XUtils.getPlugin().getResource(path), file);
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
        InputStreamReader reader = new InputStreamReader(XUtils.getPlugin().getResource(path));
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
        ConfigManager.get().invalidate(path);
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
