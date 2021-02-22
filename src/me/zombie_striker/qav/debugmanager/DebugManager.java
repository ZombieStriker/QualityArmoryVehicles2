package me.zombie_striker.qav.debugmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DebugManager {

	private static List<Player> playerRecievers = new ArrayList<>();
	private static boolean displayInConsole = true;

	private static final String debugPrefix = ChatColor.GREEN + "[DEBUG]" + ChatColor.WHITE + " ";

	public static void sendDebugMessages(String message) {
		if (displayInConsole)
			Bukkit.getConsoleSender().sendMessage(debugPrefix + message);
		if (playerRecievers.size() > 0) {
			boolean cleanEmpty = false;
			for (Player p : playerRecievers) {
				if (!p.isOnline()) {
					cleanEmpty = true;
					continue;
				}
				p.sendMessage(debugPrefix + message);
			}
			if (cleanEmpty) {
				for (Player player : new ArrayList<>(playerRecievers)) {
					if (!player.isOnline())
						playerRecievers.remove(player);
				}
			}
		}
	}

	public static void addReciever(CommandSender player) {
		if (player instanceof Player)
			playerRecievers.add((Player) player);
	}

	public static void removeReciever(CommandSender player) {
		playerRecievers.remove(player);
	}

	public static void toggleReciever(CommandSender player) {
		if (player instanceof Player) {
			if (playerRecievers.contains(player)) {
				playerRecievers.remove(player);
			} else {
				playerRecievers.add((Player) player);
			}
		}
	}

	public static boolean isDebugging() {
		return displayInConsole || playerRecievers.size() > 0;
	}

	public static List<Player> getRecievers() {
		return playerRecievers;
	}

	public static boolean getIsDisplayedInConsole() {
		return displayInConsole;
	}

	public static void setShouldDisplayInConsole(boolean b) {
		displayInConsole = b;
	}
}
