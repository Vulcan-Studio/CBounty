package coma112.cbounty;

import coma112.cbounty.config.Config;
import coma112.cbounty.database.AbstractDatabase;
import coma112.cbounty.database.MySQL;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.hooks.Token;
import coma112.cbounty.language.Language;
import coma112.cbounty.processor.BountyScheduler;
import coma112.cbounty.utils.BountyLogger;
import coma112.cbounty.utils.StartingUtils;
import lombok.Getter;
import me.realized.tokenmanager.api.TokenManager;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.SQLException;
import java.util.Objects;
import static coma112.cbounty.utils.StartingUtils.registerHooks;
import static coma112.cbounty.utils.StartingUtils.registerListenersAndCommands;

public final class CBounty extends JavaPlugin {
    @Getter public static TokenManager tokenManager;
    @Getter public static PlayerPointsAPI playerPointsManager;
    @Getter private static CBounty instance;
    @Getter private static AbstractDatabase databaseManager;
    private static Language language;
    private static Config config;
    private static Token token;

    @Override
    public void onLoad() {
        instance = this;

        StartingUtils.checkVersion();

    }

    @Override
    public void onEnable() {
        StartingUtils.checkVM();
        saveDefaultConfig();

        initializeComponents();
        registerListenersAndCommands();
        initializeDatabaseManager();
        registerHooks();

        MySQL mysql = (MySQL) databaseManager;
        mysql.createTable();

        if (ConfigKeys.RANDOM_BOUNTY_ENABLED.getBoolean()) new BountyScheduler().startScheduling();

        StartingUtils.checkUpdates();
        new Metrics(this, 22080);
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
    }

    public Language getLanguage() {
        return language;
    }

    public Config getConfiguration() {
        return config;
    }

    public Token getToken() {
        return token;
    }

    private void initializeComponents() {
        config = new Config();
        token = new Token();

        String languageCode = ConfigKeys.LANGUAGE.getString().toLowerCase();

        saveResource("locales/messages_en.yml", false);
        saveResource("locales/messages_hu.yml", false);
        saveResource("locales/messages_de.yml", false);

        language = new Language("messages_" + languageCode);
    }

    private void initializeDatabaseManager() {
        try {
            databaseManager = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
        } catch (SQLException | ClassNotFoundException exception) {
            BountyLogger.error(exception.getMessage());
        }
    }
}