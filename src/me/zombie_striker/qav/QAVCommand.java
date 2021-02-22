package me.zombie_striker.qav;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QAVCommand implements CommandExecutor, TabCompleter {

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
	private Main main;

	public QAVCommand(Main main) {
	this.main = main;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if(args.length==0){
			sendHelper(sender);
			return true;
		}
		if(args[0].equalsIgnoreCase(subcommand_GiveVehicle)) {
			if (!sender.hasPermission(PermissionHandler.PERM_GIVE)) {
				sender.sendMessage(Main.prefix+ MessagesConfig.COMMANDMESSAGES_NO_PERM);
				return true;
			}
			if(args.length < 2){
				sender.sendMessage("Usage /qav "+subcommand_GiveVehicle+" <vehicle>");
				return true;
			}
			String name = args[1];
			AbstractVehicle ve = QualityArmoryVehicles.getVehicle(name);
			if(ve==null){
				sender.sendMessage("Vehicle does not exist.");
				return true;
			}
			Player reciever = null;
			if(sender instanceof Player){
				reciever = (Player) sender;
			}
			if(args.length > 2){
				reciever = Bukkit.getPlayer(args[2]);
			}
			if(reciever==null){
				sender.sendMessage("Player \""+args[2]+"\"is not on the server.");
				return true;
			}
			reciever.getInventory().addItem(ItemFact.getCarItem(ve));
			sender.sendMessage(Main.prefix+ " Gave "+ChatColor.GOLD+reciever.getName()+" "+ve.getName()+".");
			return true;
		}
		if(args[0].equalsIgnoreCase(subcommand_removeNearbyVehicles)){
			int radius = 6;
			if(args.length > 1){
				radius = Integer.parseInt(args[1]);
			}
			if(sender instanceof Player){
				Player player = (Player) sender;
				for(VehicleEntity ve : new ArrayList<>(Main.vehicles)){
					if(ve.getDriverSeat().getLocation().distanceSquared(player.getLocation()) < radius*radius){
						ve.deconstruct(null,"removeNearbyCommand");
					}
				}
			}
		}


		if(args[0].equalsIgnoreCase("getresourcepack")){

		}

		if(args[0].equalsIgnoreCase(subcommand_Shop)){

		}
		return false;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String commandname, @NotNull String[] args) {

		if (args.length == 1) {
			ArrayList<String> a = new ArrayList<String>();
			a(a, subcommand_SpawnVehicle, args[0]);
			a(a, subcommand_GiveVehicle, args[0]);
			a(a, subcommand_setAsPass, args[0]);
			a(a, subcommand_Shop, args[0]);
			a(a, subcommand_removeNearbyVehicles, args[0]);
			a(a, subcommand_callback, args[0]);
			a(a, subcommand_callbackAll, args[0]);
			if (main.enableGarage)
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
			return a;
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase(subcommand_SpawnVehicle) || args[0].equalsIgnoreCase(subcommand_GiveVehicle)) {
				ArrayList<String> a = new ArrayList<String>();
				for (AbstractVehicle s : Main.vehicleTypes) {
					a(a, s.getName(), args[1]);
				}
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
