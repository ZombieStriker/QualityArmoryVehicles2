package me.zombie_striker.qav.customitemmanager.qav.versions.V1_14;

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
		initItems(true);
	}

	public void initItems(boolean b) {
		if (CustomItemManager.getResourcepack() == null)
			CustomItemManager.setResourcepack("https://www.dropbox.com/s/baqqgxkwsz3io23/QualityArmoryCarsOnlyV2.7.zip?dl=1");
		if (b) {

			VehicleYML.registerVehicle(VehicleTypes.CAR, "t50", 1).setDisplayname("T50")
					.setCenter(new Vector(-1.5, 0, 0)).setFrontVectorOffset(4).setBackVectorOffset(4)
					.setPassagerLocations(new Vector(-2, 2, 0), new Vector(-2, 2, 1), new Vector(-2, 2, -1))
					.setStopMeleeDamage(true).setStopProjectileDamage(true).setRequireFuel(true).setTrunkSize(18)
					.setWidth(2).setHeight(3).setMaxHealth(300).setCost(5000).setMaterial(Material.RABBIT_HIDE)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setComplexAdditionAt(0,new Vector(0,0,0),"t50tankcannon")
					.setLore("&7Press [Space] to break",
							"&7Enter the passager seat to firte the cannons.")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "moskvitch", 2).setDisplayname("Moskvitch")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2).setMaterial(Material.RABBIT_HIDE)
					.setMaxHealth(100).setCost(2000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "ladaniva", 3).setDisplayname("LadaNiva")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.3).setBackVectorOffset(1.25).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setTrunkSize(18)
					.setWidth(0.74).setHeight(2).setMaxHealth(100).setCost(2000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "jeep", 4).setDisplayname("Jeep")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.1).setBackVectorOffset(1.1).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStaticTurning(false).setRequireFuel(true).setTrunkSize(18).setWidth(0.74).setHeight(1.6)
					.setMaxHealth(100).setCost(2000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.HELI, "helicopter", 5).setDisplayname("Helicopter")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(2.3).setBackVectorOffset(2.8).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(0, 2, 1), new Vector(-1, 2, 0), new Vector(-1, 2, 1))
					.setStopMeleeDamage(true).setRequireFuel(true).setTrunkSize(18).setWidth(0.74).setHeight(3)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setMaxHealth(250).setCost(4000)
					.setLore("&7Press [Space] to start flying", "&7 Press [F] to toggle the accent/decent speed")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.BOAT, "smalcivilboat", 6).setDisplayname("SmalCivilBoat")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(2.3).setBackVectorOffset(2.8).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, 1))
					.setRequireFuel(true).setWidth(1.4).setHeight(1.5).setMaxHealth(100).setCost(1000).save();
			VehicleYML.registerVehicle(VehicleTypes.PLANE, "mig1", 7).setDisplayname("MiG1")
					.setCenter(new Vector(-1, 0, 0)).setFrontVectorOffset(2).setBackVectorOffset(2.3).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(-1, 1, 0), new Vector(-1, 1, -1), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setRequireFuel(true).setWidth(0.74).setHeight(2.3).setMaxHealth(150)
					.setCost(6000).setLore("&7Press [Space] to start flying").save();
			VehicleYML.registerVehicle(VehicleTypes.PLANE, "polikarpov", 8).setDisplayname("Polikarpov")
					.setCenter(new Vector(-1, 0, 0)).setFrontVectorOffset(2).setBackVectorOffset(2.3).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(-1, 1, 0), new Vector(-1, 1, -1), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setRequireFuel(true).setWidth(0.74).setHeight(2.3).setMaxHealth(150)
					.setCost(6000).setLore("&7Press [Space] to start flying").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "cadillac62", 9).setDisplayname("Cadillac-Series62")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45).setMaterial(Material.RABBIT_HIDE)

					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStaticTurning(false).setRequireFuel(true).enablePlayerBodyFix(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(100).setCost(2000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "motorcycle", 10).setDisplayname("Motorcycle")
					.setActivationRadius(1).setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(2.4)
					.setBackVectorOffset(1).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(-1, 1, 0)).setStaticTurning(false).setRequireFuel(true)
					.enablePlayerBodyFix(true).setWidth(0.45).setHeight(1).setMaxHealth(50).setCost(1000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "semitrailertruck", 11)
					.setDisplayname("Semi-Trailer Truck").setCenter(new Vector(-1.5, 0, 0.8)).setFrontVectorOffset(4.2)
					.setBackVectorOffset(4).setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setMaterial(Material.RABBIT_HIDE)
					.setActivationRadius(3)
					.setPassagerLocations(new Vector(0, 2, 1), new Vector(-2, 2, 0.3), new Vector(-2, 2, 1),
							new Vector(-3, 2, 0.3), new Vector(-3, 2, 1), new Vector(-4, 2, 0.3), new Vector(-4, 2, 1))
					.setStaticTurning(false).setRequireFuel(true).setTrunkSize(27).setWidth(1.4).setHeight(4)
					.setMaxHealth(200).setCost(2500).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "shoppingcart", 12).setDisplayname("Shopping Cart")
					.setActivationRadius(1).setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(2)
					.setBackVectorOffset(1).setMaxForwardSpeed(2).setBaseAcceleration(0.04)
					.setPassagerLocations(new Vector(1, 1, 0)).setStaticTurning(false).setRequireFuel(false)
					.enablePlayerBodyFix(true).setWidth(0.45).setHeight(1).setMaxHealth(20).setCost(100).setMaterial(Material.RABBIT_HIDE)
					.setDrivingSounds(false).save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "police", 13).setDisplayname("Police Car").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000).setKeyInputManagerF(FInputManager.POLICE_SIREN)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk", "&7Press [F] to activate sirens.")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "unmarkedvan", 14).setDisplayname("Unmarked Van").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1),
							new Vector(-2, 0, 0), new Vector(-2, 0, 1), new Vector(-2.7, 0, 0), new Vector(-2.7, 0, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(1500).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk", "&7Press [RMB] to activate sirens.")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "bus", 15).setDisplayname("City bus").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-2, 0, 1)).setFrontVectorOffset(4.8).setBackVectorOffset(3.5)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setMaxForwardSpeed(2).setBaseAcceleration(0.045)
					.setActivationRadius(4.6)
					.setPassagerLocations(new Vector(-0.5, 2, 2), new Vector(-1.5, 2, 0), new Vector(-1.5, 2, 2),
							new Vector(-2.5, 2, 0), new Vector(-2.5, 2, 2), new Vector(-4, 2, 0), new Vector(-4, 2, 1),
							new Vector(-4, 2, 2))
					.setStaticTurning(false).setRequireFuel(true).setTrunkSize(27).setWidth(2).setHeight(4.5)
					.setMaxHealth(200).setCost(4000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();

			VehicleYML.registerVehicle(VehicleTypes.PLANE, "b2", 16).setDisplayname("B-2 Stealth Bomber")
					.setCenter(new Vector(-2, 0, 0)).setFrontVectorOffset(4).setBackVectorOffset(5).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(-1.5, 2, 0)).setTrunkSize(27).setStopMeleeDamage(true)
					.setRequireFuel(true).setWidth(3.5).setHeight(4.2).setMaxHealth(1050).setCost(12000)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HAND).setKeyInputManagerF(FInputManager.TNTBOMBER)
					.setKeyInputManagerLMB(FInputManager.LAUNCHER_556)
					.setLore("&7Press [Space] to start flying", "&7Press [F] to drop TNT (from the vehicle's trunk)",
							Main.minihandler == null
									? "&7 Rapidly press [LMB] to shoot 5.55 rounds(from the vehicle's trunk)"
									: "")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.PLANE, "privateplane", 17).setDisplayname("Private Plane")
					.setCenter(new Vector(-2, 0, 0)).setFrontVectorOffset(4).setBackVectorOffset(4)
					.setPassagerLocations(
							new Vector(-1.5, 2, -0.3),
							new Vector(-1.5, 2, 0.3),
							new Vector(-2.6, 2, -0.3),
							new Vector(-2.6, 2, 0.3),
							new Vector(-4, 2, -0.3),
							new Vector(-4, 0, 0.3),
							new Vector(-2.7, 0, 0.3),
							new Vector(-1.5, 0, 0.3))
					.setTrunkSize(45).setStopMeleeDamage(true).setRequireFuel(true).setWidth(3.5).setHeight(4.2)
					.setMaxHealth(1050).setCost(8000).setModelSize(ModelSize.ADULT_ARMORSTAND_HAND).setMaterial(Material.RABBIT_HIDE)
					.setLore("&7Press [Space] to start flying").save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "ambulance", 18).setDisplayname("Ambulance")

					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(3.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1.2, 1, 0.5), new Vector(-1.2, 1, 0),
							new Vector(-1.2, 1, 1), new Vector(-2, 1, 0), new Vector(-2, 1, 1), new Vector(-3, 1, 0),
							new Vector(-3, 1, 1), new Vector(-2.2, 1, 0.5))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2.5).setMaterial(Material.RABBIT_HIDE)
					.setMaxHealth(150).setCost(3000).setKeyInputManagerF(FInputManager.POLICE_SIREN)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [F] to activate sirens.", "&7Press [LMB] to honk")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "sw_speederbike", 19).setDisplayname("Speeder Bike")
					.setActivationRadius(1).setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(3)
					.setBackVectorOffset(1)
					.setPassagerLocations(new Vector(-1, 1, 0)).setStaticTurning(false).setRequireFuel(true)
					.enablePlayerBodyFix(true).setWidth(0.45).setHeight(1).setMaxHealth(50).setCost(1000).setMaterial(Material.RABBIT_HIDE)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "sw_landspeeder", 20).setDisplayname("Land Speeder")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(4.3).setBackVectorOffset(1.25)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setTrunkSize(18).setMaterial(Material.RABBIT_HIDE)
					.setWidth(0.74).setHeight(2).setMaxHealth(100).setCost(2000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();

			VehicleYML.registerVehicle(VehicleTypes.TRAIN, "trolley", 21).setDisplayname("Trolley Streetcar")
					.setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(3).setBackVectorOffset(3).setMaterial(Material.RABBIT_HIDE)
					.setMaxBackupSpeed(0.4).setMaxForwardSpeed(0.4)
					.setPassagerLocations(new Vector(0, 1, 0.5), new Vector(-1, 1, -0.5), new Vector(-1, 1, 0.5),
							new Vector(1, 1, -0.5), new Vector(1, 1, 0.5), new Vector(-2, 1, -0.5),
							new Vector(-2, 1, 0.5), new Vector(2, 1, -0.5), new Vector(2, 1, 0.5))
					.setStopMeleeDamage(true).setStaticTurning(true).setRequireFuel(false).setTrunkSize(45)
					.setWidth(0.77).setHeight(3).setMaxHealth(400).setCost(4000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "bumpercar_red", 22).setDisplayname("BumperCar")
					.setActivationRadius(1.4).setCenter(new Vector(0, 0, 0)).setFrontVectorOffset(2)
					.setBackVectorOffset(1.5).setMaxForwardSpeed(1.4).setBaseAcceleration(0.055)
					.setPassagerLocations(new Vector(-1, 0, 0)).setStaticTurning(true).setRequireFuel(false)
					.enablePlayerBodyFix(true).setWidth(0.45).setHeight(1).setMaxHealth(50).setCost(1000).setMaterial(Material.RABBIT_HIDE)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.setDriverseatOffset(new Vector(0, 0, 0)).save();


			VehicleYML.registerVehicle(VehicleTypes.CAR, "firetruck",
					23).setDisplayname("FireTruck").setActivationRadius(3).setCenter(new
					Vector(0, 0, 0)).setFrontVectorOffset(2)
					.setBackVectorOffset(7).setMaxForwardSpeed(1.4).setBaseAcceleration(0.055).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(0, 2,
							1.2)).setWidth(1.5).setHeight(4).setMaxHealth(500).setCost(15000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK).setKeyInputManagerF(FInputManager.POLICE_SIREN)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk", "&7Press [F] to activate sirens.")
					.setDriverseatOffset(new Vector(0, 2, 0)).setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "taxi", 24).setDisplayname("Taxi").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "hearse", 25).setDisplayname("Hearse").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1.3, 1, 0.5), new Vector(-2.3, 1, 0.5))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();


			VehicleYML.registerVehicle(VehicleTypes.PLANE, "ufo", 26).setDisplayname("UFO").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-1, 0, 0)).setFrontVectorOffset(4.5).setBackVectorOffset(4.5)
					.setPassagerLocations(new Vector(-1.5, 2, 0)).setTrunkSize(54).setStopMeleeDamage(true)
					.setRequireFuel(false).setWidth(4.5).setHeight(4.2).setMaxHealth(1050).setCost(42069)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setKeyInputManagerLMB(FInputManager.CAR_HONK).setDrivingSounds(false)
					.setLore("&7Press [Space] to start flying", "&7 Press [LMB] to make a mysterious sound.")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "newsvan", 27).setDisplayname("News Van").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "swatvan", 28).setDisplayname("S.W.A.T Van").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1), new Vector(-2, 1, 0), new Vector(-2, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(500).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "streetsweep", 29).setDisplayname("Street Sweeper").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(150).setCost(3000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.HELI, "helicopterorange", 30).setDisplayname("Helicopter[Orange]")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(2.3).setBackVectorOffset(2.8).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(0, 2, 1), new Vector(-1, 2, 0), new Vector(-1, 2, 1))
					.setStopMeleeDamage(true).setRequireFuel(true).setTrunkSize(18).setWidth(0.74).setHeight(3)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setMaxHealth(250).setCost(4000)
					.setLore("&7Press [Space] to start flying", "&7 Press [F] to toggle the accent/decent speed")
					.save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "garbagetruck", 31).setDisplayname("Garbage Truck").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 2, 1))
					.setStopMeleeDamage(true).setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setStaticTurning(false).setRequireFuel(true).setWidth(2).setHeight(3.5)
					.setMaxHealth(550).setCost(20000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();
			VehicleYML.registerVehicle(VehicleTypes.CAR, "presidentiallimo", 32).setDisplayname("Presidential Limo").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1.5, 1, 0), new Vector(-1.5, 1, 1), new Vector(-3, 1, 0), new Vector(-3, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2).setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD)
					.setMaxHealth(1150).setCost(17760)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();


			VehicleYML.registerVehicle(VehicleTypes.CAR, "lamborghini", 33).setDisplayname("Lamborghini").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 0, 1)).setDriverseatOffset(new Vector(0, 0, 0))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2)
					.setMaxHealth(250).setCost(30000).setMaxForwardSpeed(1.4).setBaseAcceleration(0.075)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();


			VehicleYML.registerVehicle(VehicleTypes.CAR, "warthog", 34).setDisplayname("M12 Warthog")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.3).setBackVectorOffset(1.25).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setTrunkSize(27)
					.setWidth(0.74).setHeight(2.5).setMaxHealth(1000).setCost(10000)
					.setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk")
					.save();


			VehicleYML.registerVehicle(VehicleTypes.HELI, "falconhelicopter", 35).setDisplayname("UH-144 Falcon")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(2.3).setBackVectorOffset(2.8).setMaterial(Material.RABBIT_HIDE)
					.setPassagerLocations(new Vector(0, 2, 1), new Vector(-1, 2, 0), new Vector(-1, 2, 1))
					.setStopMeleeDamage(true).setRequireFuel(true).setTrunkSize(18).setWidth(0.74).setHeight(3)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD).setMaxHealth(250).setCost(50000)
					.setLore("&7Press [Space] to start flying", "&7 Press [F] to toggle the accent/decent speed")
					.save();

			VehicleYML.registerVehicle(VehicleTypes.CAR, "schoolbus", 36).setDisplayname("School Bus").setMaterial(Material.RABBIT_HIDE)
					.setCenter(new Vector(-2, 0, 1)).setFrontVectorOffset(4.8).setBackVectorOffset(3.5)
					.setModelSize(ModelSize.ADULT_ARMORSTAND_HEAD)
					.setActivationRadius(4.6)
					.setPassagerLocations(new Vector(-0.5, 2, 2), new Vector(-1.5, 2, 0),
							new Vector(-1.5, 2, 2), new Vector(-2.5, 2, 0),
							new Vector(-2.5, 2, 2), new Vector(-4, 2, 0),
							new Vector(-4, 2, 2))
					.setStaticTurning(false).setRequireFuel(true).setTrunkSize(27).setWidth(2).setHeight(4.5)
					.setMaxHealth(200).setCost(4000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();


			VehicleYML.registerVehicle(VehicleTypes.CAR, "bmw", 37).setDisplayname("BMW")
					.setCenter(new Vector(-0.8, 0, 0.5)).setFrontVectorOffset(3.25).setBackVectorOffset(2.45)
					.setPassagerLocations(new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, 1))
					.setStopMeleeDamage(true).setStaticTurning(false).setRequireFuel(true).setWidth(0.74).setHeight(2).setMaterial(Material.RABBIT_HIDE)
					.setMaxHealth(100).setCost(9000).setKeyInputManagerLMB(FInputManager.CAR_HONK)
					.setLore("&7Press [Space] to break", "&7Press [LMB] to honk").save();


		}
	}

	public String getIngString(Material m, int durability, int amount) {
		return m.toString() + "," + durability + "," + amount;
	}


}
