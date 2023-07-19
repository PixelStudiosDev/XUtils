package me.cubecrafter.xutils.menu;

import lombok.Data;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.Tasks;
import me.cubecrafter.xutils.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class Menu implements InventoryHolder {

    private final Player player;
    private final Map<Integer, MenuItem> items = new HashMap<>();

    private Inventory inventory;
    private BukkitTask updateTask;

    private boolean autoUpdate = true;
    private boolean parsePlaceholders = false;
    private int updateInterval = 20;

    public Menu(Player player) {
        this.player = player;

        MenuListener.register();
    }

    public MenuItem getItem(int slot) {
        return items.get(slot);
    }

    public void setItem(MenuItem item, List<Integer> slots) {
        for (int slot : slots) {
            items.put(slot, item);
        }
    }

    public void setItem(MenuItem item, Integer... slots) {
        setItem(item, Arrays.asList(slots));
    }

    public void fillBorders(MenuItem item) {
        for (int i = 0; i < getRows() * 9; i++) {
            if (i < 9 || i % 9 == 0 || i % 9 == 8 || i >= getRows() * 9 - 9) {
                items.put(i, item);
            }
        }
    }

    public void open() {
        Tasks.sync(() -> {
            if (inventory == null) {
                String title = parsePlaceholders ? TextUtil.parsePlaceholders(player, getTitle()) : getTitle();
                this.inventory = Bukkit.createInventory(this, getRows() * 9, title);
            }

            if (autoUpdate) {
                this.updateTask = Tasks.repeat(this::updateInventory, updateInterval, updateInterval);
            }

            updateInventory();
            player.openInventory(inventory);
        });
    }

    public void close() {
        player.closeInventory();
    }

    public void setTitle(String title) {
        if (!ReflectionUtil.supports(20)) return;

        if (parsePlaceholders) {
            title = TextUtil.parsePlaceholders(player, title);
        }

        player.getOpenInventory().setTitle(title);
    }

    public void updateInventory() {
        items.clear();
        update();

        inventory.clear();
        items.forEach((slot, item) -> {
            ItemStack stack = item.getItem();

            if (parsePlaceholders) {
                stack = TextUtil.parsePlaceholders(player, item.getItem());
            }

            inventory.setItem(slot, stack);
        });
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public abstract void update();
    public abstract int getRows();
    public abstract String getTitle();

}
