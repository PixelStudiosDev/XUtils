package dev.pixelstudios.xutils;

import dev.pixelstudios.xutils.item.TagHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class XUtils extends JavaPlugin {

    @Setter
    private static Plugin plugin;

    @Getter @Setter
    private static TagHandler customTagHandler;

    @Override
    public void onEnable() {
        plugin = this;
    }

    public static Plugin getPlugin() {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(XUtils.class);
        }
        return plugin;
    }

}
