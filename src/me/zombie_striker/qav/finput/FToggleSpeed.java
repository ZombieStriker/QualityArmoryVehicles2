package me.zombie_striker.qav.finput;


import me.zombie_striker.qav.VehicleEntity;

import java.util.Map;

public class FToggleSpeed implements FInput {

	public FToggleSpeed() {
		FInputManager.add(this);
	}

	@Override
	public void onInputF(VehicleEntity ve) {

	}
	@Override
	public void onInputLMB(VehicleEntity ve) {		

	}
	@Override
	public void onInputRMB(VehicleEntity ve) {		

	}

	@Override
	public void serialize(Map<String, Object> map, VehicleEntity ve) {

	}

	@Override
	public String getName() {
		return FInputManager.TOGGLE_SPEED_HELI;
	}

}
