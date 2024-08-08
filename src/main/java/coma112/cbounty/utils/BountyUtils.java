package coma112.cbounty.utils;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.FormatType;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.hooks.CoinsEngine;
import coma112.cbounty.hooks.Vault;
import coma112.cbounty.processor.MessageProcessor;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("deprecation")
public final class BountyUtils {
    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MessageProcessor.process(message)));
    }

    public static boolean hasItem(@NotNull Inventory inventory, @NotNull ItemStack item) {
        return Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .anyMatch(stack -> stack.isSimilar(item));
    }

    public static boolean handleTokenReward(@NotNull Player player, int reward) {
        if (CBounty.getInstance().getToken().getTokens(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN.getMessage());
            return false;
        } else {
            CBounty.getTokenManager().removeTokens(player, reward);
            return true;
        }
    }

    public static boolean handleMoneyReward(@NotNull Player player, int reward) {
        Economy economy = Vault.getEconomy();

        if (economy.getBalance(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
            return false;
        } else {
            economy.withdrawPlayer(player, reward);
            return true;
        }
    }

    public static boolean handlePlayerPointsReward(@NotNull Player player, int reward) {
        PlayerPointsAPI api = CBounty.getPlayerPointsManager();
        UUID uuid = player.getUniqueId();

        if (api.look(uuid) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_PLAYERPOINTS.getMessage());
            return false;
        } else {
            api.take(uuid, reward);
            return true;
        }
    }

    public static boolean handleCoinsEngineReward(@NotNull Player player, int reward) {
        if (CoinsEngineAPI.getBalance(player, CoinsEngine.getCurrency()) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_COINSENGINE.getMessage());
            return false;
        } else {
            CoinsEngineAPI.removeBalance(player, CoinsEngine.getCurrency(), reward);
            return true;
        }
    }

    public static boolean handleLevelReward(@NotNull Player player, int reward) {
        if (player.getLevel() < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_LEVEL.getMessage());
            return false;
        } else {
            player.setLevel(player.getLevel() - reward);
            return true;
        }
    }

    public static NamedTextColor getNamedTextColor(@NotNull String colorName) {
        return switch (colorName) {
            case "BLACK", "black" -> NamedTextColor.namedColor(0);
            case "DARK_BLUE", "dark_blue" -> NamedTextColor.namedColor(170);
            case "DARK_GREEN", "dark_green" -> NamedTextColor.namedColor(43520);
            case "DARK_AQUA", "dark_aqua" -> NamedTextColor.namedColor(43690);
            case "DARK_GRAY", "dark_gray" -> NamedTextColor.namedColor(5592405);
            case "BLUE", "blue" -> NamedTextColor.namedColor(5592575);
            case "GREEN", "green" -> NamedTextColor.namedColor(5635925);
            case "AQUA", "aqua" -> NamedTextColor.namedColor(5636095);
            case "DARK_RED", "dark_red" -> NamedTextColor.namedColor(11141120);
            case "DARK_PURPLE", "dark_purple" -> NamedTextColor.namedColor(11141290);
            case "GRAY", "gray" -> NamedTextColor.namedColor(11184810);
            case "RED", "red" -> NamedTextColor.namedColor(16733525);
            case "LIGHT_PURPLE", "light_purple" -> NamedTextColor.namedColor(16733695);
            case "GOLD", "gold" -> NamedTextColor.namedColor(16755200);
            case "YELLOW", "yellow" -> NamedTextColor.namedColor(16777045);
            case "WHITE", "white" -> NamedTextColor.namedColor(16777215);
            default -> null;
        };
    }


    public static int getMinimumReward(RewardType rewardType) {
        return switch (rewardType) {
            case TOKEN -> ConfigKeys.DEPENDENCY_TOKENMANAGER_MIN.getInt();
            case MONEY -> ConfigKeys.DEPENDENCY_MONEY_MIN.getInt();
            case PLAYERPOINTS -> ConfigKeys.DEPENDENCY_PLAYERPOINTS_MIN.getInt();
            case LEVEL -> ConfigKeys.DEPENDENCY_LEVEL_MIN.getInt();
            case COINSENGINE -> ConfigKeys.DEPENDENCY_COINSENGINE_MIN.getInt();
        };
    }

    public static int getMaximumReward(RewardType rewardType) {
        return switch (rewardType) {
            case TOKEN -> ConfigKeys.DEPENDENCY_TOKENMANAGER_MAX.getInt();
            case MONEY -> ConfigKeys.DEPENDENCY_MONEY_MAX.getInt();
            case PLAYERPOINTS -> ConfigKeys.DEPENDENCY_PLAYERPOINTS_MAX.getInt();
            case LEVEL -> ConfigKeys.DEPENDENCY_LEVEL_MAX.getInt();
            case COINSENGINE -> ConfigKeys.DEPENDENCY_COINSENGINE_MAX.getInt();
        };
    }

    public static String formatPrice(int price) {
        if (!ConfigKeys.FORMATTING_ENABLED.getBoolean()) return String.valueOf(price);

        return switch (FormatType.valueOf(ConfigKeys.FORMATTING_TYPE.getString())) {
            case DOT, dot -> String.format("%,d", price).replace(",", ".");
            case COMMAS, commas -> String.format("%,d", price);
            case BASIC, basic -> {
                DecimalFormat formatter = new DecimalFormat("#.#");

                if (price < 1000) yield String.valueOf(price);
                else if (price < 1000000) yield formatter.format(price / 1000.0) + ConfigKeys.BASIC_FORMAT_M.getString();
                else yield formatter.format(price / 1000000.0) + ConfigKeys.BASIC_FORMAT_K.getString();
            }
        };
    }
}
