package coma112.cbounty.menu;

import coma112.cbounty.CBounty;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuUpdater extends BukkitRunnable {
    private final Menu menu;

    public MenuUpdater(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void run() {
        menu.setMenuItems();
        menu.menuUtils.getOwner().updateInventory();
    }

    public void start(int intervalTicks) {
        runTaskTimer(CBounty.getInstance(), intervalTicks, intervalTicks);
    }
}


