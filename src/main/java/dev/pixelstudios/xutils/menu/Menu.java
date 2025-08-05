package dev.pixelstudios.xutils.menu;

import dev.pixelstudios.xutils.item.ItemBuilder;
import dev.pixelstudios.xutils.item.ItemUtil;
import lombok.Getter;
import lombok.Setter;
import dev.pixelstudios.xutils.ReflectionUtil;
import dev.pixelstudios.xutils.Tasks;
import dev.pixelstudios.xutils.placeholder.PlaceholderMap;
import dev.pixelstudios.xutils.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public abstract class Menu implements InventoryHolder {

    protected final Player player;
    protected ConfigurationSection config;
    protected final PlaceholderMap placeholders = new PlaceholderMap();

    private final InventoryType inventoryType;
    private final Map<Integer, MenuItem> items = new HashMap<>();

    private Inventory inventory;
    private BukkitTask updateTask;

    @Setter
    private Set<Integer> draggableSlots = new HashSet<>();
    @Setter
    private boolean autoUpdate = true;
    @Setter
    private int updateInterval = 20;
    @Setter
    private boolean parsePlaceholders = true;
    @Setter
    private boolean allowCustomItems = true;

    public Menu(Player player, InventoryType type) {
        this.player = player;
        this.inventoryType = type;

        MenuListener.register();
    }

    public Menu(Player player, ConfigurationSection section) {
        this(player, InventoryType.CHEST);
        this.config = section;
    }

    public Menu(Player player) {
        this(player, InventoryType.CHEST);
    }

    public MenuItem getItem(int slot) {
        return items.get(slot);
    }

    public MenuItem setItem(MenuItem item) {
        if (item.getSlots().contains(-1)) {
            return item;
        }

        item.getSlots().forEach(slot -> {
            if (slot < 0 || slot >= getRows() * 9) {
                TextUtil.error("Slot " + slot + " is out of bounds for menu '" + getTitle() + "'");
                return;
            }

            items.put(slot, item);
        });

        return item;
    }

    public MenuItem setItem(String key) {
        return setItem(key, null);
    }

    public MenuItem setItem(String key, ItemBuilder defaultItem) {
        ConfigurationSection section = config.getConfigurationSection("items." + key);

        if (section == null) {
            TextUtil.error("Missing item '" + key + "' in menu '" + getTitle() + "'");
            return null;
        }

        return setItem(new MenuItem(section, defaultItem, player));
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
                if (getRows() < 1 || getRows() > 6) {
                    TextUtil.error("Invalid row count for menu '" + getTitle() + "' (" + getRows() + ")");
                    return;
                }

                if (inventoryType != InventoryType.CHEST) {
                    this.inventory = Bukkit.createInventory(this, inventoryType, processText(getTitle()));
                } else {
                    this.inventory = Bukkit.createInventory(this, getRows() * 9, processText(getTitle()));
                }
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

    public void updateInventory() {
        items.clear();
        inventory.clear();

        // Custom items - lower priority
        updateCustomItems();
        // Default items - higher priority
        update();

        items.forEach((slot, item) -> {
            inventory.setItem(slot, processItem(item));
        });

        updateTitle();
    }

    public void addDraggableSlots(Integer... slots) {
        draggableSlots.addAll(Arrays.asList(slots));
    }

    public void addPlaceholders(PlaceholderMap map) {
        placeholders.merge(map);
    }

    public int getRows() {
        return config != null ? config.getInt("rows", 6) : 6;
    }

    public String getTitle() {
        return config != null ? config.getString("title", "Menu") : "Menu";
    }

    public void onClose() {}

    public abstract void update();

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void updateCustomItems() {
        if (!allowCustomItems || config == null || !config.isConfigurationSection("custom-items")) {
            return;
        }

        for (String key : config.getConfigurationSection("custom-items").getKeys(false)) {
            setItem(new MenuItem(config.getConfigurationSection("custom-items." + key), null, player));
        }
    }

    private void updateTitle() {
        if (!ReflectionUtil.supports(20)) {
            return;
        }

        if (!inventory.getViewers().contains(player)) {
            return;
        }

        String title = processText(getTitle());
        player.getOpenInventory().setTitle(title);
    }

    private ItemStack processItem(MenuItem item) {
        ItemStack stack = item.getItem().build();

        ItemUtil.parsePlaceholders(stack, placeholders);

        if (parsePlaceholders) {
            ItemUtil.parsePlaceholders(stack, player);
        }

        return stack;
    }

    private String processText(String text) {
        if (parsePlaceholders) {
            text = TextUtil.parsePlaceholders(player, text);
        }

        return placeholders.parse(text);
    }

}
