package dev.pixelstudios.xutils.item.provider;

import dev.pixelstudios.xutils.item.ItemProvider;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.inventory.ItemStack;

public class CraftEngineProvider extends ItemProvider {

    @Override
    public String[] getIds() {
        return new String[] {
            "craftengine", "ce"
        };
    }

    @Override
    public String getPlugin() {
        return "CraftEngine";
    }

    @Override
    public ItemStack getItem(String item) {
        CustomItem<ItemStack> customItem = CraftEngineItems.byId(Key.of(item));
        return customItem == null ? null : customItem.buildItemStack();
    }

    @Override
    public String getItemKey(ItemStack item) {
        Key key = CraftEngineItems.getCustomItemId(item);
        return key == null ? null : key.asString();
    }

}
