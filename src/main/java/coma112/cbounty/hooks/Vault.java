package coma112.cbounty.hooks;

import coma112.cbounty.CBounty;
import coma112.cbounty.utils.BountyLogger;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class Vault {
    @Getter private static Economy economy = null;

    private Vault() {}

    private static void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = CBounty.getInstance().getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp != null) economy = rsp.getProvider();
    }

    static {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) setupEconomy();
    }
}
