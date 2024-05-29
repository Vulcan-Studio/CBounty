package coma112.cbounty.version.nms.v1_19_R3;

import coma112.cbounty.utils.BountyLogger;
import coma112.cbounty.version.ServerVersionSupport;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Version implements ServerVersionSupport {

    @Contract(pure = true)
    public Version(@NotNull Plugin plugin) {
        BountyLogger.info("Loading support for version 1.19.3...");
        BountyLogger.info("Support for 1.19.3 is loaded!");
    }

    @Override
    public String getName() {
        return "1.19_R3";
    }

    @Override
    public boolean isSupported() {
        return true;
    }
}
