package me.zombie_striker.qav.finput;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Bukkit;
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
		handlers.put(c.getName(), c);
	}

	public static FInput getHandler(String name) {
		return handlers.get(name.toUpperCase());
	}

	@EventHandler
	public void onF(PlayerSwapHandItemsEvent event) {
		if (event.getPlayer().getVehicle() != null && QualityArmoryVehicles.isVehicle(event.getPlayer().getVehicle())) {
			VehicleEntity vehicle = QualityArmoryVehicles.getVehicleEntityByEntity(event.getPlayer().getVehicle());
			if (vehicle == null) return;

			if (event.getPlayer().getVehicle().equals(vehicle.getDriverSeat())) {
				FInput input = vehicle.getType().getInput(FInput.ClickType.F);
				if (input == null) return;

				event.setCancelled(true);
				Main.DEBUG("Calling " + input + " Input for " + vehicle.getType().getName());
				input.onInput(vehicle);
			}
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (event.getPlayer().getVehicle() != null && QualityArmoryVehicles.isVehicle(event.getPlayer().getVehicle())) {
			VehicleEntity vehicle = QualityArmoryVehicles.getVehicleEntityByEntity(event.getPlayer().getVehicle());
			if (vehicle == null) return;

			if (event.getPlayer().getVehicle().equals(vehicle.getDriverSeat())) {
				FInput input = null;

				if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
					input = vehicle.getType().getInput(FInput.ClickType.LEFT);
				} else if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					input = vehicle.getType().getInput(FInput.ClickType.RIGHT);
				}

				if (input == null) return;

				event.setCancelled(true);
				Main.DEBUG("Calling " + input + " Input for " + vehicle.getType().getName());
				input.onInput(vehicle);
			}
		}
	}
}
