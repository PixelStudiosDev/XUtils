package dev.pixelstudios.xutils.objects;

import dev.pixelstudios.xutils.text.TextUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Hologram {

    private final List<ArmorStand> stands = new ArrayList<>();
    private final List<String> lines;

    private Location location;
    private boolean visible = true;

    public Hologram(Location location, List<String> lines) {
        this.location = location;
        this.lines = lines;

        update();
    }

    public Hologram(Location location) {
        this(location, new ArrayList<>());
    }

    public void addLine(String line) {
        this.lines.add(line);

        if (visible) {
            update();
        }
    }

    public void setLines(List<String> lines) {
        this.lines.clear();
        this.lines.addAll(lines);

        if (visible) {
            update();
        }
    }

    public void clear() {
        this.lines.clear();

        if (visible) {
            update();
        }
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) return;
        this.visible = visible;

        if (visible) {
            update();
        } else {
            destroy();
        }
    }

    public void setLocation(Location location) {
        this.location = location;

        if (visible) {
            update();
        }
    }

    public void destroy() {
        this.visible = false;

        stands.forEach(ArmorStand::remove);
        stands.clear();
    }

    private void update() {
        destroy();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            ArmorStand stand = spawnStand(line, location.clone().add(0, 0.3 * (lines.size() - 1 - i), 0));
            stands.add(stand);
        }
    }

    private static ArmorStand spawnStand(String name, Location location) {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        stand.setGravity(false);
        stand.setRemoveWhenFarAway(false);
        stand.setVisible(false);
        stand.setCanPickupItems(false);
        stand.setArms(false);
        stand.setBasePlate(false);
        stand.setMarker(true);
        stand.setCustomNameVisible(true);
        stand.setCustomName(TextUtil.color(name));

        return stand;
    }

}
