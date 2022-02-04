package me.zombie_striker.qav.api;

import me.zombie_striker.qav.api.events.VehicleSpawnEvent;
import me.zombie_striker.qav.customitemmanager.MaterialStorage;
import me.zombie_striker.qav.*;
import me.zombie_striker.qav.api.events.PlayerEnterQAVehicleEvent;
import me.zombie_striker.qav.hooks.model.Animation;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"deprecation","unused"})
public class QualityArmoryVehicles {

	private static Main main;

	public static void setPlugin(Main main){
		QualityArmoryVehicles.main = main;
	}
	public static Main getPlugin(){
		return main;
	}

	public static VehicleEntity getVehicleEntityByEntity(Entity entity){
		for(VehicleEntity ve : Main.vehicles){
			if(ve!=null && entity instanceof ArmorStand)
				if(ve.getDriverSeat()==entity || ve.getModelEntities().contains(entity) || ve.getPassagerSeats().contains(entity)){
					return ve;
				}
		}
		return null;
	}

	public static Vector rotateRelToCar(Entity e, Vector offset, boolean reverse) {
		VehicleEntity ve = getVehicleEntityByEntity(e);
		Validate.notNull(ve);
		return rotateRelToCar(ve, ve.getModelEntity(), offset, reverse);
	}

	public static Vector rotateRelToCar(ArmorStand model, Vector offset, boolean reverse) {
		VehicleEntity ve = getVehicleEntityByEntity(model);
		return rotateRelToCar(ve, model, offset, reverse);
	}

	public static Vector rotateRelToCar(VehicleEntity ve, ArmorStand model, Vector offset, boolean reverse) {
		if (model == null)
			return new Vector(0, 0, 1);
		EulerAngle ea = model.getHeadPose();
		double cos = Math.cos(ve.getAngleRotation() - Main.YOFFSET);
		double sin = Math.sin(ve.getAngleRotation() - Main.YOFFSET);
		Vector newVal;
		if (ea.getX() == 0) {
			newVal = new Vector((offset.getX() * cos) - (offset.getZ() * sin), offset.getY(),
					(offset.getZ() * cos) + (offset.getX() * sin));
		} else {
			double shrinkByForY = Math.cos(ea.getX());
			double yOffset = Math.sin(ea.getX());
			double xzdistance = Math.sqrt((offset.getX() * offset.getX()) + (offset.getZ() * offset.getZ()));
			double y = xzdistance * yOffset;
			newVal = new Vector(((offset.getX() * cos) - (offset.getZ() * sin)) * shrinkByForY, offset.getY() + y,
					((offset.getZ() * cos) + (offset.getX() * sin)) * shrinkByForY);
		}
		if (reverse)
			newVal.multiply(-1);
		return newVal;
	}

	public static boolean isVehicleByItem(ItemStack is) {
		return getVehicleByItem(is) != null;
	}

	public static AbstractVehicle getVehicleByItem(MaterialStorage ms) {
		for (AbstractVehicle ve : Main.vehicleTypes) {
			if (ve.getMaterial() == ms.getMat())
				if (ve.getItemData() == ms.getData())
					return ve;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static @Nullable AbstractVehicle getVehicleByItem(ItemStack is) {
		if (is == null)
			return null;
		int data;
		try {
			data = is.getItemMeta().getCustomModelData();
		} catch (Error | Exception e4) {
			data = is.getDurability();
		}
		for (AbstractVehicle ve : Main.vehicleTypes) {
			if (ve.getMaterial() == is.getType())
				if (ve.getItemData() == data)
					return ve;
		}
		return null;
	}


	public static ItemStack getVehicleItemStack(AbstractVehicle type) {
		return ItemFact.getCarItem(type);
	}

	public static VehicleEntity getVehiclePlayerLookingAt(Player player){
		for(VehicleEntity ve : Main.vehicles){
			if(ve.getBoundingBox().intersects(player.getEyeLocation(),player.getLocation().getDirection(),6)){
				return ve;
			}
		}
		return null;
	}



	public static AbstractVehicle getVehicle(String name) {
		for (AbstractVehicle e : Main.vehicleTypes) {
			if (e.getName().equalsIgnoreCase(name)) {
				return e;
			}
		}
		return null;
	}

	public static boolean isQAVEntity(Entity e) {
		return isVehicle(e) || isPassager(e);
	}

	public static boolean isVehicle(Entity e) {
		if (e == null || e.getCustomName() == null)
			return false;
		return (e.getCustomName().startsWith(Main.VEHICLEPREFIX)) || (e.getCustomName().startsWith(Main.MODEL_PREFIX));
	}

	public static boolean isPassager(Entity e) {
		if (e == null)
			return false;
		if (e.getCustomName() == null)
			return false;
		return e.getCustomName().startsWith(Main.PASSAGER_PREFIX);
	}

	public static boolean isWithinVehicle(Location to, VehicleEntity ve) {
		return ve.getBoundingBox().intersects(to);
	}

	public static VehicleEntity spawnVehicle(UnlockedVehicle ab,@Nullable Player player) {
		return spawnVehicle(ab.getVehicleType(), player);
	}
	public static VehicleEntity spawnVehicle(AbstractVehicle ab,@Nullable Player player) {
		return spawnVehicle(ab,player.getLocation(),player);
	}
	public static VehicleEntity spawnVehicle(AbstractVehicle ab, Location location,@Nullable Player player) {
		VehicleEntity vehicleEntity = new VehicleEntity(ab,location.getBlock().getRelative(BlockFace.UP).getLocation(),player != null ? player.getUniqueId() : null);
		VehicleSpawnEvent event =  new VehicleSpawnEvent(player, vehicleEntity);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCanceled()) {
			return null;
		}
		vehicleEntity.spawn();
		return vehicleEntity;
	}

	public static void setAddPassager(VehicleEntity vehicleEntity, Player clicker, int i) {
		spawnPassager(vehicleEntity,i).setPassenger(clicker);
	}

	@SuppressWarnings("deprecation")
	public static void addPlayerToCar(VehicleEntity ve, Player player, boolean allowDriverSeat) {
		PlayerEnterQAVehicleEvent event = new PlayerEnterQAVehicleEvent(ve, player);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCanceled()) {
			return;
		}
		if(ve==null)
			return;
		if(ve.getDriverSeat()==null){
			return;
		}
		if ((ve.getDriverSeat().getPassenger() == null && allowDriverSeat)
				&& (!Main.requirePermissionToDrive || PermissionHandler.canDrive(player, ve.getType()))) {
			if (ve.getOwner() == null && Main.setOwnerIfNoneExist) {
				ve.setOwner(player.getUniqueId());
				if (MessagesConfig.MESSAGE_NOW_OWN_CAR.length() > 1) {
					player.sendMessage(MessagesConfig.MESSAGE_NOW_OWN_CAR.replace("%car%",
							ChatColor.stripColor(ve.getType().getDisplayname())));
				}
			}
			ve.getType().playAnimation(ve, Animation.AnimationType.ENTER, "driver");
			ve.getDriverSeat().setPassenger(player);
		} else {
			if (ve.getPassagers().size() < ve.getType().getPassagerSpots().size()) {
				int seatId = ve.getFirstSeat();
				if (seatId < 0)
					return;

				ve.getType().playAnimation(ve, Animation.AnimationType.ENTER, seatId + "");
				setAddPassager(ve, player, seatId);
			}
		}
	}

