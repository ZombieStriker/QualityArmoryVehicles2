package me.zombie_striker.qav.finput;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.attachments.Attachment;
import me.zombie_striker.qav.input.VehicleInput;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashMap;

public class FInputManager implements Listener {
	public static String CAR_HONK = "HONK";
	public static String POLICE_SIREN = ("SIREN");
	public static String MININUKE_BOMBER = ("MININUKE_BOMBER");
	public static String TNTBOMBER = ("TNT_BOMBER");
	public static String LAUNCHER_40mm = ("40MM_LAUNCHER");
	public static String LAUNCHER_556 = ("BULLETS_556");

	public static void init(Main plugin) {
		Bukkit.getPluginManager().registerEvents(new FInputManager(),plugin);
	}

	public static HashMap<String, FInput> handlers = new HashMap<>();

	public static void add(FInput c) {
		handlers.put(c.getName().toUpperCase(), c);
	}

	public static FInput getHandler(String name) {
		return handlers.get(name.toUpperCase());
	}

	@EventHandler
	public void onF(PlayerSwapHandItemsEvent event) {
		if (handle(event.getPlayer(), VehicleInput.F, FInput.ClickType.F))
			event.setCancelled(true);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		boolean handled = false;
		if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			handled = handle(event.getPlayer(), VehicleInput.LMB, FInput.ClickType.LEFT);
		} else if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			handled = handle(event.getPlayer(), VehicleInput.RMB, FInput.ClickType.RIGHT);
		}

		if (handled)
			event.setCancelled(true);
	}

	/**
	 * Sends a click to the attachments controlled by the seat of the player, then to the vehicle if the player is the driver.
	 *
	 * @return true if the click was used
	 */
	private boolean handle(Player player, VehicleInput vehicleInput, FInput.ClickType clickType) {
		if (player.getVehicle() == null || !QualityArmoryVehicles.isVehicle(player.getVehicle())) return false;

		VehicleEntity vehicle = QualityArmoryVehicles.getVehicleEntityByEntity(player.getVehicle());
		if (vehicle == null) return false;

		Integer seat = vehicle.getSeatIndex(player.getVehicle());
		if (seat == null) return false;

		if (vehicle.handleAttachmentInput(player, seat, vehicleInput)) return true;
		if (seat != Attachment.DRIVER_SEAT) return false;

		FInput input = vehicle.getType().getInput(clickType);
		if (input == null) return false;

		Main.DEBUG("Calling " + input + " Input for " + vehicle.getType().getName());
		input.onInput(vehicle);
		return true;
	}
}
