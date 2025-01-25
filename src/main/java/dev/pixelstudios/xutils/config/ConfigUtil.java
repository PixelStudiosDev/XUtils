package dev.pixelstudios.xutils.config;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ConfigUtil {

    public List<ConfigurationSection> getSectionList(ConfigurationSection config, String path) {
        List<ConfigurationSection> sections = new ArrayList<>();

        for (Map<?, ?> map : config.getMapList(path)) {
            sections.add(createSection(map));
        }

        return sections;
    }

    public List<ConfigurationSection> getSubSections(ConfigurationSection config) {
        List<ConfigurationSection> sections = new ArrayList<>();

        for (String key : config.getKeys(false)) {
            sections.add(config.getConfigurationSection(key));
        }

        return sections;
    }

    @SuppressWarnings("unchecked")
    private ConfigurationSection createSection(Map<?, ?> map) {
        MemoryConfiguration section = new ConfigSection();

        section.addDefaults((Map<String, Object>) map);
        section.options().copyDefaults(true);

        return section;
    }

    private class ConfigSection extends MemoryConfiguration {

        @Override
        public ConfigurationSection getConfigurationSection(String path) {
            Object value = get(path);

            if (value instanceof Map) {
                return ConfigUtil.createSection((Map<?, ?>) value);
            }

            return super.getConfigurationSection(path);
        }

    }

}
