package dev.pixelstudios.xutils.config.serializer;

import org.bukkit.Color;
import org.bukkit.DyeColor;

public class ColorSerializer implements Serializer<Color> {

    @Override
    public Color deserialize(String serialized) {
        serialized = serialized.trim();

        // Hex format
        if (serialized.startsWith("#") && serialized.length() == 7) {
            return Color.fromRGB(Integer.parseInt(serialized.substring(1), 16));
        }

        // RGB format
        String[] split = serialized.split(":");
        if (split.length == 3) {
            return Color.fromRGB(
                    Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2])
            );
        }

        // Bukkit DyeColor
        try {
            return DyeColor.valueOf(serialized.toUpperCase()).getColor();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid color: " + serialized);
        }
    }

}
