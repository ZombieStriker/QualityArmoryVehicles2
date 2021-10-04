package me.zombie_striker.qav.vehicles;

import com.comphenix.protocol.events.PacketEvent;
import me.zombie_striker.qav.*;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.finput.FInput;
import me.zombie_striker.qav.fuel.FuelItemStack;
import me.zombie_striker.qav.util.BlockCollisionUtil;
import me.zombie_striker.qav.util.HotbarMessager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractVehicle {
	protected static final double STOPPED = 0.009999999999999247;
	protected static final double pitchIncrement = Math.PI / 60;
	protected static final double maxAngle = Math.PI / 4;

	private Map<FInput.ClickType,FInput> inputs = new HashMap<>();
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
	private double jumphiehgt = 0.5;
	private HashMap<Vector, Integer> passagerOffset = new HashMap<>();

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

	public boolean handleFuel(VehicleEntity ve, PacketEvent event) {
		boolean shouldReturn = true;

		if (this.enableFuel()) {
			FuelItemStack.updateFuel(ve);
			if (ve.getFuel() <= 0) {
				event.setCancelled(true);
				shouldReturn = false;
				try {
					if (event.getPlayer() != null)
						if (!Main.useChatForMessage) {
							try {
								HotbarMessager.sendHotBarMessage(event.getPlayer(), MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
							} catch (Error | Exception ignored) {
							}
						} else {
							event.getPlayer().sendMessage(MessagesConfig.MESSAGE_HOTBAR_OUTOFFUEL);
						}
				} catch (Error | Exception ignored) {
				}
			} else {
				ve.setFuel(ve.getFuel() - 1);
			}
		}

		return shouldReturn;
	}


	public abstract void handleTurnLeft(VehicleEntity ve, PacketEvent event);

	public abstract void handleTurnRight(VehicleEntity ve, PacketEvent event);

	public abstract void handleSpeedIncrease(VehicleEntity ve, PacketEvent event);

	public abstract void handleSpeedDecrease(VehicleEntity ve, PacketEvent event);

	public abstract void handleSpace(VehicleEntity ve, PacketEvent event);

	public abstract void tick(VehicleEntity vehicleEntity);

	public ItemStack getModel() {
		if (vehicleModel == null) {
			vehicleModel = ItemFact.getCarItem(this);
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

	protected double getRotationDelta() {
		return rotationDelta;
	}

	protected double getMaxSpeed() {
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

	public void basicDirections(VehicleEntity vehicleEntity, boolean jump, boolean aquatic, boolean gravity, boolean planeFlying) {
		Location block = vehicleEntity.getDriverSeat().getLocation().subtract(0, 0.4, 0);
		Material material = BlockCollisionUtil.getMaterial(block);

		if (Main.customSpeedModifier.containsKey(material)) {
			vehicleEntity.setSpeed(vehicleEntity.getSpeed() * Main.customSpeedModifier.getOrDefault(material,1.0));
		}

		if (vehicleEntity.getSpeed() > 0) {
			if(planeFlying){
				if(vehicleEntity.getDirectionYheight()<0){
					vehicleEntity.setSpeed(vehicleEntity.getSpeed() + 0.01);
				}else{
					vehicleEntity.setSpeed(vehicleEntity.getSpeed() - 0.01);
				}
			}else {
				vehicleEntity.setSpeed(vehicleEntity.getSpeed() - 0.01);
			}
		}
		if (vehicleEntity.getSpeed() < 0) {
			vehicleEntity.setSpeed(vehicleEntity.getSpeed() + 0.01);
		}

		if (vehicleEntity.getSpeed() == STOPPED) {
			vehicleEntity.setSpeed(0.0);
		}

		Vector velocity = vehicleEntity.getDirection().clone();
		velocity.normalize().multiply(vehicleEntity.getSpeed());

		if (planeFlying) {
			double velDir = velocity.length();
			velocity.setY(vehicleEntity.getDirectionYheight());
			if (velocity.length() != 0.0) velocity.normalize();
			velocity.multiply(velDir);

			double pitch = vehicleEntity.getModelEntity().getHeadPose().getX();
			if (vehicleEntity.getSpeed() <= 0.01 && !vehicleEntity.isOnGround()) {
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
			if (vehicleEntity.getDriverSeat().getLocation().getBlock().getType() == Material.WATER) {
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
					velocity.setY(0.2);
					goingUp = true;
				}
			}
			if (!goingUp) {
				Location movingTo2 = vehicleEntity.getCenter().clone().add(vehicleEntity.getDirection().clone());
				if (BlockCollisionUtil.isSolidAt(movingTo2)) {
					if (!BlockCollisionUtil.isSolidAt(movingTo2.clone().add(0, 1, 0))) {
						velocity.setY(0.2);
						goingUp = true;
					}
				}
			}
			if (!goingUp) {
				Location movingTo3 = vehicleEntity.getCenter().clone().subtract(vehicleEntity.getDirection().clone().multiply(vehicleEntity.getBoundingBox().getWidth()));
				if (BlockCollisionUtil.isSolidAt(movingTo3)) {
					if (!BlockCollisionUtil.isSolidAt(movingTo3.clone().add(0, 1, 0))) {
						velocity.setY(0.2);
						goingUp = true;
					}
				}
			}
			if (!goingUp) {
				Location movingTo4 = vehicleEntity.getCenter().clone().subtract(vehicleEntity.getDirection().clone());
				if (BlockCollisionUtil.isSolidAt(movingTo4)) {
					if (!BlockCollisionUtil.isSolidAt(movingTo4.clone().add(0, 1, 0))) {
						velocity.setY(0.2);
					}
				}
			}
		}

		applyModifiers(vehicleEntity, velocity);
		vehicleEntity.getDriverSeat().setVelocity(velocity);
		handleOtherStands(vehicleEntity,velocity);
	}

	public void applyModifiers(VehicleEntity entity, Vector vector) {
		// TODO: Apply backup speed
	}

	@SuppressWarnings("deprecation")
	public void handleOtherStands(VehicleEntity ve, Vector velocity) {
		for (Entity e : ve.getPassagerSeats()) {
			Location offset = ve.getDriverSeat().getLocation().clone()
					.add(QualityArmoryVehicles.rotateRelToCar(ve, (ArmorStand) ve.getDriverSeat(),
							getPassagerSpots().get(
									Integer.parseInt(e.getCustomName().split(Main.PASSAGER_PREFIX)[1])),
							false));

			offset.add(ve.getDriverSeat().getVelocity());
			offset.subtract(0,0.6,0);

			Vector newVelo = velocity.clone();
			double offDis = offset.distanceSquared(e.getLocation());
			if (offDis > 1) {
				final Entity rider = e.getPassenger();

				if (rider == null) {
					e.remove();
					continue;
				}

				e.eject();
				e.teleport(offset);
				rider.teleport(offset);
				e.setPassenger(rider);

			}

			Vector distance = offset.toVector().clone().subtract(e.getLocation().toVector());
			newVelo.add(distance);
			e.setVelocity(newVelo);
		}
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

	public @Nullable FInput getInput(FInput.ClickType type) {
		return inputs.get(type);
	}

	public Map<FInput.ClickType, FInput> getInputs() {
		return inputs;
	}

	@Override
	public String toString() {
		return "AbstractVehicle{" +
				"inputs=" + inputs +
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
				", passagerOffset=" + passagerOffset +
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
