package me.cubecrafter.xutils;

import com.cryptomorin.xseries.ReflectionUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

@UtilityClass
@SuppressWarnings("deprecation")
public class VersionUtil {

    private static Method SPIGOT;
    private static Method SET_UNBREAKABLE;

    static {
        if (!ReflectionUtil.supports(12)) {
            SPIGOT = ReflectionUtil.getMethod(ItemMeta.class, "spigot");
            SET_UNBREAKABLE = ReflectionUtil.getMethod(SPIGOT.getReturnType(), "setUnbreakable", boolean.class);
        }
    }

    public static void setUnbreakable(ItemStack item, boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();

        if (ReflectionUtils.supports(12)) {
            meta.setUnbreakable(unbreakable);
        } else {
            Object spigot = ReflectionUtil.invokeMethod(SPIGOT, meta);
            ReflectionUtil.invokeMethod(SET_UNBREAKABLE, spigot, unbreakable);
        }

        item.setItemMeta(meta);
    }

    public static void showPlayer(Player viewer, Player target) {
        if (ReflectionUtil.supports(12)) {
            viewer.showPlayer(XUtils.getPlugin(), target);
        } else {
            viewer.showPlayer(target);
        }
    }

    public static void hidePlayer(Player viewer, Player target) {
        if (ReflectionUtil.supports(12)) {
            viewer.hidePlayer(XUtils.getPlugin(), target);
        } else {
            viewer.hidePlayer(target);
        }
    }

    public static void setVisibility(Player viewer, Player target, boolean visible) {
        if (visible) {
            showPlayer(viewer, target);
        } else {
            hidePlayer(viewer, target);
        }
    }

}
