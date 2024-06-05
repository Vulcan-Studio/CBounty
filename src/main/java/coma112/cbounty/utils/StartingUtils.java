package coma112.cbounty.utils;

import coma112.cbounty.CBounty;
import coma112.cbounty.hooks.Placeholder;
import coma112.cbounty.hooks.PlayerPoints;
import coma112.cbounty.hooks.Token;
import coma112.cbounty.hooks.Vault;
import coma112.cbounty.update.UpdateChecker;
import coma112.cbounty.version.MinecraftVersion;
import coma112.cbounty.version.ServerVersionSupport;
import coma112.cbounty.version.VersionSupport;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartingUtils {
    private static boolean isSupported;

    public static void registerHooks() {
        Placeholder.registerHook();
        Vault.register();
        Token.register();
        PlayerPoints.register();
    }

    public static void registerListenersAndCommands() {
        ListenerRegister.registerEvents();
        CommandRegister.registerCommands();
    }

    public static void checkVM() {
        if (getVMVersion() < 11) {
            Bukkit.getPluginManager().disablePlugin(CBounty.getInstance());
            return;
        }

        if (!isSupported) {
            BountyLogger.error("This version of CBounty is not supported on this server version.");
            BountyLogger.error("Please consider updating your server version to a newer version.");
            CBounty.getInstance().getServer().getPluginManager().disablePlugin(CBounty.getInstance());
        }
    }

    public static void checkVersion() {
        VersionSupport support;

        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception ignored) {
            isSupported = false;
            return;
        }

        try {
            int midVersion = Integer.parseInt((Bukkit.getServer().getClass().getName().split("\\.")[3]).split("_")[1]);

            if (midVersion <= 12) {
                isSupported = false;
                return;
            }

            BountyLogger.info("Found everything moving onto VersionSupport...");
            support = new VersionSupport(CBounty.getInstance(), MinecraftVersion.getCurrentVersion());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            BountyLogger.error(exception.getMessage());
            isSupported = false;
            return;
        }

        ServerVersionSupport nms = support.getVersionSupport();
        isSupported = true;
    }

    public static void checkUpdates() {
        new UpdateChecker(116501).getVersion(version -> {
            BountyLogger.info(CBounty.getInstance().getDescription().getVersion().equals(version) ? "Everything is up to date" : "You are using an outdated version! Please download the new version so that your server is always fresh! The newest version: " + version);
        });
    }

    static int getVMVersion() {
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
