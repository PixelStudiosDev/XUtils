package dev.pixelstudios.xutils.config.serializer;

import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.ArmorTrim;

public class ArmorTrimSerializer implements Serializer<ArmorTrim> {

    @Override
    public ArmorTrim deserialize(String serialized) {
        String[] split = serialized.split(":");

        if (split.length < 2) {
            throw new IllegalArgumentException("Invalid armor trim format: " + serialized);
        }

        return new ArmorTrim(
                Registry.TRIM_MATERIAL.match(split[0]),
                Registry.TRIM_PATTERN.match(split[1])
        );
    }

}
