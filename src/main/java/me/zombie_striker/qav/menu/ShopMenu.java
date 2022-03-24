package me.zombie_striker.qav.menu;

import dev.triumphteam.gui.guis.GuiItem;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.UnlockedVehicle;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.fuel.FuelItemStack;
import me.zombie_striker.qav.fuel.RepairItemStack;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.qamini.EconHandler;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopMenu extends Menu {
    private static final Map<UUID,Long> LAST_SHOP = new HashMap<>();

    public ShopMenu(@NotNull Player player) {
        super(4, MessagesConfig.MENU_SHOP_TITLE, player);
    }

    @Override
    public void setupItems() {
        this.setPageButtons();

        if (Main.repairItem.shouldBeInShop()) {
            this.addItem(new GuiItem(ItemFact.a(Main.repairItem.getMaterial(), Main.repairItem.getData(), true, Main.repairItem.getName(),
                    MessagesConfig.ICONLORE_COST + Main.repairItem.getCost()), (e) -> {
                if (!canShop(e.getWhoClicked())) return;

                RepairItemStack repairItem = Main.repairItem;

                if (!EconHandler.hasEnough(repairItem.getCost(), (Player) e.getWhoClicked())) {
                    e.getWhoClicked().sendMessage(MessagesConfig.MESSAGE_NOT_ENOUGH_MONEY);
                    return;
                }

                if (e.getWhoClicked().getInventory().firstEmpty() == -1) {
                    e.getWhoClicked().sendMessage("Your inventory is full.");
                    Main.DEBUG("inventory was full");
                    return;
                }

                EconHandler.pay(repairItem.getCost(), (Player) e.getWhoClicked());
                e.getWhoClicked().getInventory().addItem(repairItem.asItem());
                LAST_SHOP.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
            }));
        }
        for (FuelItemStack fuel : FuelItemStack.getFuels()) {
            if (fuel.isAllowedInShop()) {
                this.addItem(new GuiItem(ItemFact.a(fuel.getMaterial(), fuel.getData(), true, fuel.getDisplayname(),
                        MessagesConfig.ICONLORE_COST + fuel.getCost()), (e) -> {
                    if (!canShop(e.getWhoClicked())) return;

                    if (!EconHandler.hasEnough(fuel.getCost(), (Player) e.getWhoClicked())) {
                        e.getWhoClicked().sendMessage(MessagesConfig.MESSAGE_NOT_ENOUGH_MONEY);
                        return;
                    }

                    if (e.getWhoClicked().getInventory().firstEmpty() == -1) {
                        e.getWhoClicked().sendMessage("Your inventory is full.");
                        Main.DEBUG("inventory was full");
                        return;
                    }

                    EconHandler.pay(fuel.getCost(), (Player) e.getWhoClicked());
                    e.getWhoClicked().getInventory().addItem(fuel.getItemStack());
                    LAST_SHOP.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
                }));
            }
        }
        for (AbstractVehicle ab : Main.vehicleTypes) {
            if (ab.isAllowedInShop()) {
                if (!Main.enable_RequirePermToBuyVehicle || PermissionHandler.canDrive(player, ab)) {
                    this.addItem(new GuiItem(ItemFact.a(ab.getMaterial(), ab.getItemData(), true, ab.getDisplayname(),
                            MessagesConfig.ICONLORE_COST + ab.getCost()), (e) -> {
                        if (!canShop(e.getWhoClicked())) return;

                        if (Main.enableVehicleLimiter) {
                            int maxAmount = PermissionHandler.getMaxOwnVehicles((Player) e.getWhoClicked());
                            int spawned = QualityArmoryVehicles.getOwnedVehicles(e.getWhoClicked().getUniqueId()).size();
                            if (spawned >= maxAmount) {
                                e.getWhoClicked().sendMessage(MessagesConfig.MESSAGE_TOO_MANY_VEHICLES);
                                return;
                            }
                        }

                        if (EconHandler.hasEnough(ab.getCost(), (Player) e.getWhoClicked())) {
                            ItemStack item = ItemFact.getCarItem(ab);
                            if (e.getWhoClicked().getInventory().firstEmpty() == -1) {
                                e.getWhoClicked().sendMessage("Your inventory is full.");
                                Main.DEBUG("inventory was full");
                                return;
                            }
                            EconHandler.pay(ab.getCost(), (Player) e.getWhoClicked());
                            e.getWhoClicked().sendMessage(MessagesConfig.MESSAGE_BOUGHT_CAR
                                    .replace("%car%", ab.getDisplayname()).replace("%price%", ab.getCost() + ""));
                            if (Main.enableGarage) {
                                QualityArmoryVehicles.addUnlockedVehicle((Player) e.getWhoClicked(), new UnlockedVehicle(ab,ab.getMaxHealth(),true));
                            } else {
                                e.getWhoClicked().getInventory().addItem(item);
                            }
                            Main.DEBUG("Finished paying for vehicle");
                            LAST_SHOP.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
                        } else {
                            e.getWhoClicked().sendMessage(MessagesConfig.MESSAGE_NOT_ENOUGH_MONEY);
                        }
                    }));
                }
            }
        }
    }

    private boolean canShop(HumanEntity player) {
        if (Main.enableShopCooldown && LAST_SHOP.containsKey(player.getUniqueId())) {
            if ((System.currentTimeMillis() - LAST_SHOP.get(player.getUniqueId())) < 500) {
                player.sendMessage(MessagesConfig.COOLDOWN.replace("%time%", "500"));
                return false;
            }

            LAST_SHOP.remove(player.getUniqueId());
        }

        return true;
    }
}
