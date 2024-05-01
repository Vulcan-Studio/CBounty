package coma112.cbounty;

import coma112.cbounty.config.Config;
import coma112.cbounty.database.AbstractDatabase;
import coma112.cbounty.database.MySQL;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.hooks.BountyEconomy;
import coma112.cbounty.hooks.Placeholder;
import coma112.cbounty.hooks.Vault;
import coma112.cbounty.language.Language;
import coma112.cbounty.hooks.Token;
import coma112.cbounty.update.UpdateChecker;
import coma112.cbounty.utils.CommandRegister;
import coma112.cbounty.utils.ListenerRegister;
import lombok.Getter;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

@SuppressWarnings("deprecation")
public final class CBounty extends JavaPlugin {
    @Getter public static TokenManager tokenManager;

    @Getter private static CBounty instance;
    @Getter private static AbstractDatabase databaseManager;
    private static Language language;
    private static Config config;
    private static Token token;

    @Override
    public void onEnable() {
        instance = this;

        initializeComponents();
        registerListenersAndCommands();
        initializeDatabaseManager();
        registerHooks();

        MySQL mysql = (MySQL) databaseManager;
        mysql.createTable();

        new UpdateChecker(116501).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("Everything is up to date");
            } else {
                getLogger().warning("You are using an outdated version! Please download the new version so that your server is always fresh! The newest version: " + version);
            }
        });
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
        language = new Language();
        config = new Config();
        token = new Token();
    }

    private void registerHooks() {
        Placeholder.registerHook();
        Vault.register();
        Token.register();
    }

    private void registerListenersAndCommands() {
        ListenerRegister.registerEvents();
        CommandRegister.registerCommands();
    }

    private void initializeDatabaseManager() {
        try {
            databaseManager = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
        } catch (SQLException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }
}
