package me.cubecrafter.xutils.menu;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class PaginatedMenu extends Menu {

    private int page;

    public PaginatedMenu(Player player) {
        super(player);
    }

    @Override
    public void update() {
        update(page);
    }

    public boolean nextPage() {
        if (page < Math.max(0, getMaxPages() - 1)) {
            page++;
            updateInventory();
            return true;
        }
        return false;
    }

    public boolean previousPage() {
        if (page > 0) {
            page--;
            updateInventory();
            return true;
        }
        return false;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isFirstPage() {
        return page == 0;
    }

    public boolean isLastPage() {
        return page == getMaxPages() - 1;
    }

    public abstract int getMaxPages();
    public abstract void update(int page);

    /**
     * Returns a sublist of the given list based on the current page and the items per page.
     * @param list The list to get the sublist from
     * @param itemsPerPage The amount of items per page
     * @return The sublist
     */
    public <T> List<T> getPageItems(List<T> list, int itemsPerPage) {
        int fromIndex = Math.min(page * itemsPerPage, list.size());
        int toIndex = Math.min((page + 1) * itemsPerPage, list.size());
        return list.subList(fromIndex, toIndex);
    }

    /**
     * Calculates the maximum amount of pages based on the total amount of items and the items per page.
     * @param items The list of items
     * @param itemsPerPage The amount of items per page
     * @return The maximum amount of pages
     */
    public int calculateMaxPages(List<?> items, int itemsPerPage) {
        return (int) Math.max(Math.ceil(items.size() / (double) itemsPerPage), 1);
    }

}
