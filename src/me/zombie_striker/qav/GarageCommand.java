package me.zombie_striker.qav;

import me.zombie_striker.qav.menu.MenuHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GarageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return true;
        }

        Player target = (Player) commandSender;

        if (args.length == 1) {
            if (!commandSender.hasPermission("qualityarmoryvehicles.garage.other")) {
                commandSender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_PERM);
                return true;
            }

            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            commandSender.sendMessage(Main.prefix + " This player does not exist.");
            return true;
        }

        MenuHandler.openGarageToOther((Player) commandSender,target);

        return true;
    }

}
