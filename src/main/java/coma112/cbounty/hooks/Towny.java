package coma112.cbounty.hooks;

import com.palmergames.bukkit.towny.TownyAPI;
import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;

public class Towny {
    public static void register() {
        if (isEnabled() && CBounty.getInstance().getServer().getPluginManager().getPlugin("Towny") != null) CBounty.townyAPI = TownyAPI.getInstance();
    }

    public static boolean isEnabled() {
        return ConfigKeys.DEPENDENCY_TOWNY_ENABLED.getBoolean();
    }
}
