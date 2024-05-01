package coma112.cbounty.utils;

import coma112.cbounty.CBounty;
import coma112.cbounty.commands.*;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public class CommandRegister {
    public static void registerCommands() {
        BukkitCommandHandler handler = BukkitCommandHandler.create(CBounty.getInstance());
        handler.register(new CommandBounty());
    }
}
