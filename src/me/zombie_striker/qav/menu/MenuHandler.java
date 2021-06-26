package me.zombie_striker.qav.menu;

import me.zombie_striker.qav.*;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.easygui.ClickData;
import me.zombie_striker.qav.easygui.EasyGUI;
import me.zombie_striker.qav.easygui.EasyGUICallable;
import me.zombie_striker.qav.fuel.FuelItemStack;
import me.zombie_striker.qav.menu.easyguicallables.ShopCallable;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MenuHandler implements Listener {

	public static HashMap<UUID, VehicleEntity> storedLookAt = new HashMap<>();

	public static VehicleEntity getStoredVehicle(UUID player) {
		return storedLookAt.get(player);
	}

	public static boolean isVehicleWindow(InventoryView view, VehicleEntity ve) {
		if (view.getTitle().startsWith("QAV" + ve.getVehicleUUID().toString()))
			return true;
		return false;
	}

	public static void openOverview(Player player, VehicleEntity ve) {
		storedLookAt.put(player.getUniqueId(), ve);
		EasyGUI gui = openOverview(ve);
		player.openInventory(gui.getPageByID(0));
	}
	public static EasyGUI openOverview(VehicleEntity ve) {

		ItemStack[] buttons = new ItemStack[Main.vehicleTypes.size()];
		EasyGUICallable[] callables = new EasyGUICallable[buttons.length];

		final EasyGUI gui = EasyGUI.generateGUIIfNoneExist(18, "QAV" + ve.getVehicleUUID().toString() + "Overview",
				MessagesConfig.MENU_OVERVIEW_TITLE.replace("%cartype%", ve.getType().getDisplayname()), true);

		//storedLookAt.remove(player.getUniqueId());

		if (Main.enableTrunks) {
			List<ItemStack> quickTrunk = Arrays.asList(ve.getTrunk().getContents());
		//	if (quickTrunk.contains(null))
		//		quickTrunk.remove(null);
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
			gui.updateButton(4, ItemFact.a(Material.CHEST, MessagesConfig.ICON_TRUNK, trunklore), new EasyGUICallable() {
				@Override
				public void call(ClickData data) {
					data.cancelPickup(true);
					VehicleEntity ve = getStoredVehicle(data.getClicker().getUniqueId());
					Main.DEBUG("will open trunk");
					data.getClicker().openInventory(ve.getTrunk());
				}
			});
		}
		if (ve.getWhiteList() != null) {
			String[] lore = new String[1 + ve.getWhiteList().size()];
			lore[0] = MessagesConfig.ICONLORE_LIST_WHITELIST;
			int i = 1;
			for (UUID pass : ve.getWhiteList()) {
				OfflinePlayer op = Bukkit.getOfflinePlayer(pass);
				lore[i] = ChatColor.GRAY + (op != null ? op.getName() : null);
				i++;
			}
			gui.updateButton(1, ItemFact.askull(null, MessagesConfig.ICON_ADD_WHITELIST, lore), new EasyGUICallable() {
				@Override
				public void call(ClickData data) {
					data.cancelPickup(true);
					Main.DEBUG("will open add whitelist");
					openAddWhitelist(data.getClicker(), getStoredVehicle(data.getClicker().getUniqueId()));
				}
			});
			gui.updateButton(10, ItemFact.a(Material.BARRIER, MessagesConfig.ICON_REMOVE_WHITELIST, lore), new EasyGUICallable() {
				@Override
				public void call(ClickData data) {
					data.cancelPickup(true);
					Main.DEBUG("will open add whitelist");
					openRemoveWhitelist(data.getClicker(), getStoredVehicle(data.getClicker().getUniqueId()));
				}
			});
		}
		gui.updateButton(5, ItemFact.a(Material.COBBLESTONE_STAIRS, MessagesConfig.ICON_PASSAGERS), new EasyGUICallable() {
			@Override
			public void call(ClickData data) {
				data.cancelPickup(true);
				Main.DEBUG("will open passagers");
				openSetAsPassager(data.getClicker(), getStoredVehicle(data.getClicker().getUniqueId()));
			}
		});
		Material sign = null;
		try {
			sign = Material.valueOf("OAK_SIGN");
		} catch (Error | Exception e4) {
			sign = Material.valueOf("SIGN");
		}
		final Material sign2 = sign;
		gui.updateButton(0, ItemFact.a(sign, MessagesConfig.translatePublic(ve), MessagesConfig.ICONLORE_PUBLIC), new EasyGUICallable() {
			@Override
			public void call(ClickData data) {
				data.cancelPickup(true);
				VehicleEntity ve = getStoredVehicle(data.getClicker().getUniqueId());
				Main
						.DEBUG("Swapping ispublic from " + ve.allowsPassagers() + " to " + (!ve.allowsPassagers()));
				ve.setAllowsPassagers(!ve.allowsPassagers());
				gui.updateButton(0, ItemFact.a(sign2, MessagesConfig.translatePublic(ve), MessagesConfig.ICONLORE_PUBLIC));
			}
		});


		if (ve.getType().enableFuel()) {


			List<ItemStack> quickFuel = new ArrayList<ItemStack>(Arrays.asList(ve.getFuels().getContents()));
			int fuelInt = 0;
			String[] fuelLore = new String[1];
			for (ItemStack is : quickFuel) {
				if (is != null)
					fuelInt += FuelItemStack.getFuelForItem(is) * is.getAmount();
			}
			fuelLore[0] = MessagesConfig.ICONLORE_TRUNK_CONTAINS + " " + (fuelInt / 20) + "s";


			gui.updateButton(7, ItemFact.a(Material.COAL, MessagesConfig.ICON_CHECK_FUEL, fuelLore), new EasyGUICallable() {
				@Override
				public void call(ClickData data) {
					data.cancelPickup(true);
					VehicleEntity ve = getStoredVehicle(data.getClicker().getUniqueId());
					Main.DEBUG("will open check fuel");
					openFuelTank(data.getClicker(), ve);
				}
			});
		}
		gui.updateButton(16, ItemFact.a(Material.IRON_BLOCK, MessagesConfig.ICON_HEALTH, MessagesConfig.ICONLORE_HEALTH_FORMAT.replace("%maxhealth%", ve.getType().getMaxHealth() + "")
				.replace("%health%", "" + ve.getHealth())), new EasyGUICallable() {
			@Override
			public void call(ClickData data) {
			}
		});
		if (Main.allowVehiclePickup) {
			gui.updateButton(8, ItemFact.a(ve.getType().getMaterial(), ve.getType().getItemData(), true, MessagesConfig.ICON_PICKUP,
					MessagesConfig.ICONLORE_PICKUP_OWNER, MessagesConfig.ICONLORE_PICKUP_TRUNK), new EasyGUICallable() {
				@Override
				public void call(ClickData data) {
					data.cancelPickup(true);
					if (data.getClicker() == null)
						return;
					if(!data.getClicker().hasPermission(PermissionHandler.PERM_PICKUP)) {
						return;
					}
					VehicleEntity ve = getStoredVehicle(data.getClicker().getUniqueId());
					if (ve == null) {
						data.getClicker().closeInventory();
						return;
					}

					Main.DEBUG("will open pickup");
					if (ve.getOwner() == null || ve.getOwner().equals(data.getClicker().getUniqueId())) {
						data.getClicker().closeInventory();
						storedLookAt.remove(data.getClicker().getUniqueId());
						Entity driver = null;
					//	if (ve.getDriverSeat() == null)
					//		VehicleEntity.DriverFinder.updateNearbyVehicles(ve, ve.getLastLoc());
						driver = ve.getDriverSeat().getPassenger();
						if (driver != null) {
							data.getClicker().sendMessage(MessagesConfig.MESSAGE_CannotPickupWhileInVehicle);
							return;
						}
						data.getClicker().closeInventory();

						if (data.getClicker().getInventory().firstEmpty() == -1)
							data.getClicker().sendMessage(MessagesConfig.MESSAGE_PICKUP_DROPPED);

						QAVCommand.callback(ve,data.getClicker(),"pickup_menu");
					}
				}
			});
		}

		if (ve.getOwner() != null) {
			OfflinePlayer op = Bukkit.getOfflinePlayer(ve.getOwner());
			String name = (op == null ? "Null Owner" : (op.getName() != null ? op.getName() : "Null Name"));
			gui.updateButton(17, ItemFact.a(Material.BARRIER, MessagesConfig.ICON_OWNERSHIP,
					MessagesConfig.ICONLORE_currentowner.replaceAll("%owner%", name)), new EasyGUICallable() {
				@Override
				public void call(ClickData data) {
					VehicleEntity ve = getStoredVehicle(data.getClicker().getUniqueId());
					if (ve == null)
						return;
					//if (ve.getDriverSeat() == null)
					//	VehicleEntity.DriverFinder.updateNearbyVehicles(ve, ve.getLastLoc());
					if (ve.getOwner() != null && ve.getOwner().equals(data.getClicker().getUniqueId())) {
						ve.setOwner(null);
						data.getClicker().sendMessage(MessagesConfig.MESSAGE_NO_OWNER_NOW);
						data.getClicker().closeInventory();
					}
				}
			});
		}
		//player.openInventory(overview);
		return gui;
	}

	public static void giveOrDrop(Player player, ItemStack is) {
		if (player.getInventory().firstEmpty() != -1) {
			player.getInventory().addItem(is);
		} else {
			player.getWorld().dropItem(player.getLocation(), is);
		}
	}

	public static void openShop(Player player, Class<?> class1) {
		List<EasyGUICallable> callables = new ArrayList<EasyGUICallable>();
		List<ItemStack> buttons = new ArrayList<ItemStack>();
		ShopCallable button = new ShopCallable();
		int i = 0;
		for (FuelItemStack fuel : FuelItemStack.getFuels()) {
			if (fuel.isAllowedInShop()) {
				buttons.add(ItemFact.a(fuel.getMaterial(), fuel.getData(), true, fuel.getDisplayname(),
						ChatColor.GOLD + "Cost: " + fuel.getCost()));
				callables.add(button);
				i++;
			}
		}
		for (AbstractVehicle ab : Main.vehicleTypes) {
			if (ab.isAllowedInShop()) {
				if (!Main.enable_RequirePermToBuyVehicle || PermissionHandler.canDrive(player, ab))
					if (class1 == null || class1.isInstance(ab)) {
						buttons.add(ItemFact.a(ab.getMaterial(), ab.getItemData(), true, ab.getDisplayname(),
								ChatColor.GOLD + "Cost: " + ab.getCost()));
						callables.add(button);
						i++;
					}
			}
		}
		EasyGUI gui = EasyGUI.generateGUIIfNoneExist(buttons.toArray(new ItemStack[buttons.size()]),
				MessagesConfig.MENU_SHOP_TITLE, false);
		gui.updateAllCallables(callables.toArray(new EasyGUICallable[callables.size()]));

		storedLookAt.remove(player.getUniqueId());
		if (gui.getPageByID(0) != null)
			player.openInventory(gui.getPageByID(0));
	}

	public static void openGarageToOther(final Player player, final Player target) {
		final List<UnlockedVehicle> list = QualityArmoryVehicles.unlockedVehicles(target);

		EasyGUICallable call = new EasyGUICallable() {
			@Override
			public void call(ClickData data) {

				Main.DEBUG("Open Garage");

				if (!Main.enableGarage) {
					data.cancelPickup(true);
					data.getClicker().getInventory().addItem(data.getClickedItem());
					data.getClicker().closeInventory();
					data.getClicker().sendMessage("Something went wrong with the garage. Only the plugin should be allowed to add items to the garage.");
					return;
				}

				AbstractVehicle ab = QualityArmoryVehicles.getVehicleByItem(data.getClickedItem());
				if (Main.enableVehicleLimiter
						&& QualityArmoryVehicles.getOwnedVehicles(target.getUniqueId())
						.size() >= PermissionHandler.getMaxOwnVehicles(target)) {
					data.getClicker().sendMessage(MessagesConfig.MESSAGE_TOO_MANY_VEHICLES);
					data.getClicker().closeInventory();
				} else {
					if (Main.requirePermissionToDrive) {
						if (!PermissionHandler.canDrive(player, ab)) {
							data.getClicker().sendMessage(MessagesConfig.MESSAGE_NO_PERM_DRIVE);
							data.cancelPickup(true);
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
						data.getClicker().closeInventory();
						QualityArmoryVehicles.removeUnlockedVehicle(target,unlockedVehicle);
						Main.vehicles
								.stream()
								.filter((entity) -> entity.getOwner().equals(target.getUniqueId()))
								.filter((entity) -> entity.getType().getName().equals(unlockedVehicle.getVehicleType().getName()))
								.findFirst()
								.ifPresent((ve) -> QAVCommand.callback(ve,target,"Garage callback"));
						return;
					}

					if (inUse >= amountHas) {
						data.getClicker().sendMessage(MessagesConfig.MESSAGE_TOO_MANY_VEHICLES_Type);
						data.cancelPickup(true);
						Main.DEBUG("Player has too many vehicles.");
						return;
					}

					VehicleEntity ve = QualityArmoryVehicles.spawnVehicle(unlockedVehicle, data.getClicker());
					if (!Main.enableGarageCallback)
						QualityArmoryVehicles.removeUnlockedVehicle(target,unlockedVehicle);
					else {
						QualityArmoryVehicles.removeUnlockedVehicle(target,unlockedVehicle);
						unlockedVehicle.setInGarage(false);
						QualityArmoryVehicles.addUnlockedVehicle(target,unlockedVehicle);
					}

					if (Main.garageFuel)
						ve.setFuel(500 * 64);
					ve.getDriverSeat().setPassenger(data.getClicker());
					Main.DEBUG("Set as passager and added fuel");
				}
				data.getClicker().closeInventory();
			}

		};


		int size = Math.max(9, list.size());
		String title = player.getUniqueId() == target.getUniqueId() ? MessagesConfig.MENU_GARAGE_TITLE : String.format(MessagesConfig.MENU_OTHER_GARAGE_TITLE, target.getName());
		EasyGUI gui = EasyGUI.generateGUIIfNoneExist(new ItemStack[size], player.getUniqueId() + MessagesConfig.MENU_GARAGE_TITLE, title, true, true);
		storedLookAt.remove(player.getUniqueId());
		int i = 0;
		for (UnlockedVehicle ab : list) {
			gui.updateButton(i, ItemFact.getCarItem(ab.getVehicleType()), call);
			i++;
		}
		player.openInventory(gui.getPageByID(0));
	}

	public static void openGarage(final Player player) {
		openGarageToOther(player,player);
	}

	public static void openAddWhitelist(Player player, VehicleEntity ve) {

		EasyGUICallable call = new EasyGUICallable() {
			@Override
			public void call(ClickData data) {

				Main.DEBUG("Opend add whitelist");
				if (data.getClickedItem() == null || data.getClickedItem().getType() == Material.AIR
						|| !data.getClickedItem().hasItemMeta())
					return;
				OfflinePlayer clicked = getClickedSkull(data.getClickedItem());
				VehicleEntity ve = storedLookAt.get(data.getClicker().getUniqueId());
				if (ve == null)
					return;
				//if (ve.getDriverSeat() == null)
				//	VehicleEntity.DriverFinder.updateNearbyVehicles(ve, ve.getLastLoc());
				if (clicked != null && ve != null) {
					data.getClicker()
							.sendMessage(MessagesConfig.MESSAGE_ADD_PLAYER_WHITELIST.replace("%name%", clicked.getName()));
					if (!ve.allowUserDriver(clicked.getUniqueId()))
						ve.addToWhitelist(clicked.getUniqueId());
					openOverview(data.getClicker(), ve);
					Main.DEBUG("Added to whitelist");
				}
			}
		};
		List<Player> players = new ArrayList<>();

		for (Player p0 : Bukkit.getOnlinePlayers()) {
			if (ve.getWhiteList() == null || !ve.getWhiteList().contains(p0.getUniqueId()))
				players.add(p0);
		}

		EasyGUI gui = EasyGUI.generateGUIIfNoneExist(Math.max(9, (players.size() / 9) * 9), "QAV" + ve.getVehicleUUID().toString() + "AddWhitelist", MessagesConfig.MENU_ADD_ALLOWED_TITLE.replace("%cartype%", ve.getType().getDisplayname()), true);


		//Inventory overview = Bukkit.createInventory(null, ((Bukkit.getOnlinePlayers().size() % 9) + 1) * 9,
		//		MessagesConfig.MENU_ADD_ALLOWED_TITLE.replace("%cartype%", ve.getType().getDisplayname()));
		int i = 0;
		for (Player online : players) {
			if(Main.useHeads) {
				gui.updateButton(i, ItemFact.askull(online.getName(), ChatColor.YELLOW + online.getName()), call);
			}else{
				gui.updateButton(i, ItemFact.a(Material.STONE, ChatColor.YELLOW + online.getName()), call);
			}
			i++;
		}
		player.openInventory(gui.getPageByID(0));
		storedLookAt.put(player.getUniqueId(), ve);
	}

	public static void openRemoveWhitelist(Player player, VehicleEntity ve) {
		EasyGUICallable call = new EasyGUICallable() {
			@Override
			public void call(ClickData data) {

				Main.DEBUG("Clicked remove whitelist");
				if (data.getClickedItem() == null || data.getClickedItem().getType() == Material.AIR
						|| !data.getClickedItem().hasItemMeta())
					return;
				OfflinePlayer clicked = getClickedSkull(data.getClickedItem());
				VehicleEntity ve = storedLookAt.get(data.getClicker().getUniqueId());
				if (clicked != null && ve != null) {
					if (MessagesConfig.MESSAGE_REMOVE_PLAYER_WHITELIST != null)
						data.getClicker().sendMessage(
								MessagesConfig.MESSAGE_REMOVE_PLAYER_WHITELIST.replace("%name%", clicked.getName()));
					if (ve.allowUserDriver(clicked.getUniqueId()))
						ve.removeFromWhitelist(clicked.getUniqueId());
					openOverview(data.getClicker(), ve);
					Main.DEBUG("Removed from whitelist");
				}
			}
		};
		List<String> players = new ArrayList<>();
		if (ve.getWhiteList() != null)
			for (UUID online : ve.getWhiteList()) {
				if (!online.equals(ve.getOwner())) {
					String name = Bukkit.getOfflinePlayer(online).getName();
					players.add(name);
				}
			}
		EasyGUI gui = EasyGUI.generateGUIIfNoneExist(Math.max(9, (players.size() / 9) * 9), "QAV" + ve.getVehicleUUID().toString() + "RemoveWhitelist", MessagesConfig.MENU_REMOVE_ALLOWED_TITLE.replace("%cartype%", ve.getType().getDisplayname()), true);


		//Inventory overview = Bukkit.createInventory(null, ((ve.getWhiteList().size() % 9) + 1) * 9,
		//		MessagesConfig.MENU_REMOVE_ALLOWED_TITLE.replace("%cartype%", ve.getType().getDisplayname()));
		int i = 0;
		for (String name : players) {
			if(Main.useHeads) {
				gui.updateButton(i, ItemFact.askull(name, ChatColor.YELLOW + name), call);
			}else{
				gui.updateButton(i, ItemFact.a(Material.STONE, ChatColor.YELLOW + name), call);
			}
			i++;

		}
		player.openInventory(gui.getPageByID(0));
		storedLookAt.put(player.getUniqueId(), ve);
	}

	@SuppressWarnings("deprecation")
	public static void openSetAsPassager(Player player, VehicleEntity ve) {

		EasyGUICallable call = new EasyGUICallable() {
			@Override
			public void call(ClickData data) {
				Main.DEBUG("Clicked setaspassager");
				VehicleEntity ve = storedLookAt.get(data.getClicker().getUniqueId());
				if (data.getClickedItem() != null && ve != null) {
					if (data.getClickedItem().getType() == Material.BRICK_STAIRS) {
						Entity driver = null;
						driver = ve.getDriverSeat().getPassenger();
						if (driver == null) {
							if (!Main.requirePermissionToDrive || PermissionHandler.canDrive(player, ve.getType())) {
								ve.getDriverSeat().setPassenger(data.getClicker());
								Main.DEBUG("Added player to seat!");
							}else{;
								Main.DEBUG("Stopped player from being added to seat!");
							}
							} else {
							Main.DEBUG("Another passager is already in driver seat : "
									+ (driver != null ? driver.getName() : "ERROR"));
						}
					} else {
						Entity pass = null;
						if ((pass = ve.getPassager(data.getClickedItem().getAmount() - 1)) == null
								&& data.getSlot() - 1 < ve.getType().getPassagerSpots().size()) {
							QualityArmoryVehicles.setAddPassager(ve,
									data.getClicker(), data.getClickedItem().getAmount() - 1);
							Main.DEBUG("Added player to seat!");
						} else {
							Entity rider = pass.getPassenger();
							Main
									.DEBUG("Another passager is already in the " + (data.getClickedItem().getAmount() - 1)
											+ " seat : " + (rider != null ? rider.getName() : "ERROR"));
						}
					}
					data.getClicker().closeInventory();
				}

			}
		};

		EasyGUI gui = EasyGUI.generateGUIIfNoneExist(Math.max(9, ve.getType().getPassagerSpots().size() + 1), "QAV" + ve.getVehicleUUID().toString() + "PassagerSeats", MessagesConfig.MENU_PASSAGER_SEATS_TITLE.replace("%cartype%", ve.getType().getDisplayname()), true);


		//Inventory overview = Bukkit.createInventory(null, (((ve.getType().getPassagerSpots().size() + 1) % 9) + 1) * 9,
		//		MessagesConfig.MENU_PASSAGER_SEATS_TITLE.replace("%cartype%", ve.getType().getDisplayname()));
		String name = null;
		ItemStack is = null;
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
		gui.updateButton(0, is, call);
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
			gui.updateButton(1 + i, is, call);
		}
		player.openInventory(gui.getPageByID(0));
		storedLookAt.put(player.getUniqueId(), ve);
	}

	public static void openFuelTank(Player player, VehicleEntity ve) {
		//List<ItemStack> fuels = ve.getFuels();
		Inventory overview = ve.getFuels();
		player.openInventory(overview);
		storedLookAt.put(player.getUniqueId(), ve);
	}

	public static boolean clickedIcon(ItemStack clicked, String nameCheck) {
		return clicked != null && clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()
				&& clicked.getItemMeta().getDisplayName().equals(nameCheck);
	}

	@SuppressWarnings("deprecation")
	public static OfflinePlayer getClickedSkull(ItemStack clicked) {
		try {
			if (clicked != null && clicked.getType() != Material.PLAYER_HEAD) {
				Bukkit.getOfflinePlayer(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
			}
		}catch (Error|Exception e4){
			if (clicked != null && clicked.getType().name().equals("SKULL_ITEM")) {
				Bukkit.getOfflinePlayer(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
			}
		}
		try {
			return clicked == null ? null : ((SkullMeta) clicked.getItemMeta()).getOwningPlayer();
		} catch (Error | Exception e4) {
		}
		try {
			return clicked == null ? null : Bukkit.getOfflinePlayer(((SkullMeta) clicked.getItemMeta()).getOwner());
		}catch(Error|Exception e4){
			return clicked == null ? null : Bukkit.getOfflinePlayer((ChatColor.stripColor(clicked.getItemMeta().getDisplayName())));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(final InventoryClickEvent e) {

		if (e.getView() == null || e.getView().getTitle() == null) {
			Main.DEBUG("Player Clicked Inventory that was not a menu");
			return;
		}

		if (e.getInventory() != null) {
			if (e.getView().getTitle()
					.endsWith(getTitle((Player) e.getWhoClicked(), MessagesConfig.MENU_ADD_ALLOWED_TITLE))
					|| e.getView().getTitle()
					.endsWith(getTitle((Player) e.getWhoClicked(), MessagesConfig.MENU_REMOVE_ALLOWED_TITLE))
					|| e.getView().getTitle()
					.equals(getTitle((Player) e.getWhoClicked(), MessagesConfig.MENU_FUELTANK_TITLE))
					|| e.getView().getTitle().endsWith(MessagesConfig.MENU_GARAGE_TITLE)
					|| e.getView().getTitle()
					.equals(getTitle((Player) e.getWhoClicked(), MessagesConfig.MENU_PASSAGER_SEATS_TITLE))
					|| e.getView().getTitle()
					.equals(getTitle((Player) e.getWhoClicked(), MessagesConfig.MENU_OVERVIEW_TITLE))
					|| e.getView().getTitle()
					.equals(getTitle((Player) e.getWhoClicked(), MessagesConfig.MENU_SHOP_TITLE))) {
				if (e.getClick().isShiftClick()) {
					e.setCancelled(true);
				}
			} else {
				return;
			}
		}
		if (e.getClickedInventory() == null) {
			return;
		}
		Main.DEBUG("Clicked a custom GUI. Title was " + e.getView().getTitle());
		if (!storedLookAt.containsKey(e.getWhoClicked().getUniqueId())) {
			return;
		}
		if (e.getView().getTitle()
				.equals(getTitle((Player) e.getWhoClicked(), MessagesConfig.MENU_FUELTANK_TITLE))) {
			if (e.getClick().isShiftClick()) {
				e.setCancelled(true);
				Main.DEBUG("Shift click");

				return;
			}
			if (FuelItemStack.getFuelForItem(e.getCursor()) <= 0 && e.getClickedInventory() != e.getWhoClicked().getInventory()) {
				e.setCancelled(true);
				Main.DEBUG("Non fuel");

				return;
			}
			Main.DEBUG("Start check fuel tank");
			final VehicleEntity ve = storedLookAt.get(e.getWhoClicked().getUniqueId());
			/*new BukkitRunnable() {

				@Override
				public void run() {
					ve.setFuels(e.getInventory().getContents());
				}
			}.runTaskLater(Main.instance, 1);*/
		} else {
			Main.DEBUG("Reached end, but no inventory was found.");
		}
	}

	public String getTitle(Player p, String title) {
		String newTitle = title;
		if (title.contains("%cartype%"))
			if (storedLookAt.containsKey(p.getUniqueId()))
				newTitle = title.replace("%cartype%", storedLookAt.get(p.getUniqueId()).getType().getDisplayname());
		return newTitle;
	}

	@EventHandler
	public void onClose(final InventoryCloseEvent e) {
		if (storedLookAt.containsKey(e.getPlayer().getUniqueId())) {
			if (e.getView().getTitle()
					.equals(getTitle((Player) e.getPlayer(), MessagesConfig.MENU_FUELTANK_TITLE))) {
				Main.DEBUG("Start check fuel tank on close");
				final VehicleEntity ve = storedLookAt.get(e.getPlayer().getUniqueId());
				new BukkitRunnable() {

					@Override
					public void run() {
						//ve.setFuels(e.getInventory().getContents());
						FuelItemStack.updateFuel(ve);
					}
				}.runTaskLater(QualityArmoryVehicles.getPlugin(), 1);
			}
		}
	}
}
