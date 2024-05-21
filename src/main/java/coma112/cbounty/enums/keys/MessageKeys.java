package coma112.cbounty.enums.keys;

import coma112.cbounty.CBounty;
import coma112.cbounty.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public enum MessageKeys {
        NO_PERMISSION("messages.no-permission"),
        RELOAD("messages.reload"),
        PLAYER_REQUIRED("messages.player-required"),
        FIRST_PAGE("messages.first-page"),
        LAST_PAGE("messages.last-page"),
        SET_BOUNTY_USAGE("messages.setbounty-usage"),
        INVALID_REWARDTYPE("messages.invalid-rewardtype"),
        INVALID_NUMBER("messages.invalid-number"),
        PLAYER_NOT_FOUND("messages.player-not-found"),
        CANT_BE_YOURSELF("messages.cant-be-yourself"),
        ALREADY_BOUNTY("messages.already-bounty"),
        NO_NEGATIVE("messages.no-negative"),
        SUCCESSFUL_SET("messages.successful-set"),
        BOUNTY_DEAD_EVERYONE("messages.bounty-dead-everyone"),
        BOUNTY_DEAD_TARGET("messages.bounty-dead-target"),
        BOUNTY_DEAD_KILLER("messages.bounty-dead-killer"),
        NOT_ENOUGH_TOKEN("messages.not-enough-token"),
        NOT_ENOUGH_MONEY("messages.not-enough-money"),
        MAX_TOP("messages.maximum-top"),
        TOP_HEADER("messages.top.header"),
        TOP_MESSAGE("messages.top.message"),
        MAX_BOUNTY("messages.max-bounty"),
        FEATURE_DISABLED("messages.feature-disabled"),
        NOT_BOUNTY("messages.not-a-bounty"),
        MAX_REWARD_LIMIT("messages.max-reward-limit"),
        REMOVE_PLAYER("successful-remove-player"),
        REMOVE_TARGET("successful-remove-target"),
        FEATURE_DISABLED_EVENT("messages.feature-disabled-event");

        private final String path;

        MessageKeys(@NotNull String path) {
            this.path = path;
        }

        public String getMessage() {
            return MessageProcessor.process(CBounty.getInstance().getLanguage().getString(path));
        }
    }
