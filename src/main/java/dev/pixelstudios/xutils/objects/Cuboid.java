package dev.pixelstudios.xutils.objects;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import dev.pixelstudios.xutils.config.serializer.ConfigSerializer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class Cuboid implements Iterable<Block> {

    private final World world;
    private final int xMin, yMin, zMin;
    private final int xMax, yMax, zMax;

    public Cuboid(Location first, Location second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Cuboid corners cannot be null");
        }

        if (!first.getWorld().equals(second.getWorld())) {
            throw new IllegalArgumentException("Cuboid corners must be in the same world");
        }

        this.world = first.getWorld();

        this.xMin = Math.min(first.getBlockX(), second.getBlockX());
        this.yMin = Math.min(first.getBlockY(), second.getBlockY());
        this.zMin = Math.min(first.getBlockZ(), second.getBlockZ());

        this.xMax = Math.max(first.getBlockX(), second.getBlockX());
        this.yMax = Math.max(first.getBlockY(), second.getBlockY());
        this.zMax = Math.max(first.getBlockZ(), second.getBlockZ());
    }

    public void fill(String material) {
        XMaterial xMaterial = XMaterial.matchXMaterial(material).orElse(XMaterial.STONE);

        for (Block block : this) {
            XBlock.setType(block, xMaterial);
        }
    }

    public void fillRandom(List<String> materials) {
        if (materials.isEmpty()) return;

        List<XMaterial> xMaterials = materials.stream()
                .map(mat -> XMaterial.matchXMaterial(mat).orElse(XMaterial.STONE))
                .collect(Collectors.toList());

        for (Block block : this) {
            int index = ThreadLocalRandom.current().nextInt(xMaterials.size());
            XBlock.setType(block, xMaterials.get(index));
        }
    }

    public void replace(Set<String> fromMaterials, String toMaterial) {
        Set<XMaterial> fromXMaterials = fromMaterials.stream()
                .map(XMaterial::matchXMaterial)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Optional<XMaterial> toXMaterial = XMaterial.matchXMaterial(toMaterial);

        if (fromXMaterials.isEmpty() || !toXMaterial.isPresent()) return;

        XMaterial target = toXMaterial.get();

        for (Block block : this) {
            if (fromXMaterials.contains(XBlock.getType(block))) {
                XBlock.setType(block, target);
            }
        }
    }

    public void replace(String fromMaterial, String toMaterial) {
        replace(Collections.singleton(fromMaterial), toMaterial);
    }

    public boolean isInside(Location location) {
        if (location == null) return false;

        return location.getWorld().equals(world)
                && location.getBlockX() >= xMin && location.getBlockX() <= xMax
                && location.getBlockY() >= yMin && location.getBlockY() <= yMax
                && location.getBlockZ() >= zMin && location.getBlockZ() <= zMax;
    }

    public boolean isInside(Block block) {
        if (block == null) return false;
        return isInside(block.getLocation());
    }

    public long getBlockCount() {
        return (long) (xMax - xMin + 1) * (yMax - yMin + 1) * (zMax - zMin + 1);
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidIterator();
    }

    private class CuboidIterator implements Iterator<Block> {

        private int nextX = xMin;
        private int nextY = yMin;
        private int nextZ = zMin;

        @Override
        public boolean hasNext() {
            return nextX <= xMax && nextY <= yMax && nextZ <= zMax;
        }

        @Override
        public Block next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more blocks in cuboid iterator");
            }

            Block block = world.getBlockAt(nextX, nextY, nextZ);

            nextZ++;
            if (nextZ > zMax) {
                nextZ = zMin;
                nextY++;
                if (nextY > yMax) {
                    nextY = yMin;
                    nextX++;
                }
            }

            return block;
        }

    }

    @Getter
    public static class Builder {

        private Location first;
        private Location second;

        public Builder first(Location first) {
            this.first = first;
            return this;
        }

        public Builder second(Location second) {
            this.second = second;
            return this;
        }

        public boolean hasFirst() {
            return first != null;
        }

        public boolean hasSecond() {
            return second != null;
        }

        public boolean isComplete() {
            return hasFirst() && hasSecond() && first.getWorld().equals(second.getWorld());
        }

        public Cuboid build() {
            if (!isComplete()) {
                throw new IllegalStateException("Cannot build cuboid, both corners must be set and in the same world");
            }
            return new Cuboid(first, second);
        }

        public Builder load(ConfigurationSection section) {
            if (section.isString("pos1")) {
                this.first = ConfigSerializer.deserialize(section.getString("pos1"), Location.class);
            }
            if (section.isString("pos2")) {
                this.second = ConfigSerializer.deserialize(section.getString("pos2"), Location.class);
            }
            return this;
        }

        public void save(ConfigurationSection section) {
            if (first != null) {
                section.set("pos1", ConfigSerializer.serialize(first));
            }
            if (second != null) {
                section.set("pos2", ConfigSerializer.serialize(second));
            }
        }

    }

}
