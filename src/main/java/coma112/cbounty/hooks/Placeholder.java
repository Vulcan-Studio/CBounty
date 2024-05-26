package coma112.cbounty.hooks;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "cb";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Coma112";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
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

        return null;
    }

    public static void registerHook() {
        if (CBounty.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) new Placeholder().register();
    }
}
