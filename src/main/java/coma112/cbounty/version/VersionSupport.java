package coma112.cbounty.version;

import coma112.cbounty.utils.BountyLogger;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

@Getter
public class VersionSupport {
    private final ServerVersionSupport versionSupport;

    public VersionSupport(@NotNull Plugin plugin, @NotNull MinecraftVersion version) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (version == MinecraftVersion.UNKNOWN) throw new IllegalArgumentException("VERSION NOT FOUND!!! ");


        Class<?> clazz = Class.forName("coma112.cbounty.version.nms." + version.name() + ".Version");
        versionSupport = (ServerVersionSupport) clazz.getConstructor(Plugin.class).newInstance(plugin);

        if (!versionSupport.isSupported()) {
            BountyLogger.warn("---   VERSION IS SUPPORTED BUT,   ---");
            BountyLogger.warn("The version you are using is badly");
            BountyLogger.warn("implemented. Many features won't work.");
            BountyLogger.warn("Please consider updating your server ");
            BountyLogger.warn("version to a newer version. (like 1.19_R2)");
            BountyLogger.warn("---   PLEASE READ THIS MESSAGE!   ---");
        }
    }
}
