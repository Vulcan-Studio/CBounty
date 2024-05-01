package coma112.cbounty.menu.menus;

import coma112.cbounty.CBounty;
import coma112.cbounty.config.ConfigKeys;
import coma112.cbounty.item.IItemBuilder;
import coma112.cbounty.item.ItemBuilder;
import coma112.cbounty.language.MessageKeys;
import coma112.cbounty.managers.Bounty;
import coma112.cbounty.menu.PaginatedMenu;
import coma112.cbounty.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class BountiesMenu extends PaginatedMenu {
    public BountiesMenu(MenuUtils menuUtils) {
        super(menuUtils);
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.MENU_TITLE;
    }

    @Override
    public int getSlots() {
        return ConfigKeys.MENU_SIZE;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        List<Bounty> bounties = CBounty.getDatabaseManager().getBounties();

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        switch (event.getSlot()) {
            case 45 -> {
                if (page == 0) {
                    player.sendMessage(MessageKeys.FIRST_PAGE);
                } else {
                    page--;
                    super.open();
                }
            }

            case 53 -> {
                int nextPageIndex = page + 1;
                int totalPages = (int) Math.ceil((double) bounties.size() / getMaxItemsPerPage());

                if (nextPageIndex >= totalPages) {
                    player.sendMessage(MessageKeys.LAST_PAGE);
                } else {
                    page++;
                    super.open();
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        List<Bounty> bounties = CBounty.getDatabaseManager().getBounties();
        inventory.clear();
        addMenuBorder();

        int startIndex = page * getMaxItemsPerPage();
        int endIndex = Math.min(startIndex + getMaxItemsPerPage(), bounties.size());

        for (int i = startIndex; i < endIndex; i++) {
            Bounty bounty = bounties.get(i);
            ItemStack item = createBountyItem(bounty);
            inventory.addItem(item);
        }
    }

    private static ItemStack createBountyItem(@NotNull Bounty bounty) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(bounty.target());
        ItemStack itemStack = IItemBuilder.createItemFromSection("bounty-item");
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            String displayName = meta.getDisplayName()
                    .replace("{target}", bounty.target())
                    .replace("{id}", String.valueOf(bounty.id()));
            meta.setDisplayName(displayName);

            List<String> lore = meta.getLore();
            if (lore != null) {
                List<String> replacedLore = new ArrayList<>();
                for (String line : lore) {
                    replacedLore.add(line
                            .replace("{streak}", String.valueOf(CBounty.getDatabaseManager().getStreak(player)))
                            .replace("{reward_type}", bounty.reward_type().name())
                            .replace("{reward}", String.valueOf(bounty.reward()))
                            .replace("{player}", bounty.player()));
                }
                meta.setLore(replacedLore);
            }
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
