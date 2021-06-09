package me.zombie_striker.qav.perms;

import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.entity.Player;

public class PermissionHandler {

	public static final String PERM_UNLIM = "qualityarmoryvehicles.unlimitedvehicles";
	public static final String PERM_OWNEDAMOUNT = "qualityarmoryvehicles.vehiclelimit.";
	
	public static final String PERM_DRIVABLE = "qualityarmoryvehicles.candrive.";
	
	public static final String PERM_CALLBACK_ALL = "qualityarmoryvehicles.callbackAll";
	public static final String PERM_CALLBACK = "qualityarmoryvehicles.callback";
	
	public static final String PERM_OVERRIDE_WHITELIST_COMMAND = "qualityarmoryvehicles.overrideWhitelistCommand";
	public static final String PERM_OVERRIDE_WHITELIST = "qualityarmoryvehicles.overrideWhitelist";
	
	public static final String PERM_OPEN_VEHICLE_GUI = "qualityarmoryvehicles.usevehiclegui";

	public static final String PERM_ADD_VEHICLE= "qualityarmoryvehicles.addvehicle";
	public static final String PERM_REMOVE_VEHICLE = "qualityarmoryvehicles.removevehicle";

	public static final String PERM_REMOVEDBUGGED = "qualityarmoryvehicles.removebugged";

	public static final String PERM_PICKUP = "qualityarmoryvehicles.pickupvehicle";

	public static final String PERM_GIVE = "qualityarmoryvehicles.give";

	public static final String PERM_DEBUG = "qualityarmoryvehicles.debug";

	

	public static boolean hasUnlimitedVehicles(Player player) {
		return player.hasPermission(PERM_UNLIM);
	}
	public static boolean canDrive(Player player, AbstractVehicle av) {
		return player.hasPermission(PERM_DRIVABLE+av.getName());
	}

	public static int getMaxOwnVehicles(Player player) {
		if (hasUnlimitedVehicles(player))
			return 999999;
		int amount = 0;
		for (int i = 100; i > 0; i--) {
			if (player.hasPermission(PERM_OWNEDAMOUNT + i)) {
				amount = i;
				break;
			}
		}
		return amount;
	}
}
