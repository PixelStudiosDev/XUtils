package dev.pixelstudios.xutils.objects;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Cuboid implements Iterable<Block> {

    private World world;
    private int xMin, xMax, yMin, yMax, zMin, zMax;
    private Location first, second;

    public Cuboid(Location first, Location second) {
        setFirst(first);
        setSecond(second);
    }

    public Cuboid() {
    }

    public void setFirst(Location first) {
        this.first = first;
        this.world = first.getWorld();

        this.xMin = Math.min(first.getBlockX(), second == null ? 0 : second.getBlockX());
        this.xMax = Math.max(first.getBlockX(), second == null ? 0 : second.getBlockX());
        this.yMin = Math.min(first.getBlockY(), second == null ? 0 : second.getBlockY());
        this.yMax = Math.max(first.getBlockY(), second == null ? 0 : second.getBlockY());
        this.zMin = Math.min(first.getBlockZ(), second == null ? 0 : second.getBlockZ());
        this.zMax = Math.max(first.getBlockZ(), second == null ? 0 : second.getBlockZ());
    }

    public void setSecond(Location second) {
        this.second = second;
        this.world = second.getWorld();

        this.xMin = Math.min(first == null ? 0 : first.getBlockX(), second.getBlockX());
        this.xMax = Math.max(first == null ? 0 : first.getBlockX(), second.getBlockX());
        this.yMin = Math.min(first == null ? 0 : first.getBlockY(), second.getBlockY());
        this.yMax = Math.max(first == null ? 0 : first.getBlockY(), second.getBlockY());
        this.zMin = Math.min(first == null ? 0 : first.getBlockZ(), second.getBlockZ());
        this.zMax = Math.max(first == null ? 0 : first.getBlockZ(), second.getBlockZ());
    }

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>(getBlockCount());
        for (int x = xMin; x <= xMax; ++x) {
            for (int y = yMin; y <= yMax; ++y) {
                for (int z = zMin; z <= zMax; ++z) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public void fill(String material) {
        for (Block block : getBlocks()) {
            XBlock.setType(block, XMaterial.matchXMaterial(material).orElse(XMaterial.STONE));
        }
    }

    public void fillRandom(List<String> materials) {
        for (Block block : getBlocks()) {
            String material = materials.get(ThreadLocalRandom.current().nextInt(materials.size()));
            XBlock.setType(block, XMaterial.matchXMaterial(material).orElse(XMaterial.STONE));
        }
    }

    public boolean isInside(Location location) {
        if (!isSet()) return false;
        return location.getWorld().equals(world)
                && location.getBlockX() >= xMin
                && location.getBlockX() <= xMax
                && location.getBlockY() >= yMin
                && location.getBlockY() <= yMax
                && location.getBlockZ() >= zMin
                && location.getBlockZ() <= zMax;
    }

    public int getBlockCount() {
        return (xMax - xMin + 1) * (yMax - yMin + 1) * (zMax - zMin + 1);
    }

    public boolean isSet() {
        return first != null && second != null;
    }

    @Override
    public Iterator<Block> iterator() {
        return getBlocks().iterator();
    }

}
