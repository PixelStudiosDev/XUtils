package dev.pixelstudios.xutils.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class ConfigNode {

    private final ConfigurationSection section;
    private final String key;

    public <T> T as(Class<T> type) {
        return section.getObject(key, type);
    }

    public String asString() {
        if (section.isList(key)) {
            return String.join(", ", section.getStringList(key));
        } else {
            return section.getString(key);
        }
    }

    public int asInt() {
        return section.getInt(key);
    }

    public boolean asBoolean() {
        return section.getBoolean(key);
    }

    public double asDouble() {
        return section.getDouble(key);
    }

    public long asLong() {
        return section.getLong(key);
    }

    public List<String> asStringList() {
        return section.getStringList(key);
    }

    public List<Integer> asIntList() {
        return section.getIntegerList(key);
    }

    public ConfigurationSection asSection() {
        return section.getConfigurationSection(key);
    }

    public Set<String> getKeys() {
        return asSection().getKeys(false);
    }

    public void set(Object value) {
        section.set(key, value);
    }

    public void delete() {
        section.set(key, null);
    }

    public boolean exists() {
        return section.contains(key);
    }

}
