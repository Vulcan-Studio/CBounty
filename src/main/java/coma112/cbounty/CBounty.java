package coma112.cbounty;

import coma112.cbounty.config.Config;
import coma112.cbounty.database.AbstractDatabase;
import coma112.cbounty.database.MySQL;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.hooks.Placeholder;
import coma112.cbounty.hooks.PlayerPoints;
import coma112.cbounty.hooks.Token;
import coma112.cbounty.hooks.vault.Vault;
import coma112.cbounty.language.Language;
import coma112.cbounty.processor.BountyScheduler;
import coma112.cbounty.update.UpdateChecker;
import coma112.cbounty.utils.BountyLogger;
import coma112.cbounty.utils.CommandRegister;
import coma112.cbounty.utils.ListenerRegister;
import coma112.cbounty.version.MinecraftVersion;
import coma112.cbounty.version.ServerVersionSupport;
import coma112.cbounty.version.VersionSupport;
import lombok.Getter;
import me.realized.tokenmanager.api.TokenManager;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CBounty extends JavaPlugin {
    @Getter
    public static TokenManager tokenManager;

    @Getter
    public static PlayerPointsAPI playerPointsManager;

    public static ServerVersionSupport nms;

    @Getter
    private static CBounty instance;

    @Getter
    private static AbstractDatabase databaseManager;

    private static Language language;
    private static Config config;
    private static Token token;
    private boolean isSupported;

    @Override
    public void onLoad() {
        instance = this;

        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception ignored) {
            isSupported = false;
            return;
        }

        VersionSupport support;
        try {
            int midVersion = Integer.parseInt((Bukkit.getServer().getClass().getName().split("\\.")[3]).split("_")[1]);

            if (midVersion <= 12) {
                isSupported = false;
                return;
            }

            BountyLogger.info("Found everything moving onto VersionSupport...");
            support = new VersionSupport(this, MinecraftVersion.getCurrentVersion());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            exception.printStackTrace();
            isSupported = false;
            return;
        }

        nms = support.getVersionSupport();
        isSupported = true;
    }

    @Override
    public void onEnable() {
        if (getVMVersion() < 11) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!isSupported) {
            BountyLogger.error("This version of CBounty is not supported on this server version.");
            BountyLogger.error("Please consider updating your server version to a newer version.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        saveResource("locales/messages_en.yml", false);

        initializeComponents();
        registerListenersAndCommands();
        initializeDatabaseManager();
        registerHooks();

        MySQL mysql = (MySQL) databaseManager;
        mysql.createTable();

        if (ConfigKeys.RANDOM_BOUNTY_ENABLED.getBoolean()) new BountyScheduler().startScheduling();

        new UpdateChecker(116501).getVersion(version -> {
            getLogger().info(this.getDescription().getVersion().equals(version) ? "Everything is up to date" : "You are using an outdated version! Please download the new version so that your server is always fresh! The newest version: " + version);
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
        PlayerPoints.register();
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

    int getVMVersion() {
        String javaVersion = System.getProperty("java.version");
        Matcher matcher = Pattern.compile("(?:1\\.)?(\\d+)").matcher(javaVersion);
        if (!matcher.find()) return -1;
        String version = matcher.group(1);

        try {
            return Integer.parseInt(version);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}