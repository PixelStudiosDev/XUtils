package dev.pixelstudios.xutils.item.provider;

import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class EcoItemsProvider extends ItemProvider {

    @Override
    public String[] getIds() {
        return new String[] {
            "ecoitems"
        };
    }

    @Override
    public String getPlugin() {
        return "EcoItems";
    }

    @Override
    public ItemStack getItem(String item) {
        TestableItem testableItem = Items.lookup("ecoitems:" + item);
        return testableItem instanceof EmptyTestableItem ? null : testableItem.getItem();
    }

    @Override
    public String getItemKey(ItemStack item) {
        CustomItem customItem = Items.getCustomItem(item);
        if (customItem == null) return null;

        NamespacedKey key = customItem.getKey();

        return key.getNamespace().equals("ecoitems") ? key.getKey() : null;
    }

}
