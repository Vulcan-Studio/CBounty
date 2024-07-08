package coma112.cbounty.hooks;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import lombok.Getter;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

public class CoinsEngine {
    @Getter private static Currency currency;

    public static void register() {
        if (isEnabled() && CBounty.getInstance().getServer().getPluginManager().getPlugin("CoinsEngine") != null)
            currency = CoinsEngineAPI.getCurrency(ConfigKeys.DEPENDENCY_COINSENGINE_CURRENCY.getString());
    }

    public static boolean isEnabled() {
        return ConfigKeys.DEPENDENCY_COINSENGINE.getBoolean();
    }
}
