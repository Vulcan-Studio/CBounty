package coma112.cbounty.utils;

import coma112.cbounty.CBounty;
import coma112.cbounty.listeners.BountyDeathListener;
import coma112.cbounty.listeners.GlowingListener;
import coma112.cbounty.menu.MenuListener;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class ListenerRegister {
    public static void registerEvents() {
        Set<Class<? extends Listener>> listenerClasses = getListenerClasses();

        for (Class<? extends Listener> clazz : listenerClasses) {
            try {
                CBounty.getInstance().getServer().getPluginManager().registerEvents(clazz.newInstance(), CBounty.getInstance());
            } catch (InstantiationException | IllegalAccessException exception) {
                BountyLogger.error(exception.getMessage());
            }
        }
    }

    private static Set<Class<? extends Listener>> getListenerClasses() {
        Set<Class<? extends Listener>> listenerClasses = new HashSet<>();
        listenerClasses.add(MenuListener.class);
        listenerClasses.add(BountyDeathListener.class);
        listenerClasses.add(GlowingListener.class);
        return listenerClasses;
    }
}

