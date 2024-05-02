package coma112.cbounty.hooks;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import me.realized.tokenmanager.api.TokenManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;

public class Token {
    public int getTokens(@NotNull Player player) {
        OptionalLong tokensOptional =  CBounty.getTokenManager().getTokens(player);
        return tokensOptional.isPresent() ? (int) tokensOptional.getAsLong() : 0;
    }

    public static void register() {
        if (ConfigKeys.DEPENDENCY_TOKENMANAGER.getBoolean()) {
            if (CBounty.getInstance().getServer().getPluginManager().getPlugin("TokenManager") != null) CBounty.tokenManager = (TokenManager) CBounty.getInstance().getServer().getPluginManager().getPlugin("TokenManager");
        }
    }

    public boolean isEnabled() {
        return ConfigKeys.DEPENDENCY_TOKENMANAGER.getBoolean();
    }
}