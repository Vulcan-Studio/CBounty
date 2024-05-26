package coma112.cbounty.config;

import coma112.cbounty.CBounty;
import coma112.cbounty.utils.ConfigUtils;

public class Config extends ConfigUtils {
    public Config() {
        super(CBounty.getInstance().getDataFolder().getPath(), "config");
        save();
    }
}

