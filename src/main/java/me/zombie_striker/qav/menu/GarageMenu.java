package me.zombie_striker.qav.menu;

import dev.triumphteam.gui.guis.GuiItem;
import me.zombie_striker.qav.*;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.util.VehicleUtils;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GarageMenu extends Menu {
    private final Player target;

    public GarageMenu(@NotNull Player player, @NotNull Player target) {
        super(4, player.getUniqueId() == target.getUniqueId() ? MessagesConfig.MENU_GARAGE_TITLE : String.format(MessagesConfig.MENU_OTHER_GARAGE_TITLE, target.getName()), player);
        this.target = target;
    }

    @Override
    public void setupItems() {
        this.setPageButtons();

        final List<UnlockedVehicle> list = QualityArmoryVehicles.unlockedVehicles(target);
        for (UnlockedVehicle uv : list) {
            this.addItem(new GuiItem(ItemFact.getCarItem(uv.getVehicleType()), (e) -> {
                Main.DEBUG("Open Garage");

                if (!Main.enableGarage) {
                    e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage("Something went wrong with the garage. Only the plugin should be allowed to add items to the garage.");
                    return;
                }

                AbstractVehicle ab = QualityArmoryVehicles.getVehicleByItem(e.getCurrentItem());
                if (Main.enableVehicleLimiter
                        && QualityArmoryVehicles.getOwnedVehicles(target.getUniqueId())
                        .size() >= PermissionHandler.getMaxOwnVehicles(target)) {
                    e.getWhoClicked().sendMessage(MessagesConfig.MESSAGE_TOO_MANY_VEHICLES);
                    e.getWhoClicked().closeInventory();
                } else {
                    if (Main.requirePermissionToDrive) {
                        if (!PermissionHandler.canDrive(player, ab)) {
                            e.getWhoClicked().sendMessage(MessagesConfig.MESSAGE_NO_PERM_DRIVE);
                            Main.DEBUG("Cannot drive because player does not have permission");
                            return;
                        }
                    }


                    int inUse = 0;
                    for (VehicleEntity ve : QualityArmoryVehicles.getOwnedVehicles(target.getUniqueId())) {
                        if (ve != null)
                            if(ve.getDriverSeat()!=null && ve.getModelEntity()!=null)
                                if (ve.getType() == ab)
                                    inUse++;
                    }
                    int amountHas = 0;
                    for (UnlockedVehicle alllist : list)
                        if (alllist.getVehicleType() == ab)
                            amountHas++;

                    UnlockedVehicle unlockedVehicle = QualityArmoryVehicles.findUnlockedVehicle(player, ab);
                    if (!unlockedVehicle.isInGarage()) {
                        e.getWhoClicked().closeInventory();
                        QualityArmoryVehicles.removeUnlockedVehicle(target,unlockedVehicle);
                        Main.vehicles
                                .stream()
                                .filter((entity) -> entity.getOwner().equals(target.getUniqueId()))
                                .filter((entity) -> entity.getType().getName().equals(unlockedVehicle.getVehicleType().getName()))
                                .findFirst()
                                .ifPresent((ve) -> VehicleUtils.callback(ve,target,"Garage callback"));
                        return;
                    }

                    if (inUse >= amountHas) {
                        e.getWhoClicked().sendMessage(MessagesConfig.MESSAGE_TOO_MANY_VEHICLES_Type);
                        Main.DEBUG("Player has too many vehicles.");
                        return;
                    }

                    VehicleEntity ve = QualityArmoryVehicles.spawnVehicle(unlockedVehicle, (Player) e.getWhoClicked());
                    if (ve == null)
                        return;
                    if (!Main.enableGarageCallback)
                        QualityArmoryVehicles.removeUnlockedVehicle(target,unlockedVehicle);
                    else {
                        QualityArmoryVehicles.removeUnlockedVehicle(target,unlockedVehicle);
                        unlockedVehicle.setInGarage(false);
                        QualityArmoryVehicles.addUnlockedVehicle(target,unlockedVehicle);
                    }

                    if (Main.garageFuel)
                        ve.setFuel(500 * 64);
                    ve.getDriverSeat().setPassenger(e.getWhoClicked());
                    Main.DEBUG("Set as passager and added fuel");
                }
                e.getWhoClicked().closeInventory();
            }));
        }
    }
}
