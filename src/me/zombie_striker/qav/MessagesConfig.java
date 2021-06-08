package me.zombie_striker.qav;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.util.HotbarMessager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

//import me.zombie_striker.pluginconstructor.HotbarMessager;

public class MessagesConfig {

	public static String MENU_OVERVIEW_TITLE = "&6%cartype%:&f Overview";
	public static String MENU_ADD_ALLOWED_TITLE = "&6%cartype%:&f Add To Whitelist";
	public static String MENU_REMOVE_ALLOWED_TITLE = "&6%cartype%:&f Remove From Whitelist";
	public static String MENU_PASSAGER_SEATS_TITLE = "&6%cartype%:&f Empty Seats";
	public static String MENU_FUELTANK_TITLE = "&6%cartype%:&f Check Fuel";
	public static String MENU_SHOP_TITLE = "&6Vehicle shop";
	public static String MENU_GARAGE_TITLE = "&6Your vehicles";
	public static String MENU_OTHER_GARAGE_TITLE = "&6%s's vehicles";
	public static String ICON_ADD_WHITELIST = "&9Add player to whitelist";
	public static String ICON_REMOVE_WHITELIST = "&9Remove player from whitelist";
	public static String ICON_CHECK_FUEL = "&9Check Fueltank";
	public static String ICON_PICKUP = "&9Pickup Vehicle";
	public static String ICON_TRUNK = "&8Open Trunk";
	public static String ICON_HEALTH = "&8Vehicle's Health:";
	public static String ICON_OWNERSHIP = "&9Remove Ownership";
	public static String ICON_ISPUBLIC = "&9Open To The Public = %public%";
	public static String ICON_PASSAGERS = "&9Enter a Passager Seat";
	public static String ICON_PASSAGERS_FULL = "&aSeat taken by %name%";
	public static String ICON_PASSAGERS_EMPTY = "&aEmpty";
	public static String ICONLORE_LIST_WHITELIST = "&fCurrently whitelisted:";
	public static String ICONLORE_PUBLIC = "&fPublic vehicles allow all players to ride as passagers.";
	public static String ICONLORE_PICKUP_OWNER = "&fOnly available for owner";
	public static String ICONLORE_PICKUP_TRUNK = "&fAll items in trunk will be given to the player or dropped";
	public static String ICONLORE_LIST_FUEL = "&fFuel In tank: ";
	public static String ICONLORE_TRUNK_CONTAINS = "&fContains:";
	public static String ICONLORE_HEALTH_FORMAT = "&f%health% / %maxhealth%";
	public static String ICONLORE_currentowner = "&fCurrent owner: %owner%";
	public static String ICONLORE_PASSAGERS_DRIVERSEAT = "&aDriver Seat";
	public static String MESSAGE_ADD_PLAYER_WHITELIST = "&fAdded player %name% to whitelist";
	public static String MESSAGE_REMOVE_PLAYER_WHITELIST = "&fRemoved player %name% from whitelist";
	public static String MESSAGE_PICKUP_FULL = "&fYour inventory is full!";
	public static String MESSAGE_PICKUP_DROPPED = "&fThere were too many items in the trunk. Some items have been dropped to the floor.";
	public static String MESSAGE_SIGN_SHOP = "[QAV-Shop]";
	public static String MESSAGE_SIGN_GARAGE = "[QAV-Garage]";
	public static String MESSAGE_HELI_CHANGESPEED = "&fSpeed setting set to %speed%";
	public static String MESSAGE_BLACKLIST_WORLD = "&aYou are not allowed to place vehicles in this world.";
	public static String MESSAGE_NOW_OWN_CAR = "&fYou are now the owner of this %car%";
	public static String MESSAGE_BOUGHT_CAR = "&fYou have bought %car% for $%price%";
	public static String MESSAGE_NOT_ENOUGH_MONEY = "&aYou do not have enough money!";
	public static String MESSAGE_TOO_MANY_VEHICLES = "&fYou have too many vehicles spawned! Pick up some of your vehicles to use this one.";
	public static String MESSAGE_TOO_MANY_VEHICLES_Type = "&fThis vehicle has already been spawned!";
	public static String MESSAGE_NO_PERM_DRIVE = "&fyou do not have permission to drive this vehicle.";
	public static String MESSAGE_NO_OWNER_NOW = "&fThis vehicle is now public. Anyone can drive it or pick it up.";
	public static String MESSAGE_CannotPickupWhileInVehicle = "&fYou cannot pickup vehicles that are being driven.";
	public static String MESSAGE_HOTBAR_OUTOFFUEL = "&fYour vehicle is out of fuel. Find coal and Shift-click the car";
	public static String subcommand_GiveVehicle = " <car> <?:player>: Gives you or another player a car";
	public static String subcommand_SpawnVehicle = " <car> : Spawns a car at your location";
	public static String subcommand_removeNearbyVehicles = " <distance> : Removes all cars nearby";
	public static String subcommand_setAsPass = " <id> : Sets the player as a passager for the closest car. (Seats start at 0)";
	public static String subcommand_Shop = " : Opens the vehicle shop";
	public static String subcommand_garage = " : Opens the player's garage";
	public static String subcommand_callbackAll = " <radius> : Callsback all vehicles you own in the world";
	public static String subcommand_callback = " <radius> : Callsback all vehicles you own within a radius";
	public static String subcommand_addToWhitelist = " <player>: Adds the player to the vehicle's whitelist";
	public static String subcommand_removeFromWhitelist = " <player>: Removes the player to the vehicle's whitelist";
	public static String subcommand_registerfuel = " <ticks of fuel>: Registers the item in the player's main hand as a fuel";
	public static String subcommand_debug = " : ADMIN ONLY: Starts the debug messages.";
	private static File messagesymlfile = new File(QualityArmoryVehicles.getPlugin().getDataFolder(), "messages.yml");
	private static FileConfiguration messagesyml;
	private static boolean forceUpdate = false;

