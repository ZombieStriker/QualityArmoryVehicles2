package me.zombie_striker.qav;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.hooks.model.Animation;
import me.zombie_striker.qav.hooks.model.ModelEngineHook;
import me.zombie_striker.qav.util.BlockCollisionUtil;
import me.zombie_striker.qav.util.ExposeDebug;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class VehicleEntity implements ConfigurationSerializable {

	public AbstractVehicle vehicleType;
	@ExposeDebug public boolean allowsPassagers = false;

	@ExposeDebug private double rotation = 0;
	@ExposeDebug private Vector direction = new Vector(0, 0, 1);
	@ExposeDebug private Vector centerOffset = new Vector(0, 0, 0);
	private BoundingBox boundingBox;
	@ExposeDebug private double speed = 0;
	@ExposeDebug private UUID vehicleUUID = UUID.randomUUID();

	@ExposeDebug private UUID owner;

	private List<ArmorStand> modelParts = new ArrayList<>();
	private Entity driverseat;
	private HashMap<Integer, Entity> passagers = new HashMap<Integer, Entity>();
	@ExposeDebug private Inventory inventory;
	@ExposeDebug private Inventory fuels;
	@ExposeDebug private int fuel = 0;
	@ExposeDebug private double yheight = 0;
	@ExposeDebug private List<UUID> whitelist = new ArrayList<>();
	@ExposeDebug private double health;

	public VehicleEntity(AbstractVehicle vehicleType, Location location, UUID owner) {
		this.vehicleType = vehicleType;

		boundingBox = new BoundingBox(location, vehicleType.getWidthRadius(), vehicleType.getHeight());

		this.owner = Main.onlyPublicVehicles ? null : owner;
		this.whitelist.add(owner);

		health = vehicleType.getMaxHealth();

		Main.vehicles.add(this);
	}

	public VehicleEntity(Map<String, Object> data) {
		vehicleUUID = UUID.fromString((String) data.get("uuid"));
		vehicleType = QualityArmoryVehicles.getVehicle((String) data.get("type"));
		if(vehicleType==null)
			return;
		Location loc = (Location) data.get("loc");
		if(loc==null)
			return;
		try {
			if (!loc.isWorldLoaded()) {
				loc.getChunk().load();
			}
		} catch (Exception | Error ignored) {}
		for (Entity e : loc.getWorld().getNearbyEntities(loc, 6, 6, 6)) {
			if (e.getCustomName() != null && e.getCustomName().startsWith(Main.VEHICLEPREFIX)) {
				if (e.getCustomName().trim().endsWith(vehicleUUID.toString().trim())) {
					driverseat = e;
				}
			}

			if (e instanceof ArmorStand && e.getCustomName() != null && e.getCustomName().startsWith(Main.MODEL_PREFIX)) {
				if (e.getCustomName().trim().endsWith(vehicleUUID.toString().trim())) {
					modelParts.add((ArmorStand) e);
				}
			}
		}

		if (data.containsKey("owner"))
			owner = Main.onlyPublicVehicles ? null : UUID.fromString((String) data.get("owner"));

		fuel = (int) data.get("fuel");
		if(data.containsKey("fuels")) {
			ArrayList<ItemStack> items = (ArrayList<ItemStack>) data.get("fuels");

			getFuels().setContents(items.toArray(new ItemStack[0]));
		}
		if(data.containsKey("inventory")) {
			ArrayList<ItemStack> items = (ArrayList<ItemStack>) data.get("inventory");

			getTrunk().setContents(items.toArray(new ItemStack[0]));
		}

		health = (double) data.get("health");

		boundingBox = new BoundingBox(loc, vehicleType.getWidthRadius(), vehicleType.getHeight());

		List<String> whitelist = (List<String>) data.get("whitelist");
		for (String string : whitelist) {
			this.whitelist.add(UUID.fromString(string));
		}

		rotation = (double) data.get("angle");
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> data = new HashMap<>();
		data.put("uuid", vehicleUUID.toString());
		if(driverseat!=null)
		data.put("loc", getDriverSeat().getLocation());
		data.put("fuel", fuel);
		if (fuels != null)
			data.put("fuels", fuels.getContents());
		if (getTrunk() != null)
			data.put("inventory", getTrunk().getContents());
		if(vehicleType!=null)
		data.put("type", vehicleType.getName());
		if (owner != null)
			data.put("owner", owner.toString());
		data.put("health", health);
		data.put("angle", getAngleRotation());

		List<String> whitelist = new ArrayList<>();
		for (UUID uuid : getWhiteList()) {
			whitelist.add(uuid.toString());
		}
		data.put("whitelist", whitelist);

		return data;
	}

	public void spawnOrFind() {
		if (driverseat != null && driverseat.isValid()) {
			return;
		}

		this.spawn();
	}

	@SuppressWarnings("deprecation")
	public void spawn() {
		Location loc = boundingBox.getLocation();
		ArmorStand model = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		modelParts.add(model);

		model.setHelmet(getType().getModel());

		if (getType().getModelType() == ModelSize.BABY_ARMORSTAND_HEAD) {
			model.setSmall(true);
		}

		model.setInvulnerable(true);
		model.setVisible(false);
		if (Main.separateModelAndDriver) {
			model.setCustomName(Main.MODEL_PREFIX + vehicleUUID.toString());

			Entity driverSeat;

			if (getType().getModelType().equals(ModelSize.TURTLE)) {
				try {
					driverSeat = loc.getWorld().spawnEntity(loc.clone().add(vehicleType.getDriverSeat()), EntityType.TURTLE);
					giveEffects(((LivingEntity) driverSeat));
				} catch (Exception ignored) {
					driverSeat = loc.getWorld().spawnEntity(loc.clone().add(vehicleType.getDriverSeat()), EntityType.ARMOR_STAND);
					((ArmorStand) driverSeat).setVisible(false);
				}
			} else {
				driverSeat = loc.getWorld().spawnEntity(loc.clone().add(vehicleType.getDriverSeat()), EntityType.ARMOR_STAND);
				((ArmorStand) driverSeat).setVisible(false);
			}

			driverSeat.setCustomName(Main.VEHICLEPREFIX + vehicleUUID.toString());
			driverSeat.setInvulnerable(true);
			this.driverseat = driverSeat;
		} else {
			model.setCustomName(Main.VEHICLEPREFIX + vehicleUUID.toString());
			this.driverseat = model;
		}

		ModelEngineHook.createModel(this);
		vehicleType.playAnimation(this, Animation.AnimationType.SPAWN);
	}

	public void tick() {
		vehicleType.tick(this);
	}


	public List<ArmorStand> getModelEntities() {
		return modelParts;
	}

	public ArmorStand getModelEntity() {
		return modelParts.get(0);
	}

	public Entity getDriverSeat() {
		return driverseat;
	}

	public double getAngleRotation() {
		return rotation;
	}

	public Vector getDirection() {
		return direction;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	public AbstractVehicle getType() {
		return vehicleType;
	}

	public void setType(AbstractVehicle vehicle) {
		this.vehicleType = vehicle;
	}

	public Collection<Entity> getPassagerSeats() {
		return passagers.values();
	}

	public void setAngle(double v) {
		Main.DEBUG("Setting angle to " + v);

		this.rotation = v;
		direction = new Vector(-Math.sin(rotation), direction.getY(), Math.cos(rotation));
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public Inventory getTrunk() {
		if (this.inventory == null) {
			this.inventory = Bukkit.createInventory(null, getType().getTrunkSize());
		}
		return inventory;
	}

	public boolean isInvalid() {
		if (driverseat == null) {
			return true;
		}
		return false;
	}

	public Inventory getFuels() {
		if (this.fuels == null) {
			this.fuels = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', MessagesConfig.MENU_FUELTANK_TITLE.replace("%cartype%", this.getType().getDisplayname())));
		}
		return this.fuels;
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int i) {
		this.fuel = i;
	}

	public Location getCenter() {
		return driverseat.getLocation().add(QualityArmoryVehicles.rotateRelToCar(this, getModelEntity(), centerOffset, false));
	}

	public void setDirectionYHeight(double i) {
		yheight = i;
	}

	public double getDirectionYheight() {
		return yheight;
	}

	public UUID getVehicleUUID() {
		return vehicleUUID;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID o) {
		this.owner = Main.onlyPublicVehicles ? null : o;
	}

	public void deconstruct(Player player, String message) {
		vehicleType.playAnimation(this, Animation.AnimationType.DESPAWN);
		driverseat.remove();
		driverseat = null;
		for (ArmorStand stand : getModelEntities()) {
			stand.remove();
		}
		for (Entity entity : getPassagerSeats()) {
			entity.remove();
		}

		passagers.clear();
		modelParts.clear();
		Main.vehicles.remove(this);
		Main.DEBUG(this.getVehicleUUID() + " removed: " + message);
	}

	public List<UUID> getWhiteList() {
		return whitelist;
	}

	public boolean allowUserPassager(UUID uuid) {
		if (allowsPassagers || Main.onlyPublicVehicles)
			return true;
		return whitelist == null || whitelist.contains(uuid);
	}

	public boolean allowsPassagers() {
		return allowsPassagers;
	}

	public void setAllowsPassagers(boolean b) {
		this.allowsPassagers = b;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) { this.health = health; }

	public void addToWhitelist(UUID uniqueId) {
		this.whitelist.add(uniqueId);
	}

	public void removeFromWhitelist(UUID uniqueId) {
		this.whitelist.remove(uniqueId);
	}

	public boolean allowUserDriver(UUID uniqueId) {
		return Main.onlyPublicVehicles || this.whitelist.contains(uniqueId);
	}

	public Entity getPassager(int i) {
		return passagers.get(i);
	}

	public HashMap<Integer, Entity> getPassagers() {
		return passagers;
	}

	public void updateSeats() {
		for (Map.Entry<Integer, Entity> e : new ArrayList<>(passagers.entrySet())) {
			if (e.getValue().getPassenger() == null) {
				passagers.remove(e.getKey());
				e.getValue().remove();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public int getFirstSeat() {
		for (Map.Entry<Integer,Entity> entry : passagers.entrySet()) {
			if (entry.getValue().getPassenger() == null)
				return entry.getKey();
		}
		return -1;
	}

	public void addPassager(int seatID, Entity seat) {
		passagers.put(seatID, seat);
	}

	public Entity spawnSeat(Location spawn, int seatID) {
		Entity used = null;
		double size = 2;//seatID == -1 ? getType().getSeatHeight() : getType().getPassagerSizeById(seatID);
		if (Main.useTurtles || size < 1) {
			try {
				used = spawn.getWorld().spawnEntity(spawn, EntityType.TURTLE);
				used.setInvulnerable(true);
				((Ageable) used).setBaby();
			} catch (Error | Exception e4) {
				try {
					if (!Main.swapEndermiteWithChicken) {
						used = spawn.getWorld().spawnEntity(spawn, EntityType.ENDERMITE);
					} else {
						used = spawn.getWorld().spawnEntity(spawn, EntityType.CHICKEN);
						((Chicken) used).setBaby();
					}
				} catch (Error | Exception e43) {
					used = spawn.getWorld().spawnEntity(spawn, EntityType.CHICKEN);
					((Chicken) used).setBaby();
				}
			}
			try {
				((LivingEntity) used).setCollidable(false);
				((LivingEntity) used).setSilent(true);
			} catch (Exception | Error e3) {
			}
			giveEffects(((LivingEntity) used));
		} else {
			used = spawn.getWorld().spawnEntity(spawn, EntityType.ARMOR_STAND);
			if (size < 2) {
				((ArmorStand) used).setSmall(true);
			}
			((ArmorStand) used).setVisible(false);
			((ArmorStand) used).setInvulnerable(false);
			((ArmorStand) used).setCollidable(false);
		}
		used.setCustomName(Main.VEHICLEPREFIX + vehicleUUID.toString());
		return used;
	}

	private void giveEffects(@NotNull LivingEntity entity) {
		entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -10000,false,false),
				false);
		entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1,false,false),
				false);
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 16,false,false),
				false);
		((Mob) entity).setAware(false);
	}

	public boolean isOnGround() {
		Location movingTo4 = getCenter().clone().subtract(0, 1, 0);
		if (BlockCollisionUtil.isSolidAt(movingTo4)) {
			return true;
		}
		return false;
	}

	public boolean isSubmerged() {
		if (driverseat.getLocation().add(0, 1.6, 0).getBlock().getType() == Material.WATER)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "{" +
				"vehicleType=" + vehicleType +
				", allowsPassagers=" + allowsPassagers +
				", rotation=" + rotation +
				", direction=" + direction +
				", centerOffset=" + centerOffset +
				", boundingBox=" + boundingBox +
				", speed=" + speed +
				", vehicleUUID=" + vehicleUUID +
				", owner=" + owner +
				", modelParts=" + modelParts +
				", driverseat=" + driverseat +
				", passagers=" + passagers +
				", inventory=" + inventory +
				", fuels=" + fuels +
				", fuel=" + fuel +
				", yheight=" + yheight +
				", whitelist=" + whitelist +
				", health=" + health +
				'}';
	}
}
