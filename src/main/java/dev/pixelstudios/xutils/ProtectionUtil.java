package dev.pixelstudios.xutils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ProtectionUtil {

    private static WorldGuardAdapter ADAPTER;

    static {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (plugin != null) {
            String version = plugin.getDescription().getVersion();

            if (version.startsWith("7")) {
                ADAPTER = new WorldGuardAdapter.ModernAdapter();
            } else if (version.startsWith("6")) {
                ADAPTER = new WorldGuardAdapter.LegacyAdapter();
            }
        }
    }

    public static Set<String> getRegions(Location location) {
        if (ADAPTER == null) {
            return Collections.emptySet();
        }

        return ADAPTER.getApplicableRegions(location).getRegions().stream()
                .map(ProtectedRegion::getId)
                .collect(Collectors.toSet());
    }

    private interface WorldGuardAdapter {

        ApplicableRegionSet getApplicableRegions(Location location);

        class LegacyAdapter implements WorldGuardAdapter {

            private final com.sk89q.worldguard.bukkit.RegionContainer container;

            public LegacyAdapter() {
                WorldGuardPlugin plugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
                this.container = (com.sk89q.worldguard.bukkit.RegionContainer) ReflectionUtil.getFieldValue(WorldGuardPlugin.class, "regionContainer", plugin);
            }

            @Override
            public ApplicableRegionSet getApplicableRegions(Location location) {
                com.sk89q.worldguard.bukkit.RegionQuery query = container.createQuery();
                return query.getApplicableRegions(location);
            }

        }

        class ModernAdapter implements WorldGuardAdapter {

            @Override
            public ApplicableRegionSet getApplicableRegions(Location location) {
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();

                return query.getApplicableRegions(BukkitAdapter.adapt(location));
            }

        }

    }

}
