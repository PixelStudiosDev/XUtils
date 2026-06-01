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

    public static int MAJOR_VERSION, MINOR_VERSION, PATCH_VERSION;

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

        if (!supports(1, 12, 0)) {
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

    public static boolean supports(int major, int minor, int patch) {
        if (major != 1 && major < 26)
            throw new IllegalArgumentException("Unexpected major version: " + major + "." + minor + "." + patch);
        if (major == 1 && minor > 21)
            throw new IllegalArgumentException("Unexpected minor version: " + major + "." + minor + "." + patch);
        if (major == 1 && patch > 11)
            throw new IllegalArgumentException("Unexpected patch version: " + major + "." + minor + "." + patch);

        return MAJOR_VERSION == major ? MINOR_VERSION == minor ? PATCH_VERSION >= patch : MINOR_VERSION > minor : MAJOR_VERSION > major;
    }

    public static void setUnbreakable(ItemMeta meta, boolean unbreakable) {
        if (supports(1, 12, 0)) {
            meta.setUnbreakable(unbreakable);
        } else {
            Object spigot = ReflectionUtil.invokeMethod(SPIGOT, meta);
            ReflectionUtil.invokeMethod(SET_UNBREAKABLE, spigot, unbreakable);
        }
    }

    public static void showPlayer(Player viewer, Player target) {
        if (supports(1, 12, 0)) {
            viewer.showPlayer(XUtils.getPlugin(), target);
        } else {
            viewer.showPlayer(target);
        }
    }

    public static void hidePlayer(Player viewer, Player target) {
        if (supports(1, 12, 0)) {
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
