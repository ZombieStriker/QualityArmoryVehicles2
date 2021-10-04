package me.zombie_striker.qav;

import me.zombie_striker.qav.customitemmanager.CustomItemManager;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.debugmanager.DebugManager;
import me.zombie_striker.qav.menu.MenuHandler;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.premium.PremiumHandler;
import me.zombie_striker.qav.util.ForksUtil;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QAVCommand implements CommandExecutor, TabCompleter {
	public static String subcommand_reload = "reload";
	public static String subcommand_SpawnVehicle = "spawnVehicle";
	public static String subcommand_GiveVehicle = "give";
	public static String subcommand_setAsPass = "setAsPassager";
	public static String subcommand_Shop = "shop";
	public static String subcommand_removeNearbyVehicles = "removeNearbyVehicles";
	public static String subcommand_callback = "callback";
	public static String subcommand_callbackAll = "callbackAll";
	public static String subcommand_garage = "garage";
	public static String subcommand_removeFromWhitelist = "removeFromWhitelist";
	public static String subcommand_addToWhitelist = "addToWhitelist";
	public static String subcommand_registerfuel = "registerFuel";
	public static String subcommand_debug = "debug";
	public static String subcommand_debugpl2 = "debug2";
	public static String subcommand_removevehicle = "removeVehicle";
	public static String subcommand_sittingInWhatVehicle = "sittingInWhatVehicle";
	public static String subcommand_addvehicle = "addVehicle";
	public static String subcommand_removebugged = "removeBugged";
	private final Main main;

	public QAVCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if (args.length == 0) {
			sendHelper(sender);
			return true;
		}
		if (args[0].equalsIgnoreCase(subcommand_GiveVehicle)) {
			if (!sender.hasPermission(PermissionHandler.PERM_GIVE)) {
				sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage("Usage /qav " + subcommand_GiveVehicle + " <vehicle>");
				return true;
			}
			String name = args[1];

			AbstractVehicle ve = QualityArmoryVehicles.getVehicle(name);
			if (ve == null && !name.equalsIgnoreCase("repair")) {
				sender.sendMessage("Vehicle does not exist.");
				return true;
			}
			Player reciever = null;
			if (sender instanceof Player) {
				reciever = (Player) sender;
			}
			if (args.length > 2) {
				reciever = Bukkit.getPlayer(args[2]);
			}
			if (reciever == null) {
				sender.sendMessage("Player \"" + args[2] + "\"is not on the server.");
				return true;
			}

			if (name.equalsIgnoreCase("repair")) {
				reciever.getInventory().addItem(Main.repairItem.asItem());
				return true;
			}

			if (Main.enableGarage) {
				QualityArmoryVehicles.addUnlockedVehicle(reciever,new UnlockedVehicle(ve,ve.getMaxHealth(), true));
			} else {
				reciever.getInventory().addItem(ItemFact.getCarItem(ve));
			}
			sender.sendMessage(Main.prefix + " Gave " + ChatColor.GOLD + reciever.getName() + " " + ve.getName() + ".");
			return true;
		}
		if (args[0].equalsIgnoreCase("list")) {
			StringBuilder builder = new StringBuilder();
			for (AbstractVehicle vehicleType : Main.vehicleTypes) {
				builder.append(vehicleType.getName()).append(", ");
			}

			sender.sendMessage(Main.prefix + " Loaded vehicles: " + builder.substring(0, builder.length()-2));
			return true;
		}
		if (args[0].equalsIgnoreCase(subcommand_removeNearbyVehicles)) {
			int radius = 6;
			if (args.length > 1) {
				radius = Integer.parseInt(args[1]);
			}
			if (sender instanceof Player) {
				Player player = (Player) sender;
				for (VehicleEntity ve : new ArrayList<>(Main.vehicles)) {
					if (ve.getDriverSeat().getLocation().distanceSquared(player.getLocation()) < radius * radius) {
						ve.deconstruct(null, "removeNearbyCommand");
					}
				}
			}
		}

		if (args[0].equalsIgnoreCase(subcommand_SpawnVehicle)) {
			if (!sender.hasPermission(PermissionHandler.PERM_SPAWN)) {
				sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage("Usage /qav " + subcommand_SpawnVehicle + " <vehicle> [x] [y] [z]");
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs));
				return true;
			}

			Player target = (Player) sender;

			String name = args[1];
			Location location = target.getLocation();

			if (args.length == 5) {
				try {
					location = new Location(target.getWorld(), Double.parseDouble(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4]));
				} catch (NumberFormatException ignored) {}
			}

			AbstractVehicle ab = QualityArmoryVehicles.getVehicle(name);
			if (ab == null) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.COMMANDMESSAGES_VALID_VEHICLE));
				return true;
			}

			VehicleEntity ve = new VehicleEntity(ab, location.getBlock().getRelative(BlockFace.UP).getLocation(), target.getUniqueId());
			ve.spawn();
			return true;
		}


		if (args[0].equalsIgnoreCase("getresourcepack")) {
			ForksUtil.sendComponent(sender,Main.prefix + " " + MessagesConfig.COMMANDMESSAGES_TEXTURE, null, CustomItemManager.getResourcepack() == null ? "" : CustomItemManager.getResourcepack());
		    return true;
		}

		if (args[0].equalsIgnoreCase(subcommand_removevehicle)) {
			if (!sender.hasPermission(PermissionHandler.PERM_REMOVE_VEHICLE)) {
				sender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_PERM);
				return true;
			}

			if (args.length != 2) {
				sender.sendMessage(Main.prefix + "&7 Try to use &6/qav removeVehicle <type>");
				return true;
			}

			final List<VehicleEntity> collect = Main.vehicles.stream()
					.filter(ve -> ve.getType().getName().equalsIgnoreCase(args[1]))
					.collect(Collectors.toList());

			Main.vehicles.removeIf(ve -> ve.getType().getName().equalsIgnoreCase(args[1]));

			for (VehicleEntity entity : collect) {
				entity.deconstruct(null,"Remove command");
			}
		}

		if (args[0].equalsIgnoreCase("license")) {
			PremiumHandler.sendMessage(sender);
			return true;
		}

		if (args[0].equalsIgnoreCase(subcommand_debug)) {
			if (!sender.hasPermission(PermissionHandler.PERM_DEBUG)) {
				sender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_PERM);
				return true;
			}

			String toggle = DebugManager.toggleReciever(sender) ? Main.prefix + " Debugging enabled" : Main.prefix + " Debugging disabled";
			sender.sendMessage(toggle);
			return true;
		}

		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("qualityarmoryvehicles.reload")) {
				sender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_PERM);
				return true;
			}
			long time = System.currentTimeMillis();
			main.initVals();
			Main.loadVehicles(true);
			sender.sendMessage(Main.prefix + " Reloaded config and vehicle files in " + (System.currentTimeMillis() - time) + "ms");
			return true;
		}

		if (args[0].equalsIgnoreCase(subcommand_Shop)) {
			MenuHandler.openShop((Player) sender, AbstractVehicle.class);
			return true;
		}
		if (args[0].equalsIgnoreCase(subcommand_garage)) {
			MenuHandler.openGarage((Player) sender);
		}
		if (args[0].equalsIgnoreCase(subcommand_callbackAll)) {
			if (!sender.hasPermission(PermissionHandler.PERM_CALLBACK_ALL)) {
				sender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_PERM);
				return true;
			}

			for (VehicleEntity ve : new ArrayList<>(Main.vehicles)) {
				if (Bukkit.getPlayer(ve.getOwner()) != null)
					this.callback(ve, Bukkit.getPlayer(ve.getOwner()));
			}
			sender.sendMessage("Called back all vehicles on the server.");
			return true;
		}
		if (args[0].equalsIgnoreCase(subcommand_callback)) {
			if (!sender.hasPermission(PermissionHandler.PERM_CALLBACK)) {
				sender.sendMessage(MessagesConfig.COMMANDMESSAGES_NO_PERM);
				return true;
			}

			Location loc = null;
			if (sender instanceof Player) {
				loc = ((Player) sender).getLocation();
			} else {
				sender.sendMessage("Only players can use this command");
				return true;
			}
			int radius = 6;
			if (args.length >= 2) {
				radius = Integer.parseInt(args[1]);
			}
			for (VehicleEntity ve : new ArrayList<>(Main.vehicles)) {
				if (ve.getDriverSeat().getLocation().distanceSquared(loc) < radius * radius)
					callback(ve, (Player) sender);
			}
			sender.sendMessage("Called back all vehicles within a "+radius+" radius of the player.");
			return true;
		}
		return false;
	}

	public static void callback(VehicleEntity ve, Player player) {
		callback(ve,player,"Callback");
	}

	public static void callback(VehicleEntity ve, Player player, String message) {
		for (ItemStack item : ve.getTrunk()) {
			if (item != null)
				MenuHandler.giveOrDrop(player,item);
		}

		for (ItemStack item : ve.getFuels().getContents()) {
			if (item != null)
				MenuHandler.giveOrDrop(player,item);
		}

		ve.getFuels().clear();
		ve.getTrunk().clear();

		ve.deconstruct(player, message);
		if(!Main.enableGarage)
			MenuHandler.giveOrDrop(player,ItemFact.getCarItem(ve.getType()));
		else
			QualityArmoryVehicles.addUnlockedVehicle(player,new UnlockedVehicle(ve.getType(),ve.getHealth(), true));
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String commandname, @NotNull String[] args) {

		if (args.length == 1) {
			ArrayList<String> a = new ArrayList<String>();
			a(a, subcommand_reload, args[0]);
			a(a, subcommand_SpawnVehicle, args[0]);
			a(a, subcommand_GiveVehicle, args[0]);
			a(a, subcommand_setAsPass, args[0]);
			a(a, subcommand_Shop, args[0]);
			a(a, subcommand_removeNearbyVehicles, args[0]);
			a(a, subcommand_callback, args[0]);
			a(a, subcommand_callbackAll, args[0]);
			if (Main.enableGarage)
				a(a, subcommand_garage, args[0]);
			a(a, subcommand_removeFromWhitelist, args[0]);
			a(a, subcommand_addToWhitelist, args[0]);
			a(a, "getresourcepack", args[0]);
			a(a, subcommand_registerfuel, args[0]);
			a(a, subcommand_debug, args[0]);
			a(a, subcommand_debugpl2, args[0]);
			a(a, subcommand_sittingInWhatVehicle, args[0]);
			a(a, subcommand_addvehicle, args[0]);
			a(a, subcommand_removevehicle, args[0]);
			a(a, subcommand_removebugged, args[0]);
			a(a, "list", args[0]);
			a(a, "license", args[0]);
			return a;
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase(subcommand_SpawnVehicle) || args[0].equalsIgnoreCase(subcommand_GiveVehicle)) {
				ArrayList<String> a = new ArrayList<String>();
				for (AbstractVehicle s : Main.vehicleTypes) {
					a(a, s.getName(), args[1]);
				}
				a(a, "repair", args[1]);
				return a;
			} else if (args[0].equalsIgnoreCase(subcommand_addvehicle) || args[0].equalsIgnoreCase(subcommand_removevehicle)) {
				ArrayList<String> a = new ArrayList<String>();
				for (AbstractVehicle s : Main.vehicleTypes) {
					a(a, s.getName(), args[1]);
				}
				return a;
			}
		}
		if (args.length == 3 || args.length == 4 || args.length == 5) {
			if (args[0].equalsIgnoreCase(subcommand_SpawnVehicle)) {
				ArrayList<String> a = new ArrayList<String>();
				a(a, "~", args[1]);
				return a;
			}
		}
		return null;
	}

	public void a(List<String> a, String b, String arg) {
		if (b.toLowerCase().startsWith(arg.toLowerCase()))
			a.add(b);
	}

	public Object a(String path, Object o) {
		if (main.getConfig().contains(path))
			return main.getConfig().get(path);
		main.getConfig().set(path, o);
		main.saveConfig();
		return o;
	}


	public void sendHelper(CommandSender sender) {
		sender.sendMessage(Main.prefix + " Commands");
		sender.sendMessage("/QAV " + subcommand_GiveVehicle + ChatColor.GRAY + MessagesConfig.subcommand_GiveVehicle);
		sender.sendMessage("/QAV " + subcommand_SpawnVehicle + ChatColor.GRAY + MessagesConfig.subcommand_SpawnVehicle);
		sender.sendMessage("/QAV " + subcommand_removeNearbyVehicles + ChatColor.GRAY + MessagesConfig.subcommand_removeNearbyVehicles);
		sender.sendMessage("/QAV " + subcommand_setAsPass + ChatColor.GRAY + MessagesConfig.subcommand_setAsPass);

		sender.sendMessage("/QAV " + subcommand_Shop + ChatColor.GRAY + MessagesConfig.subcommand_Shop);
		sender.sendMessage("/QAV " + "list" + ChatColor.GRAY + " : Get loaded vehicles list");
		if (Main.enableGarage)
			sender.sendMessage("/QAV " + subcommand_garage + ChatColor.GRAY + MessagesConfig.subcommand_garage);
		sender.sendMessage(
				"/QAV " + subcommand_callback + ChatColor.GRAY + MessagesConfig.subcommand_callback);
		sender.sendMessage("/QAV " + subcommand_callbackAll + ChatColor.GRAY + MessagesConfig.subcommand_callbackAll);
		sender.sendMessage(
				"/QAV " + subcommand_addToWhitelist + ChatColor.GRAY + MessagesConfig.subcommand_addToWhitelist);
		sender.sendMessage(
				"/QAV " + subcommand_removeFromWhitelist + ChatColor.GRAY + MessagesConfig.subcommand_removeFromWhitelist);
		sender.sendMessage("/QAV " + subcommand_registerfuel
				+ ChatColor.GRAY + MessagesConfig.subcommand_registerfuel);
		if (sender.hasPermission("qualityarmoryvehicles.debug")) {
			sender.sendMessage("/QAV " + subcommand_debug + ChatColor.GRAY + MessagesConfig.subcommand_debug);
		}

	}
}
