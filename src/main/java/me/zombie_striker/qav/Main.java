package me.zombie_striker.qav;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.cryptomorin.xseries.ReflectionUtils;
import com.google.common.io.Files;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.customitemmanager.AbstractItem;
import me.zombie_striker.qav.customitemmanager.CustomItemManager;
import me.zombie_striker.qav.customitemmanager.qav.versions.V1_13.CustomVehicleItem;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.api.events.PlayerExitQAVehicleEvent;
import me.zombie_striker.qav.config.VehicleLoader;
import me.zombie_striker.qav.debugmanager.DebugManager;
import me.zombie_striker.qav.easygui.EasyGUI;
import me.zombie_striker.qav.finput.*;
import me.zombie_striker.qav.finput.inputs.*;
import me.zombie_striker.qav.fuel.FuelItemStack;
import me.zombie_striker.qav.fuel.RepairItemStack;
import me.zombie_striker.qav.hooks.model.ModelEngineHook;
import me.zombie_striker.qav.hooks.ProtectionHandler;
import me.zombie_striker.qav.hooks.QuickShopHook;
import me.zombie_striker.qav.qamini.EconHandler;
import me.zombie_striker.qav.qamini.ParticleHandlers;
import me.zombie_striker.qav.qamini.QAMini;
import me.zombie_striker.qav.util.ForksUtil;
import me.zombie_striker.qav.util.VehicleUtils;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin {

	public static boolean debugWithCommand = false;
	public static boolean debug = false;
	public static boolean verboseLogging = false;

	public static String prefix = "&6[QA-Vehicles]&f";

	public static QAMini minihandler = null;// = new QAMini();


	public static boolean ENABLE_FILE_CREATION = true;

	public static String PASSAGER_PREFIX = "QA-Passager=";
	public static String MODEL_PREFIX = "QA-Model=";
	public static boolean enableGarage=false;
	public static boolean enableGarageCallback=false;
	public static boolean useChatForMessage=false;
	public static String VEHICLEPREFIX = "(QAV)";
	public ProtocolManager protocolManager;
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
	public static boolean swapEndermiteWithChicken = false;
	public static boolean garageFuel = true;
	public static boolean enableCarPushing = true;
	public static boolean enableEntityPushing = true;
	public static boolean enableVehiclePlayerCollision = true;
	public static boolean requirePermissionToDrive = false;
	public static boolean setOwnerIfNoneExist = false;
	public static boolean enableTrunks = true;
	public static boolean antiCheatHook = false;
	public static boolean freezeOnDestroy = false;
	public static boolean bypassCoalInCreative = true;
	public static boolean sendActionBarOnMove = true;
	public static boolean enableShopCooldown = false;
	public static boolean onlyPublicVehicles = false;
	public static boolean disableing = false;
	public static boolean enable_RequirePermToBuyVehicle = false;
	public static Object BOUNDINGBOX;
	public static boolean useHeads = true;
	private static File vehicledatayml;
	private static boolean debug_PL_PacketlistenerActive = false;
	// Roughly 1 coal for 25 seconds. 64 coal for ~26 minutes
	private static boolean USE_MANUAL_13 = false;
	public static boolean useDamage = false;
	public static boolean separateModelAndDriver = false;
	public static boolean useTurtles = false;





	public static List<AbstractVehicle> vehicleTypes = new ArrayList<>();
	public static List<VehicleEntity> vehicles = new ArrayList<>();

	public static void DEBUG(String message) {
		DebugManager.sendDebugMessages(message);
	}
	public static boolean isVersionHigherThan(int first, int second) {
		return QAMini.isVersionHigherThan(first, second);
	}

	@Override
	public void onEnable() {
		ConfigurationSerialization.registerClass(UnlockedVehicle.class);
		ConfigurationSerialization.registerClass(VehicleEntity.class);

		if (this.getDescription().getVersion().contains("SNAPSHOT")) {
			this.getLogger().warning(String.format("You are using a SNAPSHOT version of %s(%s). This is not recommended for production use.", this.getName(), this.getDescription().getVersion()));
		}

		new MetricsLite(this,12753);

		carData = new File(getDataFolder(),"vehicles");
		if(!carData.exists()){
			carData.mkdirs();
		}

		initVals();

		ParticleHandlers.initValues();
		if (ReflectionUtils.supports(9)) {
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
			String message = (String) a("QAMini.resourcepackurl", CustomItemManager.getResourcepack());
			if (message != null && !message.equals(CustomItemManager.getResourcepack())) {
				this.getConfig().set("QAMini.resourcepackurl", CustomItemManager.getResourcepack());
				this.saveConfig();
			}
		} else {
			CustomItemManager.setResourcepack((String) a("QAMini.resourcepackurl", CustomItemManager.getResourcepack()));
		}
		loadComplexParts(false);
		loadVehicles(false);

		QAVCommand command = new QAVCommand();
		getCommand("QualityArmoryVehicles").setExecutor(command);
		getCommand("QualityArmoryVehicles").setTabCompleter(command);
		if (enableGarage)
			getCommand("garage").setExecutor(new GarageCommand());

		Bukkit.getPluginManager().registerEvents(new QAVListener(this), this);
		EasyGUI.INIT(this);

		Bukkit.getScheduler().runTaskLater(this, () -> {
			FileConfiguration dataconfig = YamlConfiguration.loadConfiguration(vehicledatayml);

			if(dataconfig.contains("data")) {
				for (VehicleEntity ve : ((List<VehicleEntity>) dataconfig.get("data"))) {
					if (ve != null && ve.getDriverSeat() != null)
						vehicles.add(ve);
				}

				this.getLogger().info("Successfully loaded " + vehicles.size() + " spawned vehicles.");
			}
		}, 20);


		protocolManager = ProtocolLibrary.getProtocolManager();

		Main main = this;

		new BukkitRunnable(){
			public void run(){
				for(VehicleEntity ve : new ArrayList<>(vehicles)){
					if(ve!=null && ve.getDriverSeat()!=null)
					ve.tick();
				}
			}
		}.runTaskTimer(this,1,1);

		ProtectionHandler.init();
		if (Bukkit.getPluginManager().isPluginEnabled("QuickShop")) {
			Bukkit.getPluginManager().registerEvents(new QuickShopHook(), this);
		}
		if (Bukkit.getPluginManager().isPluginEnabled("ModelEngine") && (boolean) a("hooks.ModelEngine", false)) {
			ModelEngineHook.init();
		}

		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {

			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				try {
					event.getPlayer().getVehicle();
				} catch (UnsupportedOperationException e) {
					DEBUG("The method getVehicle is not supported for temporary players.");
					return;
				}
				if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE
						&& player.getVehicle() != null) {

					VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(player.getVehicle());
					if (ve == null)
						return;
					
					if (!ve.getDriverSeat().equals(player.getVehicle())) return;

					new BukkitRunnable() {
						public void run() {
							if (event.getPacket().getFloat().read(0) < 0)
								ve.getType().handleTurnLeft(ve, event);
							if (event.getPacket().getFloat().read(0) > 0)
								ve.getType().handleTurnRight(ve, event);
							if (event.getPacket().getFloat().read(1) < 0)
								ve.getType().handleSpeedDecrease(ve, event);
							if (event.getPacket().getFloat().read(1) > 0)
								ve.getType().handleSpeedIncrease(ve, event);
							if (event.getPacket().getBooleans().read(0))
								ve.getType().handleSpace(ve, event);
							if (event.getPacket().getBooleans().read(1)) {
								//Shift
								PlayerExitQAVehicleEvent event = new PlayerExitQAVehicleEvent(ve,player);
								Bukkit.getPluginManager().callEvent(event);

								if (removeVehicleOnDismount && QualityArmoryVehicles.isVehicle(player.getVehicle())) {
									VehicleEntity vehicle = QualityArmoryVehicles.getVehicleEntityByEntity(player.getVehicle());
									if (vehicle != null && vehicle.getDriverSeat().equals(player.getVehicle())) {
										VehicleUtils.callback(vehicle,player, "Dismount");
									}
								}
							}

						}
					}.runTaskLater(main, 0);
				}
			}
		});
	}

	@Override
	public void onDisable() {
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(vehicledatayml);
		yaml.set("data",vehicles);
		try {
			yaml.save(vehicledatayml);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		disableCreativeCloning = (boolean) a("enable_StopCreativeDuplication", disableCreativeCloning);
		enableVehiclePlayerCollision = (boolean) a("enable_VehiclePlayerCollision", enableVehiclePlayerCollision);
		swapEndermiteWithChicken = (boolean) a("enable_SwapEndermiteWithCheckenForLowRider", swapEndermiteWithChicken);
		garageFuel = (boolean) a("enable_FuelCarsWhenSpawnedFromGarage", garageFuel);
		enableCarPushing = (boolean) a("enable_PushCarsOnCrash", enableCarPushing);
		enableEntityPushing = (boolean) a("enable_PushEntityOnCollide", enableEntityPushing);
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
		useTurtles = (boolean) a("unsafe.useTurtles", useTurtles);

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
