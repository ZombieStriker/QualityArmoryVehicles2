package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.debugmanager.DebugManager;
import me.zombie_striker.qav.perms.PermissionHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DebugCommand extends SubCommand {

    public DebugCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return sender.hasPermission(PermissionHandler.PERM_DEBUG) ? MessagesConfig.subcommand_debug : null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_DEBUG)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        String toggle = DebugManager.toggleReciever(sender) ? Main.prefix + " Debugging enabled" : Main.prefix + " Debugging disabled";
        sender.sendMessage(toggle);
    }
}
