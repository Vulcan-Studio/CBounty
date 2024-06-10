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
    YES("messages.yes"),
    NO("messages.no"),
    DEPENDENCY_TOKENMANAGER("dependency.tokenmanager"),
    DEPENDENCY_PLAYERPOINTS("dependency.playerpoints"),
    DEPENDENCY_LEVEL("dependency.level"),
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

    WEBHOOK_RANDOM_SENDER("webhook.random-sender"),

    WEBHOOK_BOUNTY_DEATH_EMBED_TITLE("webhook.bounty-death-embed.title"),
    WEBHOOK_BOUNTY_DEATH_EMBED_DESCRIPTION("webhook.bounty-death-embed.description"),
    WEBHOOK_BOUNTY_DEATH_EMBED_COLOR("webhook.bounty-death-embed.color"),
    WEBHOOK_BOUNTY_DEATH_EMBED_AUTHOR_NAME("webhook.bounty-death-embed.author-name"),
    WEBHOOK_BOUNTY_DEATH_EMBED_AUTHOR_URL("webhook.bounty-death-embed.author-url"),
    WEBHOOK_BOUNTY_DEATH_EMBED_AUTHOR_ICON("webhook.bounty-death-embed.author-icon"),
    WEBHOOK_BOUNTY_DEATH_EMBED_FOOTER_TEXT("webhook.bounty-death-embed.footer-text"),
    WEBHOOK_BOUNTY_DEATH_EMBED_FOOTER_ICON("webhook.bounty-death-embed.footer-icon"),
    WEBHOOK_BOUNTY_DEATH_EMBED_THUMBNAIL("webhook.bounty-death-embed.thumbnail"),
    WEBHOOK_BOUNTY_DEATH_EMBED_IMAGE("webhook.bounty-death-embed.image"),

    WEBHOOK_BOUNTY_CREATE_EMBED_TITLE("webhook.bounty-create-embed.title"),
    WEBHOOK_BOUNTY_CREATE_EMBED_DESCRIPTION("webhook.bounty-create-embed.description"),
    WEBHOOK_BOUNTY_CREATE_EMBED_COLOR("webhook.bounty-create-embed.color"),
    WEBHOOK_BOUNTY_CREATE_EMBED_AUTHOR_NAME("webhook.bounty-create-embed.author-name"),
    WEBHOOK_BOUNTY_CREATE_EMBED_AUTHOR_URL("webhook.bounty-create-embed.author-url"),
    WEBHOOK_BOUNTY_CREATE_EMBED_AUTHOR_ICON("webhook.bounty-create-embed.author-icon"),
    WEBHOOK_BOUNTY_CREATE_EMBED_FOOTER_TEXT("webhook.bounty-create-embed.footer-text"),
    WEBHOOK_BOUNTY_CREATE_EMBED_FOOTER_ICON("webhook.bounty-create-embed.footer-icon"),
    WEBHOOK_BOUNTY_CREATE_EMBED_THUMBNAIL("webhook.bounty-create-embed.thumbnail"),
    WEBHOOK_BOUNTY_CREATE_EMBED_IMAGE("webhook.bounty-create-embed.image"),

    WEBHOOK_BOUNTY_REMOVE_EMBED_TITLE("webhook.bounty-remove-embed.title"),
    WEBHOOK_BOUNTY_REMOVE_EMBED_DESCRIPTION("webhook.bounty-remove-embed.description"),
    WEBHOOK_BOUNTY_REMOVE_EMBED_COLOR("webhook.bounty-remove-embed.color"),
    WEBHOOK_BOUNTY_REMOVE_EMBED_AUTHOR_NAME("webhook.bounty-remove-embed.author-name"),
    WEBHOOK_BOUNTY_REMOVE_EMBED_AUTHOR_URL("webhook.bounty-remove-embed.author-url"),
    WEBHOOK_BOUNTY_REMOVE_EMBED_AUTHOR_ICON("webhook.bounty-remove-embed.author-icon"),
    WEBHOOK_BOUNTY_REMOVE_EMBED_FOOTER_TEXT("webhook.bounty-remove-embed.footer-text"),
    WEBHOOK_BOUNTY_REMOVE_EMBED_FOOTER_ICON("webhook.bounty-remove-embed.footer-icon"),
    WEBHOOK_BOUNTY_REMOVE_EMBED_THUMBNAIL("webhook.bounty-remove-embed.thumbnail"),
    WEBHOOK_BOUNTY_REMOVE_EMBED_IMAGE("webhook.bounty-remove-embed.image");

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
