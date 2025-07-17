package dev.pixelstudios.xutils.menu;

import dev.pixelstudios.xutils.SoundUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

@Getter @Setter
public abstract class PaginatedMenu<T> extends Menu {

    private static String SWITCH_PAGE_SOUND = "UI_BUTTON_CLICK";

    private int page;
    private boolean updateTitle;

    public PaginatedMenu(Player player, ConfigurationSection section) {
        super(player, section);

        placeholders.addNumber("previous_page", () -> page);
        placeholders.addNumber("page", () -> page + 1);
        placeholders.addNumber("next_page", () -> page + 2);
        placeholders.addNumber("max_pages", this::getMaxPages);
    }

    public PaginatedMenu(Player player) {
        this(player, null);
    }

    public static void setSwitchPageSound(String sound) {
        SWITCH_PAGE_SOUND = sound;
    }

    @Override
    public void update() {
        List<T> items = getPageItems();
        List<Integer> itemSlots = getItemSlots();

        for (int i = 0; i < items.size(); i++) {
            MenuItem item = getItem(items.get(i));
            if (item == null) continue;

            item.getSlots().clear();
            item.slots(itemSlots.get(i));

            setItem(item);
        }

        setPageButtons();
        update(page);
    }

    public boolean setPage(int page) {
        if (page >= 0 && page < getMaxPages()) {
            this.page = page;
            updateInventory();

            if (updateTitle) {
                updateTitle();
            }
            return true;
        }
        return false;
    }

    public boolean nextPage() {
        if (!isLastPage()) {
            page++;
            updateInventory();

            SoundUtil.play(player, SWITCH_PAGE_SOUND);

            if (updateTitle) {
                updateTitle();
            }
            return true;
        }
        return false;
    }

    public boolean previousPage() {
        if (!isFirstPage()) {
            page--;
            updateInventory();

            SoundUtil.play(player, SWITCH_PAGE_SOUND);

            if (updateTitle) {
                updateTitle();
            }
            return true;
        }
        return false;
    }

    public boolean isFirstPage() {
        return page == 0;
    }

    public boolean isLastPage() {
        return page == getMaxPages() - 1;
    }

    public int getMaxPages() {
        List<T> items = getAllItems();
        return (int) Math.max(Math.ceil(items.size() / (double) getItemSlots().size()), 1);
    }

    public List<T> getPageItems() {
        List<T> items = getAllItems();
        List<Integer> itemSlots = getItemSlots();

        int from = Math.min(page * itemSlots.size(), items.size());
        int to = Math.min((page + 1) * itemSlots.size(), items.size());

        return items.subList(from, to);
    }

    public abstract void update(int page);

    public abstract List<T> getAllItems();

    public abstract List<Integer> getItemSlots();

    public abstract MenuItem getItem(T object);

    private void setPageButtons() {
        if (config == null) return;

        if (!isFirstPage() && config.isConfigurationSection("items.previous-page")) {
            setItem("previous-page").action(this::previousPage);
        }

        if (!isLastPage() && config.isConfigurationSection("items.next-page")) {
            setItem("next-page").action(this::nextPage);
        }
    }

}
