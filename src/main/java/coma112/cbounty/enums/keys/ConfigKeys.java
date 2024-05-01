package coma112.cbounty.enums.keys;

import coma112.cbounty.CBounty;
import coma112.cbounty.processor.MessageProcessor;

public enum ConfigKeys {
    MAXIMUM_TOP("max.top-to-get"),
    MAXIMUM_BOUNTY("max.bounty-per-player"),
    MENU_SIZE("menu.size"),
    BACK_SLOT("menu.back-item.slot"),
    FORWARD_SLOT("menu.forward-item.slot"),
    MENU_TICK("menu.update-tick"),
    MENU_TITLE("menu.title"),
    YES("messages.yes"),
    NO("messages.no"),
    DEPENDENCY_TOKENMANAGER("dependency.tokenmanager");

    private final String path;

    ConfigKeys(String path) {
        this.path = path;
    }

    public String getString() {
        return MessageProcessor.process(CBounty.getInstance().getConfiguration().getString(path));
    }

    public int getInt() {
        return CBounty.getInstance().getConfiguration().getInt(path);
    }

    public boolean getBoolean() {
        return CBounty.getInstance().getConfiguration().getBoolean(path);
    }
}
