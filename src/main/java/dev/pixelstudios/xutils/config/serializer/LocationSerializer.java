package dev.pixelstudios.xutils.config.serializer;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer implements Serializer<Location> {

    @Override
    public String serialize(Location location) {
        StringBuilder builder = new StringBuilder();

        builder.append(location.getWorld().getName())
                .append(":").append(location.getX())
                .append(":").append(location.getY())
                .append(":").append(location.getZ());

        if (location.getYaw() != 0 && location.getPitch() != 0) {
            builder.append(":").append(location.getYaw())
                    .append(":").append(location.getPitch());
        }

        return builder.toString();
    }

    @Override
    public Location deserialize(String serialized) {
        String[] split = serialized.replace(" ", "").split(":");

        if (split.length != 4 && split.length != 6) {
            throw new IllegalArgumentException("Invalid location format: " + serialized);
        }

        if (split.length == 4) {
            return new Location(
                    Bukkit.getWorld(split[0]),
                    Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])
            );
        }

        return new Location(
                Bukkit.getWorld(split[0]),
                Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]),
                Float.parseFloat(split[4]), Float.parseFloat(split[5])
        );
    }

}
