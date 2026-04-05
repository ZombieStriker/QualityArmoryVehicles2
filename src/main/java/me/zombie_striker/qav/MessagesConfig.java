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

public class MessagesConfig {

	public static String MENU_OVERVIEW_TITLE = "&6%cartype%:&f Overview";
	public static String MENU_ADD_ALLOWED_TITLE = "&6%cartype%:&f Add To Whitelist";
	public static String MENU_REMOVE_ALLOWED_TITLE = "&6%cartype%:&f Remove From Whitelist";
	public static String MENU_PASSAGER_SEATS_TITLE = "&6%cartype%:&f Empty Seats";
	public static String MENU_FUELTANK_TITLE = "&6%cartype%:&f Check Fuel";
	public static String MENU_SHOP_TITLE = "&6Vehicle shop";
	public static String MENU_GARAGE_TITLE = "&6Your vehicles";
	public static String MENU_OTHER_GARAGE_TITLE = "&6%s's vehicles";
	public static String MENU_TRAM_TRACKS_TITLE = "&8Tram Tracks";
	public static String MENU_TRAM_EDIT_TITLE = "&8Edit Track";
	public static String MENU_TRAM_STOPS_TITLE = "&8Track Stops";
	public static String MENU_TRAM_TRAINS_TITLE = "&8Track Trains";
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
	public static String MESSAGE_TRAM_SELECT_STOP = " &7Left-click a rail block to add the next station. Type &f/qav tracks cancel&7 to exit.";
	public static String MESSAGE_TRAM_STOP_PROMPT = " &7Enter dwell time (seconds):";
	public static String MESSAGE_TRAM_STOP_ADDED = " &aAdded stop &f%name%&a (%seconds%s).";
	public static String MESSAGE_TRAM_TRACK_NOT_FOUND = "&c Track not found: &f%track%";
	public static String MESSAGE_TRAM_TRACK_NOT_FOUND_GENERIC = "&c Track not found.";
	public static String MESSAGE_TRAM_SAVE_FAILED = "&c Failed to save tracks.yml. Check console.";
	public static String MESSAGE_TRAM_DWELL_RANGE = "&c Please enter a number between 0 and 3600.";
	public static String MESSAGE_TRAM_DWELL_INVALID = "&c Please enter a valid integer.";
	public static String MESSAGE_TRAM_PLAYERS_ONLY = "&c Players only.";
	public static String MESSAGE_TRAM_DEFAULT_STOP_NAME = "Station %index%";
	public static String MESSAGE_TRAM_STOP_NAME_PROMPT = " &7Enter station name (or leave blank for default):";
	public static String MESSAGE_TRAM_TITLE_CURRENT_STOP = "&eCurrent stop: &f%current%";
	public static String MESSAGE_TRAM_TITLE_NEXT_STOP = "&7Next stop: &f%next%";
	public static String MESSAGE_TRAM_TITLE_LAST_STOP = "&7Last stop";
	public static String TRAM_CANCEL_KEYWORD = "cancel";
	public static String COMMAND_TRAM_DESCRIPTION = " : Manage train tracks";
	public static String COMMAND_TRAM_CONSOLE_HINT = "&7 Use &f/qav tracks list&7 or &f/qav tracks create <id>";
	public static String COMMAND_TRAM_UNKNOWN_SUBCOMMAND = "&c Unknown subcommand. Try: /qav tracks menu|list|create|delete|edit|assigntrain|start|stop|cancel";
	public static String COMMAND_TRAM_USAGE_CREATE = "&c Usage: /qav tracks create <id>";
	public static String COMMAND_TRAM_TRACK_ALREADY_EXISTS =  "&c A track with that id already exists.";
	public static String COMMAND_TRAM_TRACK_CREATED = "&a Created track &f%track%&a in world &f%world%";
	public static String COMMAND_TRAM_USAGE_DELETE = "&c Usage: /qav tracks delete <id>";
	public static String COMMAND_TRAM_NO_TRACK_WITH_ID = "&c No track exists with id &f%track%";
	public static String COMMAND_TRAM_TRACK_DELETED = "&e Deleted track &f%track%";
	public static String COMMAND_TRAM_TRACKS_LIST = "&7 Tracks: &f%tracks%";
	public static String COMMAND_TRAM_TRACKS_LIST_EMPTY = "(none)";
	public static String COMMAND_TRAM_USAGE_SET_RUNNING = "&c Usage: /qav tracks %action% <id>";
	public static String COMMAND_TRAM_TRACK_STARTED = "&a Started track &f%track%";
	public static String COMMAND_TRAM_TRACK_STOPPED = "&e Stopped track &f%track%";
	public static String COMMAND_TRAM_USAGE_EDIT = "&c Usage: /qav tracks edit <id>";
	public static String COMMAND_TRAM_USAGE_ASSIGNTRAIN = "&c Usage: /qav tracks assigntrain <id> <type> [spawnDelaySeconds]";
	public static String COMMAND_TRAM_ASSIGNTRAIN_INVALID_DELAY = "&c Spawn delay must be an integer from 0 to 86400 (seconds).";
	public static String COMMAND_TRAM_NO_VEHICLE_FOUND = "&c The specified vehicle does not exist";
	public static String COMMAND_TRAM_VEHICLE_NOT_TRAIN = "&c That vehicle is not a train.";
	public static String MESSAGE_TRAM_ASSIGN_PREVIEW_HINT = " &7Preview at the first stop: &fClick&7 to rotate. Run the &fsame assigntrain command again&7 to save, or &f/qav tracks cancel&7 to discard.";
	public static String MESSAGE_TRAM_ASSIGN_CONFIRMED = " &aSaved &f%train%&a on &f%track%&a (delay &f%delay%s&a, facing &f%dir%&a).";
	public static String MESSAGE_TRAM_ASSIGN_NEED_STOPS = "&c Add at least one stop to this track before assigning a train.";
	public static String MESSAGE_TRAM_ASSIGN_SPAWN_FAILED = "&c Could not spawn the preview train (blocked or invalid location).";
	public static String MESSAGE_TRAM_ASSIGN_ROTATED = " &7Facing: &f%dir%&7 — run &fassigntrain&7 again with the same arguments to save.";
	public static String MESSAGE_TRAM_DIR_SOUTH = "South";
	public static String MESSAGE_TRAM_DIR_EAST = "East";
	public static String MESSAGE_TRAM_DIR_NORTH = "North";
	public static String MESSAGE_TRAM_DIR_WEST = "West";
	public static String COMMAND_TRAM_CANCEL = "&7 Stopped track creation.";
	public static String MENU_TRAM_LABEL_STOPS = "&bStops";
	public static String MENU_TRAM_LORE_STOPS_DESCRIPTION = "&7Manage stations along the route (visit order).";
	public static String MENU_TRAM_LABEL_TRAINS = "&bTrains";
	public static String MENU_TRAM_LORE_TRAINS_DESCRIPTION = "&7Assign multiple trains to this track.";
	public static String MENU_TRAM_LABEL_LOOPING = "&bLooping";
	public static String MENU_TRAM_LORE_LOOPING_DESCRIPTION = "&7Toggle whether this track loops.";
	public static String MENU_TRAM_LORE_CURRENT = "&7Current: &f%value%";
	public static String MENU_TRAM_BACK = "&cBack";
	public static String MENU_TRAM_LOOP_ENABLED = "enabled";
	public static String MENU_TRAM_LOOP_DISABLED = "disabled";
	public static String MESSAGE_TRAM_LOOP_STATUS = "&7Looping is now &f%state%";
	public static String MENU_TRAM_STOP_ID = "&7Stop ID: &f%id%";
	public static String MENU_TRAM_STOP_ORDER = "&7Order: &f%order%";
	public static String MENU_TRAM_STOP_BLOCK = "&7Block: &f%x%, %y%, %z%";
	public static String MENU_TRAM_STOP_DWELL = "&7Dwell: &f%seconds%s";
	public static String MENU_TRAM_REMOVE_HINT = "&eRight-click to remove";
	public static String MENU_TRAM_TRAIN_STATION = "&7Station: &f%station%";
	public static String MENU_TRAM_TRAIN_STATION_UNKNOWN = "&8—";
	public static String MENU_TRAM_ADD_STOP = "&aAdd stop";
	public static String MENU_TRAM_ADD_STOP_LORE = "&7Click to start station selection mode.";
	public static String MENU_TRAM_ADD_TRAIN = "&aAdd train";
	public static String MENU_TRAM_ADD_TRAIN_LORE = "&7Use: &f/qav tracks assigntrain <id> <type> [delaySeconds]&7 — preview, right-click to face, same command to confirm.";
	public static String MENU_TRAM_TRAIN_SPAWN_DELAY = "&7Spawn delay: &f%delay%s";
	public static String MENU_TRAM_TRAIN_FACING = "&7Facing: &f%dir%";
	public static String MENU_TRAM_ADD_TRAIN_CHAT = " &7Use &f/qav tracks assigntrain %track%";
	public static String MENU_TRAM_TRACK_ID = "&7ID: &f%id%";
	public static String MENU_TRAM_TRACK_WORLD = "&7World: &f%world%";
	public static String MENU_TRAM_TRACK_STOPS = "&7Stops: &f%count%";
	public static String MENU_TRAM_TRACK_TRAINS = "&7Trains: &f%count%";
	public static String MENU_TRAM_EDIT_HINT = "&eLeft-click to edit";
	public static String MENU_TRAM_CREATE_TRACK = "&aCreate Track";
	public static String MENU_TRAM_CREATE_TRACK_LORE = "&7Use: &f/qav tracks create <id>";
	public static String MENU_TRAM_CREATE_TRACK_CHAT = " &7Create with &f/qav tracks create <id>";
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