	public static String COMMANDMESSAGES_NO_PERM = colorize("&4You do not have permission to use this command.");
	public static String COMMANDMESSAGES_ONLY_PLAYERs = "&4Only players can use this command.";
	public static String COMMANDMESSAGES_ADD_VEHICLE_GARAGE = " The vehicle \"%car%\" has been added to %name%'s garage.";
	public static String COMMANDMESSAGES_VALID_NAME = "&4 The name provided is not of a player that is on the server.";
	public static String COMMANDMESSAGES_VALID_VEHICLE = "&4 The name provided is not of a registered vehicle.";
	public static String COMMANDMESSAGES_VALID_DISTANCE = "&4 A valid distance is required.";
	public static String COMMANDMESSAGES_ALL_REMOVE_FROM_WORLD = " All %count% vehicles have been removed from all worlds.";
	public static String COMMANDMESSAGES_SPAWN_VEHICLE = " Vehicle %car% has been spawned.";
	public static String COMMANDMESSAGE_REMOVE_NEARBY = " Removed %count% nearby vehicles within a %distance% radius.";
	public static String COMMANDMESSAGE_CALLBACKALL = " All %count% vehicles have been returned to their owners.";
	public static String COMMANDMESSAGES_REMOVE_VEHICLE_GARAGE =  "All \"%car%\" vehicle types have been removed from %name%'s garage.";

