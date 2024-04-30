package coma112.cbounty.utils;

import coma112.cbounty.CBounty;
import coma112.cbounty.commands.CommandBountiesMenu;
import coma112.cbounty.commands.CommandReload;
import coma112.cbounty.commands.CommandSetBounty;
import coma112.cbounty.commands.CommandTopStreak;
import coma112.cbounty.subcommand.PluginCommand;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CommandRegister {
    @SuppressWarnings("deprecation")
    public static void registerCommands() {
        for (Class<? extends PluginCommand> clazz : getCommandClasses()) {
            try {
                PluginCommand commandInstance = clazz.getDeclaredConstructor().newInstance();
                Objects.requireNonNull(Bukkit.getCommandMap()).register(CBounty.getInstance().getDescription().getName(), commandInstance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private static Set<Class<? extends PluginCommand>> getCommandClasses() {
        Set<Class<? extends PluginCommand>> commandClasses = new HashSet<>();
        commandClasses.add(CommandReload.class);
        commandClasses.add(CommandSetBounty.class);
        commandClasses.add(CommandBountiesMenu.class);
        commandClasses.add(CommandTopStreak.class);

        return commandClasses;
    }

}
