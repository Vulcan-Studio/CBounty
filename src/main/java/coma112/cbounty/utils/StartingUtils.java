package coma112.cbounty.utils;

import com.github.Anon8281.universalScheduler.utils.JavaUtil;
import coma112.cbounty.CBounty;
import coma112.cbounty.hooks.*;
import coma112.cbounty.update.UpdateChecker;
import coma112.cbounty.version.MinecraftVersion;
import coma112.cbounty.version.ServerVersionSupport;
import coma112.cbounty.version.VersionSupport;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class StartingUtils {
    public static final boolean isFolia = JavaUtil.classExists("io.papermc.paper.threadedregions.RegionizedServer");
    private static boolean isSupported;

    public static void registerHooks() {
        Placeholder.registerHook();
        Token.register();
        PlayerPoints.register();
        CoinsEngine.register();
    }

    public static void registerListenersAndCommands() {
        RegisterUtils.registerEvents();
        RegisterUtils.registerCommands();
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
            String[] classParts = Bukkit.getServer().getClass().getName().split("\\.");
            if (classParts.length < 4) {
                BountyLogger.error("Unexpected server class name format: " + Bukkit.getServer().getClass().getName());
                isSupported = false;
                return;
            }
            String[] versionParts = classParts[3].split("_");
            if (versionParts.length < 2) {
                BountyLogger.error("Unexpected version format in class name: " + classParts[3]);
                isSupported = false;
                return;
            }
            int midVersion = Integer.parseInt(versionParts[1]);

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

    public static void saveResourceIfNotExists(@NotNull String resourcePath) {
        if (!new File(CBounty.getInstance().getDataFolder(), resourcePath).exists()) CBounty.getInstance().saveResource(resourcePath, false);
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
