package dev.pixelstudios.xutils.menu;

import dev.pixelstudios.xutils.Events;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitTask;

public class MenuListener implements Listener {

    private static MenuListener instance;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Menu menu = getMenu(event.getInventory());
        if (menu == null) return;

        int slot = event.getRawSlot();

        if (slot >= event.getInventory().getSize()) {
            // Prevent moving items into the menu by shift-clicking
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
            }
            return;
        }

        MenuItem item = menu.getItem(slot);

        if (item != null) {
            item.onClick(event, menu);
            menu.updateInventory();
        } else {
            event.setCancelled(!menu.getDraggableSlots().contains(slot));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Menu menu = getMenu(event.getInventory());
        if (menu == null) return;

        for (int slot : event.getRawSlots()) {
            if (slot >= event.getInventory().getSize()) continue;

            if (!menu.getDraggableSlots().contains(slot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Menu menu = getMenu(event.getInventory());
        if (menu == null) return;

        BukkitTask task = menu.getUpdateTask();

        if (task != null) {
            task.cancel();
        }

        menu.onClose();
    }

    private Menu getMenu(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof Menu) {
            return (Menu) holder;
        } else {
            return null;
        }
    }

    public static void register() {
        if (instance == null) {
            instance = new MenuListener();
            Events.register(instance);
        }
    }

}
