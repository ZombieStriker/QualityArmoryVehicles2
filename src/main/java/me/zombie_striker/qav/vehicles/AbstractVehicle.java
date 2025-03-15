package me.zombie_striker.qav.vehicles;

import me.zombie_striker.qav.*;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.attachments.Attachment;
import me.zombie_striker.qav.finput.FInput;
import me.zombie_striker.qav.fuel.FuelItemStack;
import me.zombie_striker.qav.hooks.ProtectionHandler;
import me.zombie_striker.qav.hooks.model.Animation;
import me.zombie_striker.qav.hooks.model.ModelEngineHook;
import me.zombie_striker.qav.nms.NMSUtil;
import me.zombie_striker.qav.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractVehicle {
	protected static final double pitchIncrement = Math.PI / 60;
	protected static final double maxAngle = Math.PI / 4;

	private double rotationMultiplier = 1;
	private Map<String,FInput> inputs = new HashMap<>();
	private double widthRadius = 5;
	private double height = 4;
	private String internalName;
	private boolean bodyFix = false;
	private double rotationDelta = 0.1;
	private double maxSpeed = 0.9;
	private double maxBackupSpeed = 0.8;
	private boolean destructable = true;
	private boolean disableMeleeDamage = true;
	private boolean disableProjectileDamage = true;
	private double jumphiehgt = 0.2;
	private Vector driverSeat;
	private HashMap<Vector, Integer> passagerOffset = new HashMap<>();
	private List<Attachment> attachments = new ArrayList<>();
	private List<Animation> animations = new ArrayList<>();

	private int id;
	private Material material;
	private ItemStack vehicleModel = null;
	private Vector center;
	private String displayname;
	private List<String> lore;
	private double acceleration = 0.1;
	private ModelSize size = ModelSize.BABY_ARMORSTAND_HEAD;
	private boolean canJump = true;
	private double maxhealth;
	private int price;
	private boolean inShop;
	private String sound;
	private boolean playSoundsDriving;
	private float soundVolume = 1;
	private int trunksize;
	private boolean enableFuels = false;

	public AbstractVehicle(String name, int id) {
		this.internalName = name;
		this.id = id;
	}

	public boolean handleFuel(VehicleEntity ve, Player player) {
		boolean shouldReturn = true;

		if (Main.bypassCoalInCreative) {
			@SuppressWarnings("deprecation") Entity passenger = ve.getDriverSeat().getPassenger();
			if (passenger instanceof Player && ((Player) passenger).getGameMode().equals(GameMode.CREATIVE)) {
				return true;
			}
		}

		if (this.enableFuel()) {
			FuelItemStack.updateFuel(ve);
			if (ve.getFuel() <= 0) {
				shouldReturn = false;
				try {
					if (player != null)
						if (!Main.useChatForMessage) {
							try {
								HotbarMessager.sendHotBarMessage(player, MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
							} catch (Error | Exception ignored) {
							}
						} else {
							player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
						}
				} catch (Error | Exception ignored) {
				}
			} else {
				ve.setFuel(ve.getFuel() - 1);
			}
		}

		return shouldReturn;
	}

	public boolean hasFuel(VehicleEntity ve) {
		if (!enableFuel()) return true;

		if (Main.bypassCoalInCreative) {
			@SuppressWarnings("deprecation") Entity passenger = ve.getDriverSeat().getPassenger();
			if (passenger instanceof Player && ((Player) passenger).getGameMode().equals(GameMode.CREATIVE)) {
				return true;
			}
		}

		return ve.getFuel() > 0;
	}

	public abstract void handleTurnLeft(VehicleEntity ve, Player player);

	public abstract void handleTurnRight(VehicleEntity ve, Player player);

	public abstract void handleSpeedIncrease(VehicleEntity ve, Player player);

	public abstract void handleSpeedDecrease(VehicleEntity ve, Player player);

	public abstract void handleSpace(VehicleEntity ve, Player player);

	public abstract void tick(VehicleEntity vehicleEntity);

	public ItemStack getModel() {
		if (vehicleModel == null) {
			vehicleModel = ItemFact.getItem(this);
		}
		return vehicleModel;
	}

	public void setModelItemStack(ItemStack itemstack) {
		this.vehicleModel = itemstack;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidthRadius() {
		return widthRadius;
	}

	public void setWidthRadius(double radius) {
		this.widthRadius = radius;
	}

	public boolean enableBodyFix() {
		return bodyFix;
	}

	public double getRotationDelta() {
		return rotationDelta;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxAcceleration) {
		this.maxSpeed = maxAcceleration;
	}

	public void setDeconstructable(boolean canDeconstructByEnvironment) {
		this.destructable = canDeconstructByEnvironment;
	}

	public void setStopsProjectileDamage(boolean stopProjectileDamage) {
		disableProjectileDamage = stopProjectileDamage;
	}

	public void setStopsMeleeDamage(boolean stopsMeleeDamageDamage) {
		disableMeleeDamage = stopsMeleeDamageDamage;
	}

	public Material getMaterial() {
		if (material != null) {
			return material;
		} else {
			material = Material.RABBIT_HIDE;
		}
		if (vehicleModel == null) {
			vehicleModel = QualityArmoryVehicles.getVehicleItemStack(this);
		}
		return vehicleModel.getType();
	}

	public void setMaterial(Material vehicle_texture_material) {
		this.material = vehicle_texture_material;
	}

	public int getItemData() {
		return id;
	}

	public void setJumpHeight(double jumpHeight) {
		this.jumphiehgt = jumpHeight;
	}

	public void setCenter(Vector center) {
		this.center = center;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public boolean hasLore() {
		return lore != null;
	}

	public void setMaxBackupSpeed(double maxReverseAcceleration) {
		this.maxBackupSpeed = maxReverseAcceleration;
	}

	public double getMaxBackupSpeed() {
		return maxBackupSpeed;
	}

	public void setAccerlationSpeed(double baseAcceleration) {
		this.acceleration = baseAcceleration;
	}

	public void setModelSize(ModelSize adultArmorstandHead) {
		this.size = adultArmorstandHead;
	}

	boolean canJump() {
		return canJump;
	}

	public void setTurnRate(double turnSpeedInRadians) {
		rotationDelta = turnSpeedInRadians;
	}

	public void setEnableFuel(boolean requiresFuel) {
		this.enableFuels = requiresFuel;
	}

	public void setCanJump(boolean canJumpOnBlocks) {
		this.canJump = canJumpOnBlocks;
	}

	public void setBodyFix(boolean enablePlayerBodyDirectionFix) {
		this.bodyFix = enablePlayerBodyDirectionFix;
	}

	public void setPrice(int cost) {
		this.price = cost;
	}

	public void setAllowInShop(boolean allowedInShop) {
		this.inShop = allowedInShop;
	}

	public void setPlayCustomSounds(boolean playDrivingSounds) {
		this.playSoundsDriving = playDrivingSounds;
	}

	public int getTrunkSize() {
		return trunksize;
	}

	public void setTrunkSize(int trunksize) {
		this.trunksize = trunksize;
	}

	public Vector getCenterFromControlSeat() {
		return new Vector(0, 0, 0);
	}

	public Vector getDriverSeat() {
		return driverSeat;
	}

	public void setDriverSeat(Vector driverSeat) {
		this.driverSeat = driverSeat;
	}

	public String getName() {
		return internalName;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public double getSoundVolume() {
		return soundVolume;
	}

	public void setSoundVolume(float soundVolume) {
		this.soundVolume = soundVolume;
	}

	public boolean canPlaySkidSounds() {
		return playSoundsDriving;
	}

	public ModelSize getModelType() {
		return size;
	}

	public void basicDirections(VehicleEntity vehicleEntity, boolean jump, boolean aquatic) {
		basicDirections(vehicleEntity, jump, aquatic, true, false);
	}

	@SuppressWarnings("deprecation")
	public void basicDirections(VehicleEntity vehicleEntity, boolean jump, boolean aquatic, boolean gravity, boolean planeFlying) {
		if (vehicleEntity.getHealth() <= 0.0 && Main.freezeOnDestroy) return;

		Location block = Main.separateModelAndDriver ? vehicleEntity.getModelEntity().getLocation().subtract(0,0.4,0): vehicleEntity.getDriverSeat().getLocation().subtract(0, 0.4, 0);
		Material material = BlockCollisionUtil.getMaterial(block);

		if (Main.customSpeedModifier.containsKey(material)) {
			vehicleEntity.setSpeed(vehicleEntity.getSpeed() * Main.customSpeedModifier.getOrDefault(material,1.0));
		}

		if (vehicleEntity.getSpeed() > 0) {
			if(planeFlying){
				// Apply air resistance - planes should always gradually slow down unless actively accelerating
				// The deceleration rate depends on the pitch:
				if(vehicleEntity.getDirectionYheight() < -0.15) {
					// When diving steeply (negative Y), slight acceleration due to gravity
					// But cap the speed at maxSpeed * 2 to prevent excessive speeds
					double newSpeed = vehicleEntity.getSpeed() + 0.01;
					vehicleEntity.setSpeed(Math.min(newSpeed, vehicleEntity.getType().getMaxSpeed() * 2));
				} else if(vehicleEntity.getDirectionYheight() > 0.15) {
					// When climbing steeply (positive Y), strong deceleration against gravity
					vehicleEntity.setSpeed(vehicleEntity.getSpeed() - 0.02);
				} else {
					// When level flying (Y near 0), apply moderate air resistance
					// This prevents the exponential speed increase in level flight
					vehicleEntity.setSpeed(vehicleEntity.getSpeed() - 0.005);
				}

				// When S key is pressed, the plane should slow down regardless of pitch/dive
				if (vehicleEntity.isBackwardMovement()) {
					vehicleEntity.setSpeed(Math.max(0, vehicleEntity.getSpeed() - 0.03));
				}

				if (Main.modernPlaneMovements && vehicleEntity.getDriverSeat().getPassenger() instanceof Player) {
					Player player = (Player) vehicleEntity.getDriverSeat().getPassenger();
					Vector direction = player.getEyeLocation().getDirection();
					vehicleEntity.setDirectionYHeight(direction.getY());
					HeadPoseUtil.setHeadPoseUsingReflection(vehicleEntity);
					vehicleEntity.getModelEntity()
							.setHeadPose(new EulerAngle(direction.getY() * -1, vehicleEntity.getModelEntity().getHeadPose().getY(), 0));
				}
			}else {
				vehicleEntity.setSpeed(vehicleEntity.getSpeed() - 0.01);
			}
		}
		if (vehicleEntity.getSpeed() < 0) {
			vehicleEntity.setSpeed(vehicleEntity.getSpeed() + 0.01);
		}

		if (vehicleEntity.getSpeed() > 0.0 && vehicleEntity.getSpeed() < 0.09) {
			vehicleEntity.setSpeed(0.0);
		}

		Vector velocity = vehicleEntity.getDirection().clone();
		velocity.normalize().multiply(vehicleEntity.getSpeed());

		if (planeFlying) {
			double velDir = velocity.length();

			double gravityFactor = 0.05;
			if (vehicleEntity.getSpeed() < 0.3) {
				gravityFactor = 0.1;
			}

			velocity.setY(vehicleEntity.getDirectionYheight());
			if (velocity.length() != 0.0) velocity.normalize();
			velocity.multiply(velDir);

			if (vehicleEntity.getSpeed() < 0.1) {
				velocity.setY(velocity.getY() - gravityFactor);
			}

			double pitch = vehicleEntity.getModelEntity().getHeadPose().getX();

			if (vehicleEntity.getSpeed() <= 0.2 && !vehicleEntity.isOnGround()) {
				pitch = vehicleEntity.getModelEntity().getHeadPose().getX();
				pitch += AbstractVehicle.pitchIncrement;
				if (pitch > AbstractVehicle.maxAngle) {
					pitch = AbstractVehicle.maxAngle;
				}else  if (pitch < -AbstractVehicle.maxAngle) {
					pitch = -AbstractVehicle.maxAngle;
				}else {
					vehicleEntity.setDirectionYHeight(vehicleEntity.getDirectionYheight() - 0.1);
				}
				vehicleEntity.getModelEntity()
						.setHeadPose(new EulerAngle(pitch, vehicleEntity.getModelEntity().getHeadPose().getY(), 0));
			} else if (vehicleEntity.isOnGround()) {
				double adjustment = 0.01;

				if (Math.abs(pitch) < adjustment) {
					pitch = 0;
				} else if (pitch > 0) {
					pitch -= adjustment;
				} else if (pitch < 0) {
					pitch += adjustment;
				}

				vehicleEntity.getModelEntity()
						.setHeadPose(new EulerAngle(pitch, vehicleEntity.getModelEntity().getHeadPose().getY(), 0));

				if (vehicleEntity.getDirectionYheight() < 0) {
					vehicleEntity.setSpeed(Math.max(0, vehicleEntity.getSpeed() - 0.03));
					vehicleEntity.setDirectionYHeight(0);
				}
			}
			if (pitch > maxAngle) {
				pitch = maxAngle;
			}
			if (pitch < -maxAngle) {
				pitch = -maxAngle;
			}
			vehicleEntity.getModelEntity()
					.setHeadPose(new EulerAngle(pitch, vehicleEntity.getModelEntity().getHeadPose().getY(), 0));

		} else if (gravity) {
			//Add Gravity
			if (!BlockCollisionUtil.isSolid(material)) {
				velocity.setY(Math.max(-1,vehicleEntity.getDriverSeat().getVelocity().getY() - 0.05));
			}
		} else {
			velocity.setY(vehicleEntity.getDirectionYheight());
			if (vehicleEntity.getDirectionYheight() > 0) {
				vehicleEntity.setDirectionYHeight(vehicleEntity.getDirectionYheight() - 0.05);
			} else if (vehicleEntity.getDirectionYheight() < 0) {
				vehicleEntity.setDirectionYHeight(vehicleEntity.getDirectionYheight() + 0.05);
			}
		}

		if (aquatic) {
			Material type = BlockCollisionUtil.getMaterial(vehicleEntity.getDriverSeat().getLocation());
			if (type == Material.WATER || type == Material.SEAGRASS || type == Material.TALL_SEAGRASS || type == Material.KELP) {
				if (vehicleEntity.getDriverSeat().getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WATER) {
					velocity.setY(0.1);
				} else {
					velocity.setY(0);
				}
			} else {
				return;
			}
		}

		//Jump
		if (canJump()) {
			Location movingTo = vehicleEntity.getCenter().clone().add(vehicleEntity.getDirection().clone().multiply(vehicleEntity.getBoundingBox().getWidth()));
			boolean goingUp = false;
			if (BlockCollisionUtil.isSolidAt(movingTo)) {
				if (!BlockCollisionUtil.isSolidAt(movingTo.clone().add(0, 1, 0))) {
					velocity.setY(jumphiehgt);
					goingUp = true;
				}
			}
			if (!goingUp) {
				Location movingTo2 = vehicleEntity.getCenter().clone().add(vehicleEntity.getDirection().clone());
				if (BlockCollisionUtil.isSolidAt(movingTo2)) {
					if (!BlockCollisionUtil.isSolidAt(movingTo2.clone().add(0, 1, 0))) {
						velocity.setY(jumphiehgt);
						goingUp = true;
					}
				}
			}
			if (!goingUp) {
				Location movingTo3 = vehicleEntity.getCenter().clone().subtract(vehicleEntity.getDirection().clone().multiply(vehicleEntity.getBoundingBox().getWidth()));
				if (BlockCollisionUtil.isSolidAt(movingTo3)) {
					if (!BlockCollisionUtil.isSolidAt(movingTo3.clone().add(0, 1, 0))) {
						velocity.setY(jumphiehgt);
						goingUp = true;
					}
				}
			}
			if (!goingUp) {
				Location movingTo4 = vehicleEntity.getCenter().clone().subtract(vehicleEntity.getDirection().clone());
				if (BlockCollisionUtil.isSolidAt(movingTo4)) {
					if (!BlockCollisionUtil.isSolidAt(movingTo4.clone().add(0, 1, 0))) {
						velocity.setY(jumphiehgt);
					}
				}
			}
		}

		try {
			if (vehicleEntity.getDriverSeat().getLocation().getY() >= vehicleEntity.getDriverSeat().getLocation().getWorld().getMaxHeight()) {
				Main.DEBUG("Y limit: " + vehicleEntity.getDriverSeat().getLocation().getWorld().getMaxHeight() + " y: " + vehicleEntity.getDriverSeat().getLocation().getY());
				velocity.setY(velocity.getY()-1);
			}
		} catch (Error | Exception ignored) {}

		if (vehicleEntity.getDriverSeat().getLocation().getY() >= Main.maxYheightForVehicles) {
			Main.DEBUG("Y limit: " + Main.maxYheightForVehicles + " y: " + vehicleEntity.getDriverSeat().getLocation().getY());
			velocity.setY(velocity.getY()-1);
		}

		if (Main.enableCrossVehicleCollision) {
			for (VehicleEntity vehicle : Main.vehicles) {
				if (vehicle.equals(vehicleEntity)) continue;

				if (QualityArmoryVehicles.isWithinVehicle(vehicleEntity.getDriverSeat().getLocation(), vehicle)) {
					Main.DEBUG("Collision with vehicle: " + vehicle.getType().getName());
					velocity.multiply(-1);
				}
			}
		}

		applyModifiers(vehicleEntity, velocity);
		vehicleEntity.getDriverSeat().setVelocity(velocity);
		handleOtherStands(vehicleEntity,velocity);

		playAnimation(vehicleEntity, Animation.AnimationType.RUN);

		Entity passenger = vehicleEntity.getDriverSeat().getPassenger();
		if (passenger instanceof Player) {
			Player player = (Player) passenger;
			// Handle protection
			if (!ProtectionHandler.canMove(player,vehicleEntity.getDriverSeat().getLocation())) {
				VehicleUtils.callback(vehicleEntity, Bukkit.getPlayer(vehicleEntity.getOwner()), "Not allowed");
			}

			// Send actionbar
			if (Main.sendActionBarOnMove) {
				if ((!player.getGameMode().equals(GameMode.CREATIVE) || !Main.bypassCoalInCreative) && vehicleEntity.getFuel() <= 0) {
					return;
				}

				HotbarMessager.sendHotBarMessage(player,MessagesConfig.MESSAGE_ACTIOBAR_MOVE.replace("%type%", vehicleEntity.getType().getDisplayname())
						.replace("%fuel%", String.valueOf(vehicleEntity.getFuel()))
						.replace("%speed%", String.valueOf(Math.round(vehicleEntity.getSpeed() * 20 * (vehicleEntity.getSpeed() < 0 ? -1 : 1)))));
			}
		}

	}

	public void applyModifiers(VehicleEntity entity, Vector vector) {
		// TODO: Apply backup speed
	}

	public void handleOtherStands(VehicleEntity ve, Vector velocity) {
		if (!ve.getDriverSeat().equals(ve.getModelEntity())) {
			Location to = this.getDriverSeat() != null
					? ve.getDriverSeat().getLocation()
					.subtract(QualityArmoryVehicles.rotateRelToCar(ve, ve.getModelEntity(),
							this.getDriverSeat(), false))
					: ve.getDriverSeat().getLocation();
			if (checkDistance(ve.getModelEntity(),to,false)) {
				HeadPoseUtil.setHeadPoseUsingReflection(ve);
			}
			Vector velo = velocity.clone();
			Vector distanceC = to.clone().subtract(ve.getModelEntity().getLocation()).toVector();
			velo.add(distanceC);
			ve.getModelEntity().setVelocity(velo);
		}

		for (Entity e : ve.getPassagerSeats()) {
			Location offset = ve.getDriverSeat().getLocation().clone()
					.add(QualityArmoryVehicles.rotateRelToCar(ve, ve.getDriverSeat(),
							getPassagerSpots().get(
									Integer.parseInt(e.getCustomName().split(Main.PASSAGER_PREFIX)[1])),
							false));

			offset.add(ve.getDriverSeat().getVelocity());
			offset.subtract(0, 0.6, 0);

			Vector newVelo = velocity.clone();
			checkDistance(e, offset, true);

			Vector distance = offset.toVector().clone().subtract(e.getLocation().toVector());
			newVelo.add(distance);
			e.setVelocity(newVelo);
		}

		for (Map.Entry<Integer,Entity> entry : ve.getAttachments().entrySet()) {
			Attachment attachment = attachments.get(entry.getKey());
			Location offset = ve.getDriverSeat().getLocation().clone().add(attachment.getVector());

			offset.add(ve.getDriverSeat().getVelocity());
			offset.subtract(0, 0.6, 0);

			Vector newVelo = velocity.clone();
			checkDistance(entry.getValue(), offset, true);

			Vector distance = offset.toVector().clone().subtract(entry.getValue().getLocation().toVector());
			newVelo.add(distance);
			entry.getValue().setVelocity(newVelo);
			attachment.animate(ve,(ArmorStand) entry.getValue());
		}
	}

	@SuppressWarnings("deprecation")
	private boolean checkDistance(Entity entity, Location offset, boolean passengers) {
		double offDis = offset.distance(entity.getLocation());
		if (offDis > 1) {
			final Entity rider = entity.getPassenger();

			if (passengers && rider == null) {
				entity.remove();
				return false;
			}

			NMSUtil.teleport(entity,offset);

			Main.DEBUG("Moved other stand. Previous rider: " + rider + " - new rider: " + entity.getPassenger());
			return true;
		}

		return false;
	}

	public boolean isAllowedInShop() {
		return inShop;
	}

	public int getCost() {
		return price;
	}

	public boolean enableFuel() {
		return enableFuels;
	}

	public double getMaxHealth() {
		return maxhealth;
	}

	public void setMaxHealth(double v) {
		this.maxhealth = v;
	}

	public List<Vector> getPassagerSpots() {
		return new ArrayList<>(passagerOffset.keySet());
	}

	public void setPassagerSpots(HashMap<Vector, Integer> sizes) {
		this.passagerOffset = sizes;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<Animation> getAnimations() {
		return animations;
	}

	public void playAnimation(VehicleEntity entity, Animation.AnimationType event, String... args) {
		animations.stream()
				.filter(animation -> animation.getType().equals(event) && Arrays.equals(animation.getArgs(), args))
				.forEach(animation -> ModelEngineHook.playAnimation(entity, animation.getId()));
	}

	public @Nullable FInput getInput(FInput.@NotNull ClickType type) {
		return inputs.get(type.toString());
	}

	public Map<String,FInput> getInputs() {
		return inputs;
	}

	public double getRotationMultiplier() {
		return rotationMultiplier;
	}

	public void setRotationMultiplier(double rotationMultiplier) {
		this.rotationMultiplier = rotationMultiplier;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	@Override
	public String toString() {
		return "{" +
				"className='" + this.getClass().getName() + '\'' +
				"rotationMultiplier=" + rotationMultiplier +
				", inputs=" + inputs +
				", widthRadius=" + widthRadius +
				", height=" + height +
				", internalName='" + internalName + '\'' +
				", bodyFix=" + bodyFix +
				", rotationDelta=" + rotationDelta +
				", maxSpeed=" + maxSpeed +
				", maxBackupSpeed=" + maxBackupSpeed +
				", destructable=" + destructable +
				", disableMeleeDamage=" + disableMeleeDamage +
				", disableProjectileDamage=" + disableProjectileDamage +
				", jumphiehgt=" + jumphiehgt +
				", driverSeat=" + driverSeat +
				", passagerOffset=" + passagerOffset +
				", animations=" + animations +
				", id=" + id +
				", material=" + material +
				", vehicleModel=" + vehicleModel +
				", center=" + center +
				", displayname='" + displayname + '\'' +
				", lore=" + lore +
				", acceleration=" + acceleration +
				", size=" + size +
				", canJump=" + canJump +
				", maxhealth=" + maxhealth +
				", price=" + price +
				", inShop=" + inShop +
				", sound='" + sound + '\'' +
				", playSoundsDriving=" + playSoundsDriving +
				", soundVolume=" + soundVolume +
				", trunksize=" + trunksize +
				", enableFuels=" + enableFuels +
				'}';
	}
}