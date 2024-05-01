package coma112.cbounty.commands;

import coma112.cbounty.config.ConfigKeys;
import coma112.cbounty.managers.Top;
import coma112.cbounty.subcommand.CommandInfo;
import coma112.cbounty.subcommand.PluginCommand;
import coma112.cbounty.language.MessageKeys;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
@CommandInfo(name = "streaktop", requiresPlayer = false)
public class CommandTopStreak extends PluginCommand {
    public CommandTopStreak() {
        super("streaktop");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {
        int value;

        if (args.length == 0) {
            sender.spigot().sendMessage(Top.getTopStreak(10));
            return true;
        }

        try {
            value = Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(MessageKeys.INVALID_NUMBER);
            return true;
        }

        if (value <= 0) {
            sender.sendMessage(MessageKeys.NO_NEGATIVE);
            return true;
        }

        if (value > 15) {
            sender.sendMessage(MessageKeys.MAX_TOP.replace("{top}", String.valueOf(ConfigKeys.MAXIMUM_TOP)));
            return true;
        }

        sender.spigot().sendMessage(Top.getTopStreak(value));
        return true;

    }
}
