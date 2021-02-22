package me.zombie_striker.qav.config;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.ModelSize;
import me.zombie_striker.qav.VehicleTypes;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.qamini.QAMini;
import me.zombie_striker.qav.vehicles.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VehicleLoader {

	/*public static void loadComplexFiles() {
		int count = 0;
		for (File f : Main.complexData.listFiles()) {
			try {
				if(loadComplexAdditoonFile(f))count++;
			} catch (Error | Exception e4) {
				Main.instance.getLogger().warning("Could not load file " + f.getName());
				e4.printStackTrace();
			}
		}
		if(!QAMini.verboseLogging){
			Main.instance.getLogger().info("-Loaded "+count+" Complex Additions");
		}
	}*/

	public static void loadVehicleFiles() {
		int count = 0;
		for (File f : Main.carData.listFiles()) {
			try {
				if(loadVehicleFile(f))count++;
			} catch (Error | Exception e4) {
				QualityArmoryVehicles.getPlugin().getLogger().warning("Could not load file " + f.getName());
				e4.printStackTrace();
			}
		}
		if(!Main.verboseLogging){
			QualityArmoryVehicles.getPlugin().getLogger().info("-Loaded "+count+" Vehicle types");
		}
	}

	/*public static boolean loadComplexAdditoonFile(File f) {
		FileConfiguration c = YamlConfiguration.loadConfiguration(f);
		Material material = Material.matchMaterial(c.getString("material"));
		int id = c.getInt("id");
		String name = c.getString("name");
		ComplexAddition additon = new ComplexAddition(material,id,name);
		
		if(c.contains("action.lmb"))
			additon.lmb = ComplexAdditionAction.getAction(ComplexAdditionAction.Actions.getByName(c.getString("action.lmb")));
		if(c.contains("action.left"))
			additon.left = ComplexAdditionAction.getAction(ComplexAdditionAction.Actions.getByName(c.getString("action.left")));

		if(c.contains("action.right"))
			additon.right = ComplexAdditionAction.getAction(ComplexAdditionAction.Actions.getByName(c.getString("action.right")));
		if(c.contains("action.up"))
			additon.forward = ComplexAdditionAction.getAction(ComplexAdditionAction.Actions.getByName(c.getString("action.up")));
		if(c.contains("action.down"))
			additon.back = ComplexAdditionAction.getAction(ComplexAdditionAction.Actions.getByName(c.getString("action.down")));
		Main.complexAdditions.add(additon);
		try {
			me.zombie_striker.qg.api.QualityArmory.registerNewUsedExpansionItem(additon.getBaseMaterial(),
					additon.getData());
		} catch (Error | Exception e) {
			QAMini.registeredItems.add(MaterialStorage.getMS(additon.getBaseMaterial(), additon.getData(), 0));
		}

		return true;
	}*/
	public static boolean loadVehicleFile(File f) {
		FileConfiguration c = YamlConfiguration.loadConfiguration(f);
		String name = c.getString("name");
		if (Main.verboseLogging)
			QualityArmoryVehicles.getPlugin().getLogger().info("Loading vehicle \"" + name + "\"");
		int id = c.getInt("id");
		VehicleTypes v = VehicleTypes.getTypeByName(c.getString("vehicle_type"));

		AbstractVehicle vehicle = null;
		switch (v) {
			case BOAT:
				vehicle = new AbstractBoat(name,id);
				break;
			case CAR:
				vehicle = new AbstractCar(name,id);
				break;
			case HELI:
				vehicle = new AbstractHelicopter(name,id);
				break;
			case PLANE:
				vehicle = new AbstractPlane(name,id);
				break;
			case TRAIN:
				vehicle = new AbstractTrain(name,id);
				break;
		}
		if (c.contains("canDeconstructByEnvironment")) {
			vehicle.setDeconstructable(c.getBoolean("canDeconstructByEnvironment"));
		}
		if (vehicle instanceof AbstractHelicopter) {
			//if (c.contains("descentSpeed"))
				//((AbstractHelicopter) vehicle).setDescentSpeed(c.getDouble("descentSpeed"));
		}

		if (c.contains("sound")) {
			vehicle.setSound(c.getString("sound"));
		}
		/*if (c.contains("set_seat_on_ground") && c.getBoolean("set_seat_on_ground")) {
			vehicle.setHasDriverseatOffset(true);
			vehicle.setDriverseatOffset(new Vector(0, 0, 0));
			vehicle.setSeatHeight(0);
		}
		if (c.contains("useHandForModel") && c.getBoolean("useHandForModel")) {
			vehicle.setUseHandsForModel(true);
		}*/

		/*if (c.contains("driverseat.Offset")) {
			vehicle.setDriverseatOffset(c.getVector("driverseat.Offset"));
			vehicle.setSeatHeight(vehicle.getDriverseatOffset().getY());
			vehicle.setDriverseatOffset(vehicle.getDriverseatOffset().setY(0));
		}
		if (c.getBoolean("driverseat.UseOffsetSeatFromModel") || (vehicle.getDriverseatOffset() != null
				&& (vehicle.getDriverseatOffset().getX() != 0 || vehicle.getDriverseatOffset().getZ() != 0
				|| vehicle.getSeatHeight() != (vehicle.getModelSize() == ModelSize.ADULT_ARMORSTAND_HAND || vehicle.getModelSize() == ModelSize.ADULT_ARMORSTAND_HEAD ? 2 : 1))))
			vehicle.setHasDriverseatOffset(true);*/

		if (c.contains("allowedInShop"))
			vehicle.setAllowInShop(c.getBoolean("allowedInShop"));
		/*if (c.contains("MaxFInputStates"))
			vehicle.setFInputMax(c.getInt("MaxFInputStates"));*/
		if (c.contains("playDrivingSounds"))
			vehicle.setPlayCustomSounds(c.getBoolean("playDrivingSounds"));
		if (c.contains("cost"))
			vehicle.setPrice(c.getInt("cost"));
		if (c.contains("jumpHeight"))
			vehicle.setJumpHeight(c.getDouble("jumpHeight"));
		if (c.contains("maxHealth")) {
			double health = c.getDouble("maxHealth");
			vehicle.setMaxHealth(health == -1 ? Integer.MAX_VALUE : health);
		}
		if (c.contains("soundVolume"))
			vehicle.setSoundVolume(Float.valueOf("" + c.getDouble("soundVolume")));
		if (c.contains("heightOffset"))
			vehicle.setHeight(c.getDouble("heightOffset"));
		if (c.contains("widthOffset"))
			vehicle.setWidthRadius(c.getDouble("widthOffset"));
		if (c.contains("vehicle_texture_material"))
			vehicle.setMaterial(Material.matchMaterial(c.getString("vehicle_texture_material")));
		if (c.contains("trunksize"))
			vehicle.setTrunkSize(c.getInt("trunksize"));
		if (c.contains("enablePlayerBodyDirectionFix"))
			vehicle.setBodyFix(c.getBoolean("enablePlayerBodyDirectionFix"));
		if (c.contains("ItemLore")) {
			List<String> lore = new ArrayList<>();
			for (String k : c.getStringList("ItemLore")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', k));
			}
			vehicle.setLore(lore);
		}
		if (c.contains("canJumpOnBlocks"))
			vehicle.setCanJump(c.getBoolean("canJumpOnBlocks"));
		/*if (c.contains("InputManager.keys")) {
			if (c.contains("InputManager.keys.LMB"))
				vehicle.setInputManagerLMB(c.getString("InputManager.keys.LMB"));
			if (c.contains("InputManager.keys.RMB"))
				vehicle.setInputManagerRMB(c.getString("InputManager.keys.RMB"));
			if (c.contains("InputManager.keys.F"))
				vehicle.setInputManagerF(c.getString("InputManager.keys.F"));
		} else if (c.contains("InputManager")) {
			vehicle.setInputManagerF(c.getString("InputManager"));
		}*/
		if (c.contains("RequiresFuel"))
			vehicle.setEnableFuel(c.getBoolean("RequiresFuel"));
		if (c.contains("TurnSpeedInRadians"))
			vehicle.setTurnRate(c.getDouble("TurnSpeedInRadians"));
		/*if (c.contains("useStaticTurning"))
			vehicle.setStaticTurning(c.getBoolean("useStaticTurning"));*/

		if(c.contains("model.ModelSize")){
			vehicle.setModelSize(ModelSize.valueOf(c.getString("model.ModelSize")));
		}else if (c.contains("increaseSize"))
			vehicle.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD);

		if (c.contains("baseAcceleration"))
			vehicle.setAccerlationSpeed(c.getDouble("baseAcceleration"));
		if (c.contains("maxAcceleration"))
			vehicle.setMaxSpeed(c.getDouble("maxAcceleration"));
		if (c.contains("maxReverseAcceleration"))
			vehicle.setMaxBackupSpeed(c.getDouble("maxReverseAcceleration"));
		if (c.contains("displayname"))
			vehicle.setDisplayname(c.getString("displayname"));
		if (c.contains("passagers")) {
			@SuppressWarnings("unchecked")
			List<Vector> list = (List<Vector>) c.getList("passagers");

			HashMap<Vector,Integer> sizes = new HashMap<Vector, Integer>();
			for(Vector v3 : list){
				double size = Math.min(2,v3.getY());
				double offset = v3.getY()-size;
				Vector v2 = v3.clone();
				v2.setY(offset);
				sizes.put(v2,(int)size);
			}

			vehicle.setPassagerSpots(sizes);
		}
		if (c.contains("stopProjectileDamage"))
			vehicle.setStopsProjectileDamage(c.getBoolean("stopProjectileDamage"));
		if (c.contains("stopMeleeDamage"))
			vehicle.setStopsMeleeDamage(c.getBoolean("stopMeleeDamage"));
		if (c.contains("center"))
			vehicle.setCenter(c.getVector("center"));

		/*if(c.contains("complexadditions")){
			for(String key : c.getConfigurationSection("complexadditions").getKeys(false)){
				Integer index = Integer.parseInt(key);
				String nameC = c.getString("complexadditions."+index+".name");
				Vector offset = c.getVector("complexadditions."+index+".offset");
				vehicle.setComplexAddition(index,offset,nameC);
			}
		}*/

		Main.vehicleTypes.add(vehicle);
		try {
			me.zombie_striker.qg.api.QualityArmory.registerNewUsedExpansionItem(vehicle.getMaterial(),
					vehicle.getItemData());
		} catch (Error | Exception e) {
			QAMini.registeredItems.add(MaterialStorage.getMS(vehicle.getMaterial(), vehicle.getItemData(), 0));
		}
		return true;
	}
}
