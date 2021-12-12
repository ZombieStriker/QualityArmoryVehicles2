package me.zombie_striker.qav.finput;


import me.zombie_striker.qav.VehicleEntity;

import java.util.Map;

public class FCarHonk implements FInput {

	public FCarHonk() {
		FInputManager.add(this);
	}

	@Override
	public void onInput(VehicleEntity ve) {
		ve.getDriverSeat().getWorld().playSound(ve.getDriverSeat().getLocation(),
				"honk", 1, (float) 1);
	}

	@Override
	public String getName() {
		return FInputManager.CAR_HONK;
	}

}
