package me.zombie_striker.qav.finput.inputs;

import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.finput.FInput;
import me.zombie_striker.qav.finput.FInputManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class FSiren implements FInput {

	HashMap<VehicleEntity, BukkitTask> storedSirens = new HashMap<>();

	public FSiren() {
		FInputManager.add(this);
	}

	@Override
	public void onInput(VehicleEntity ve) {
		if (storedSirens.containsKey(ve)) {
			storedSirens.remove(ve).cancel();
		} else {
			storedSirens.put(ve, new BukkitRunnable() {
				@Override
				public void run() {
					if (ve == null || ve.isInvalid()) {
						cancel();
						storedSirens.remove(ve);
						return;
					}
					try {
						ve.getDriverSeat().getWorld().playSound(ve.getDriverSeat().getLocation(),
								me.zombie_striker.qg.guns.utils.WeaponSounds.SIREN.getSoundName(), 2, (float) 1);
					} catch (Error | Exception e4) {
						ve.getDriverSeat().getWorld().playSound(ve.getDriverSeat().getLocation(),
								"siren", 2, (float) 1);

					}
				}
			}.runTaskTimer(QualityArmoryVehicles.getPlugin(), 0, 20));
		}
	}

	@Override
	public String getName() {
		return FInputManager.POLICE_SIREN;
	}

}
