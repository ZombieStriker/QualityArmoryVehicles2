package me.zombie_striker.qav.customitemmanager.qav.versions.V1_13;

import me.zombie_striker.qav.customitemmanager.AbstractItem;
import me.zombie_striker.qav.customitemmanager.CustomItemManager;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.ModelSize;
import me.zombie_striker.qav.VehicleTypes;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.config.VehicleYML;
import me.zombie_striker.qav.customitemmanager.MaterialStorage;
import me.zombie_striker.qav.finput.FInputManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;

public class CustomVehicleItem extends AbstractItem {

	public static me.zombie_striker.qav.customitemmanager.MaterialStorage m(int d) {
		return me.zombie_striker.qav.customitemmanager.MaterialStorage.getMS(Material.DIAMOND_AXE, d, 0);
	}

	@Override
	public ItemStack getItem(Material material, int data, int variant) {
		return QualityArmoryVehicles.getVehicleItemStack(QualityArmoryVehicles.getVehicleByItem(me.zombie_striker.qav.customitemmanager.MaterialStorage.getMS(material, data, variant)));//.getVehicleItemStack(QualityArmoryVehicles.getVehicleByItem(is));
	}

	@Override
	public ItemStack getItem(MaterialStorage materialStorage) {
		return QualityArmoryVehicles.getVehicleItemStack(QualityArmoryVehicles.getVehicleByItem(materialStorage));//.getVehicleItemStack(QualityArmoryVehicles.getVehicleByItem(is));

	}

	@Override
	public boolean isCustomItem(ItemStack is) {
		return QualityArmoryVehicles.isVehicleByItem(is);
	}

	@Override
	public void initItems(File dataFolder) {
		initItems(dataFolder, true);
	}