		MENU_TRAM_TRACKS_TITLE = a("Menu.Tram.TracksTitle", MENU_TRAM_TRACKS_TITLE);
		MENU_TRAM_EDIT_TITLE = a("Menu.Tram.EditTitle", MENU_TRAM_EDIT_TITLE);
		MENU_TRAM_STOPS_TITLE = a("Menu.Tram.StopsTitle", MENU_TRAM_STOPS_TITLE);
		MENU_TRAM_TRAINS_TITLE = a("Menu.Tram.TrainsTitle", MENU_TRAM_TRAINS_TITLE);

		MESSAGE_TRAM_SELECT_STOP = a("Tram.SelectStop", MESSAGE_TRAM_SELECT_STOP);
		MESSAGE_TRAM_STOP_PROMPT = a("Tram.StopPrompt", MESSAGE_TRAM_STOP_PROMPT);
		MESSAGE_TRAM_STOP_ADDED = a("Tram.StopAdded", MESSAGE_TRAM_STOP_ADDED);
		MESSAGE_TRAM_TRACK_NOT_FOUND = a("Tram.TrackNotFound", MESSAGE_TRAM_TRACK_NOT_FOUND);
		MESSAGE_TRAM_TRACK_NOT_FOUND_GENERIC = a("Tram.TrackNotFoundGeneric", MESSAGE_TRAM_TRACK_NOT_FOUND_GENERIC);
		MESSAGE_TRAM_SAVE_FAILED = a("Tram.SaveFailed", MESSAGE_TRAM_SAVE_FAILED);
		MESSAGE_TRAM_DWELL_RANGE = a("Tram.DwellRange", MESSAGE_TRAM_DWELL_RANGE);
		MESSAGE_TRAM_DWELL_INVALID = a("Tram.DwellInvalid", MESSAGE_TRAM_DWELL_INVALID);
		MESSAGE_TRAM_PLAYERS_ONLY = a("Tram.PlayersOnly", MESSAGE_TRAM_PLAYERS_ONLY);
		MESSAGE_TRAM_DEFAULT_STOP_NAME = a("Tram.DefaultStopName", MESSAGE_TRAM_DEFAULT_STOP_NAME);
		MESSAGE_TRAM_STOP_NAME_PROMPT = a("Tram.StopNamePrompt", MESSAGE_TRAM_STOP_NAME_PROMPT);
		MESSAGE_TRAM_TITLE_CURRENT_STOP = a("Tram.Runtime.TitleCurrentStop", MESSAGE_TRAM_TITLE_CURRENT_STOP);
		MESSAGE_TRAM_TITLE_NEXT_STOP = a("Tram.Runtime.TitleNextStop", MESSAGE_TRAM_TITLE_NEXT_STOP);
		MESSAGE_TRAM_TITLE_LAST_STOP = a("Tram.Runtime.TitleLastStop", MESSAGE_TRAM_TITLE_LAST_STOP);
		TRAM_CANCEL_KEYWORD = a("Tram.CancelKeyword", TRAM_CANCEL_KEYWORD);
		COMMAND_TRAM_DESCRIPTION = a("Tram.Commands.Description", COMMAND_TRAM_DESCRIPTION);
		COMMAND_TRAM_CONSOLE_HINT = a("Tram.Commands.ConsoleHint", COMMAND_TRAM_CONSOLE_HINT);
		COMMAND_TRAM_UNKNOWN_SUBCOMMAND = a("Tram.Commands.UnknownSubcommand", COMMAND_TRAM_UNKNOWN_SUBCOMMAND);
		COMMAND_TRAM_USAGE_CREATE = a("Tram.Commands.UsageCreate", COMMAND_TRAM_USAGE_CREATE);
		COMMAND_TRAM_TRACK_ALREADY_EXISTS = a("Tram.Commands.TrackAlreadyExists", COMMAND_TRAM_TRACK_ALREADY_EXISTS);
		COMMAND_TRAM_TRACK_CREATED = a("Tram.Commands.TrackCreated", COMMAND_TRAM_TRACK_CREATED);
		COMMAND_TRAM_USAGE_DELETE = a("Tram.Commands.UsageDelete", COMMAND_TRAM_USAGE_DELETE);
		COMMAND_TRAM_NO_TRACK_WITH_ID = a("Tram.Commands.NoTrackWithId", COMMAND_TRAM_NO_TRACK_WITH_ID);
		COMMAND_TRAM_TRACK_DELETED = a("Tram.Commands.TrackDeleted", COMMAND_TRAM_TRACK_DELETED);
		COMMAND_TRAM_TRACKS_LIST = a("Tram.Commands.TracksList", COMMAND_TRAM_TRACKS_LIST);
		COMMAND_TRAM_TRACKS_LIST_EMPTY = a("Tram.Commands.TracksListEmpty", COMMAND_TRAM_TRACKS_LIST_EMPTY);
		COMMAND_TRAM_USAGE_SET_RUNNING = a("Tram.Commands.UsageSetRunning", COMMAND_TRAM_USAGE_SET_RUNNING);
		COMMAND_TRAM_TRACK_STARTED = a("Tram.Commands.TrackStarted", COMMAND_TRAM_TRACK_STARTED);
		COMMAND_TRAM_TRACK_STOPPED = a("Tram.Commands.TrackStopped", COMMAND_TRAM_TRACK_STOPPED);
		COMMAND_TRAM_USAGE_EDIT = a("Tram.Commands.UsageEdit", COMMAND_TRAM_USAGE_EDIT);
		COMMAND_TRAM_USAGE_ASSIGNTRAIN = a("Tram.Commands.UsageAssignTrain", COMMAND_TRAM_USAGE_ASSIGNTRAIN);
		COMMAND_TRAM_ASSIGNTRAIN_INVALID_DELAY = a("Tram.Commands.AssignTrainInvalidDelay", COMMAND_TRAM_ASSIGNTRAIN_INVALID_DELAY);
		COMMAND_TRAM_NO_VEHICLE_FOUND = a("Tram.Commands.NoVehicleFound", COMMAND_TRAM_NO_VEHICLE_FOUND);
		COMMAND_TRAM_VEHICLE_NOT_TRAIN = a("Tram.Commands.VehicleNotTrain", COMMAND_TRAM_VEHICLE_NOT_TRAIN);
		MESSAGE_TRAM_ASSIGN_PREVIEW_HINT = a("Tram.Assign.PreviewHint", MESSAGE_TRAM_ASSIGN_PREVIEW_HINT);
		MESSAGE_TRAM_ASSIGN_CONFIRMED = a("Tram.Assign.Confirmed", MESSAGE_TRAM_ASSIGN_CONFIRMED);
		MESSAGE_TRAM_ASSIGN_NEED_STOPS = a("Tram.Assign.NeedStops", MESSAGE_TRAM_ASSIGN_NEED_STOPS);
		MESSAGE_TRAM_ASSIGN_SPAWN_FAILED = a("Tram.Assign.SpawnFailed", MESSAGE_TRAM_ASSIGN_SPAWN_FAILED);
		MESSAGE_TRAM_ASSIGN_ROTATED = a("Tram.Assign.Rotated", MESSAGE_TRAM_ASSIGN_ROTATED);
		MESSAGE_TRAM_DIR_SOUTH = a("Tram.Assign.Dir.South", MESSAGE_TRAM_DIR_SOUTH);
		MESSAGE_TRAM_DIR_EAST = a("Tram.Assign.Dir.East", MESSAGE_TRAM_DIR_EAST);
		MESSAGE_TRAM_DIR_NORTH = a("Tram.Assign.Dir.North", MESSAGE_TRAM_DIR_NORTH);
		MESSAGE_TRAM_DIR_WEST = a("Tram.Assign.Dir.West", MESSAGE_TRAM_DIR_WEST);
		COMMAND_TRAM_CANCEL = a("Tram.Commands.Cancel", COMMAND_TRAM_CANCEL);
		MENU_TRAM_LABEL_STOPS = a("Tram.Menu.Edit.Stops.Label", MENU_TRAM_LABEL_STOPS);
		MENU_TRAM_LORE_STOPS_DESCRIPTION = a("Tram.Menu.Edit.Stops.Description", MENU_TRAM_LORE_STOPS_DESCRIPTION);
		MENU_TRAM_LABEL_TRAINS = a("Tram.Menu.Edit.Trains.Label", MENU_TRAM_LABEL_TRAINS);
		MENU_TRAM_LORE_TRAINS_DESCRIPTION = a("Tram.Menu.Edit.Trains.Description", MENU_TRAM_LORE_TRAINS_DESCRIPTION);
		MENU_TRAM_LABEL_LOOPING = a("Tram.Menu.Edit.Looping.Label", MENU_TRAM_LABEL_LOOPING);
		MENU_TRAM_LORE_LOOPING_DESCRIPTION = a("Tram.Menu.Edit.Looping.Description", MENU_TRAM_LORE_LOOPING_DESCRIPTION);
		MENU_TRAM_LORE_CURRENT = a("Tram.Menu.Edit.Current", MENU_TRAM_LORE_CURRENT);
		MENU_TRAM_BACK = a("Tram.Menu.Back", MENU_TRAM_BACK);
		MENU_TRAM_LOOP_ENABLED = a("Tram.Menu.Looping.Enabled", MENU_TRAM_LOOP_ENABLED);
		MENU_TRAM_LOOP_DISABLED = a("Tram.Menu.Looping.Disabled", MENU_TRAM_LOOP_DISABLED);
		MESSAGE_TRAM_LOOP_STATUS = a("Tram.Menu.Looping.Status", MESSAGE_TRAM_LOOP_STATUS);
		MENU_TRAM_STOP_ID = a("Tram.Menu.Stops.StopId", MENU_TRAM_STOP_ID);
		MENU_TRAM_STOP_ORDER = a("Tram.Menu.Stops.Order", MENU_TRAM_STOP_ORDER);
		MENU_TRAM_STOP_BLOCK = a("Tram.Menu.Stops.Block", MENU_TRAM_STOP_BLOCK);
		MENU_TRAM_STOP_DWELL = a("Tram.Menu.Stops.Dwell", MENU_TRAM_STOP_DWELL);
		MENU_TRAM_REMOVE_HINT = a("Tram.Menu.RemoveHint", MENU_TRAM_REMOVE_HINT);
		MENU_TRAM_TRAIN_STATION = a("Tram.Menu.Trains.Station", MENU_TRAM_TRAIN_STATION);
		MENU_TRAM_TRAIN_STATION_UNKNOWN = a("Tram.Menu.Trains.StationUnknown", MENU_TRAM_TRAIN_STATION_UNKNOWN);
		MENU_TRAM_ADD_STOP = a("Tram.Menu.Stops.Add", MENU_TRAM_ADD_STOP);
		MENU_TRAM_ADD_STOP_LORE = a("Tram.Menu.Stops.AddLore", MENU_TRAM_ADD_STOP_LORE);
		MENU_TRAM_ADD_TRAIN = a("Tram.Menu.Trains.Add", MENU_TRAM_ADD_TRAIN);
		MENU_TRAM_ADD_TRAIN_LORE = a("Tram.Menu.Trains.AddLore", MENU_TRAM_ADD_TRAIN_LORE);
		MENU_TRAM_TRAIN_SPAWN_DELAY = a("Tram.Menu.Trains.SpawnDelay", MENU_TRAM_TRAIN_SPAWN_DELAY);
		MENU_TRAM_TRAIN_FACING = a("Tram.Menu.Trains.Facing", MENU_TRAM_TRAIN_FACING);
		MENU_TRAM_ADD_TRAIN_CHAT = a("Tram.Menu.Trains.AddChat", MENU_TRAM_ADD_TRAIN_CHAT);
		MENU_TRAM_TRACK_ID = a("Tram.Menu.Tracks.Id", MENU_TRAM_TRACK_ID);
		MENU_TRAM_TRACK_WORLD = a("Tram.Menu.Tracks.World", MENU_TRAM_TRACK_WORLD);
		MENU_TRAM_TRACK_STOPS = a("Tram.Menu.Tracks.Stops", MENU_TRAM_TRACK_STOPS);
		MENU_TRAM_TRACK_TRAINS = a("Tram.Menu.Tracks.Trains", MENU_TRAM_TRACK_TRAINS);
		MENU_TRAM_EDIT_HINT = a("Tram.Menu.Tracks.EditHint", MENU_TRAM_EDIT_HINT);
		MENU_TRAM_CREATE_TRACK = a("Tram.Menu.Tracks.Create", MENU_TRAM_CREATE_TRACK);
		MENU_TRAM_CREATE_TRACK_LORE = a("Tram.Menu.Tracks.CreateLore", MENU_TRAM_CREATE_TRACK_LORE);
		MENU_TRAM_CREATE_TRACK_CHAT = a("Tram.Menu.Tracks.CreateChat", MENU_TRAM_CREATE_TRACK_CHAT);

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
		new BukkitRunnable() {
			public void run() {
				if (!Main.useChatForMessage) {
					try {
						HotbarMessager.sendHotBarMessage(player, MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
					} catch (Error | Exception r5) {
					}
				} else {
					player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
				}
			}
		}.runTaskLater(QualityArmoryVehicles.getPlugin(), 0);
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
				e.printStackTrace();
			}
		}
	}

}
