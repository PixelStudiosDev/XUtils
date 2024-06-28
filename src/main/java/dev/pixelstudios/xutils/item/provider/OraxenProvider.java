package dev.pixelstudios.xutils.item.provider;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class OraxenProvider extends ItemProvider {

    @Override
    public String[] getIds() {
        return new String[] {
            "oraxen", "oxn"
        };
    }

    @Override
    public String getPlugin() {
        return "Oraxen";
    }

    @Override
    public ItemStack getItem(String item) {
        ItemBuilder builder = OraxenItems.getItemById(item);
        return builder == null ? null : builder.build();
    }

    @Override
    public String getItemKey(ItemStack item) {
        return OraxenItems.getIdByItem(item);
    }

}
