package coma112.cbounty.enums.keys;

import coma112.cbounty.CBounty;
import coma112.cbounty.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public enum ConfigKeys {
    LANGUAGE("language"),
    DATABASE("database.type"),
    MAXIMUM_TOP("max.top-to-get"),
    MAXIMUM_BOUNTY("max.bounty-per-player"),
    MENU_SIZE("menu.size"),
    BACK_SLOT("menu.back-item.slot"),
    FORWARD_SLOT("menu.forward-item.slot"),
    MENU_TICK("menu.update-tick"),
    MENU_TITLE("menu.title"),
    YES("placeholder.yes"),
    NO("placeholder.no"),
    BASIC_FORMAT_M("formatting.basic.million"),
    BASIC_FORMAT_K("formatting.basic.thousand"),
    FORMATTING_TYPE("formatting.type"),
    FORMATTING_ENABLED("formatting.enabled"),
    DEPENDENCY_TOKENMANAGER("dependency.tokenmanager.enabled"),
    DEPENDENCY_TOKENMANAGER_MAX("dependency.tokenmanager.max"),
    DEPENDENCY_TOKENMANAGER_MIN("dependency.tokenmanager.min"),
    BOUNTYFINDER_ENABLED("bountyfinder-item.enabled"),

    DEPENDENCY_PLAYERPOINTS("dependency.playerpoints.enabled"),
    DEPENDENCY_PLAYERPOINTS_MAX("dependency.playerpoints.max"),
    DEPENDENCY_PLAYERPOINTS_MIN("dependency.playerpoints.min"),
    DEPENDENCY_COINSENGINE("dependency.coinsengine.enabled"),
    DEPENDENCY_COINSENGINE_MAX("dependency.coinsengine.max"),
    DEPENDENCY_COINSENGINE_MIN("dependency.coinsengine.min"),
    DEPENDENCY_COINSENGINE_CURRENCY("dependency.coinsengine.currency"),
    DEPENDENCY_LEVEL("dependency.level.enabled"),
    DEPENDENCY_LEVEL_MAX("dependency.level.max"),
    DEPENDENCY_LEVEL_MIN("dependency.level.min"),
    DEPENDENCY_MONEY_MIN("dependency.money.min"),
    DEPENDENCY_MONEY_MAX("dependency.money.max"),
    SET_BOUNTY_ON_DEATH("set-bounty-on-death"),

    GLOWING_ENABLED("feature.glowing.enabled"),
    WEBHOOK_ENABLED("webhook.enabled"),
    WEBHOOK_URL("webhook.url"),
    GLOWING_COLOR("feature.glowing.color"),
    RANDOM_BOUNTY_ENABLED("feature.random-bounty.enabled"),
    RANDOM_BOUNTY_REWARDTYPE("feature.random-bounty.rewardType"),
    RANDOM_BOUNTY_REWARD("feature.random-bounty.reward"),
    RANDOM_BOUNTY_PLAYER_VALUE("feature.random-bounty.player-value"),
    MAX_REWARD_LIMIT("max.rewardlimit"),
    NO_BOUNTY_NEARBY("feature.bountyfinder.no-bounty-nearby"),
    NEAREST_BOUNTY("feature.bountyfinder.nearest-bounty"),
    RANDOM_BOUNTY_PER_SECOND("feature.random-bounty.per-second"),

    WEBHOOK_RANDOM_SENDER("webhook.random-sender");

    private final String path;

    ConfigKeys(@NotNull String path) {
        this.path = path;
    }

    public String getString() { return MessageProcessor.process(CBounty.getInstance().getConfiguration().getString(path)); }

    public int getInt() {
        return CBounty.getInstance().getConfiguration().getInt(path);
    }

    public boolean getBoolean() {
        return CBounty.getInstance().getConfiguration().getBoolean(path);
    }




}
