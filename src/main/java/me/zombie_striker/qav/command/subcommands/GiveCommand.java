package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.UnlockedVehicle;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GiveCommand extends SubCommand {

    public GiveCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_GIVE)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }
        if (args.length < 1) {
            this.help(sender);
            return;
        }
        String name = args[0];

        AbstractVehicle ve = QualityArmoryVehicles.getVehicle(name);
        if (ve == null && !name.equalsIgnoreCase("repair")) {
            sender.sendMessage("Vehicle does not exist.");
            return;
        }
        Player reciever = null;
        if (sender instanceof Player) {
            reciever = (Player) sender;
        }
        if (args.length > 1) {
            reciever = Bukkit.getPlayer(args[1]);
        }
        if (reciever == null) {
            sender.sendMessage("Player \"" + args[1] + "\"is not on the server.");
            return;
        }

        if (name.equalsIgnoreCase("repair")) {
            reciever.getInventory().addItem(Main.repairItem.asItem());
            return;
        }

        if (Main.enableGarage) {
            QualityArmoryVehicles.addUnlockedVehicle(reciever,new UnlockedVehicle(ve,ve.getMaxHealth(), true));
        } else {
            reciever.getInventory().addItem(ItemFact.getItem(ve));
        }
        sender.sendMessage(Main.prefix + " Gave " + ChatColor.GOLD + reciever.getName() + " " + ve.getName() + ".");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 0 || args.length == 1) {
            for (AbstractVehicle vehicle : Main.vehicleTypes) {
                if (vehicle.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(vehicle.getName());
                }
            }
        } else {
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).collect(java.util.stream.Collectors.toList()));
        }

        return list;
    }

    @Override
    public String getDescription(@NotNull CommandSender sender) {
        return MessagesConfig.subcommand_GiveVehicle;
    }
}
