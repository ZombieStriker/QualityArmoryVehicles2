package me.zombie_striker.qav.premium;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PremiumHandler {

    @Contract(pure = true)
    public static @NotNull String getUser() {
        return "%%__USER__%%";
    }

    public static boolean isPremium() {
        return !getUser().contains("__USER__");
    }

    public static void sendMessage(@NotNull CommandSender sender) {
        if (!getUser().contains("__USER__")) {
            sender.sendMessage(String.format("%s Running %s v%s. Licensed to %s", Main.prefix, QualityArmoryVehicles.getPlugin().getName(), QualityArmoryVehicles.getPlugin().getDescription().getVersion(), ChatColor.YELLOW + " https://www.spigotmc.org/members/" + getUser()));
            return;
        }

        sender.sendMessage(String.format("%s " + ChatColor.RED + "Error: This plugin is not signed with a license.", Main.prefix));
    }
}
