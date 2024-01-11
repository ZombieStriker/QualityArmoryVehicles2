package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.util.VehicleUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CallbackAllCommand extends SubCommand {

    public CallbackAllCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "callbackAll";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return sender.hasPermission(PermissionHandler.PERM_CALLBACK_ALL) ? MessagesConfig.subcommand_callbackAll : null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_CALLBACK_ALL)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        int count = 0;

        for (VehicleEntity ve : new ArrayList<>(Main.vehicles)) {
            if (Bukkit.getPlayer(ve.getOwner()) != null) {
                VehicleUtils.callback(ve, Bukkit.getPlayer(ve.getOwner()));
                count++;
            }
        }

        sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGE_CALLBACKALL.replace("%count%", String.valueOf(count)));
    }
}
