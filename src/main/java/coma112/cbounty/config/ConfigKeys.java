package coma112.cbounty.config;

import coma112.cbounty.CBounty;
import coma112.cbounty.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public class ConfigKeys {

    public static int MAXIMUM_TOP = getInt("max.top-to-get");
    public static int MAXIMUM_BOUNTY = getInt("max.bounty-per-player");
    public static int MENU_SIZE = getInt("menu.size");
    public static int BACK_SLOT = getInt("menu.back-item.slot");
    public static int FORWARD_SLOT = getInt("menu.forward-item.slot");
    public static int MENU_TICK = getInt("menu.update-tick");

    public static String MENU_TITLE = getString("menu.title");

    private static String getString(@NotNull String path) {
        return MessageProcessor.process(CBounty.getInstance().getConfiguration().getString(path));
    }

    private static int getInt(@NotNull String path) {
        return CBounty.getInstance().getConfiguration().getInt(path);
    }
}
