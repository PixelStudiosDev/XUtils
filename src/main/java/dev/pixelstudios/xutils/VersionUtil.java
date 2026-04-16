package dev.pixelstudios.xutils;

import dev.pixelstudios.xutils.text.TextUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
@SuppressWarnings("deprecation")
public class VersionUtil {

    public static int MAJOR_VERSION, MINOR_VERSION;

    private static Method SPIGOT, SET_UNBREAKABLE;

    static {
        Pattern pattern = Pattern.compile("^(?:1\\.)?(\\d+)(?:\\.(\\d+))?");
        Matcher matcher = pattern.matcher(Bukkit.getBukkitVersion());

        if (matcher.find()) {
            MAJOR_VERSION = Integer.parseInt(matcher.group(1));
            MINOR_VERSION = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
        } else {
            TextUtil.error("Failed to parse Minecraft server version: " + Bukkit.getBukkitVersion());
            MAJOR_VERSION = MINOR_VERSION = -1;
        }

        if (!supports(12)) {
            SPIGOT = ReflectionUtil.getMethod(ItemMeta.class, "spigot");
            SET_UNBREAKABLE = ReflectionUtil.getMethod(SPIGOT.getReturnType(), "setUnbreakable", boolean.class);
        }
    }

    public static boolean supports(int major) {
        return MAJOR_VERSION >= major;
    }

    public static boolean supports(int major, int minor) {
        return MAJOR_VERSION > major || (MAJOR_VERSION == major && MINOR_VERSION >= minor);
    }

    public static void setUnbreakable(ItemMeta meta, boolean unbreakable) {
        if (supports(12)) {
            meta.setUnbreakable(unbreakable);
        } else {
            Object spigot = ReflectionUtil.invokeMethod(SPIGOT, meta);
            ReflectionUtil.invokeMethod(SET_UNBREAKABLE, spigot, unbreakable);
        }
    }

    public static void showPlayer(Player viewer, Player target) {
        if (supports(12)) {
            viewer.showPlayer(XUtils.getPlugin(), target);
        } else {
            viewer.showPlayer(target);
        }
    }

    public static void hidePlayer(Player viewer, Player target) {
        if (supports(12)) {
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
