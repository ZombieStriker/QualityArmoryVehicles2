package me.zombie_striker.qav.config;

import com.cryptomorin.xseries.ReflectionUtils;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.ModelSize;
import me.zombie_striker.qav.VehicleTypes;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("unused")
public class VehicleYML {

	private final File f;
	private final FileConfiguration c;

	//private static final String LET_USERS_EDIT = "AllowUserModifications";

	private boolean needsUpdate = false;

	public static boolean exists(String name) {
		File f = new File(Main.carData, "default_" + name);
		return f.exists();
	}



	public boolean contains(String path) {
		return c.contains(path);
	}

	public Object get(String path) {
		return c.get(path);
	}

	public VehicleYML set(String name, Object v) {
		return set(false, name,v);
	}
	public VehicleYML set(boolean force, String name, Object v) {
		long lastmodifiedFile = f.lastModified();
		long lastmodifiedInternal = contains("lastModifiedByQA") ? (long) get("lastModifiedByQA") :  (  contains("AllowUserModifications") && c.getBoolean("AllowUserModifications") ? 0 : System.currentTimeMillis());
		if(!force && lastmodifiedFile-lastmodifiedInternal > 5000) {
			return this;
		}
		if (!contains(name) || !get(name).equals(v)) {
			c.set(name, v);
			needsUpdate = true;
		}
		return this;
	}

	public void verify(String name, Object v) {
		if (!contains(name)) {
			c.set(name, v);
			needsUpdate = true;
		}
	}




	public static VehicleYML registerVehicle(VehicleTypes type, String name, int id) {
		File f = new File(Main.carData, "default_" + name + ".yml");
		VehicleYML v = new VehicleYML(f);
		v.setType(type);
		if(v.contains("AllowUserModifications")){
			boolean allow = (boolean) v.get("AllowUserModifications");
			v.set(true,"AllowUserModifications",null);
			if(!allow)
			v.putTimeStamp();
		}
		v.setName(name);
		v.setID(id);
		return v;
	}

	public static @NotNull VehicleYML loadVehicle(String name) {
		File f = new File(Main.carData, "default_" + name + ".yml");
		return new VehicleYML(f);
	}

	public VehicleYML(File f) {
		this.f = f;
		this.c = YamlConfiguration.loadConfiguration(f);
	}

	public File getFile() {
		return f;
	}

	public VehicleYML setDeconstructable(boolean b) {
		set("canDeconstructByEnvironment", b);
		return this;
	}

	public VehicleYML setDriverseatOffset(Vector vector) {
		set("driverseat.Offset", vector);
		return this;
	}

	public VehicleYML setCanJumpBlocks(boolean b) {
		set("canJumpOnBlocks", b);
		return this;
	}

	public VehicleYML setKeyInputManagerF(String name) {
		set("InputManager.keys.F", name);
		return this;
	}

	public VehicleYML setKeyInputManagerLMB(String name) {
		set("InputManager.keys.LMB", name);
		return this;
	}

	public VehicleYML setKeyInputManagerRMB(String name) {
		set("InputManager.keys.RMB", name);
		return this;
	}

	public VehicleYML setLore(String... lore) {
		set("ItemLore", lore);
		return this;
	}

	public VehicleYML setUseHandsForModel(boolean b) {
		set("useHandForModel", b);
		return this;
	}
	public VehicleYML setMaxForwardSpeed(double speed) {
		set("maxAcceleration", speed);
		return this;
	}

	public VehicleYML setMaxBackupSpeed(double speed) {
		set("maxReverseAcceleration", speed);
		return this;
	}

	public VehicleYML setDrivingSounds(boolean b) {
		set("playDrivingSounds", b);
		return this;
	}

	public VehicleYML setCost(int cost) {
		set("cost", cost);
		return this;
	}

	public VehicleYML enablePlayerBodyFix(boolean b) {
		set("enablePlayerBodyDirectionFix", b);
		return this;
	}

	public VehicleYML setMaxFInputStates(int b) {
		set("MaxFInputStates", b);
		return this;
	}

	public VehicleYML setBaseAcceleration(double speed) {
		set("baseAcceleration", speed);
		return this;
	}

	public VehicleYML setStaticTurning(boolean turn) {
		set("useStaticTurning", turn);
		return this;
	}

	public VehicleYML setTurnSpeed(double speed) {
		set("TurnSpeedInRadians", speed);
		return this;
	}

	public VehicleYML setWidth(double width) {
		set("widthOffset", width);
		return this;
	}

