package coma112.cbounty.enums.keys;

import coma112.cbounty.CBounty;
import coma112.cbounty.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

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
    DEPENDENCY_TOKENMANAGER("dependency.tokenmanager"),
    GLOWING_ENABLED("feature.glowing.enabled"),
    GLOWING_COLOR("feature.glowing.color"),
    RANDOM_BOUNTY_ENABLED("feature.random-bounty.enabled"),
    RANDOM_BOUNTY_REWARDTYPE("feature.random-bounty.rewardType"),
    RANDOM_BOUNTY_REWARD("feature.random-bounty.reward"),
    RANDOM_BOUNTY_PLAYER_VALUE("feature.random-bounty.player-value"),
    MAX_REWARD_LIMIT("max.rewardlimit"),
    RANDOM_BOUNTY_PER_SECOND("feature.random-bounty.per-second");

    private final String path;

    ConfigKeys(@NotNull String path) {
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