	public static Location getPassagerOffsetLocation(ArmorStand base, AbstractVehicle cartype, int seatID) {
		return getPassagerOffsetLocation((Entity) base,cartype, seatID);
	}

	public static Location getPassagerOffsetLocation(Entity base, AbstractVehicle cartype, int seatID) {
		Vector seatid = cartype.getPassagerSpots().get(seatID);
		return base.getLocation().clone().add(rotateRelToCar(base, seatid, false));
	}
	public static Entity spawnPassager(VehicleEntity vehicleEntity, int seatID) {
		Location offset = getPassagerOffsetLocation(vehicleEntity.getModelEntity(), vehicleEntity.getType(), seatID);
        Entity passagerSeat = vehicleEntity.spawnSeat(offset.clone().subtract(0,1,0), seatID);
		passagerSeat.setCustomName(Main.PASSAGER_PREFIX + seatID);
		vehicleEntity.addPassager(seatID, passagerSeat);
		return passagerSeat;
	}

	@SuppressWarnings("unchecked")
	public static List<UnlockedVehicle> unlockedVehicles(OfflinePlayer player) {
		File playersFile = new File(Main.playerUnlock, player.getUniqueId() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		return (List<UnlockedVehicle>) c.getList("unlockedVehicles", new ArrayList<>());
	}

	@SuppressWarnings("unchecked")
	public static void addUnlockedVehicle(Player player, UnlockedVehicle ve) {
		File playersFile = new File(Main.playerUnlock, player.getUniqueId() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		List<UnlockedVehicle> list = (List<UnlockedVehicle>) c.getList("unlockedVehicles", new ArrayList<>());
		list.add(ve);
		c.set("unlockedVehicles", list);
		try {
			c.save(playersFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static File getUnlockedVehiclesFile(Player player) {
		return new File(Main.playerUnlock, player.getUniqueId() + ".yml");
	}

	@SuppressWarnings("unchecked")
	public static UnlockedVehicle findUnlockedVehicle(Player player, AbstractVehicle ve) {
		File file = getUnlockedVehiclesFile(player);
		FileConfiguration c = YamlConfiguration.loadConfiguration(file);
		List<UnlockedVehicle> list = (List<UnlockedVehicle>) c.getList("unlockedVehicles", new ArrayList<>());
		Optional<UnlockedVehicle> first = list.stream()
				.filter(i -> i.getVehicleType().getName().equals(ve.getName()))
				.findFirst();
		return first.orElse(null);
	}

	@SuppressWarnings("unchecked")
	public static void removeUnlockedVehicle(Player player, AbstractVehicle ve) {
		File playersFile = getUnlockedVehiclesFile(player);
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		List<UnlockedVehicle> list = new ArrayList<>((List<UnlockedVehicle>) c.getList("unlockedVehicles", new ArrayList<>()));
		UnlockedVehicle unlockedVehicle = findUnlockedVehicle(player, ve);
		list.remove(unlockedVehicle);
		c.set("unlockedVehicles", list);
		try {
			c.save(playersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void removeUnlockedVehicle(Player player, UnlockedVehicle ve) {
		File file = getUnlockedVehiclesFile(player);
		FileConfiguration c = YamlConfiguration.loadConfiguration(file);
		List<UnlockedVehicle> list = (List<UnlockedVehicle>) c.getList("unlockedVehicles", new ArrayList<>());
		list.remove(ve);
		c.set("unlockedVehicles", list);
		try {
			c.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Object getPlayerData(Player player, String path){
		File playersFile = new File(Main.playerUnlock, player.getUniqueId() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		return c.get(path);
	}
	public static void setPlayerData(Player player, String path, Object o){
		File playersFile = new File(Main.playerUnlock, player.getUniqueId() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		c.set(path, o);
		try {
			c.save(playersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<VehicleEntity> getOwnedVehicles(UUID player) {
		List<VehicleEntity> list = new ArrayList<>();
		for (VehicleEntity ve : Main.vehicles) {
			if (ve.getOwner() != null && ve.getOwner().equals(player)) {
				list.add(ve);
			}
		}
		return list;
	}
}
