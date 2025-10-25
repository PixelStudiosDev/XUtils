package dev.pixelstudios.xutils.config.serializer;

import org.bukkit.Color;
import org.bukkit.DyeColor;

public class ColorSerializer implements Serializer<Color> {

    @Override
    public Color deserialize(String serialized) {
        String[] split = serialized.split(":");

        Color color;

        if (split.length != 3) {
            color = DyeColor.valueOf(serialized).getColor();
        } else {
            color = Color.fromRGB(
                    Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2])
            );
        }

        return color;
    }

}
