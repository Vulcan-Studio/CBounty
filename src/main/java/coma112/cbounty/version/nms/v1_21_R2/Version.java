package coma112.cbounty.version.nms.v1_21_R2;

import coma112.cbounty.interfaces.ServerVersionSupport;
import coma112.cbounty.utils.BountyLogger;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Version implements ServerVersionSupport {

    @Contract(pure = true)
    public Version(@NotNull Plugin plugin) {
        BountyLogger.info("Loading support for version 1.21.1...");
        BountyLogger.info("Support for 1.21.1 is loaded!");
    }

    @Override
    public String getName() {
        return "1.21_R2";
    }

    @Override
    public boolean isSupported() {
        return true;
    }
}
