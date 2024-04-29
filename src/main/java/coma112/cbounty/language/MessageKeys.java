package coma112.cbounty.language;

import coma112.cbounty.CBounty;
import coma112.cbounty.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public class MessageKeys {
    public static String PREFIX = getString("prefix");
    public static String NO_PERMISSION = PREFIX + getString("messages.no-permission");
    public static String RELOAD = PREFIX + getString("messages.reload");
    public static String PLAYER_REQUIRED = PREFIX + getString("messages.player-required");
    public static String FIRST_PAGE = PREFIX + getString("messages.first-page");
    public static String LAST_PAGE = PREFIX + getString("messages.last-page");
    public static String SET_BOUNTY_USAGE = PREFIX + getString("messages.setbounty-usage");
    public static String INVALID_REWARDTYPE = PREFIX + getString("messages.invalid-rewardtype");
    public static String INVALID_NUMBER = PREFIX + getString("messages.invalid-number");
    public static String PLAYER_NOT_FOUND = PREFIX + getString("messages.player-not-found");
    public static String CANT_BE_YOURSELF = PREFIX + getString("messages.cant-be-yourself");
    public static String ALREADY_BOUNTY = PREFIX + getString("messages.already-bounty");
    public static String NO_NEGATIVE = PREFIX + getString("messages.no-negative");
    public static String SUCCESSFUL_SET = PREFIX + getString("messages.successful-set");
    public static String BOUNTY_DEAD_EVERYONE = PREFIX + getString("messages.bounty-dead-everyone");
    public static String BOUNTY_DEAD_TARGET = PREFIX + getString("messages.bounty-dead-target");
    public static String BOUNTY_DEAD_KILLER = PREFIX + getString("messages.bounty-dead-killer");
    public static String NOT_ENOUGH_TOKEN = PREFIX + getString("messages.not-enough-token");
    public static String NOT_ENOUGH_MONEY = PREFIX + getString("messages.not-enough-money");

    private static String getString(@NotNull String path) {
        return MessageProcessor.process(CBounty.getInstance().getLanguage().getString(path));
    }

}
