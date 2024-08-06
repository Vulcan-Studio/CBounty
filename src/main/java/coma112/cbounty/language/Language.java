package coma112.cbounty.language;

import coma112.cbounty.CBounty;
import coma112.cbounty.utils.ConfigUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Language extends ConfigUtils {
    public Language(@NotNull String name) {
        super(CBounty.getInstance().getDataFolder().getPath() + File.separator + "locales", name);
        save();
    }
}

