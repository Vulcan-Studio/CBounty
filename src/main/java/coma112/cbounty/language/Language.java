package coma112.cbounty.language;

import coma112.cbounty.CBounty;
import coma112.cbounty.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Language extends ConfigUtils {
    public Language() {
        super(CBounty.getInstance().getDataFolder().getPath() + File.separator + "locales", "messages_en");
        save();
    }
}

