package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.util.VehicleUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CallbackCommand extends SubCommand {

    public CallbackCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "callback";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return sender.hasPermission(PermissionHandler.PERM_CALLBACK) ? MessagesConfig.subcommand_callback : null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_CALLBACK)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        Location loc;
        if (sender instanceof Player) {
            loc = ((Player) sender).getLocation();
        } else {
            sender.sendMessage("Only players can use this command");
            return;
        }
        int radius = 6;
        if (args.length >= 1) {
            radius = Integer.parseInt(args[0]);
        }
        for (VehicleEntity ve : new ArrayList<>(Main.vehicles)) {
            if (ve.getOwner() != null && ve.getOwner().equals(((Player) sender).getUniqueId()) && ve.getDriverSeat().getLocation().distanceSquared(loc) < radius * radius)
                VehicleUtils.callback(ve, (Player) sender);
        }

        sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGE_CALLBACK.replace("%radius%", String.valueOf(radius)));
    }
}