	public static void init() {
		messagesyml = YamlConfiguration.loadConfiguration(messagesymlfile);


		subcommand_GiveVehicle = (
				a("Commands.spawnvehicle", subcommand_GiveVehicle));
		subcommand_SpawnVehicle = (
				a("Commands.spawnvehicle", subcommand_SpawnVehicle));
		subcommand_removeNearbyVehicles = (
				a("Commands.removenearby", subcommand_removeNearbyVehicles));
		subcommand_setAsPass = (
				a("Commands.setAsPassagers", subcommand_setAsPass));
		subcommand_Shop = (
				a("Commands.Shop", subcommand_Shop));
		subcommand_garage = (
				a("Commands.garage", subcommand_garage));
		subcommand_callbackAll = (
				a("Commands.callbackAll", subcommand_callbackAll));
		subcommand_callback = (
				a("Commands.callback", subcommand_callback));
		subcommand_addToWhitelist = (
				a("Commands.addToWhitelist", subcommand_addToWhitelist));
		subcommand_removeFromWhitelist = (
				a("Commands.removeFromWhitelist", subcommand_removeFromWhitelist));
		subcommand_registerfuel = (
				a("Commands.RegisterFuels", subcommand_registerfuel));
		subcommand_debug = (
				a("Commands.debug", subcommand_debug));


		MENU_OVERVIEW_TITLE = (
				a("Menu.Overview.Title", MENU_OVERVIEW_TITLE));
		MENU_ADD_ALLOWED_TITLE =(
				a("Menu.Add_Whitelist.Title", MENU_ADD_ALLOWED_TITLE));
		MENU_REMOVE_ALLOWED_TITLE = (
				a("Menu.Remove_Whitelist.Title", MENU_REMOVE_ALLOWED_TITLE));
		MENU_FUELTANK_TITLE = (
				a("Menu.Check_FuelTank.Title", MENU_FUELTANK_TITLE));
		MENU_SHOP_TITLE =( a("Menu.Shop.Title", MENU_SHOP_TITLE));
		MENU_GARAGE_TITLE = ( a("Menu.Garage.Title", MENU_GARAGE_TITLE));
		MENU_PASSAGER_SEATS_TITLE = (
				a("Menu.setAsPassager.Title", MENU_PASSAGER_SEATS_TITLE));

		ICON_ADD_WHITELIST = (
				a("Icon.Add_Whitelist.Title", ICON_ADD_WHITELIST));
		ICON_REMOVE_WHITELIST = (
				a("Icon.Remove_Whitelist.Title", ICON_REMOVE_WHITELIST));
		ICON_CHECK_FUEL = (a("Icon.CheckFueltank.Title", ICON_CHECK_FUEL));
		ICON_ISPUBLIC = ( a("Icon.Public_Status.Title", ICON_ISPUBLIC));
		ICON_TRUNK = ( a("Icon.Trunk.Title", ICON_TRUNK));
		ICON_PICKUP = ( a("Icon.Pickup.Title", ICON_PICKUP));
		ICON_HEALTH = ( a("Icon.Health.Title", ICON_HEALTH));
		ICON_OWNERSHIP = ( a("Icon.Remove_Ownership.Title", ICON_OWNERSHIP));
		ICON_PASSAGERS = ( a("Icon.SetAsPassager.Title", ICON_PASSAGERS));
		ICON_PASSAGERS_EMPTY = (
				a("Icon.PASSAGER.EMPTY.Title", ICON_PASSAGERS_EMPTY));
		ICON_PASSAGERS_FULL = (
				a("Icon.PASSAGER.TAKEN.Title", ICON_PASSAGERS_FULL));

		ICONLORE_LIST_FUEL = (
				a("Icon.Check_FuelTank.Lore", ICONLORE_LIST_FUEL));
		ICONLORE_currentowner = (
				a("Icon.Remove_Ownership.Lore", ICONLORE_currentowner));
		ICONLORE_LIST_WHITELIST = (
				a("Icon.Add_Whitelist.Lore", ICONLORE_LIST_WHITELIST));
		ICONLORE_PICKUP_OWNER = (
				a("Icon.Pickup.Lore_Owner", ICONLORE_PICKUP_OWNER));
		ICONLORE_PICKUP_TRUNK = (
				a("Icon.Pickup.Lore_Trunk", ICONLORE_PICKUP_TRUNK));
		ICONLORE_TRUNK_CONTAINS = (
				a("Icon.Trunk.Lore_Contains", ICONLORE_TRUNK_CONTAINS));
		ICONLORE_HEALTH_FORMAT = (
				a("Icon.Health.Lore_Format", ICONLORE_HEALTH_FORMAT));
		ICONLORE_PUBLIC = (
				a("Icon.Public_Status.Lore_Format", ICONLORE_PUBLIC));
		ICONLORE_PASSAGERS_DRIVERSEAT = (
				a("Icon.PASSAGER.DRIVERSEAT.Lore_Format", ICONLORE_PASSAGERS_DRIVERSEAT));

		MESSAGE_ADD_PLAYER_WHITELIST = (
				a("Messages.addplayertowhitelist", MESSAGE_ADD_PLAYER_WHITELIST));
		MESSAGE_REMOVE_PLAYER_WHITELIST = (
				a("Messages.removeplayerfromwhitelist", MESSAGE_REMOVE_PLAYER_WHITELIST));
		MESSAGE_PICKUP_DROPPED = (
				a("Messages.pickup.dropped", MESSAGE_PICKUP_DROPPED));
		MESSAGE_PICKUP_FULL = (
				a("Messages.pickup.full", MESSAGE_PICKUP_FULL));
		MESSAGE_HOTBAR_OUTOFFUEL = (
				a("MessagesHotbar.OutOfFuel", MESSAGE_HOTBAR_OUTOFFUEL));
		MESSAGE_NOT_ENOUGH_MONEY = (
				a("Messages.Not_enough_money", MESSAGE_NOT_ENOUGH_MONEY));
		MESSAGE_BOUGHT_CAR = (
				a("MessagesHotbar.Bought_car", MESSAGE_BOUGHT_CAR));
		MESSAGE_NO_OWNER_NOW = (
				a("Messages.Ownership_Removed", MESSAGE_NO_OWNER_NOW));
		MESSAGE_CannotPickupWhileInVehicle = (
				a("Messages.Cannot_Pickup_Vehicle_While_Driving", MESSAGE_CannotPickupWhileInVehicle));
		MESSAGE_NOW_OWN_CAR = (
				a("Messages.NewOwnerMessage", MESSAGE_NOW_OWN_CAR));
		MESSAGE_HELI_CHANGESPEED = (
				a("Messages.InputChangeSpeed", MESSAGE_HELI_CHANGESPEED));
		MESSAGE_TOO_MANY_VEHICLES = (
				a("Messages.ToManyVehicles", MESSAGE_TOO_MANY_VEHICLES));
		MESSAGE_TOO_MANY_VEHICLES_Type = (
				a("Messages.ToManyVehiclesType", MESSAGE_TOO_MANY_VEHICLES_Type));

		MESSAGE_BLACKLIST_WORLD = (
				a("Messages.BlacklistedWorld_StopPlace", MESSAGE_BLACKLIST_WORLD));
		MESSAGE_NO_PERM_DRIVE = (
				a("Messages.NoPermissionToDrive", MESSAGE_NO_PERM_DRIVE));

		MESSAGE_SIGN_SHOP = ( a("Messages.sign.QAVShop", MESSAGE_SIGN_SHOP));
		MESSAGE_SIGN_GARAGE = (
				a("Messages.sign.QAVGarage", MESSAGE_SIGN_GARAGE));



		b();
	}

