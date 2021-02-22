package me.zombie_striker.qav.api;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class QualityArmoryVehicles {

	private static Main main;

	public static void setPlugin(Main main1){
		main = main1;
	}
	public static Main getPlugin(){
		return main;
	}

	public static VehicleEntity getVehicleEntityByEntity(Entity entity){
		for(VehicleEntity ve : main.vehicles){
			if(ve!=null)
			if(ve.getDriverSeat()==entity || ve.getModelEntities().contains(entity) || ve.getPassagerSeats().contains(entity)){
				return ve;
			}
		}
		return null;
	}

	public static Vector rotateRelToCar(Entity e, Vector offset, boolean reverse) {
		VehicleEntity ve = getVehicleEntityByEntity(e);
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
		Vector newVal = null;
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

	@SuppressWarnings("deprecation")
	public static AbstractVehicle getVehicleByItem(MaterialStorage ms) {
		for (AbstractVehicle ve : Main.vehicleTypes) {
			if (ve.getMaterial() == ms.getMat())
				if (ve.getItemData() == ms.getData())
					return ve;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static AbstractVehicle getVehicleByItem(ItemStack is) {
		if (is == null)
			return null;
		int data = 0;
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

	public static boolean isVehicle(Entity e) {
		if (e == null || e.getCustomName() == null)
			return false;
		return (e.getCustomName().startsWith(Main.VEHICLEPREFIX));
	}

	public static boolean isPassager(Entity e) {
		if (e == null)
			return false;
		if (e.getCustomName() == null)
			return false;
		return e.getCustomName().startsWith(Main.PASSAGER_PREFIX);
	}

	public static boolean isWithinVehicle(Location to, VehicleEntity ve) {
		if(ve.getBoundingBox().intersects(to))
			return true;
		return false;
	}

	public static VehicleEntity spawnVehicle(AbstractVehicle ab, Player player) {
		VehicleEntity vehicleEntity = new VehicleEntity(ab,player.getLocation(),player.getUniqueId());
		return vehicleEntity;
	}
	public static VehicleEntity spawnVehicle(AbstractVehicle ab, Location location, Player player) {
		VehicleEntity vehicleEntity = new VehicleEntity(ab,location,player.getUniqueId());
		return vehicleEntity;
	}

	public static void setAddPassager(VehicleEntity vehicleEntity, Player clicker, int i) {
		Vector offset = QualityArmoryVehicles.rotateRelToCar(vehicleEntity.getModelEntity(),vehicleEntity.getType().getPassagerSpots().get(i),false);
	}	@Deprecated
	public static Location getPassagerOffsetLocation(ArmorStand base, AbstractVehicle cartype, int seatID) {
		Vector seatid = cartype.getPassagerSpots().get(seatID);
		return base.getLocation().clone().add(rotateRelToCar(base, seatid, false));
	}

	public static Location getPassagerOffsetLocation(Entity base, AbstractVehicle cartype, int seatID) {
		Vector seatid = cartype.getPassagerSpots().get(seatID);
		return base.getLocation().clone().add(rotateRelToCar(base, seatid, false));
	}
	public static Entity spawnPassager(VehicleEntity vehicleEntity, Location loc, int seatID) {
		Location offset = getPassagerOffsetLocation(vehicleEntity.getModelEntity(), vehicleEntity.getType(), seatID);
		double yOffset = vehicleEntity.getType().getPassagerSpots().get(seatID).getY();
		VehicleEntity ve = vehicleEntity;
		Entity passagerSeat = ve.spawnSeat(offset, yOffset, seatID);
		passagerSeat.setCustomName(Main.PASSAGER_PREFIX + seatID);
		ve.addPassager(seatID, passagerSeat);
		return passagerSeat;
	}

	public static List<AbstractVehicle> unlockedVehicles(Player player) {
		File playersFile = new File(Main.playerUnlock, player.getUniqueId().toString() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		List<AbstractVehicle> list = new ArrayList<AbstractVehicle>();
		for (String s : c.getStringList("unlockedVehicles")) {
			list.add(QualityArmoryVehicles.getVehicle(s));
		}
		return list;
	}

	public static void addUnlockedVehicle(Player player, AbstractVehicle ve) {
		File playersFile = new File(Main.playerUnlock, player.getUniqueId().toString() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		List<String> list = c.getStringList("unlockedVehicles");
		if (list == null)
			list = new ArrayList<String>();
		list.add(ve.getName());
		c.set("unlockedVehicles", list);
		try {
			c.save(playersFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void removeUnlockedVehicle(Player player, AbstractVehicle ve) {
		File playersFile = new File(Main.playerUnlock, player.getUniqueId().toString() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		List<String> list = c.getStringList("unlockedVehicles");
		if (list == null)
			list = new ArrayList<String>();
		list.remove(ve.getName());
		c.set("unlockedVehicles", list);
		try {
			c.save(playersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Object getPlayerData(Player player, String path){
		File playersFile = new File(Main.playerUnlock, player.getUniqueId().toString() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		return c.get(path);
	}
	public static void setPlayerData(Player player, String path, Object o){
		File playersFile = new File(Main.playerUnlock, player.getUniqueId().toString() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(playersFile);
		c.set(path, o);
		try {
			c.save(playersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<VehicleEntity> getOwnedVehicles(UUID player) {
		List<VehicleEntity> list = new ArrayList<VehicleEntity>();
		for (VehicleEntity ve : Main.vehicles) {
			if (ve.getOwner() != null && ve.getOwner().equals(player)) {
				list.add(ve);
			}
		}
		return list;
	}
}
