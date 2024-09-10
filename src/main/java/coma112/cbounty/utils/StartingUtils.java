package coma112.cbounty.utils;

import com.github.Anon8281.universalScheduler.utils.JavaUtil;
import coma112.cbounty.CBounty;
import coma112.cbounty.hooks.*;
import coma112.cbounty.update.UpdateChecker;
import coma112.cbounty.enums.VersionType;
import coma112.cbounty.version.VersionSupport;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static coma112.cbounty.enums.VersionType.determineVersion;

@SuppressWarnings("deprecation")
public final class StartingUtils {
    public static final boolean isFolia = JavaUtil.classExists("io.papermc.paper.threadedregions.RegionizedServer");
    private static boolean isSupported;
    @Getter
    public static final Map<Long, String> basicFormatOverrides = new ConcurrentHashMap<>();

    public static void registerHooks() {
        Placeholder.registerHook();
        Placeholder.registerHook();
        Token.register();
        PlayerPoints.register();
        CoinsEngine.register();
        Towny.register();
    }

    public static void registerListenersAndCommands() {
        RegisterUtils.registerListeners();
        RegisterUtils.registerCommands();
    }

    public static void checkVM() {
        if (getVMVersion() < 18) {
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
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception ignored) {
            isSupported = false;
            BountyLogger.error("### SpigotConfig class not found. This might indicate an unsupported server. ###");
            return;
        }

        try {
            BountyLogger.info("### Detected Bukkit version string: {} ###", Bukkit.getVersion());

            Pattern pattern = Pattern.compile("\\(MC: (\\d{1,2})\\.(\\d{1,2})(?:\\.(\\d{1,2}))?\\)");
            Matcher matcher = pattern.matcher(Bukkit.getVersion());

            if (matcher.find()) {
                int majorVersion = Integer.parseInt(matcher.group(1));
                int minorVersion = Integer.parseInt(matcher.group(2));
                int patchVersion = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                VersionType version = determineVersion(majorVersion, minorVersion, patchVersion);

                if (version == VersionType.UNKNOWN) {
                    isSupported = false;

                    BountyLogger.error("### Unknown Minecraft version: {}.{}.{} ###", majorVersion, minorVersion, patchVersion);
                    return;
                }

                isSupported = new VersionSupport(version).getVersionSupport() != null;
            }
        } catch (Exception exception) {
            isSupported = false;

            BountyLogger.error("### Exception occurred during version check: {} ###", exception.getMessage());
        }

        if (!isSupported) {
            BountyLogger.error("### This version of CBounty is not supported on this server version. ###");
            BountyLogger.error("### Please consider updating your server version to a newer version. ###");
            Bukkit.getPluginManager().disablePlugin(CBounty.getInstance());
        }
    }

    public static void checkUpdates() {
        new UpdateChecker(116501).getVersion(version -> {
            BountyLogger.info(CBounty.getInstance().getDescription().getVersion().equals(version) ? "Everything is up to date" : "You are using an outdated version! Please download the new version so that your server is always fresh! The newest version: " + version);
        });
    }

    public static void saveResourceIfNotExists(@NotNull String resourcePath) {
        if (!new File(CBounty.getInstance().getDataFolder(), resourcePath).exists()) CBounty.getInstance().saveResource(resourcePath, false);
    }

    public static void loadBasicFormatOverrides() {
        ConfigurationSection section = CBounty.getInstance().getConfiguration().getSection("formatting.basic");

        if (section == null) return;

        section.getKeys(false).forEach(key -> {
            try {
                basicFormatOverrides.put(Long.parseLong(key), section.getString(key));
            } catch (NumberFormatException exception) {
                BountyLogger.error(exception.getMessage());
            }
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
