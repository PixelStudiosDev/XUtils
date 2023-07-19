package me.cubecrafter.xutils.menu;

import me.cubecrafter.xutils.Events;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitTask;

public class MenuListener {

    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;

        Events.subscribe(InventoryClickEvent.class, event -> {
            InventoryHolder holder = event.getInventory().getHolder();

            if (!(holder instanceof Menu)) return;

            Menu menu = (Menu) event.getInventory().getHolder();
            MenuItem item = menu.getItem(event.getSlot());

            if (item != null) {
                item.onClick(event);
                menu.updateInventory();
            } else {
                event.setCancelled(true);
            }
        });

        Events.subscribe(InventoryCloseEvent.class, event -> {
            InventoryHolder holder = event.getInventory().getHolder();

            if (!(holder instanceof Menu)) return;

            Menu menu = (Menu) event.getInventory().getHolder();
            BukkitTask task = menu.getUpdateTask();

            if (task != null) {
                task.cancel();
            }
        });
    }

}
