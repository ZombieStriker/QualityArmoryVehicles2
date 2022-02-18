package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RemoveNearbyCommand extends SubCommand {

    public RemoveNearbyCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "removeNearbyVehicles";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return MessagesConfig.subcommand_removeNearbyVehicles;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_REMOVE_VEHICLE)) {
            sender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        int radius = 6;
        if (args.length > 0) {
            radius = Integer.parseInt(args[0]);
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (VehicleEntity ve : new ArrayList<>(Main.vehicles)) {
                if (ve.getDriverSeat().getLocation().distanceSquared(player.getLocation()) < radius * radius) {
                    ve.deconstruct(null, "removeNearbyCommand");
                }
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            list.add(String.valueOf(i));
        }

        return list;
    }
}
