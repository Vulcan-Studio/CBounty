package coma112.cbounty.commands;

import coma112.cbounty.menu.menus.BountiesMenu;
import coma112.cbounty.subcommand.CommandInfo;
import coma112.cbounty.subcommand.PluginCommand;
import coma112.cbounty.utils.MenuUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "bounties", requiresPlayer = true)
public class CommandBountiesMenu extends PluginCommand {
    public CommandBountiesMenu() {
        super("bounties");
    }

    @Override
    public boolean run(@NotNull Player player, @NotNull String[] args) {
        new BountiesMenu(MenuUtils.getMenuUtils(player)).open();
        return true;
    }
}

