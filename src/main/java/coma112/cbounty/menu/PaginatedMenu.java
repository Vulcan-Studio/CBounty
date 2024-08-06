package coma112.cbounty.menu;

import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.ItemKeys;
import coma112.cbounty.item.IItemBuilder;
import coma112.cbounty.managers.Bounty;
import coma112.cbounty.utils.MenuUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int nextPage = page + 1;
    protected int totalPages = (int) Math.ceil((double) getList().size() / getMaxItemsPerPage());
    protected int startIndex = page * getMaxItemsPerPage();
    protected int endIndex = Math.min(startIndex + getMaxItemsPerPage(), getList().size());
    @Getter protected int maxItemsPerPage = ConfigKeys.MENU_SIZE.getInt() - 2;

    public abstract List<Bounty> getList();

    public PaginatedMenu(@NotNull MenuUtils menuUtils) {
        super(menuUtils);
    }

    public void addMenuBorder() {
        inventory.setItem(ConfigKeys.BACK_SLOT.getInt(), ItemKeys.BACK_ITEM.getItem());
        inventory.setItem(ConfigKeys.FORWARD_SLOT.getInt(), ItemKeys.FORWARD_ITEM.getItem());
    }
}

