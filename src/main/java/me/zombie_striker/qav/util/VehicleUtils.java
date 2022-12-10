package me.zombie_striker.qav.util;

import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.UnlockedVehicle;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cryptomorin.xseries.XItemStack.giveOrDrop;

public final class VehicleUtils {
    private static final List<UUID> OVERRIDE_WHITELIST = new ArrayList<>();

    public static void callback(VehicleEntity ve, Player player) {
        callback(ve,player,"Callback");
    }

    public static void callback(VehicleEntity ve, Player player, String message) {
        if (player != null) {
            for (ItemStack item : ve.getTrunk()) {
                if (item != null)
                    giveOrDrop(player,item);
            }

            for (ItemStack item : ve.getFuels().getContents()) {
                if (item != null)
                    giveOrDrop(player,item);
            }

        }

        ve.getFuels().clear();
        ve.getTrunk().clear();

        ve.deconstruct(player, message);
        if (player != null) {
            if(!Main.enableGarage)
                giveOrDrop(player, ItemFact.getItem(ve.getType()));
            else {
                List<UnlockedVehicle> unlockedVehicles = QualityArmoryVehicles.unlockedVehicles(player);
                UnlockedVehicle uv = QualityArmoryVehicles.findUnlockedVehicle(player, ve.getType());

                if (uv == null) {
                    uv = new UnlockedVehicle(ve.getType(),ve.getHealth(), true);
                } else {
                    unlockedVehicles.remove(uv);
                }

                uv.setInGarage(true);
                uv.setHealth(ve.getHealth());
                unlockedVehicles.add(uv);
                QualityArmoryVehicles.setUnlockedVehicles(player,unlockedVehicles);
            }
        }
    }

    public static boolean isOverrideWhitelisted(UUID uuid) {
        return OVERRIDE_WHITELIST.contains(uuid);
    }

    public static void setOverrideWhitelisted(UUID uuid, boolean whitelisted) {
        if (whitelisted) {
            OVERRIDE_WHITELIST.add(uuid);
        } else {
            OVERRIDE_WHITELIST.remove(uuid);
        }
    }

    public static void toggleOverrideWhitelisted(UUID uuid) {
        setOverrideWhitelisted(uuid, !isOverrideWhitelisted(uuid));
    }

}
