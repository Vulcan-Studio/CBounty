package coma112.cbounty.hooks;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.FormatType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.utils.BountyUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@SuppressWarnings("deprecation")
public class Placeholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "cb";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Coma112 & B3rcyy (Vulcan Studio)";
    }

    @Override
    public @NotNull String getVersion() {
        return CBounty.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {
        if (params.equals("isbounty")) return CBounty.getDatabaseManager().isBounty(player) ? ConfigKeys.YES.getString() : ConfigKeys.NO.getString();

        if (params.startsWith("top_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (CBounty.getDatabaseManager().getTopStreakPlayer(position) != null) return CBounty.getDatabaseManager().getTopStreakPlayer(position);
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        if (params.startsWith("topstreak_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (CBounty.getDatabaseManager().getTopStreak(position) != 0) return String.valueOf(CBounty.getDatabaseManager().getTopStreak(position));
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        if (params.startsWith("price_")) {
            try {
                Player selectedPlayer = Bukkit.getPlayerExact(String.valueOf(params.split("_")[1]));

                if (selectedPlayer != null && CBounty.getDatabaseManager().isBounty(selectedPlayer)) return BountyUtils.formatPrice(CBounty.getDatabaseManager().getReward(selectedPlayer));
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        return switch (params) {
            case "price" -> {
                if (!CBounty.getDatabaseManager().isBounty(player)) yield "";
                else yield BountyUtils.formatPrice(CBounty.getDatabaseManager().getReward(player));
            }

            case "rewardtype" -> {
                if (!CBounty.getDatabaseManager().isBounty(player)) yield "";
                else yield String.valueOf(CBounty.getDatabaseManager().getRewardType(player));
            }

            case "sender" -> {
                if (!CBounty.getDatabaseManager().isBounty(player)) yield "";
                else yield CBounty.getDatabaseManager().getSender(player).getName();
            }

            default -> "";
        };
    }

    public static void registerHook() {
        new Placeholder().register();
    }
}
