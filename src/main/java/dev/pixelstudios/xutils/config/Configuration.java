package dev.pixelstudios.xutils.config;

import dev.pixelstudios.xutils.FileUtil;
import dev.pixelstudios.xutils.config.serializer.ConfigSerializer;
import dev.pixelstudios.xutils.XUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    public Configuration(File file) {
        this.file = FileUtil.create(file);
        this.name = file.getName();

        this.load();
    }

    public Configuration(String path) {
        this(new File(XUtils.getPlugin().getDataFolder(), path));
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
        return update(Collections.emptySet());
    }

    public boolean update(Set<String> ignoredKeys) {
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

    public <T> void serialize(String path, T object) {
        ConfigSerializer.serialize(object, this, path);
    }

    public <T> T deserialize(String path, Class<T> objectClass) {
        return ConfigSerializer.deserialize(objectClass, this, path);
    }

    public List<ConfigurationSection> getSubSections(String path) {
        return ConfigUtil.getSubSections(getConfigurationSection(path));
    }

}
