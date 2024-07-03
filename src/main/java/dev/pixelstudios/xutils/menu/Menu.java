package dev.pixelstudios.xutils.menu;

import dev.pixelstudios.xutils.item.ItemUtil;
import lombok.Getter;
import lombok.Setter;
import dev.pixelstudios.xutils.ReflectionUtil;
import dev.pixelstudios.xutils.Tasks;
import dev.pixelstudios.xutils.objects.PlaceholderMap;
import dev.pixelstudios.xutils.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter @Setter
public abstract class Menu implements InventoryHolder {

    protected final Player player;

    private final Map<Integer, MenuItem> items = new HashMap<>();

    private Inventory inventory;
    private BukkitTask updateTask;
    private Set<Integer> draggableSlots = new HashSet<>();

    private boolean autoUpdate = true;
    private int updateInterval = 20;

    private PlaceholderMap placeholders = new PlaceholderMap();
    private boolean parsePlaceholders = true;

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
                String title = placeholders.parse(getTitle());

                if (parsePlaceholders) {
                    title = TextUtil.parsePlaceholders(player, title);
                }

                this.inventory = Bukkit.createInventory(this, getRows() * 9, TextUtil.color(title));
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

        title = placeholders.parse(title);

        if (parsePlaceholders) {
            title = TextUtil.parsePlaceholders(player, title);
        }

        player.getOpenInventory().setTitle(TextUtil.color(title));
    }

    public void updateInventory() {
        items.clear();
        inventory.clear();

        update();

        items.forEach((slot, item) -> {
            ItemStack parsed = item.getItem().build();
            
            ItemUtil.parsePlaceholders(parsed, placeholders);

            if (parsePlaceholders) {
                ItemUtil.parsePlaceholders(parsed, player);
            }

            inventory.setItem(slot, parsed);
        });
    }

    public void addDraggableSlots(Integer... slots) {
        draggableSlots.addAll(Arrays.asList(slots));
    }

    public void placeholder(String key, String value) {
        placeholders.add(key, value);
    }

    public void updateTitle() {
        setTitle(getTitle());
    }

    public void onClose() {
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public abstract void update();
    public abstract int getRows();
    public abstract String getTitle();

}
