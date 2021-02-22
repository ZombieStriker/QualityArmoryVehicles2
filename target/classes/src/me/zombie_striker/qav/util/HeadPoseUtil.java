package me.zombie_striker.qav.util;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class HeadPoseUtil {

	static HashMap<VehicleEntity, Float> chaningPos = new HashMap<>();

	public static void setHeadPoseUsingReflection(VehicleEntity ve) {
		setHeadPoseUsingReflection(ve, ve.getModelEntity());
	}

	public static void setHeadPoseUsingReflection(final VehicleEntity ve, final ArmorStand a) {
		if (!ve.getType().enableBodyFix() || ve.getDriverSeat()!=ve.getModelEntity()) {
			updateArmorstandPart(ve,a,a.getHeadPose().getX(), ve.getAngleRotation(), a.getHeadPose().getZ());
			return;
		}

		if (chaningPos.containsKey(ve)) {
			if (Math.abs(ve.getAngleRotation() - chaningPos.get(ve)) < Math.PI / 2) {
				updateArmorstandPart(ve,a,a.getHeadPose().getX(), ve.getAngleRotation() - chaningPos.get(ve),
						a.getHeadPose().getZ());
				return;
			}
			updateArmorstandPart(ve,a,a.getHeadPose().getX(), ve.getAngleRotation() - chaningPos.get(ve), a.getHeadPose().getZ());
		}
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				ArmorStand e = (ArmorStand) a.getWorld().spawnEntity(a.getLocation(), EntityType.ARMOR_STAND);
				e.setVisible(false);
				e.setSmall(a.isSmall());
				e.setVelocity(a.getVelocity());
				Entity tempL = ve.getDriverSeat().getPassenger();
				ve.getDriverSeat().eject();
				e.setPassenger(tempL);

				Location temp = ve.getDriverSeat().getLocation();
				updateArmorstandPart(ve,a, a.getHeadPose().getX(), 0, a.getHeadPose().getZ());
				temp.setYaw((float) ve.getAngleRotation());
				Vector to = new Vector(-Math.sin(ve.getAngleRotation()), 0, Math.cos(ve.getAngleRotation()));
				temp.setDirection(to);
				ve.getDriverSeat().teleport(temp);
				a.setPassenger(tempL);
				ve.getDriverSeat().setVelocity(e.getVelocity());
				e.remove();
				chaningPos.put(ve, (float) ve.getAngleRotation());
			}
		}.runTaskLater(QualityArmoryVehicles.getPlugin(), 0);
	}

	public static void updateArmorstandPart(VehicleEntity ve, ArmorStand a, double x, double y, double z){
		/*if(ve.getType().getModelSize()== ModelSize.ADULT_ARMORSTAND_HAND){
			a.setRightArmPose(new EulerAngle((-Math.PI / 2) +x, y, z));
		}*/
		a.setHeadPose(new EulerAngle(x,y,z));
	}
}
