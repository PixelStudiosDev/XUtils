package me.cubecrafter.xutils;

import com.cryptomorin.xseries.ReflectionUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

@UtilityClass
public class BlockUtil {

    private static final BlockFace[] AXIS = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    /**
     * Get the BlockFace targeted by a player.
     * @param player the player
     * @return the targeted BlockFace, or null if the block isn't occluding
     */
    public static BlockFace getTargetedFace(Player player) {
        List<Block> targets = player.getLastTwoTargetBlocks((Set<Material>) null, 100);
        if (targets.size() != 2 || !targets.get(1).getType().isOccluding()) return null;

        Block target = targets.get(1);
        Block adjacent = targets.get(0);

        return target.getFace(adjacent);
    }

    /**
     * Get the closest cardinal direction a player is facing.
     * @param player the player
     * @return the direction the player is facing
     */
    public static BlockFace getFacing(Player player) {
        if (ReflectionUtil.supports(13)) {
            return player.getFacing();
        } else {
            return AXIS[Math.round(player.getLocation().getYaw() / 90f) & 0x3].getOppositeFace();
        }
    }

}
