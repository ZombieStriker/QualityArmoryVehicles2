package me.zombie_striker.qav.vehicles;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.events.VehicleChangeSpeedEvent;
import me.zombie_striker.qav.hitch.HitchManager;
import me.zombie_striker.qav.util.HotbarMessager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A vehicle without an engine that has to be pulled by one or more hitched animals (horses, donkeys, mules...).
 * Its speed and jump height are derived from the stats of the hitched animals.
 */
public class AbstractCarriage extends AbstractCar {

	private final List<Vector> hitchPoints = new ArrayList<>(Collections.singletonList(new Vector(0, 0, 3)));
	private final Set<String> allowedAnimals = new HashSet<>(Arrays.asList("HORSE", "DONKEY", "MULE"));
	private double hitchRadius = 6;
	private double speedMultiplier = 2.0;
	private double jumpMultiplier = 0.5;

	private final Map<UUID, Long> lastNoAnimalMessage = new HashMap<>();

	public AbstractCarriage(String name, int id) {
		super(name, id);
	}

	public void loadHitchSettings(@NotNull ConfigurationSection c) {
		if (c.contains("hitch.points")) {
			List<?> list = c.getList("hitch.points", new ArrayList<>());
			List<Vector> points = new ArrayList<>();
			for (Object o : list) {
				if (o instanceof Vector) points.add((Vector) o);
			}
			if (!points.isEmpty()) {
				hitchPoints.clear();
				hitchPoints.addAll(points);
			}
		}
		if (c.contains("hitch.radius"))
			hitchRadius = c.getDouble("hitch.radius");
		if (c.contains("hitch.speedMultiplier"))
			speedMultiplier = c.getDouble("hitch.speedMultiplier");
		if (c.contains("hitch.jumpMultiplier"))
			jumpMultiplier = c.getDouble("hitch.jumpMultiplier");
		if (c.contains("hitch.allowedAnimals")) {
			allowedAnimals.clear();
			for (String s : c.getStringList("hitch.allowedAnimals")) {
				allowedAnimals.add(s.toUpperCase(Locale.ROOT));
			}
		}
	}

	/**
	 * @return the maximum speed this carriage can reach with the animals currently hitched, 0 if none are hitched
	 */
	public double getMaxSpeed(@NotNull VehicleEntity ve) {
		return HitchManager.getAverageSpeed(ve) * speedMultiplier;
	}

	@Override
	protected double getJumpHeight(VehicleEntity ve) {
		return HitchManager.getAverageJumpStrength(ve) * jumpMultiplier;
	}

	@Override
	public void handleSpeedIncrease(VehicleEntity ve, Player player) {
		double max = getMaxSpeed(ve);
		if (max <= 0) {
			notifyNoAnimal(player);
			return;
		}

		double newSpeed = Math.min(ve.getSpeed() + 0.1, max);
		VehicleChangeSpeedEvent e = new VehicleChangeSpeedEvent(ve, ve.getSpeed(), newSpeed);
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCanceled())
			return;
		ve.setSpeed(newSpeed);
	}

	@Override
	public void handleSpeedDecrease(VehicleEntity ve, Player player) {
		double max = getMaxSpeed(ve);
		if (max <= 0) {
			notifyNoAnimal(player);
			return;
		}

		double newSpeed = Math.max(ve.getSpeed() - 0.1, -Math.min(getMaxBackupSpeed(), max));
		VehicleChangeSpeedEvent e = new VehicleChangeSpeedEvent(ve, ve.getSpeed(), newSpeed);
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCanceled())
			return;
		ve.setSpeed(newSpeed);
	}

	@Override
	public void tick(VehicleEntity vehicleEntity) {
		if (vehicleEntity.getDriverSeat() == null) return;

		if (!HitchManager.hasHitchedAnimals(vehicleEntity) && vehicleEntity.getSpeed() != 0) {
			// The animals have been lost while moving: let the carriage roll to a stop.
			vehicleEntity.setSpeed(vehicleEntity.getSpeed() * 0.8);
		}

		super.tick(vehicleEntity);
		HitchManager.tick(vehicleEntity);
	}

	@Override
	public boolean enableFuel() {
		return false;
	}

	private void notifyNoAnimal(Player player) {
		if (player == null) return;
		long now = System.currentTimeMillis();
		Long last = lastNoAnimalMessage.get(player.getUniqueId());
		if (last != null && now - last < 2000) return;
		lastNoAnimalMessage.put(player.getUniqueId(), now);

		if (!Main.useChatForMessage) {
			try {
				HotbarMessager.sendHotBarMessage(player, MessagesConfig.MESSAGE_HITCH_NO_ANIMAL);
				return;
			} catch (Error | Exception ignored) {
			}
		}
		player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_HITCH_NO_ANIMAL);
	}

	public List<Vector> getHitchPoints() {
		return hitchPoints;
	}

	public Set<String> getAllowedAnimals() {
		return allowedAnimals;
	}

	public double getHitchRadius() {
		return hitchRadius;
	}

	public double getSpeedMultiplier() {
		return speedMultiplier;
	}

	public double getJumpMultiplier() {
		return jumpMultiplier;
	}
}
