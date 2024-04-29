package coma112.cbounty.hooks;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {
    @Getter
    private static Economy economy = null;

    private Vault() {}

    private static void setupEconomy() {
        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (rsp != null) economy = rsp.getProvider();
    }

    static {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) setupEconomy();
    }

}
