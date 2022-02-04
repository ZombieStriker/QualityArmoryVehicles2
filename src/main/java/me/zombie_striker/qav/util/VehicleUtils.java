package me.zombie_striker.qav.util;

import com.cryptomorin.xseries.ReflectionUtils;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.UnlockedVehicle;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.menu.MenuHandler;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class VehicleUtils {
    public static void callback(VehicleEntity ve, Player player) {
        callback(ve,player,"Callback");
    }

    public static void callback(VehicleEntity ve, Player player, String message) {
        if (player != null) {
            for (ItemStack item : ve.getTrunk()) {
                if (item != null)
                    MenuHandler.giveOrDrop(player,item);
            }

            for (ItemStack item : ve.getFuels().getContents()) {
                if (item != null)
                    MenuHandler.giveOrDrop(player,item);
            }

        }

        ve.getFuels().clear();
        ve.getTrunk().clear();

        ve.deconstruct(player, message);
        if (player != null) {
            if(!Main.enableGarage)
                MenuHandler.giveOrDrop(player, ItemFact.getCarItem(ve.getType()));
            else
                QualityArmoryVehicles.addUnlockedVehicle(player,new UnlockedVehicle(ve.getType(),ve.getHealth(), true));
        }
    }

}
