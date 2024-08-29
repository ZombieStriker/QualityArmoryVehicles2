package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.UnlockedVehicle;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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
    @SuppressWarnings("deprecation")
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_REMOVE_VEHICLE)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.prefix + "&7 Try to use &6/qav removeVehicle <type> [player]"));
            return;
        }

        OfflinePlayer target = args.length == 2 ? Bukkit.getOfflinePlayer(args[1]) : null;

        final List<VehicleEntity> collect = Main.vehicles.stream()
                .filter(ve -> ve.getType().getName().equalsIgnoreCase(args[0]))
                .filter(ve -> target == null || target.getUniqueId().equals(ve.getOwner()))
                .collect(Collectors.toList());

        for (VehicleEntity entity : collect) {
            entity.deconstruct(null, "Remove command");
        }

        if (target == null)
            Bukkit.getScheduler().runTaskAsynchronously(QualityArmoryVehicles.getPlugin(), () -> {
                List<File> files = QualityArmoryVehicles.getUnlockedVehiclesFiles();
                for (File file : files) {
                    List<UnlockedVehicle> unlockedVehicles = QualityArmoryVehicles.parseUnlockedVehicles(file);
                    unlockedVehicles.removeIf(unlockedVehicle -> unlockedVehicle.getVehicleType().getName().equalsIgnoreCase(args[0]));

                    QualityArmoryVehicles.setUnlockedVehicles(file, unlockedVehicles);
                }
            });
        else {
            Bukkit.getScheduler().runTaskAsynchronously(QualityArmoryVehicles.getPlugin(), () -> {
                File file = QualityArmoryVehicles.getUnlockedVehiclesFile(target);
                List<UnlockedVehicle> unlockedVehicles = QualityArmoryVehicles.parseUnlockedVehicles(file);
                unlockedVehicles.removeIf(unlockedVehicle -> unlockedVehicle.getVehicleType().getName().equalsIgnoreCase(args[0]));

                QualityArmoryVehicles.setUnlockedVehicles(file, unlockedVehicles);
            });
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
