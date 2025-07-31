package me.zombie_striker.qav;

import com.cryptomorin.xseries.reflection.XReflection;
import com.google.common.io.Files;
import com.tcoded.folialib.FoliaLib;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.config.VehicleLoader;
import me.zombie_striker.qav.customitemmanager.AbstractItem;
import me.zombie_striker.qav.customitemmanager.CustomItemManager;
import me.zombie_striker.qav.customitemmanager.pack.MultiVersionPackProvider;
import me.zombie_striker.qav.customitemmanager.pack.StaticPackProvider;
import me.zombie_striker.qav.customitemmanager.qav.versions.V1_13.CustomVehicleItem;
import me.zombie_striker.qav.debugmanager.DebugManager;
import me.zombie_striker.qav.finput.FInputManager;
import me.zombie_striker.qav.finput.inputs.*;
import me.zombie_striker.qav.fuel.FuelItemStack;
import me.zombie_striker.qav.fuel.RepairItemStack;
import me.zombie_striker.qav.hooks.ProtectionHandler;
import me.zombie_striker.qav.hooks.QualityArmoryListener;
import me.zombie_striker.qav.hooks.QuickShopHook;
import me.zombie_striker.qav.hooks.implementation.WorldGuardHook;
import me.zombie_striker.qav.hooks.model.ModelEngineHook;
import me.zombie_striker.qav.input.LegacyInputListener;
import me.zombie_striker.qav.input.ModernInputListener;
import me.zombie_striker.qav.nms.NMSUtil;
import me.zombie_striker.qav.premium.PremiumHandler;
import me.zombie_striker.qav.qamini.EconHandler;
import me.zombie_striker.qav.qamini.ParticleHandlers;
import me.zombie_striker.qav.qamini.QAMini;
import me.zombie_striker.qav.util.ForksUtil;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin {

	public static FoliaLib foliaLib;

	public static boolean debugWithCommand = false;
	public static boolean debug = false;
	public static boolean verboseLogging = false;

	public static String prefix = "&6&lQAVehicles &f&l»&7";

	public static QAMini minihandler = null;// = new QAMini();


	public static boolean ENABLE_FILE_CREATION = true;

	public static String PASSAGER_PREFIX = "QA-Passager=";
	public static String MODEL_PREFIX = "QA-Model=";
	public static boolean enableGarage=false;
	public static boolean enableGarageCallback=false;
	public static boolean useChatForMessage=false;
	public static String VEHICLEPREFIX = "(QAV)";
	public static File carData;
	public static final double YOFFSET = Math.PI * 3 / 2;// Math.PI*3/2;//Math.PI * 69 / 64;


	public static boolean cleanVehiclesOnEmpty = false;
	public static List<String> blacklistedWorlds = new ArrayList<>();

	public static HashMap<Material, Double> customSpeedModifier = new HashMap<>();
	public static File playerUnlock;
	public static File items;
	public static File fuelYML;
	public static File repairYML;
	public static RepairItemStack repairItem;
	public static int maxYheightForVehicles = 256;
	public static boolean enableVehicleLimiter = false;
	public static boolean allowVehiclePickup = true;
	public static boolean setOwnerOnPlacement = true;
	public static boolean disableAllFuelChecks = false;
	public static boolean enableVehicleDamage = true;
	public static boolean disableCreativeCloning = false;
	public static boolean removeVehicleOnDismount = false;
	public static boolean removeVehicleONLEAVE = false;
	public static boolean destroyVehicleONLEAVE = false;
	public static boolean destroyOnWater = true;
	public static boolean swapEndermiteWithChicken = false;
	public static boolean garageFuel = true;
	public static boolean enableVehiclePlayerCollision = true;
	public static boolean enableCrossVehicleCollision = false;
	public static boolean requirePermissionToDrive = false;
	public static boolean setOwnerIfNoneExist = false;
	public static boolean enableTrunks = true;
	public static boolean antiCheatHook = false;
	public static boolean freezeOnDestroy = false;
	public static boolean bypassCoalInCreative = true;
	public static boolean sendActionBarOnMove = true;
	public static boolean enableShopCooldown = false;
	public static boolean onlyPublicVehicles = false;
	public static boolean enable_RequirePermToBuyVehicle = false;
	public static boolean useHeads = true;
	private static File vehicledatayml;
	// Roughly 1 coal for 25 seconds. 64 coal for ~26 minutes
	private static boolean USE_MANUAL_13 = false;
	public static boolean useDamage = false;
	public static boolean separateModelAndDriver = false;
	public static boolean modernPlaneMovements = true;





	public static List<AbstractVehicle> vehicleTypes = new ArrayList<>();
	public static List<VehicleEntity> vehicles = new ArrayList<>();

	public static void DEBUG(String message) {
		DebugManager.sendDebugMessages(message);
	}

	public static void DEBUG(Object @NotNull ... message) {
		for (Object o : message) {
			DebugManager.sendDebugMessages(o.toString());
		}
	}

	public static boolean isVersionHigherThan(int first, int second) {
		return QAMini.isVersionHigherThan(first, second);
	}

	@Override
	public void onLoad() {
		try {
			if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null)
				WorldGuardHook.register();
		} catch (Error | Exception ignored) {}
	}

	@Override
	public void onEnable() {
		foliaLib = new FoliaLib(this);

		ConfigurationSerialization.registerClass(UnlockedVehicle.class);
		ConfigurationSerialization.registerClass(VehicleEntity.class);

		if (this.getDescription().getVersion().contains("SNAPSHOT")) {
			this.getLogger().warning(String.format("You are using a SNAPSHOT version of %s(%s). This is not recommended for production use.", this.getName(), this.getDescription().getVersion()));
		} else if (!PremiumHandler.isPremium()) {
			this.getLogger().warning("You are using a leaked version of the plugin. Please consider buying the premium version.");
		}

		MetricsLite metrics = new MetricsLite(this,12753);
		metrics.addCustomChart(
				new MetricsLite.SimplePie(
						"premium",
						() -> PremiumHandler.isPremium() ? "true" : "false"
				)
		);

		carData = new File(getDataFolder(),"vehicles");
		if(!carData.exists()){
			carData.mkdirs();
		}

		initVals();

		ParticleHandlers.initValues();
		if (XReflection.supports(9)) {
			FInputManager.init(this);
			new FMininukeBomber();
			new FCarHonk();
			new FSiren();
			new F40mmLauncher();
			new FTNTBomber();
		}

		try {
			new FShootBullet();
		} catch (Error | Exception re325) {
		}

		if (ENABLE_FILE_CREATION) {
			AbstractItem item = null;
			if (!isVersionHigherThan(1, 14) || USE_MANUAL_13) {
				item = new CustomVehicleItem();
			} else {
				item = new me.zombie_striker.qav.customitemmanager.qav.versions.V1_14.CustomVehicleItem();
			}
			CustomItemManager.registerItemType("vehicles", item);
			item.initItems(getDataFolder());
		}

		if (!QAMini.overrideURL) {
			this.getConfig().set("QAMini.resourcepackurl", CustomItemManager.getResourcepackProvider().serialize());
			this.saveConfig();
		} else {
			if (!getConfig().contains("QAMini.resourcepackurl")) {
				getConfig().set("QAMini.resourcepackurl", CustomItemManager.getResourcepackProvider().serialize());
				this.saveConfig();
			} else {
				if (getConfig().get("QAMini.resourcepackurl") instanceof String)
					CustomItemManager.setResourcepack(new StaticPackProvider(getConfig().getString("QAMini.resourcepackurl")));
				else {
					ConfigurationSection packSection = getConfig().getConfigurationSection("QAMini.resourcepackurl");
					if (packSection != null) {
						if (packSection.contains("21")) {
							packSection.set("21-4", packSection.getString("21"));
							packSection.set("21", null);
							this.saveConfig();
						}

						CustomItemManager.setResourcepack(new MultiVersionPackProvider(packSection));
					}
				}
			}
		}

		NMSUtil.init();

		loadComplexParts(false);
		loadVehicles(false);

		QAVCommand command = new QAVCommand();
		getCommand("QualityArmoryVehicles").setExecutor(command);
		getCommand("QualityArmoryVehicles").setTabCompleter(command);
		if (enableGarage)
			getCommand("garage").setExecutor(new GarageCommand());

		Bukkit.getPluginManager().registerEvents(new QAVListener(this), this);

		FileConfiguration dataconfig = YamlConfiguration.loadConfiguration(vehicledatayml);

		if(dataconfig.contains("data")) {
			for (VehicleEntity ve : ((List<VehicleEntity>) dataconfig.get("data"))) {
				if (ve != null && ve.getDriverSeat() != null)
					vehicles.add(ve);
			}

			this.getLogger().info("Successfully loaded " + vehicles.size() + " spawned vehicles.");
		}

		Main.foliaLib.getScheduler().runTimer(() -> {
			for(VehicleEntity ve : new ArrayList<>(vehicles)){
				if(ve!=null && ve.getDriverSeat()!=null)
					ve.tick();
			}
		}, 1, 1);

		ProtectionHandler.init();
		if (Bukkit.getPluginManager().isPluginEnabled("QuickShop")) {
			Bukkit.getPluginManager().registerEvents(new QuickShopHook(), this);
		}
		if (Bukkit.getPluginManager().isPluginEnabled("ModelEngine") && (boolean) a("hooks.ModelEngine", false)) {
			ModelEngineHook.init();
		}

		if (XReflection.supports(21,2)) new ModernInputListener().register();
		else new LegacyInputListener().register();
	}

	@Override
	public void onDisable() {
		ModernInputListener.unregister();

		FileConfiguration yaml = new YamlConfiguration();
		yaml.set("data",vehicles);
		try {
			yaml.save(vehicledatayml);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (VehicleEntity ve : vehicles) {
			if (ve != null)
				ve.deconstruct(null, "Disabling", true);
		}

		vehicles.clear();
	}

	public void initVals() {
		reloadConfig();

		Main.vehicledatayml = new File(this.getDataFolder(), "vehicledata.yml");


		QualityArmoryVehicles.setPlugin(this);
		EconHandler.setupEconomy();
		if (Bukkit.getPluginManager().getPlugin("QualityArmory") == null) {
			minihandler = new QAMini();
			Bukkit.getPluginManager().registerEvents(minihandler, this);
			ParticleHandlers.initValues();
			try {
				ParticleHandlers.initValues();
			} catch (Error | Exception e5) {
			}

			QAMini.overrideURL = (boolean) a("QAMini.resourcepackurl_override", QAMini.overrideURL);
			QAMini.S_ITEM_VARIENTS_NEW = ChatColor.translateAlternateColorCodes('&',
					(String) a("QAMini.variantPrefix", QAMini.S_ITEM_VARIENTS_NEW));
			QAMini.shouldSend = (boolean) a("QAMini.sendResourcepack", QAMini.shouldSend);
			QAMini.sendTitleOnJoin = (boolean) a("QAMini.sendResourcepackTitleOnJoin", QAMini.sendTitleOnJoin);
			QAMini.sendOnJoin = (boolean) a("QAMini.sendResourcepackOnJoin", QAMini.sendOnJoin);
			QAMini.kickIfDeny = (boolean) a("QAMini.kickIfRejectResourcepack", QAMini.kickIfDeny);
			QAMini.verboseLogging = (boolean) a("QAMini.verboseItemLogging", QAMini.verboseLogging);
		} else {
			Plugin qa = Bukkit.getPluginManager().getPlugin("QualityArmory");
			QAMini.verboseLogging = qa.getConfig().getBoolean("verboseItemLogging");
			try {
				this.getServer().getPluginManager().registerEvents(new QualityArmoryListener(), this);
			} catch (Error | Exception ignored) {
			}
		}

		items = new File(this.getDataFolder(), "items");

		if (!items.exists())
			items.mkdirs();

		fuelYML = new File(items, "fuels.yml");

		File oldFuel = new File(this.getDataFolder(), "fuels.yml");
		if (oldFuel.exists()) {
			try {
				Files.move(oldFuel, fuelYML);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!fuelYML.exists())
			try {
				fuelYML.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		repairYML = new File(items, "repair.yml");
		if (!repairYML.exists())
			try {
				repairYML.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		try {
			if (repairItem == null)
				repairItem = RepairItemStack.loadFromFile();

			repairItem.reload();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * a("fuel_ratios.COAL", 500); a("fuel_ratios.COAL_BLOCK", 500 * 9);
		 * a("fuel_ratios.CHARCOAL", 500); a("fuel_ratios.OAK_WOOD", 80);
		 * a("fuel_ratios.WOOD", 80); a("fuel_ratios.BLAZE_POWDER", 100);
		 * a("fuel_ratios.BLAZE_ROD", 200);
		 */
		if (getConfig().contains("fuel_ratios")) {
			for (String key : getConfig().getConfigurationSection("fuel_ratios").getKeys(false)) {
				try {
					FuelItemStack.registerNewFuelToConfig(null, Material.matchMaterial(key), (short) 0, null,
							getConfig().getInt("fuel_ratios." + key), fuelYML);
				} catch (Error | Exception e4) {
					e4.printStackTrace();
				}
			}
			getConfig().set("fuel_ratios", null);
			saveConfig();
		} else {
			FuelItemStack.loadFuels(fuelYML);
		}
		playerUnlock = new File(this.getDataFolder(),"playerdata");
		playerUnlock.mkdirs();
		prefix = ChatColor.translateAlternateColorCodes('&', (String) a("prefix",prefix));
		USE_MANUAL_13 = (boolean) a("USE_1_13_MODEL_SYSTEM", USE_MANUAL_13);
		enableVehicleLimiter = (boolean) a("enable_VehicleLimiter", enableVehicleLimiter);
		allowVehiclePickup = (boolean) a("enable_PickupVehicles", allowVehiclePickup);
		enableGarage = (boolean) a("enable_UnlockableVehicles", enableGarage);
		enableGarageCallback = (boolean) a("enable_GarageCallback", enableGarageCallback);
		requirePermissionToDrive = (boolean) a("enable_RequirePermsToDriveType", requirePermissionToDrive);
		setOwnerOnPlacement = (boolean) a("enable_SetOwnerOnVehicleSpawn", setOwnerOnPlacement);
		enableVehicleDamage = (boolean) a("enable_VehicleDamage", enableVehicleDamage);
		disableAllFuelChecks = (boolean) a("enable_Debug_RemoveFuelChecks", disableAllFuelChecks);
		setOwnerIfNoneExist = (boolean) a("enable_SetOwnerOfVehicleIfUnowned", setOwnerIfNoneExist);
		ENABLE_FILE_CREATION = (boolean) a("Enable_Creation_Of_Default_Files", ENABLE_FILE_CREATION);
		removeVehicleOnDismount = (boolean) a("enable_RemoveVehiclesOnDismount", removeVehicleOnDismount);
		removeVehicleONLEAVE = (boolean) a("enable_RemoveVehiclesOnPlayerQuit", removeVehicleONLEAVE);
		destroyVehicleONLEAVE = (boolean) a("enable_DestroyVehiclesOnPlayerQuit", destroyVehicleONLEAVE);
		destroyOnWater = (boolean) a("enable_DestroyVehiclesOnWater", destroyOnWater);
		disableCreativeCloning = (boolean) a("enable_StopCreativeDuplication", disableCreativeCloning);
		enableVehiclePlayerCollision = (boolean) a("enable_VehiclePlayerCollision", enableVehiclePlayerCollision);
		enableCrossVehicleCollision = (boolean) a("enable_CrossVehicleCollision", enableCrossVehicleCollision);
		swapEndermiteWithChicken = (boolean) a("enable_SwapEndermiteWithCheckenForLowRider", swapEndermiteWithChicken);
		garageFuel = (boolean) a("enable_FuelCarsWhenSpawnedFromGarage", garageFuel);
		useHeads = (boolean) a("enable_UseHeadsForGUI", useHeads);
		debug = (boolean) a("ENABLE_DEBUG", debug);
		debugWithCommand = (boolean) a("override_debug_withCommand", debugWithCommand);
		antiCheatHook = (boolean) a("enable_AntiCheatHook", antiCheatHook);
		freezeOnDestroy = (boolean) a("freezeOnDestroy", freezeOnDestroy);
		bypassCoalInCreative = (boolean) a("bypassCoalInCreative", bypassCoalInCreative);
		sendActionBarOnMove = (boolean) a("sendActionBarOnMove", sendActionBarOnMove);
		enableShopCooldown = (boolean) a("enableShopCooldown", enableShopCooldown);
		onlyPublicVehicles = (boolean) a("makeVehiclesPublic", onlyPublicVehicles);
		useDamage = (boolean) a("unsafe.useDamageInsteadOfCustomModelData", useDamage);
		separateModelAndDriver = (boolean) a("unsafe.separateModelAndDriver", separateModelAndDriver);
		modernPlaneMovements = (boolean) a("modernPlaneMovements", modernPlaneMovements);
		enable_RequirePermToBuyVehicle = (boolean) a("enable_RequirePermToBuyVehicle", enable_RequirePermToBuyVehicle);

		DebugManager.setShouldDisplayInConsole((boolean) a("ENABLE_DEBUG", false));
		enableTrunks = (boolean) a("enable_VehiclesHaveTrunks", enableTrunks);
		a("blockAccelerationReduction.BLOCK_NAME", 1.0);
		for (String s : getConfig().getConfigurationSection("blockAccelerationReduction")
				.getKeys(false)) {
			try {
				Material used = Material.matchMaterial(s);
				customSpeedModifier.put(used, getConfig().getDouble("blockAccelerationReduction." + s));
			} catch (Error | Exception e) {
			}
		}
		cleanVehiclesOnEmpty = (boolean) a("enable_RemoveVehiclesOnEmpty", cleanVehiclesOnEmpty);
		blacklistedWorlds = (List<String>) a("BlacklistedWorlds", Arrays.asList("example_world_name"));

		maxYheightForVehicles = (int) a("maxYHeight", maxYheightForVehicles);

		// HeliBlades = new ItemStack(
		// Material.matchMaterial((String) a("HELIBLADES.item.Material",
		// Material.DIAMOND_AXE.name())), 1,
		// (short) ((int) a("HELIBLADES.item.Durability", 85)));

		useChatForMessage = (boolean) a("enable_useChatForOutOfdFuelMessage", useChatForMessage);

		MessagesConfig.init();
		ItemFact.init();
		ForksUtil.init();
	}


	public static Object a(String path, Object o) {
		if (QualityArmoryVehicles.getPlugin().getConfig().contains(path))
			return QualityArmoryVehicles.getPlugin().getConfig().get(path);
		QualityArmoryVehicles.getPlugin().getConfig().set(path, o);
		QualityArmoryVehicles.getPlugin().saveConfig();
		return o;
	}
	public static void loadVehicles(boolean clearExisting) {
		if (clearExisting)
			vehicleTypes.clear();
		VehicleLoader.loadVehicleFiles();
		if (clearExisting)
			for (VehicleEntity ve : vehicles)
				try {
					ve.setType(QualityArmoryVehicles.getVehicle(ve.getType().getName()));
				} catch (Error | Exception e4) {
				}
	}

	public static void loadComplexParts(boolean clearExisting) {
		/*if (clearExisting)
			complexAdditions.clear();
		VehicleLoader.loadComplexFiles();*/
	}

}
