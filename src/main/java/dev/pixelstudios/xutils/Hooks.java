package dev.pixelstudios.xutils;

import dev.pixelstudios.xutils.text.TextUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class Hooks {

    private final Set<String> HOOKS = new HashSet<>();

    public boolean register(String plugin, Runnable function) {
        if (Bukkit.getPluginManager().getPlugin(plugin) == null || HOOKS.contains(plugin)) {
            return false;
        }

        HOOKS.add(plugin);
        function.run();

        TextUtil.info("Hooked into " + plugin + ".");

        return true;
    }

    public boolean isRegistered(String plugin) {
        return HOOKS.contains(plugin);
    }

    public void unregister(String plugin) {
        HOOKS.remove(plugin);
    }

    public void unregisterAll() {
        HOOKS.clear();
    }

}