	public void initItems(File dataFolder, boolean b) {
		if (CustomItemManager.getResourcepack() == null)
			CustomItemManager.setResourcepack("https://www.dropbox.com/s/s57cq1df5ilol8h/QualityArmoryCarsOnlyV1.2.zip?dl=1");
		if (b) {
			VehicleYML.registerVehicle(VehicleTypes.CAR, "t50", 80).setDisplayname("T50")
					.setCenter(new Vector(-1.5, 0, 0)).setFrontVectorOffset(4).setBackVectorOffset(4)
					.setPassagerLocations(new Vector(-2, 0, 0), new Vector(-2, 0, 1), new Vector(-2, 0, -1))
					.setStopMeleeDamage(true).setStopProjectileDamage(true).setRequireFuel(true).setTrunkSize(18)
					.setWidth(2).setHeight(3).setMaxHealth(300).setCost(5000)
					.setKeyInputManagerLMB(FInputManager.LAUNCHER_40mm).setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD)
					.setLore("&7Press [Space] to break",
							"&7Press [LMB] to shoot a 40mm round (from the vehicle's trunk)")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "moskvitch", 81).setDisplayname("Moskvitch")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(100).setCost(2000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "ladaniva", 82).setDisplayname("LadaNiva")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.3).setBackVectorOffset(1.25)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setTrunkSize(18)
					.setWidth(0.74).setHeight(2).setMaxHealth(100).setCost(2000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "jeep", 83).setDisplayname("Jeep")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.1).setBackVectorOffset(1.1)
					.setMaxForwardSpeed(2).setBaseAcceleration(0.064)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStaticTurning(false).setRequireFuel(true).setTrunkSize(18).setWidth(0.74).setHeight(1.6)
					.setMaxHealth(100).setCost(2000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.HELI, "helicopter", 84).setDisplayname("Helicopter")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(2.3).setBackVectorOffset(2.8)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setRequireFuel(true).setTrunkSize(18).setWidth(0.74).setHeight(3)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setMaxHealth(250).setCost(4000)
					.setKeyInputManagerF(FInputManager.TOGGLE_SPEED_HELI)
					.setLore("&7Press [Space] to start flying", "&7 Press [F] to toggle the accent/decent speed")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.BOAT, "smalcivilboat", 86).setDisplayname("SmalCivilBoat")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(2.3).setBackVectorOffset(2.8)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setRequireFuel(true).setWidth(1.4).setHeight(1.5).setMaxHealth(100).setCost(1000).save();
			VehicleYML.registerVehicle(VehicleTypes.PLANE, "mig1", 87).setDisplayname("MiG1")
					.setCenter(new Vector(-1, 0, 0)).setFrontVectorOffset(2).setBackVectorOffset(2.3)
					.setPassagerLocations(new Vector(-1, 0, 0), new Vector(-1, 0, -1), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setRequireFuel(true).setWidth(0.74).setHeight(2.3).setMaxHealth(150)
					.setCost(6000).setLore("&7Press [Space] to start flying").save();
			VehicleYML.registerVehicle(VehicleTypes.PLANE, "polikarpov", 88).setDisplayname("Polikarpov")
					.setCenter(new Vector(-1, 0, 0)).setFrontVectorOffset(2).setBackVectorOffset(2.3)
					.setPassagerLocations(new Vector(-1, 0, 0), new Vector(-1, 0, -1), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setRequireFuel(true).setWidth(0.74).setHeight(2.3).setMaxHealth(150)
					.setCost(6000).setLore("&7Press [Space] to start flying").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "cadillac62", 89).setDisplayname("Cadillac-Series62")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setMaxForwardSpeed(2).setBaseAcceleration(0.060)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStaticTurning(false).setRequireFuel(true).enablePlayerBodyFix(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(100).setCost(2000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "motorcycle", 92).setDisplayname("Motorcycle")
					.setActivationRadius(1).setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(2.4)
					.setBackVectorOffset(1).setMaxForwardSpeed(2).setBaseAcceleration(0.065)
					.setPassagerLocations(new Vector(-1, 0, 0)).setStaticTurning(false).setRequireFuel(true)
					.enablePlayerBodyFix(true).setWidth(0.45).setHeight(1).setMaxHealth(50).setCost(1000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "semitrailertruck", 94)
					.setDisplayname("Semi-Trailer Truck").setCenter(new Vector(-1.5, 0, 0.8)).setFrontVectorOffset(4.2)
					.setBackVectorOffset(4).setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setMaxForwardSpeed(2).setBaseAcceleration(0.045)
					.setActivationRadius(3)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-2, 0, 0.3), new Vector(-2, 0, 1),
							new Vector(-3, 0, 0.3), new Vector(-3, 0, 1), new Vector(-4, 0, 0.3), new Vector(-4, 0, 1))
					.setStaticTurning(false).setRequireFuel(true).setTrunkSize(27).setWidth(1.4).setHeight(4)
					.setMaxHealth(200).setCost(2500).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "shoppingcart", 95).setDisplayname("Shopping Cart")
					.setActivationRadius(1).setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(2)
					.setBackVectorOffset(1).setMaxForwardSpeed(2).setBaseAcceleration(0.04)
					.setPassagerLocations(new Vector(1, 0, 0)).setStaticTurning(false).setRequireFuel(false)
					.enablePlayerBodyFix(true).setWidth(0.45).setHeight(1).setMaxHealth(20).setCost(100)
					.setDrivingSounds(false).save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "police", 96).setDisplayname("Police Car")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000).setKeyInputManagerF(FInputManager.POLICE_SIREN)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk", "&7Press [F] to activate sirens.")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "unmarkedvan", 97).setDisplayname("Unmarked Van")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1),
							new Vector(-2, 0, 0), new Vector(-2, 0, 1), new Vector(-2.7, 0, 0), new Vector(-2.7, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(1500).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk", "&7Press [RMB] to activate sirens.")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "bus", 104).setDisplayname("City bus")
					.setCenter(new Vector(-2, 0, 1)).setFrontVectorOffset(4.8).setBackVectorOffset(3.5)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setMaxForwardSpeed(2).setBaseAcceleration(0.045)
					.setActivationRadius(4.6)
					.setPassagerLocations(new Vector(-0.5, 0, 2), new Vector(-1.5, 0, 0), new Vector(-1.5, 0, 2),
							new Vector(-2.5, 0, 0), new Vector(-2.5, 0, 2), new Vector(-4, 0, 0), new Vector(-4, 0, 1),
							new Vector(-4, 0, 2))
					.setStaticTurning(false).setRequireFuel(true).setTrunkSize(27).setWidth(2).setHeight(4.5)
					.setMaxHealth(200).setCost(4000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();

			VehicleYML.registerVehicle(VehicleTypes.PLANE, "b2", 111).setDisplayname("B-2 Stealth Bomber")
					.setCenter(new Vector(-2, 0, 0)).setFrontVectorOffset(4).setBackVectorOffset(5)
					.setPassagerLocations(new Vector(-1.5, 0, 0)).setTrunkSize(27).setStopMeleeDamage(true)
					.setRequireFuel(true).setWidth(3.5).setHeight(4.2).setMaxHealth(1050).setCost(12000)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setKeyInputManagerF(FInputManager.TNTBOMBER)
					.setKeyInputManagerLMB(FInputManager.LAUNCHER_556)
					.setLore("&7Press [Space] to start flying", "&7Press [F] to drop TNT (from the vehicle's trunk)",
							Main.minihandler == null
									? "&7 Rapidly press [LMB] to shoot 5.55 rounds(from the vehicle's trunk)"
									: "")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.PLANE, "privateplane", 112).setDisplayname("Private Plane")
					.setCenter(new Vector(-2, 0, 0)).setFrontVectorOffset(4).setBackVectorOffset(4)
					.setPassagerLocations(new Vector(-1.5, 0, -0.3), new Vector(-2.6, 0, -0.3), new Vector(-4, 0, -0.3),
							new Vector(-4, 0, 0.3), new Vector(-2.7, 0, 0.3), new Vector(-1.5, 0, 0.3))
					.setTrunkSize(45).setStopMeleeDamage(true).setRequireFuel(true).setWidth(3.5).setHeight(4.2)
					.setMaxHealth(1050).setCost(8000).setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD)
					.setLore("&7Press [Space] to start flying").save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "ambulance", 114).setDisplayname("Ambulance")

					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(3.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1.2, 0, 0.5), new Vector(-1.2, 0, 0),
							new Vector(-1.2, 0, 1), new Vector(-2, 0, 0), new Vector(-2, 0, 1), new Vector(-3, 0, 0),
							new Vector(-3, 0, 1), new Vector(-2.2, 0, 0.5))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2.5)
					.setMaxHealth(150).setCost(3000).setKeyInputManagerF(FInputManager.POLICE_SIREN)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [F] to activate sirens.", "&7Press [LMB] to honk")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "sw_speederbike", 115).setDisplayname("Speeder Bike")
					.setActivationRadius(1).setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(3)
					.setBackVectorOffset(1).setMaxForwardSpeed(2).setBaseAcceleration(0.065)
					.setPassagerLocations(new Vector(-1, 0, 0)).setStaticTurning(false).setRequireFuel(true)
					.enablePlayerBodyFix(true).setWidth(0.45).setHeight(1).setMaxHealth(50).setCost(1000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "sw_landspeeder", 116).setDisplayname("Land Speeder")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(4.3).setBackVectorOffset(1.25)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setTrunkSize(18)
					.setWidth(0.74).setHeight(2).setMaxHealth(100).setCost(2000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();

			VehicleYML.registerVehicle(VehicleTypes.TRAIN, "trolley", 85).setDisplayname("Trolley Streetcar")
					.setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(3).setBackVectorOffset(3)
					.setMaxBackupSpeed(0.4).setMaxForwardSpeed(0.4)
					.setPassagerLocations(new Vector(0, 0, 0.5), new Vector(-1, 0, -0.5), new Vector(-1, 0, 0.5),
							new Vector(1, 0, -0.5), new Vector(1, 0, 0.5), new Vector(-2, 0, -0.5),
							new Vector(-2, 0, 0.5), new Vector(2, 0, -0.5), new Vector(2, 0, 0.5))
					.setStopMeleeDamage(true).setStaticTurning(true).setRequireFuel(false).setTrunkSize(45)
					.setWidth(0.77).setHeight(3).setMaxHealth(400).setCost(4000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "bumpercar_red", 118).setDisplayname("BumperCar")
					.setActivationRadius(1.4).setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(2)
					.setBackVectorOffset(1.5).setMaxForwardSpeed(1.4).setBaseAcceleration(0.055)
					.setPassagerLocations(new Vector(-1, 0, 0)).setStaticTurning(true).setRequireFuel(false)
					.enablePlayerBodyFix(true).setWidth(0.45).setHeight(1).setMaxHealth(50).setCost(1000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.setDriverseatOffset(new Vector(0, 0, 0)).save();


			VehicleYML.registerVehicle(VehicleTypes.CAR, "firetruck",
					119).setDisplayname("FireTruck").setActivationRadius(3).setCenter(new
					Vector(0, 0, 0)).setFrontVectorOffset(2)
					.setBackVectorOffset(7).setMaxForwardSpeed(1.4).setBaseAcceleration(0.055)
					.setPassagerLocations(new Vector(0, 0,
							1.2)).setWidth(1.5).setHeight(4).setMaxHealth(2000).setCost(15000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK).setKeyInputManagerF(FInputManager.POLICE_SIREN)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk", "&7Press [F] to activate sirens.")
					.setDriverseatOffset(new Vector(0, 2, 0)).setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "taxi", 120).setDisplayname("Taxi")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "hearse", 121).setDisplayname("Hearse")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1.3, 0, 0.5), new Vector(-2.3, 0, 0.5))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();


			VehicleYML.registerVehicle(VehicleTypes.PLANE, "ufo", 122).setDisplayname("UFO")
					.setCenter(new Vector(-1, 0, 0)).setFrontVectorOffset(4.5).setBackVectorOffset(4.5)
					.setPassagerLocations(new Vector(-1.5, 0, 0)).setTrunkSize(54).setStopMeleeDamage(true)
					.setRequireFuel(false).setWidth(4.5).setHeight(4.2).setMaxHealth(1050).setCost(42069)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setKeyInputManagerLMB(FInputManager.CAR_HONK).setDrivingSounds(false)
					.setLore("&7Press [Space] to start flying", "&7 Press [LMB] to make a mysterious sound.")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "newsvan", 123).setDisplayname("News Van")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "swatvan", 124).setDisplayname("S.W.A.T Van")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();


			VehicleYML.registerVehicle(VehicleTypes.CAR, "streetsweep", 125).setDisplayname("Street Sweeper")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.HELI, "helicopterorange", 126).setDisplayname("Helicopter[Orange]")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(2.3).setBackVectorOffset(2.8)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setRequireFuel(true).setTrunkSize(18).setWidth(0.74).setHeight(3)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setMaxHealth(250).setCost(4000)
					.setKeyInputManagerF(FInputManager.TOGGLE_SPEED_HELI)
					.setLore("&7Press [Space] to start flying", "&7 Press [F] to toggle the accent/decent speed")
					.save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "garbagetruck", 127).setDisplayname("Garbage Truck")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setStaticTurning(false).setRequireFuel(true).setWidth(2).setHeight(3.5)
					.setMaxHealth(550).setCost(20000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "presidentiallimo", 128).setDisplayname("Presidential Limo")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(1150).setCost(17760)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();
		}
	}

	public String getIngString(Material m, int durability, int amount) {
		return m.toString() + "," + durability + "," + amount;
	}
}
