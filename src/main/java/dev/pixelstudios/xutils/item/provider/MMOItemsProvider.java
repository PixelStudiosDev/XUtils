package dev.pixelstudios.xutils.item.provider;

import dev.pixelstudios.xutils.item.ItemProvider;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.inventory.ItemStack;

public class MMOItemsProvider extends ItemProvider {

    @Override
    public String[] getIds() {
        return new String[] {
            "mmoitems"
        };
    }

    @Override
    public String getPlugin() {
        return "MMOItems";
    }

    @Override
    public ItemStack getItem(String item) {
        String[] parts = item.split(":", 2);

        if (parts.length != 2) return null;

        return MMOItems.plugin.getItem(parts[0], parts[1]);
    }

    @Override
    public String getItemKey(ItemStack item) {
        return MMOItems.getID(item);
    }

}
