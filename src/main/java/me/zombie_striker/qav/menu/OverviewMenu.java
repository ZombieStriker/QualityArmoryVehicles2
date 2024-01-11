package me.zombie_striker.qav.menu;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.guis.GuiItem;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.fuel.FuelItemStack;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.util.VehicleUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OverviewMenu extends Menu {
    private final VehicleEntity ve;

    public OverviewMenu(@NotNull Player player, @NotNull VehicleEntity vehicle) {
        super(2, MessagesConfig.MENU_OVERVIEW_TITLE.replace("%cartype%", vehicle.getType().getDisplayname()), player);
        this.ve = vehicle;
    }

    @Override
    public void setupItems() {
        if (Main.enableTrunks) {
            List<ItemStack> quickTrunk = Arrays.asList(ve.getTrunk().getContents());

            int limit = 7;
            boolean isMore = quickTrunk.size() > limit;
            String[] trunklore = new String[1 + (isMore ? limit : quickTrunk.size())];
            trunklore[0] = MessagesConfig.ICONLORE_TRUNK_CONTAINS;
            int trunkI = 1;
            for (ItemStack is : quickTrunk) {
                if (isMore && trunkI == limit) {
                    trunklore[trunkI] = ChatColor.GRAY + "+" + (quickTrunk.size() - trunkI) + " more...";
                    break;
                }
                if (is != null) {
                    trunklore[trunkI] = ChatColor.GRAY + is.getType().name() + ":" + is.getAmount();
                    trunkI++;
                }
            }
            this.setItem(4, new GuiItem(ItemFact.a(Material.CHEST, MessagesConfig.ICON_TRUNK, trunklore), (e) -> {
                Main.DEBUG("will open trunk");
                e.getWhoClicked().openInventory(ve.getTrunk());
            }));
        }

        if (ve.getWhiteList() != null) {
            String[] lore = new String[1 + ve.getWhiteList().size()];
            lore[0] = MessagesConfig.ICONLORE_LIST_WHITELIST;
            int i = 1;
            for (UUID pass : ve.getWhiteList()) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(pass);
                lore[i] = ChatColor.GRAY + op.getName();
                i++;
            }
            this.setItem(1, new GuiItem(ItemFact.askull(null, MessagesConfig.ICON_ADD_WHITELIST, lore), (e) -> {
                Main.DEBUG("will open add whitelist");
                new AddWhitelistMenu((Player) e.getWhoClicked(), this, ve).open();
            }));
            this.setItem(10, new GuiItem(ItemFact.a(Material.BARRIER, MessagesConfig.ICON_REMOVE_WHITELIST, lore), (e) -> {
                Main.DEBUG("will open add whitelist");
                new RemoveWhitelistMenu((Player) e.getWhoClicked(), this, ve).open();
            }));
        }
        this.setItem(5, new GuiItem(ItemFact.a(Material.COBBLESTONE_STAIRS, MessagesConfig.ICON_PASSAGERS), (e) -> {
            Main.DEBUG("will open passagers");
            new PassengersMenu((Player) e.getWhoClicked(), ve).open();
        }));
        this.setItem(0, new GuiItem(ItemFact.a(XMaterial.OAK_SIGN.parseMaterial(), MessagesConfig.translatePublic(ve), MessagesConfig.ICONLORE_PUBLIC), (e) -> {
            Main.DEBUG("Swapping ispublic from " + ve.allowsPassagers() + " to " + (!ve.allowsPassagers()));
            ve.setAllowsPassagers(!ve.allowsPassagers());
            this.updateItem(0, ItemFact.a(XMaterial.OAK_SIGN.parseMaterial(), MessagesConfig.translatePublic(ve), MessagesConfig.ICONLORE_PUBLIC));
        }));
        if (ve.getType().enableFuel()) {
            List<ItemStack> quickFuel = new ArrayList<>(Arrays.asList(ve.getFuels().getContents()));
            int fuelInt = 0;
            String[] fuelLore = new String[1];
            for (ItemStack is : quickFuel) {
                if (is != null)
                    fuelInt += FuelItemStack.getFuelForItem(is) * is.getAmount();
            }
            fuelLore[0] = MessagesConfig.ICONLORE_TRUNK_CONTAINS + " " + (fuelInt / 20) + "s";


            this.setItem(7, new GuiItem(ItemFact.a(Material.COAL, MessagesConfig.ICON_CHECK_FUEL, fuelLore), (e) -> {
                Main.DEBUG("will open check fuel");
                e.getWhoClicked().openInventory(ve.getFuels());
            }));
        }
        this.setItem(16, new GuiItem(ItemFact.a(Material.IRON_BLOCK, MessagesConfig.ICON_HEALTH, MessagesConfig.ICONLORE_HEALTH_FORMAT.replace("%maxhealth%", ve.getType().getMaxHealth() + "")
                .replace("%health%", "" + ve.getHealth()))));
        if (Main.allowVehiclePickup) {
            this.setItem(8, new GuiItem(ItemFact.a(ve.getType().getMaterial(), ve.getType().getItemData(), true, MessagesConfig.ICON_PICKUP,
                    MessagesConfig.ICONLORE_PICKUP_OWNER, MessagesConfig.ICONLORE_PICKUP_TRUNK), (e) -> {
                if(!e.getWhoClicked().hasPermission(PermissionHandler.PERM_PICKUP)) {
                    return;
                }
                Main.DEBUG("will open pickup");
                if (ve.getOwner() == null || ve.getOwner().equals(e.getWhoClicked().getUniqueId())) {
                    e.getWhoClicked().closeInventory();
                    Entity driver;

                    driver = ve.getDriverSeat().getPassenger();
                    if (driver != null) {
                        e.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MESSAGE_CannotPickupWhileInVehicle);
                        return;
                    }
                    e.getWhoClicked().closeInventory();

                    if (e.getWhoClicked().getInventory().firstEmpty() == -1)
                        e.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MESSAGE_PICKUP_DROPPED);

                    VehicleUtils.callback(ve,(Player) e.getWhoClicked(),"pickup_menu");
                }
            }));
        }

        if (ve.getOwner() != null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(ve.getOwner());
            String name = op.getName() != null ? op.getName() : "Null Name";
            this.setItem(17, new GuiItem(ItemFact.a(Material.BARRIER, MessagesConfig.ICON_OWNERSHIP,
                    MessagesConfig.ICONLORE_currentowner.replaceAll("%owner%", name)), (e) -> {
                if (ve.getOwner() != null && ve.getOwner().equals(e.getWhoClicked().getUniqueId())) {
                    ve.setOwner(null);
                    e.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MESSAGE_NO_OWNER_NOW);
                    e.getWhoClicked().closeInventory();
                }
            }));
        }
    }

}