	private static String colorize(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String translatePublic(VehicleEntity ve) {
		return ICON_ISPUBLIC.replaceAll("%public%",
				(ve.allowsPassagers ? ChatColor.GREEN + "" : ChatColor.DARK_RED + "") + ve.allowsPassagers);
	}

	public static String translateLocked(VehicleEntity ve) {
		return ICON_ISPUBLIC.replaceAll("%locked%",
				(ve.allowsPassagers ? ChatColor.GREEN + "" : ChatColor.DARK_RED + "") + ve.allowsPassagers);
	}


	public static void sendOutOfFuel(final Player player) {
		new BukkitRunnable() {
			public void run() {
				if (!Main.useChatForMessage) {
					try {
						HotbarMessager.sendHotBarMessage(player, MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
					} catch (Error | Exception r5) {
					}
				} else {
					player.sendMessage(MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
				}
			}
		}.runTaskLater(QualityArmoryVehicles.getPlugin(), 0);
	}

	private static String a(String path, String o) {
		if (messagesyml.contains(path))
			return ChatColor.translateAlternateColorCodes('&',messagesyml.getString(path));
		messagesyml.set(path, o);
		forceUpdate = true;
		return ChatColor.translateAlternateColorCodes('&',o);
	}

	private static void b() {
		if (forceUpdate) {
			try {
				messagesyml.save(messagesymlfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