	public VehicleYML setHeight(double height) {
		set("heightOffset", height);
		return this;
	}

	public VehicleYML setRequireFuel(boolean b) {
		set("RequiresFuel", b);
		return this;
	}

	public VehicleYML setMaxHealth(double health) {
		set("maxHealth", health);
		return this;
	}
	public VehicleYML setJumpHeight(double height) {
		set("jumpHeight", height);
		return this;
	}

	public VehicleYML setSound(String name) {
		set("sound", name);
		return this;
	}

	public boolean needsUpdate() {
		return needsUpdate;
	}

	public void save() {
		verifyNeededTags();
		if (needsUpdate) {
			try {
				putTimeStamp();
				c.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void verifyNeededTags() {
		verify("TurnSpeedInRadians", Math.PI / 80);
		verify("useStaticTurning", true);
		verify("activation_radius", 2);
		verify("vehicle_texture_material", ReflectionUtils.supports(14) ? Material.RABBIT_HIDE.name() : Material.DIAMOND_AXE.name());
		verify("RequiresFuel", false);
		verify("trunksize", 9);
		verify("widthOffset", 1.5);
		verify("maxHealth", 50);
		verify("heightOffset", 2);
		verify("allowedInShop", true);
		verify("cost", 1000);
		verify("maxAcceleration", 1.1);
		verify("baseAcceleration", 0.065);
		verify("maxReverseAcceleration", 0.45);
		verify("canJumpOnBlocks", true);
		verify("jumpHeight", 0.55);
		verify("model.ModelSize", ModelSize.BABY_ARMORSTAND_HEAD.name());
		verify("InputManager.keys.F", "none");
		verify("InputManager.keys.LMB", "none");
		verify("InputManager.keys.RMB", "none");
		verify("canDeconstructByEnvironment", true);
		verify("rotationMultiplier", 1);

		if (get("vehicle_type").equals(VehicleTypes.HELI.getName())) {
			verify("descentSpeed",-0.1);
			try {
				verify("sound", Sound.ENTITY_PLAYER_ATTACK_SWEEP.name().toLowerCase().replace("_", "."));
			} catch (Error | Exception ignored) {
			}
		} else if (get("vehicle_type").equals(VehicleTypes.CAR.getName())
				|| get("vehicle_type").equals(VehicleTypes.PLANE.getName())) {
			verify("sound", "driving");
		} else {
			verify("sound", "null");
		}
		verify("soundVolume", 1);
		verify("driverseat.UseOffsetSeatFromModel", false);
		verify("driverseat.Offset", new Vector(0, 1, 0));
	}

	public VehicleYML setComplexAdditionAt(int index, Vector offset, String complexName){
		set("complexadditions."+index+".name", complexName);
		set("complexadditions."+index+".offset", offset);
		return this;

	}


	public VehicleYML setModelSize(ModelSize modelSize) {
		set("model.ModelSize", modelSize.name());
		return this;
	}

	public VehicleYML setMaterial(Material m) {
		set("vehicle_texture_material", m.name());
		return this;
	}

	public VehicleYML setType(VehicleTypes type) {
		set("vehicle_type", type.getName());
		return this;
	}

	public VehicleYML setTrunkSize(int size) {
		set("trunksize", size);
		return this;
	}

	public VehicleYML setName(String name) {
		set("name", name);
		return this;
	}

	public VehicleYML setDisplayname(String name) {
		set("displayname", name);
		return this;
	}

	public void setID(int id) {
		set("id", id);
	}

	public VehicleYML setPassagerLocations(Vector... locs) {
		set("passagers", Arrays.asList(locs));
		return this;
	}

	public VehicleYML setStopProjectileDamage(boolean b) {
		set("stopProjectileDamage", b);
		return this;
	}

	public VehicleYML setStopMeleeDamage(boolean b) {
		set("stopMeleeDamage", b);
		return this;
	}

	public VehicleYML setCenter(Vector center) {
		set("center", center);
		return this;
	}

	public VehicleYML setFrontVectorOffset(double offset) {
		set("front_vector_offset", offset);
		return this;
	}

	public VehicleYML setBackVectorOffset(double offset) {
		set("back_vector_offset", offset);
		return this;
	}

	public VehicleYML setActivationRadius(double offset) {
		set("activation_radius", offset);
		return this;
	}

	public void putTimeStamp(){
		c.set("lastModifiedByQA", System.currentTimeMillis()+5000);
		needsUpdate = true;
	}
}
