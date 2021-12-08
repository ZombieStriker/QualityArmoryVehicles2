package me.zombie_striker.qav.menu.easyguicallables;

import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.UnlockedVehicle;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.easygui.ClickData;
import me.zombie_striker.qav.easygui.EasyGUICallable;
import me.zombie_striker.qav.fuel.FuelItemStack;
import me.zombie_striker.qav.fuel.RepairItemStack;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.qamini.EconHandler;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopCallable extends EasyGUICallable {
	private static final Map<UUID,Long> LAST_SHOP = new HashMap<>();

	@Override
	public void call(ClickData data) {
		if (Main.enableShopCooldown && LAST_SHOP.containsKey(data.getClicker().getUniqueId())) {
			if ((System.currentTimeMillis() - LAST_SHOP.get(data.getClicker().getUniqueId())) < 500) {
				data.getClicker().sendMessage(MessagesConfig.COOLDOWN.replace("%time%", "500"));
				return;
			}

			LAST_SHOP.remove(data.getClicker().getUniqueId());
		}

		Main.DEBUG("Shop menu clicked");
		data.cancelPickup(true);
			AbstractVehicle ab = QualityArmoryVehicles.getVehicleByItem(data.getClickedItem());
			if(ab==null){
				if (Main.repairItem.isItem(data.getClickedItem())) {
					RepairItemStack repairItem = Main.repairItem;

					if (!EconHandler.hasEnough(repairItem.getCost(), data.getClicker())) {
						data.getClicker().sendMessage(MessagesConfig.MESSAGE_NOT_ENOUGH_MONEY);
						return;
					}

					if (data.getClicker().getInventory().firstEmpty() == -1) {
						data.getClicker().sendMessage("Your inventory is full.");
						Main.DEBUG("inventory was full");
						return;
					}

					EconHandler.pay(repairItem.getCost(), data.getClicker());
					data.getClicker().getInventory().addItem(repairItem.asItem());
					LAST_SHOP.put(data.getClicker().getUniqueId(), System.currentTimeMillis());
				}
				if(FuelItemStack.getFuelForItem(data.getClickedItem()) > 0){
					FuelItemStack fuelItemStack = FuelItemStack.getFuelItemInstance(data.getClickedItem());
					if (fuelItemStack != null && EconHandler.hasEnough(fuelItemStack.getCost(), data.getClicker())) {
						ItemStack item = fuelItemStack.getItemStack();
						if (data.getClicker().getInventory().firstEmpty() == -1) {
							data.getClicker().sendMessage("Your inventory is full.");
							Main.DEBUG("inventory was full");
							return;
						}
						EconHandler.pay(fuelItemStack.getCost(), data.getClicker());
						data.getClicker().getInventory().addItem(item);
						Main.DEBUG("Finished paying for fuel");
						LAST_SHOP.put(data.getClicker().getUniqueId(), System.currentTimeMillis());
					} else {
						data.getClicker().sendMessage(MessagesConfig.MESSAGE_NOT_ENOUGH_MONEY);
					}
				}
				return;
			}

		if (Main.enableVehicleLimiter) {
			int maxAmount = PermissionHandler.getMaxOwnVehicles(data.getClicker());
			int spawned = QualityArmoryVehicles.getOwnedVehicles(data.getClicker().getUniqueId()).size();
			if (spawned >= maxAmount) {
				data.getClicker().sendMessage(MessagesConfig.MESSAGE_TOO_MANY_VEHICLES);
				return;
			}
		}
			if (EconHandler.hasEnough(ab.getCost(), data.getClicker())) {
				ItemStack item = ItemFact.getCarItem(ab);
				if (data.getClicker().getInventory().firstEmpty() == -1) {
					data.getClicker().sendMessage("Your inventory is full.");
					Main.DEBUG("inventory was full");
					return;
				}
				EconHandler.pay(ab.getCost(), data.getClicker());
				data.getClicker().sendMessage(MessagesConfig.MESSAGE_BOUGHT_CAR
						.replace("%car%", ab.getDisplayname()).replace("%price%", ab.getCost() + ""));
				if (Main.enableGarage) {
					QualityArmoryVehicles.addUnlockedVehicle(data.getClicker(), new UnlockedVehicle(ab,ab.getMaxHealth(),true));
				} else {
					data.getClicker().getInventory().addItem(item);
				}
				Main.DEBUG("Finished paying for vehicle");
				LAST_SHOP.put(data.getClicker().getUniqueId(), System.currentTimeMillis());
			} else {
				data.getClicker().sendMessage(MessagesConfig.MESSAGE_NOT_ENOUGH_MONEY);
			}
		data.getClicker().closeInventory();
		}

}
