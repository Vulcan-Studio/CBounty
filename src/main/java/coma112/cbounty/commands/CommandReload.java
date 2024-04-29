package coma112.cbounty.commands;

import coma112.cbounty.CBounty;
import coma112.cbounty.language.MessageKeys;
import coma112.cbounty.subcommand.CommandInfo;
import coma112.cbounty.subcommand.PluginCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "bountyreload", requiresPlayer = false, permission = "cbounty.reload")
public class CommandReload extends PluginCommand {

    public CommandReload() {
        super("bountyreload");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {

        CBounty.getInstance().getLanguage().reload();
        CBounty.getInstance().getConfiguration().reload();
        sender.sendMessage(MessageKeys.RELOAD);
        return true;
    }
}

