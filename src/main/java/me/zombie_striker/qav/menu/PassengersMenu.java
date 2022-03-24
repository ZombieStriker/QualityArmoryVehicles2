package me.zombie_striker.qav.menu;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.perms.PermissionHandler;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PassengersMenu extends Menu {
    private final VehicleEntity ve;

    public PassengersMenu(@NotNull Player player, VehicleEntity vehicle) {
        super(3, MessagesConfig.MENU_PASSAGER_SEATS_TITLE.replace("%cartype%", vehicle.getType().getDisplayname()), player);
        this.ve = vehicle;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setupItems() {
        this.setPageButtons();

        GuiAction<InventoryClickEvent> action = event -> {
            Main.DEBUG("Clicked setaspassager");
            if (event.getCurrentItem() != null && ve != null) {
                if (event.getCurrentItem().getType() == Material.BRICK_STAIRS) {
                    Entity driver;
                    driver = ve.getDriverSeat().getPassenger();
                    if (driver == null) {
                        if (!Main.requirePermissionToDrive || PermissionHandler.canDrive(player, ve.getType())) {
                            ve.getDriverSeat().setPassenger(event.getWhoClicked());
                            Main.DEBUG("Added player to seat!");
                        }else{
                            Main.DEBUG("Stopped player from being added to seat!");
                        }
                    } else {
                        Main.DEBUG("Another passager is already in driver seat : "
                                + driver.getName());
                    }
                } else {
                    Entity pass;
                    if ((pass = ve.getPassager(event.getCurrentItem().getAmount() - 1)) == null
                            && event.getSlot() - 1 < ve.getType().getPassagerSpots().size()) {
                        QualityArmoryVehicles.setAddPassager(ve,
                                (Player) event.getWhoClicked(), event.getCurrentItem().getAmount() - 1);
                        Main.DEBUG("Added player to seat!");
                    } else {
                        Main
                                .DEBUG("Another passager is already in the " + (event.getCurrentItem().getAmount() - 1)
                                        + " seat : " + (pass != null && pass.getPassenger() != null? pass.getPassenger().getName() : "ERROR"));
                    }
                }
                this.close(event.getWhoClicked());
            }
        };

        String name;
        ItemStack is;
        if (ve.getDriverSeat() == null)
            return;
        if (ve.getDriverSeat().getPassenger() != null) {
            Entity driver = ve.getDriverSeat().getPassenger();
            name = MessagesConfig.ICON_PASSAGERS_FULL.replace("%name%", driver != null ? driver.getName() : "ERROR");
            is = ItemFact.a(Material.BARRIER, name, MessagesConfig.ICONLORE_PASSAGERS_DRIVERSEAT);
        } else {
            name = MessagesConfig.ICON_PASSAGERS_EMPTY;
            is = ItemFact.a(Material.BRICK_STAIRS, name, MessagesConfig.ICONLORE_PASSAGERS_DRIVERSEAT);
        }
        //overview.setItem(0, is);
        this.setItem(0, new GuiItem(is, action));
        for (int i = 0; i < ve.getType().getPassagerSpots().size(); i++) {
            Entity seat = ve.getPassager(i);
            boolean full = seat != null;
            if (full && seat.getPassenger() == null) {
                full = false;
                ve.updateSeats();
            }
            if (full) {
                Entity e = seat.getPassenger();
                name = MessagesConfig.ICON_PASSAGERS_FULL.replace("%name%", e != null ? e.getName() : "ERROR");
                is = ItemFact.a(Material.BARRIER, name);
            } else {
                name = MessagesConfig.ICON_PASSAGERS_EMPTY;
                is = ItemFact.a(Material.COBBLESTONE_STAIRS, name);
            }
            is.setAmount(i + 1);
            this.addItem(new GuiItem(is, action));
        }

    }
}
