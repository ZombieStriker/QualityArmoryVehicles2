package me.zombie_striker.qav;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.util.HotbarMessager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class MessagesConfig {

	public static String MENU_OVERVIEW_TITLE = "&6%cartype%:&f Overview";
	public static String MENU_ADD_ALLOWED_TITLE = "&6%cartype%:&f Add To Whitelist";
	public static String MENU_REMOVE_ALLOWED_TITLE = "&6%cartype%:&f Remove From Whitelist";
	public static String MENU_PASSAGER_SEATS_TITLE = "&6%cartype%:&f Empty Seats";
	public static String MENU_FUELTANK_TITLE = "&6%cartype%:&f Check Fuel";
	public static String MENU_SHOP_TITLE = "&6Vehicle shop";
	public static String MENU_GARAGE_TITLE = "&6Your vehicles";
	public static String MENU_OTHER_GARAGE_TITLE = "&6%s's vehicles";
	public static String ICON_ADD_WHITELIST = "&7Add player to whitelist";
	public static String ICON_REMOVE_WHITELIST = "&7Remove player from whitelist";
	public static String ICON_CHECK_FUEL = "&7Check Fueltank";
	public static String ICON_PICKUP = "&7Pickup Vehicle";
	public static String ICON_TRUNK = "&7Open Trunk";
	public static String ICON_HEALTH = "&7Vehicle's Health:";
	public static String ICON_OWNERSHIP = "&7Remove Ownership";
	public static String ICON_ISPUBLIC = "&7Open to the public [%public%&7]";
	public static String ICON_PASSAGERS = "&7Enter a seat";
	public static String ICON_PASSAGERS_FULL = "&7Seat taken by &6&n%name%";
	public static String ICON_PASSAGERS_EMPTY = "&aEmpty";
	public static String ICONLORE_LIST_WHITELIST = "&7Currently whitelisted:";
	public static String ICONLORE_PUBLIC = "&7Public vehicles allow all players to ride as passengers.";
	public static String ICONLORE_PICKUP_OWNER = "&7Only available for owner";
	public static String ICONLORE_PICKUP_TRUNK = "&7All items in trunk will be given to the player or dropped";
	public static String ICONLORE_LIST_FUEL = "&fFuel In tank: ";
	public static String ICONLORE_TRUNK_CONTAINS = "&fContains:";
	public static String ICONLORE_HEALTH_FORMAT = "&c%health% &7/ &c%maxhealth%";
	public static String ICONLORE_COST = "&7Cost: ";
	public static String ICONLORE_currentowner = "&7Current owner: &6&n%owner%";
	public static String ICONLORE_PASSAGERS_DRIVERSEAT = "&aDriver Seat";
	public static String MESSAGE_ADD_PLAYER_WHITELIST = " Added player &6&n%name%&7 to whitelist";
	public static String MESSAGE_REMOVE_PLAYER_WHITELIST = " Removed player &6&n%name%&7 from whitelist";
	public static String MESSAGE_PICKUP_DROPPED = " There were too many items in the trunk. Some items have been dropped to the floor.";
	public static String MESSAGE_BLACKLIST_WORLD = "&c You are not allowed to place vehicles in this world.";
	public static String MESSAGE_BLACKLIST_PLACE = "&c You are not allowed to place vehicles in this place.";
	public static String MESSAGE_NOW_OWN_CAR = " You are now the owner of this &6&n%car%";
	public static String MESSAGE_BOUGHT_CAR = " You have bought &6%car%&7 for &6$%price%";
	public static String MESSAGE_NOT_ENOUGH_MONEY = "&c You do not have enough money!";
	public static String MESSAGE_TOO_MANY_VEHICLES = "&c You have too many vehicles spawned! Pick up some of your vehicles to use this one.";
	public static String MESSAGE_TOO_MANY_VEHICLES_Type = "&c You have spawned too many vehicles!";
	public static String MESSAGE_NO_PERM_DRIVE = "&c You do not have permission to drive this vehicle.";
	public static String MESSAGE_NO_OWNER_NOW = " This vehicle is now public. Anyone can drive it or pick it up.";
	public static String MESSAGE_CannotPickupWhileInVehicle = "&c You cannot pickup vehicles that are being driven.";
	public static String MESSAGE_HOTBAR_OUTOFFUEL = " Your vehicle is out of fuel. Find coal and Shift-click the car";
	public static String MESSAGE_REPAIR = " Your vehicle has been repaired successfully.";
	public static String MESSAGE_ACTIOBAR_MOVE = "&6Vehicle: &f%type% | &6Fuel: &f%fuel% | &6Speed: &f%speed%km/h";
	public static String subcommand_GiveVehicle = " <car> <?:player>: Gives you or another player a car";
	public static String subcommand_SpawnVehicle = " <car> : Spawns a car at your location";
	public static String subcommand_RemoveVehicle = " <car> : Removes all vehicles of a type";
	public static String subcommand_list = " : Get loaded vehicles list";
	public static String subcommand_removeNearbyVehicles = " <distance> : Removes all cars nearby";
	public static String subcommand_setAsPass = " <id> : Sets the player as a passenger for the closest car. (Seats start at 0)";
	public static String subcommand_Shop = " : Opens the vehicle shop";
	public static String subcommand_garage = " : Opens the player's garage";
	public static String subcommand_callbackAll = " <radius> : Callback all vehicles you own in the world";
	public static String subcommand_callback = " <radius> : Callback all vehicles you own within a radius";
	public static String subcommand_addToWhitelist = " <player>: Adds the player to the vehicle's whitelist";
	public static String subcommand_removeFromWhitelist = " <player>: Removes the player to the vehicle's whitelist";
	public static String subcommand_registerfuel = " <ticks of fuel>: Registers the item in the player's main hand as a fuel";
	public static String subcommand_debug = " : ADMIN ONLY: Starts the debug messages.";
	private static File messagesymlfile = new File(QualityArmoryVehicles.getPlugin().getDataFolder(), "messages.yml");
	private static FileConfiguration messagesyml;
	private static boolean forceUpdate = false;

	public static String COMMANDMESSAGES_RELOAD = " Reloaded config and vehicle files in &6&n%time%ms";
	public static String COMMANDMESSAGES_NO_PERM = "&c You do not have permission to use this command.";
	public static String COMMANDMESSAGES_ONLY_PLAYERs = "&c Only players can use this command.";
	public static String COMMANDMESSAGES_VALID_VEHICLE = "&c The name provided is not of a registered vehicle.";
	public static String COMMANDMESSAGES_REMOVE_BUGGED = " Removed all bugged vehicles from your world.";
	public static String COMMANDMESSAGE_CALLBACKALL = " &7All &6&n%count%&7 vehicles have been returned to their owners.";
	public static String COMMANDMESSAGE_CALLBACK = " Called back all vehicles within a &6&n%radius%&7 radius of the player.";
	public static String COMMANDMESSAGES_TEXTURE = " Click here to download the resource pack";
	public static String COMMANDMESSAGES_NO_VEHICLE = "&c You have to ride a vehicle to perform this command.";
	public static String COMMANDMESSAGES_WHITELIST_OVERRIDE = " You have toggled whitelist override.";
	public static String COOLDOWN = " You have to wait &6&n%time%ms&7 before performing this action again.";
	public static String NEXT_PAGE = "&aNext Page";
	public static String PREV_PAGE = "&cPrevious Page";
	public static String RESOURCEPACK_TITLE = "&a&lDownloading Resourcepack...";
	public static String RESOURCEPACK_SUBTITLE = "&fAccept the resourcepack to see the custom items";
	public static String RESOURCEPACK_CRASH = " In case the resourcepack crashes your client, reject the request and use &6&n/qa getResourcepack&7 to get the resourcepack.";

	public static void init() {
		messagesyml = YamlConfiguration.loadConfiguration(messagesymlfile);

		subcommand_GiveVehicle = (
				a("Commands.givevehicle", subcommand_GiveVehicle));
		subcommand_SpawnVehicle = (
				a("Commands.spawnvehicle", subcommand_SpawnVehicle));
		subcommand_removeNearbyVehicles = (
				a("Commands.removenearby", subcommand_removeNearbyVehicles));
		subcommand_RemoveVehicle = (
				a("Commands.removeVehicle", subcommand_RemoveVehicle));
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
		COMMANDMESSAGES_TEXTURE = (
				a("Commands.TexturePack", COMMANDMESSAGES_TEXTURE));
		COMMANDMESSAGES_NO_VEHICLE = (
				a("Commands.NoVehicle", COMMANDMESSAGES_NO_VEHICLE));
		COMMANDMESSAGES_WHITELIST_OVERRIDE = (
				a("Commands.WhitelistOverride", COMMANDMESSAGES_WHITELIST_OVERRIDE));

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
		MENU_OTHER_GARAGE_TITLE = ( a("Menu.Garage.TitleOther", MENU_OTHER_GARAGE_TITLE));
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
		ICONLORE_COST = (
				a("Icon.Shop.Cost", ICONLORE_COST));
		NEXT_PAGE =
				a("Icon.Next", NEXT_PAGE);
		PREV_PAGE =
				a("Icon.Previous", PREV_PAGE);
		MESSAGE_ADD_PLAYER_WHITELIST = (
				a("Messages.addplayertowhitelist", MESSAGE_ADD_PLAYER_WHITELIST));
		MESSAGE_REMOVE_PLAYER_WHITELIST = (
				a("Messages.removeplayerfromwhitelist", MESSAGE_REMOVE_PLAYER_WHITELIST));
		MESSAGE_PICKUP_DROPPED = (
				a("Messages.pickup.dropped", MESSAGE_PICKUP_DROPPED));
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
		MESSAGE_TOO_MANY_VEHICLES = (
				a("Messages.ToManyVehicles", MESSAGE_TOO_MANY_VEHICLES));
		MESSAGE_TOO_MANY_VEHICLES_Type = (
				a("Messages.ToManyVehiclesType", MESSAGE_TOO_MANY_VEHICLES_Type));

		MESSAGE_BLACKLIST_WORLD = (
				a("Messages.BlacklistedWorld_StopPlace", MESSAGE_BLACKLIST_WORLD));
		MESSAGE_BLACKLIST_PLACE = (
				a("Messages.BlacklistedPlace_StopPlace", MESSAGE_BLACKLIST_PLACE));
		MESSAGE_NO_PERM_DRIVE = (
				a("Messages.NoPermissionToDrive", MESSAGE_NO_PERM_DRIVE));
		COMMANDMESSAGES_NO_PERM = (
				a("Commands.NoPermission", COMMANDMESSAGES_NO_PERM));
		COMMANDMESSAGES_ONLY_PLAYERs = (
				a("Commands.OnlyPlayers", COMMANDMESSAGES_ONLY_PLAYERs));
		COMMANDMESSAGES_REMOVE_BUGGED = (
				a("Messages.removeBugged", COMMANDMESSAGES_REMOVE_BUGGED));
		COMMANDMESSAGE_CALLBACKALL = (
				a("Messages.callbackAll", COMMANDMESSAGE_CALLBACKALL));
		COMMANDMESSAGE_CALLBACK = (
				a("Messages.callback", COMMANDMESSAGE_CALLBACK));
		MESSAGE_REPAIR = (
				a("Messages.repair", MESSAGE_REPAIR));
		MESSAGE_ACTIOBAR_MOVE = (
				a("Messages.actionBar", MESSAGE_ACTIOBAR_MOVE));
		COMMANDMESSAGES_RELOAD =
				a("Messages.reload", COMMANDMESSAGES_RELOAD);
		COOLDOWN =
				a("Messages.cooldown", COOLDOWN);
		RESOURCEPACK_TITLE = a("Messages.Resourcepack.Title", RESOURCEPACK_TITLE);
		RESOURCEPACK_SUBTITLE = a("Messages.Resourcepack.Subtitle", RESOURCEPACK_SUBTITLE);
		RESOURCEPACK_CRASH = a("Messages.Resourcepack.Crash", RESOURCEPACK_CRASH);

		b();
	}

	public static String colorize(String string) {
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
		Main.foliaLib.getScheduler().runNextTick((task) -> {
            if (!Main.useChatForMessage) {
                try {
                    HotbarMessager.sendHotBarMessage(player, MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
                } catch (Error | Exception r5) {
                }
            } else {
                player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
            }
		});
	}

	private static String a(String path, String o) {
		if (messagesyml.contains(path))
			return colorize(messagesyml.getString(path));
		messagesyml.set(path, o);
		forceUpdate = true;
		return colorize(o);
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
