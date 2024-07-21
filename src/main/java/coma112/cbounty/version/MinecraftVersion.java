package coma112.cbounty.version;

import coma112.cbounty.utils.BountyLogger;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;


import java.util.regex.Pattern;
import java.util.regex.Matcher;

public enum MinecraftVersion {
    UNKNOWN,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_19_R4,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3,
    v1_20_R6,
    v1_21_R1;




    private static MinecraftVersion serverVersion;

    static {
        String bukkitVersion = Bukkit.getVersion();
        Pattern pattern = Pattern.compile("\\(MC: (\\d+)(?:\\.(\\d+))?\\)");
        Matcher matcher = pattern.matcher(bukkitVersion);

        if (matcher.find()) {
            try {
                int major = 1;
                int minor = Integer.parseInt(matcher.group(1));
                int patch = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;

                serverVersion = determineVersion(major, minor, patch);
            } catch (NumberFormatException e) {
                serverVersion = UNKNOWN;
            }
        }

    }

    public static MinecraftVersion determineVersion(int major, int minor, int patch) {
        if (major == 1) {
            switch (minor) {
                case 18:
                    return (patch == 1) ? v1_18_R1 : (patch == 2) ? v1_18_R2 : UNKNOWN;
                case 19:
                    switch (patch) {
                        case 1: return v1_19_R1;
                        case 2: return v1_19_R2;
                        case 3: return v1_19_R3;
                        case 4: return v1_19_R4;
                        default: return UNKNOWN;
                    }
                case 20:
                    switch (patch) {
                        case 1: return v1_20_R1;
                        case 2: return v1_20_R2;
                        case 3: return v1_20_R3;
                        case 6: return v1_20_R6;
                        default: return UNKNOWN;
                    }
                case 21:
                    return v1_21_R1;
                default: return UNKNOWN;
            }
        }
        return UNKNOWN;
    }



    public static MinecraftVersion getCurrentVersion() {
        BountyLogger.info("Current Minecraft version: " + serverVersion);
        return serverVersion;
    }

    public static boolean isServerVersion(@NotNull MinecraftVersion version) {
        return serverVersion == version;
    }

    public static boolean isServerVersion(@NotNull MinecraftVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public static boolean isServerVersionAbove(@NotNull MinecraftVersion version) {
        return serverVersion.ordinal() > version.ordinal();
    }

    public static boolean isServerVersionAtLeast(@NotNull MinecraftVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    public static boolean isServerVersionAtOrBelow(@NotNull MinecraftVersion version) {
        return serverVersion.ordinal() <= version.ordinal();
    }

    public static boolean isServerVersionBelow(@NotNull MinecraftVersion version) {
        return serverVersion.ordinal() < version.ordinal();
    }

    public boolean isLessThan(@NotNull MinecraftVersion other) {
        return this.ordinal() < other.ordinal();
    }

    public boolean isAtOrBelow(@NotNull MinecraftVersion other) {
        return this.ordinal() <= other.ordinal();
    }

    public boolean isGreaterThan(@NotNull MinecraftVersion other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isAtLeast(@NotNull MinecraftVersion other) {
        return this.ordinal() >= other.ordinal();
    }
}
