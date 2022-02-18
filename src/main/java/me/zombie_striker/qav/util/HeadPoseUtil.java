package me.zombie_striker.qav.util;

import com.cryptomorin.xseries.ReflectionUtils;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.hooks.model.ModelEngineHook;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class HeadPoseUtil {
	private static final Method GET_HANDLE;
	private static final Field YAW;

	static {
		Method getHandle = null;
		Field yaw = null;

		try {
			Class<?> craftEntity = ReflectionUtils.getCraftClass("entity.CraftEntity");
			Class<?> nmsEntity = ReflectionUtils.getNMSClass("world.entity", "Entity");

			if (craftEntity != null && nmsEntity != null) {
				getHandle = craftEntity.getMethod("getHandle");
				yaw = nmsEntity.getField("yaw");
			}

		} catch (NoSuchFieldException | NoSuchMethodException ignored) {}

		GET_HANDLE = getHandle;
		YAW = yaw;
	}

	static HashMap<VehicleEntity, Float> chaningPos = new HashMap<>();

	public static void setHeadPoseUsingReflection(VehicleEntity ve) {
		setHeadPoseUsingReflection(ve, ve.getModelEntity());
	}

	public static void setHeadPoseUsingReflection(final VehicleEntity ve, final ArmorStand a) {
		if (!ve.getType().enableBodyFix() || ve.getDriverSeat()!=ve.getModelEntity() || true) {
			updateArmorstandPart(ve,a,a.getHeadPose().getX(), ve.getAngleRotation(), a.getHeadPose().getZ());
			if (ModelEngineHook.isInitialized()) {
				setYaw(ve,(float) Math.toDegrees(ve.getAngleRotation()));
			}
			return;
		}
		//TODO: Fix BodyFix
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

	public static void setYaw(@NotNull VehicleEntity entity, float yaw) {
		Main.DEBUG("Setting yaw to " + yaw);

		if (ReflectionUtils.supports(13)) {
			entity.getDriverSeat().setRotation(yaw, entity.getDriverSeat().getLocation().getPitch());
		} else {
			final EulerAngle headPose = ((ArmorStand) entity.getDriverSeat()).getHeadPose();

			try {
				if (GET_HANDLE != null && YAW != null) {
					YAW.set(GET_HANDLE.invoke(entity.getDriverSeat()), yaw);
					((ArmorStand) entity.getDriverSeat()).setHeadPose(headPose);
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				Main.DEBUG("Unable to set yaw with nms: " + e.getMessage());
			}
		}
	}
}
