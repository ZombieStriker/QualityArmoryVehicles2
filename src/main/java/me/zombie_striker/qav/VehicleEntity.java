package me.zombie_striker.qav;

import com.cryptomorin.xseries.XPotion;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.api.events.PlayerExitQAVehicleEvent;
import me.zombie_striker.qav.attachments.Attachment;
import me.zombie_striker.qav.attachments.AttachmentState;
import me.zombie_striker.qav.hitbox.HitboxEntities;
import me.zombie_striker.qav.hitbox.HitboxVisualizer;
import me.zombie_striker.qav.hitch.HitchManager;
import me.zombie_striker.qav.hooks.model.Animation;
import me.zombie_striker.qav.hooks.model.ModelEngineHook;
import me.zombie_striker.qav.input.VehicleInput;
import me.zombie_striker.qav.util.BlockCollisionUtil;
import me.zombie_striker.qav.util.ExposeDebug;
import me.zombie_striker.qav.util.SchedulerUtil;
import me.zombie_striker.qav.util.VehicleUtils;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VehicleEntity implements ConfigurationSerializable {

	public AbstractVehicle vehicleType;
	@ExposeDebug public boolean allowsPassagers = false;
	private boolean displayOnly = false;
	private boolean trackAutomatic = false;

	@ExposeDebug private double rotation = 0;
	@ExposeDebug private Vector direction = new Vector(0, 0, 1);
	@ExposeDebug private Vector centerOffset = new Vector(0, 0, 0);
	private BoundingBox boundingBox;
	@ExposeDebug private double speed = 0;
	@ExposeDebug private UUID vehicleUUID = UUID.randomUUID();

	@ExposeDebug private UUID owner;

	private List<ArmorStand> modelParts = new ArrayList<>();
	private Entity driverseat;
	@ExposeDebug private HashMap<Integer, Entity> passagers = new HashMap<>();
	@ExposeDebug private HashMap<Integer, Entity> attachments = new HashMap<>();
	private final Map<Integer, AttachmentState> attachmentStates = new HashMap<>();
	private Inventory inventory;
	private Inventory fuels;
	@ExposeDebug private int fuel = 0;
	@ExposeDebug private double yheight = 0;
	@ExposeDebug private List<UUID> whitelist = new ArrayList<>();
	@ExposeDebug private double health;
	@ExposeDebug private final Map<Integer, UUID> hitchedAnimals = new HashMap<>();
	private final Map<Integer, Entity> hitchedAnimalCache = new HashMap<>();
	private final Map<Integer, Integer> hitchedMissingTicks = new HashMap<>();
	// Folia: global tick in which the last region tick was queued, -1 if none is pending
	private volatile long tickScheduledAt = -1;
	private long ticksLived = 0;

	public VehicleEntity(AbstractVehicle vehicleType, Location location, UUID owner) {
		this.vehicleType = vehicleType;

		boundingBox = new BoundingBox(location, vehicleType.getWidthRadius(), vehicleType.getHeight());

		this.owner = Main.onlyPublicVehicles ? null : owner;
		if (owner != null)
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

		allowsPassagers = (boolean) data.getOrDefault("allowsPassagers", false);
		rotation = (double) data.get("angle");

		if (data.containsKey("hitched")) {
			for (String entry : (List<String>) data.get("hitched")) {
				String[] split = entry.split(":", 2);
				if (split.length != 2) continue;
				try {
					hitchedAnimals.put(Integer.parseInt(split[0]), UUID.fromString(split[1]));
				} catch (IllegalArgumentException ignored) {
				}
			}
		}

		if (data.containsKey("attachments")) {
			for (String entry : (List<String>) data.get("attachments")) {
				String[] split = entry.split(":", 2);
				if (split.length != 2) continue;
				try {
					getAttachmentState(Integer.parseInt(split[0])).setYaw(Double.parseDouble(split[1]));
				} catch (NumberFormatException ignored) {
				}
			}
		}

		trackAutomatic = (boolean) data.getOrDefault("trackAutomatic", false);

		if (SchedulerUtil.isFolia()) {
			// Entities can only be accessed from the region owning them, so spawn/find them there
			SchedulerUtil.scheduler().runAtLocation(loc, task -> loadOnFolia(loc, 0));
		} else {
			this.spawnOrFind(loc);
		}
	}

	/**
	 * Folia: the vehicle entities are not removed on shutdown (Folia does not allow it), so they are saved
	 * with the chunk. Wait for the chunk entities to be loaded before looking for them, to avoid duplicates.
	 * Runs on the region owning the location.
	 */
	private void loadOnFolia(Location loc, int attempt) {
		Chunk chunk = loc.getChunk();
		if (attempt == 0)
			chunk.addPluginChunkTicket(QualityArmoryVehicles.getPlugin());

		if (!chunk.isEntitiesLoaded() && attempt < 100) {
			SchedulerUtil.scheduler().runAtLocationLater(loc, () -> loadOnFolia(loc, attempt + 1), 2L);
			return;
		}

		try {
			this.spawnOrFind(loc);
			if (driverseat == null) return;

			if (trackAutomatic) {
				// Track trains are respawned by the track runtime: remove the ones left from the previous run
				deconstruct(null, "Leftover track train");
				return;
			}

			if (!Main.vehicles.contains(this))
				Main.vehicles.add(this);
		} finally {
			chunk.removePluginChunkTicket(QualityArmoryVehicles.getPlugin());
		}
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
		data.put("allowsPassagers", allowsPassagers);
		if (trackAutomatic)
			data.put("trackAutomatic", true);

		if (!hitchedAnimals.isEmpty()) {
			List<String> hitched = new ArrayList<>();
			for (Map.Entry<Integer, UUID> entry : hitchedAnimals.entrySet()) {
				hitched.add(entry.getKey() + ":" + entry.getValue());
			}
			data.put("hitched", hitched);
		}

		List<String> attachmentData = new ArrayList<>();
		for (Map.Entry<Integer, AttachmentState> entry : attachmentStates.entrySet()) {
			if (entry.getValue().getYaw() != 0)
				attachmentData.add(entry.getKey() + ":" + entry.getValue().getYaw());
		}
		if (!attachmentData.isEmpty())
			data.put("attachments", attachmentData);

		return data;
	}

	public void spawnOrFind(Location loc) {
		if (driverseat != null && driverseat.isValid()) {
			return;
		}

		if (loc != null) {
			for (Entity e : loc.getWorld().getNearbyEntities(loc, 10, 10, 10)) {
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

				findAttachment(e);
			}
		}

		if (!Main.separateModelAndDriver && driverseat != null && modelParts.size() == 0) {
			modelParts.add((ArmorStand) driverseat);
		}

		if (driverseat == null) {
			this.spawn();
		} else {
			spawnAttachments();
		}
	}

	/**
	 * Keeps the attachment if it belongs to this vehicle, removes it if its attachment no longer exists.
	 */
	private void findAttachment(Entity e) {
		String name = e.getCustomName();
		if (name == null || !name.startsWith(Main.ATTACHMENT_PREFIX)) return;

		String[] split = name.substring(Main.ATTACHMENT_PREFIX.length()).split("=", 2);
		if (split.length != 2 || !split[1].trim().equals(vehicleUUID.toString())) return;

		try {
			int index = Integer.parseInt(split[0]);
			if (index < vehicleType.getAttachments().size() && vehicleType.getAttachments().get(index).isSpawnable()
					&& !attachments.containsKey(index)) {
				attachments.put(index, e);
				return;
			}
		} catch (NumberFormatException ignored) {
		}
		e.remove();
	}

	/**
	 * @return true if an attachment is missing or no longer used (e.g. added with the API or after a reload)
	 */
	private boolean needsAttachmentSync() {
		List<Attachment> types = vehicleType.getAttachments();
		int spawned = 0;
		for (int i = 0; i < types.size(); i++) {
			if (!types.get(i).isSpawnable()) continue;
			Entity entity = attachments.get(i);
			if (entity == null) return true;
			spawned++;
		}
		return spawned != attachments.size();
	}

	/**
	 * Spawns the attachments of the vehicle type that are missing
	 */
	private void spawnAttachments() {
		ArmorStand model = modelParts.isEmpty() ? null : getModelEntity();
		if (model == null) return;

		List<Attachment> types = vehicleType.getAttachments();
		// Remove the attachments no longer used, e.g. after a reload
		attachments.entrySet().removeIf(entry -> {
			if (entry.getKey() < types.size() && types.get(entry.getKey()).isSpawnable()) return false;
			entry.getValue().remove();
			return true;
		});

		for (int i = 0; i < types.size(); i++) {
			Attachment attachment = types.get(i);
			if (!attachment.isSpawnable()) continue;

			Entity existing = attachments.get(i);
			if (existing != null && existing.isValid()) continue;

			Location location = attachment.getLocation(this, i, model.getLocation());
			ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
			entity.setVisible(false);
			entity.setSmall(model.isSmall());
			entity.setInvulnerable(true);
			try {
				entity.setCollidable(false);
			} catch (Error ignored) {
			}
			entity.setCustomName(Main.ATTACHMENT_PREFIX + i + "=" + vehicleUUID);
			ItemStack helmet = attachment.build();
			if (helmet != null)
				entity.setHelmet(helmet);
			attachment.animate(this, i, entity);
			attachments.put(i, entity);
		}
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

			// The driver seat is the same kind of armor stand as the model, so with a zero Y offset the
			// driver sits exactly where they would when the model and the driver are not separated.
			ArmorStand driverSeat = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(vehicleType.getDriverSeat()), EntityType.ARMOR_STAND);
			driverSeat.setSmall(model.isSmall());
			driverSeat.setVisible(false);

			driverSeat.setCustomName(Main.VEHICLEPREFIX + vehicleUUID.toString());
			driverSeat.setInvulnerable(true);
			this.driverseat = driverSeat;
		} else {
			model.setCustomName(Main.VEHICLEPREFIX + vehicleUUID.toString());
			this.driverseat = model;
		}

		spawnAttachments();

		ModelEngineHook.createModel(this);
		vehicleType.playAnimation(this, Animation.AnimationType.SPAWN);
	}

	public void tick() {
		vehicleType.tick(this);
		if (isInvalid()) return;

		if (needsAttachmentSync())
			spawnAttachments();

		if (!modelParts.isEmpty() || driverseat != null) {
			boundingBox.setLocation((modelParts.isEmpty() ? driverseat : getModelEntity()).getLocation());
		}
		HitboxEntities.update(this);
		HitboxVisualizer.tick(this, ticksLived++);
	}

	/**
	 * Used on Folia to avoid queueing more than one tick at a time when the owning region is lagging.
	 *
	 * @param now the current global tick
	 * @return true if a new tick can be queued
	 */
	public boolean markTickScheduled(long now) {
		long scheduledAt = tickScheduledAt;
		// Safety timeout in case a queued tick is dropped (e.g. the entity got removed)
		if (scheduledAt >= 0 && now - scheduledAt < 20) return false;
		tickScheduledAt = now;
		return true;
	}

	public void markTickDone() {
		tickScheduledAt = -1;
	}

	/**
	 * Handles the movement keys pressed by a player sitting in this vehicle.
	 */
	public void handleInput(@NotNull List<VehicleInput> inputs, @NotNull Player player) {
		Integer seat = getSeatIndex(player.getVehicle());
		if (seat == null) return;

		if (seat == Attachment.DRIVER_SEAT) {
			if (inputs.contains(VehicleInput.RIGHT))
				vehicleType.handleTurnLeft(this, player);
			if (inputs.contains(VehicleInput.LEFT))
				vehicleType.handleTurnRight(this, player);
			if (inputs.contains(VehicleInput.BACKWARD))
				vehicleType.handleSpeedDecrease(this, player);
			if (inputs.contains(VehicleInput.FORWARD))
				vehicleType.handleSpeedIncrease(this, player);
			if (inputs.contains(VehicleInput.SPACE))
				vehicleType.handleSpace(this, player);
			if (inputs.contains(VehicleInput.SHIFT)) {
				PlayerExitQAVehicleEvent event = new PlayerExitQAVehicleEvent(this, player);
				Bukkit.getPluginManager().callEvent(event);

				if (Main.antiCheatHook) {
					Location location = player.getVehicle().getLocation();
					SchedulerUtil.teleport(player, location);
				}

				if (Main.removeVehicleOnDismount) {
					VehicleUtils.callback(this, player, "Dismount");
				}
			}
		}

		for (VehicleInput input : inputs) {
			handleAttachmentInput(player, seat, input);
		}
	}

	/**
	 * Sends the input to the attachments controlled by the given seat.
	 *
	 * @return true if an attachment used the input
	 */
	public boolean handleAttachmentInput(@NotNull Player player, int seat, @NotNull VehicleInput input) {
		boolean used = false;
		List<Attachment> types = vehicleType.getAttachments();
		for (int i = 0; i < types.size(); i++) {
			Attachment attachment = types.get(i);
			if (attachment.getSeat() != seat || !attachments.containsKey(i)) continue;

			if (attachment.onInput(this, i, player, input))
				used = true;
		}
		return used;
	}

	/**
	 * @param seat the entity a player is riding
	 * @return {@link Attachment#DRIVER_SEAT}, the index of the passenger seat or null if it is not a seat of this vehicle
	 */
	public @Nullable Integer getSeatIndex(@Nullable Entity seat) {
		if (seat == null) return null;
		if (seat.equals(driverseat)) return Attachment.DRIVER_SEAT;
		for (Map.Entry<Integer, Entity> entry : passagers.entrySet()) {
			if (seat.equals(entry.getValue())) return entry.getKey();
		}
		return null;
	}

	/**
	 * @param index the index of the attachment in the vehicle type
	 * @return the armor stand of the attachment, null if it is not spawned
	 */
	public @Nullable Entity getAttachment(int index) {
		return attachments.get(index);
	}

	public @NotNull AttachmentState getAttachmentState(int index) {
		return attachmentStates.computeIfAbsent(index, i -> new AttachmentState());
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

	public Map<Integer,Entity> getAttachments() {
		return attachments;
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
		deconstruct(player,message,false);
	}

	public void deconstruct(Player player, String message, boolean disabling) {
		final Entity seat = driverseat;
		if (!disabling && seat != null && SchedulerUtil.isFolia() && !SchedulerUtil.isOwned(seat)) {
			// Folia: the vehicle entities must be removed by the region owning them
			SchedulerUtil.scheduler().runAtEntity(seat, task -> {
				if (seat.equals(driverseat))
					deconstruct(player, message, false);
			});
			return;
		}

		if (disabling && SchedulerUtil.isFolia()) {
			// Folia does not allow removing entities while shutting down: they are saved with the chunk
			// and found again on the next start (see loadOnFolia). The hitbox entities are not persistent.
			HitboxEntities.forget(this);
			driverseat = null;
			passagers.clear();
			modelParts.clear();
			attachments.clear();
			Main.DEBUG(this.getVehicleUUID() + " unloaded: " + message);
			return;
		}

		vehicleType.playAnimation(this, Animation.AnimationType.DESPAWN);
		HitchManager.releaseAll(this, disabling);
		HitboxEntities.remove(this);
		if (seat != null)
			seat.remove();
		driverseat = null;
		for (ArmorStand stand : getModelEntities()) {
			stand.remove();
		}
		for (Entity entity : getPassagerSeats()) {
			entity.remove();
		}
		for (Entity entity : getAttachments().values()) {
			entity.remove();
		}

		passagers.clear();
		modelParts.clear();
		attachments.clear();
		if (!disabling) Main.vehicles.remove(this);
		Main.DEBUG(this.getVehicleUUID() + " removed: " + message);
	}

	/**
	 * @return the UUIDs of the animals pulling this vehicle, keyed by hitch point index
	 */
	public Map<Integer, UUID> getHitchedAnimals() {
		return hitchedAnimals;
	}

	public Map<Integer, Entity> getHitchedAnimalCache() {
		return hitchedAnimalCache;
	}

	public Map<Integer, Integer> getHitchedMissingTicks() {
		return hitchedMissingTicks;
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

	public boolean isDisplayOnly() {
		return displayOnly;
	}

	public void setDisplayOnly(boolean displayOnly) {
		this.displayOnly = displayOnly;
	}

	public boolean isTrackAutomatic() {
		return trackAutomatic;
	}

	public void setTrackAutomatic(boolean trackAutomatic) {
		this.trackAutomatic = trackAutomatic;
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
		if (this.getType().getModelType().equals(ModelSize.TURTLE) || size < 1) {
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
			if (size < 2 || vehicleType.getSeatsSize() == ModelSize.BABY_ARMORSTAND_HEAD) {
				((ArmorStand) used).setSmall(true);
			}
			((ArmorStand) used).setVisible(false);
			((ArmorStand) used).setInvulnerable(false);
			((ArmorStand) used).setCollidable(false);
		}
		used.setCustomName(Main.VEHICLEPREFIX + vehicleUUID.toString());
		return used;
	}

	@SuppressWarnings("deprecation")
	private void giveEffects(@NotNull LivingEntity entity) {
		entity.addPotionEffect(new PotionEffect(XPotion.JUMP_BOOST.getPotionEffectType(), Integer.MAX_VALUE, -10000,false,false),
				false);
		entity.addPotionEffect(new PotionEffect(XPotion.INVISIBILITY.getPotionEffectType(), Integer.MAX_VALUE, 1,false,false),
				false);
		entity.addPotionEffect(new PotionEffect(XPotion.SLOWNESS.getPotionEffectType(), Integer.MAX_VALUE, 16,false,false),
				false);
		((Mob) entity).setAware(false);
	}

	public boolean isOnGround() {
		Location movingTo4 = getCenter().clone().subtract(0, 1, 0);
        return BlockCollisionUtil.isSolidAt(movingTo4);
    }

	public boolean isSubmerged() {
		if (driverseat == null)
			return false;
		return BlockCollisionUtil.getMaterial(driverseat.getLocation().add(0, 1.6, 0)) == Material.WATER;
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
