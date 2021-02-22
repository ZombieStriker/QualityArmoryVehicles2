package me.zombie_striker.qav.finput;


import me.zombie_striker.qav.VehicleEntity;

import java.util.Map;

public class FCarHonk implements FInput {

	public FCarHonk() {
		FInputManager.add(this);
	}

	@Override
	public void onInputF(VehicleEntity ve) {
		onInput(ve);
	}@Override
	public void onInputLMB(VehicleEntity ve) {		
		onInput(ve);
	}@Override
	public void onInputRMB(VehicleEntity ve) {		
		onInput(ve);
	}
	
	public void onInput(VehicleEntity ve) {
		ve.getDriverSeat().getWorld().playSound(ve.getDriverSeat().getLocation(),
				"honk", 1, (float) 1);
	}

	@Override
	public void serialize(Map<String, Object> map,VehicleEntity ve) {
	}

	@Override
	public String getName() {
		return FInputManager.CAR_HONK;
	}

}
