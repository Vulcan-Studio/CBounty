package coma112.cbounty;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.palmergames.bukkit.towny.TownyAPI;
import coma112.cbounty.config.Config;
import coma112.cbounty.database.AbstractDatabase;
import coma112.cbounty.database.MySQL;
import coma112.cbounty.database.SQLite;
import coma112.cbounty.enums.DatabaseType;
import coma112.cbounty.enums.LanguageType;
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

import static coma112.cbounty.utils.StartingUtils.*;

public final class CBounty extends JavaPlugin {
    @Getter private static CBounty instance;
    @Getter private static AbstractDatabase databaseManager;
    @Getter private Language language;
    @Getter private Token token;
    @Getter private TaskScheduler scheduler;
    private Config config;

    @Getter public static TokenManager tokenManager;
    @Getter public static PlayerPointsAPI playerPointsManager;
    @Getter public static TownyAPI townyAPI;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);

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
        loadBasicFormatOverrides();

        new BountyScheduler().startScheduling();

        StartingUtils.checkUpdates();
        new Metrics(this, 22080);
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        config = new Config();
        token = new Token();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_hu.yml");
        saveResourceIfNotExists("locales/messages_de.yml");
        saveResourceIfNotExists("locales/messages_tr.yml");
        saveResourceIfNotExists("locales/messages_es.yml");

        language = new Language("messages_" + LanguageType.valueOf(ConfigKeys.LANGUAGE.getString()));
    }

    private void initializeDatabaseManager() {
        try {
            switch (DatabaseType.valueOf(ConfigKeys.DATABASE.getString())) {
                case MYSQL, mysql -> {
                    databaseManager = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
                    MySQL mysql = (MySQL) databaseManager;
                    mysql.createTable();
                }

                case SQLITE, sqlite -> {
                    databaseManager = new SQLite();
                    SQLite sqlite = (SQLite) databaseManager;
                    sqlite.createTable();
                }
            }
        } catch (SQLException | ClassNotFoundException exception) {
            BountyLogger.error(exception.getMessage());
        }
    }
}