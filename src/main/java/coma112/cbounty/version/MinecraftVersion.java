package coma112.cbounty.version;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public enum MinecraftVersion {
    UNKNOWN,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_19_R4,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3,
    v1_20_R4;

    private static final String packagePath = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final MinecraftVersion serverVersion = getVersion();

    private static MinecraftVersion getVersion() {
        for (MinecraftVersion version : values()) if (packagePath.startsWith(version.name())) return version;
        return UNKNOWN;
    }

    public static MinecraftVersion getCurrentVersion() {
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
