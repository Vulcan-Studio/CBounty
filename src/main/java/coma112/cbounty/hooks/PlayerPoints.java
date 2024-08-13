package coma112.cbounty.hooks;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;

public class PlayerPoints {
    public static void register() {
        if (ConfigKeys.DEPENDENCY_PLAYERPOINTS.getBoolean() && CBounty.getInstance().getServer().getPluginManager().getPlugin("PlayerPoints") != null) CBounty.playerPointsManager = org.black_ixx.playerpoints.PlayerPoints.getInstance().getAPI();
    }

    public static boolean isEnabled() {
        return ConfigKeys.DEPENDENCY_PLAYERPOINTS.getBoolean();
    }
}