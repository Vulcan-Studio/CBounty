package coma112.cbounty.subcommand;

import coma112.cbounty.language.MessageKeys;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
public abstract class PluginCommand extends Command {
    private final CommandInfo commandInfo;

    public PluginCommand(@NotNull String name) {
        super(name);
        commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        Objects.requireNonNull(commandInfo, "Commands must have CommandInfo annotation");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!commandInfo.permission().isEmpty()) {
            if (!sender.hasPermission(commandInfo.permission()) || !sender.isOp()) {
                sender.sendMessage(MessageKeys.NO_PERMISSION);
                return true;
            }
        }

        if (commandInfo.requiresPlayer()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageKeys.PLAYER_REQUIRED);
                return true;
            }

            return run((Player) sender, args);
        }

        return run(sender, args);
    }

    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {
        return false;
    }

    public boolean run(@NotNull Player player, @NotNull String[] args) {
        return false;
    }

}

