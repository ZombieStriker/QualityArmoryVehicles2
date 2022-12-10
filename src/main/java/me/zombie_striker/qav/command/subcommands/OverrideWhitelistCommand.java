package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.util.VehicleUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OverrideWhitelistCommand extends SubCommand {

    public OverrideWhitelistCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "overrideWhitelist";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        if (sender.hasPermission(PermissionHandler.PERM_OVERRIDE_WHITELIST_COMMAND)) {
            return " : Toggles the override whitelist";
        }

        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_OVERRIDE_WHITELIST_COMMAND)) {
            sender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }

        VehicleUtils.toggleOverrideWhitelisted(((Player) sender).getUniqueId());
        sender.sendMessage(MessagesConfig.COMMANDMESSAGES_WHITELIST_OVERRIDE);
    }
}
