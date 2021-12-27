package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ListCommand extends SubCommand {

    public ListCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription(@NotNull CommandSender sender) {
        return MessagesConfig.subcommand_list;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        StringBuilder builder = new StringBuilder();
        for (AbstractVehicle vehicleType : Main.vehicleTypes) {
            builder.append(vehicleType.getName()).append(", ");
        }

        sender.sendMessage(Main.prefix + " Loaded vehicles: " + builder.substring(0, builder.length()-2));
    }
}
