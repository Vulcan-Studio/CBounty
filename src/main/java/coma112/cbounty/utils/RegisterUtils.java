package coma112.cbounty.utils;

import coma112.cbounty.CBounty;
import coma112.cbounty.commands.CommandBounty;
import coma112.cbounty.listeners.BountyDeathListener;
import coma112.cbounty.listeners.BountyFinderListener;
import coma112.cbounty.listeners.GlowingListener;
import coma112.cbounty.menu.MenuListener;
import org.bukkit.event.Listener;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public final class RegisterUtils {
    public static void registerListeners() {
        getListenerClasses().forEach(clazz -> {
            try {
                CBounty.getInstance().getServer().getPluginManager().registerEvents(clazz.newInstance(), CBounty.getInstance());
            } catch (InstantiationException | IllegalAccessException exception) {
                BountyLogger.error(exception.getMessage());
            }
        });
    }

    public static void registerCommands() {
        BukkitCommandHandler handler = BukkitCommandHandler.create(CBounty.getInstance());

        handler.register(new CommandBounty());
    }

    private static Set<Class<? extends Listener>> getListenerClasses() {
        Set<Class<? extends Listener>> listenerClasses = new HashSet<>();

        listenerClasses.add(MenuListener.class);
        listenerClasses.add(BountyDeathListener.class);
        listenerClasses.add(GlowingListener.class);
        listenerClasses.add(BountyFinderListener.class);
        return listenerClasses;
    }
}

