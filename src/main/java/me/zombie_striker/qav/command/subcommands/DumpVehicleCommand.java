package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class DumpVehicleCommand extends SubCommand {

    public DumpVehicleCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "dumpVehicle";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return sender.hasPermission(PermissionHandler.PERM_DEBUG) ? MessagesConfig.subcommand_debug : null;
    }

    @Override
    public void perform(@NotNull CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_DEBUG)) {
            sender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(QualityArmoryVehicles.getPlugin(), () -> {
            Player player = (Player) sender;
            VehicleEntity vehicle = QualityArmoryVehicles.getVehicleEntityByEntity(player.getVehicle());

            if (vehicle == null) {
                sender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_VEHICLE);
                return;
            }

            player.sendMessage(Main.prefix + " You are riding a " + vehicle.getType().getName() + ":");

            if (args.length == 1) {
                try {
                    Field declaredField = vehicle.getClass().getDeclaredField(args[0]);
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(vehicle);
                    player.sendMessage(Main.prefix + " " + args[0] + ": " + value);
                } catch (NoSuchFieldException | IllegalAccessException ignored) {
                    player.sendMessage(Main.prefix + ChatColor.RED + " Field " + args[0] + " not found");
                }
            } else {
                player.sendMessage(Main.prefix + ChatColor.GREEN + " " + ChatColor.stripColor(vehicle.toString()));
            }

        });

    }
}
