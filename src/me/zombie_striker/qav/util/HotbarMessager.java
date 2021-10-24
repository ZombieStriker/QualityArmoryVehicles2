package me.zombie_striker.qav.util;

import com.cryptomorin.xseries.messages.ActionBar;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HotbarMessager {

	/**
	 * Sends the hotbar message 'message' to the player 'player'
	 *
	 * @param player
	 * @param message
	 */
	public static void sendHotBarMessage(Player player, String message) {
		ActionBar.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', message));
	}

}