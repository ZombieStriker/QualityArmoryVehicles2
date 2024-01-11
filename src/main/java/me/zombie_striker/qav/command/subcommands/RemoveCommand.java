package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveCommand extends SubCommand {

    public RemoveCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "removeVehicle";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return sender.hasPermission(PermissionHandler.PERM_REMOVE_VEHICLE) ? MessagesConfig.subcommand_RemoveVehicle : null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_REMOVE_VEHICLE)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.prefix + "&7 Try to use &6/qav removeVehicle <type>"));
            return;
        }

        final List<VehicleEntity> collect = Main.vehicles.stream()
                .filter(ve -> ve.getType().getName().equalsIgnoreCase(args[0]))
                .collect(Collectors.toList());

        Main.vehicles.removeIf(ve -> ve.getType().getName().equalsIgnoreCase(args[0]));

        for (VehicleEntity entity : collect) {
            entity.deconstruct(null,"Remove command");
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        for (AbstractVehicle vehicle : Main.vehicleTypes) {
            if (vehicle.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                list.add(vehicle.getName());
            }
        }

        return list;
    }
}
