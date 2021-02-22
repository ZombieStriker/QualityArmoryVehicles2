package me.zombie_striker.qav.finput;


import me.zombie_striker.qav.VehicleEntity;

import java.util.Map;

public class FToggleSpeed implements FInput {

	public FToggleSpeed() {
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
	
	@SuppressWarnings("deprecation")
	public void onInput(VehicleEntity ve) {
		//ve.setFInputState((ve.getFInputState() + 1) % ve.getType().getFInputMax());
		//ve.getDriverSeat().getPassenger().sendMessage(MessagesConfig.MESSAGE_HELI_CHANGESPEED.replaceAll("%speed%", ""+ve.fState));
	}

	@Override
	public void serialize(Map<String, Object> map, VehicleEntity ve) {
		//map.put("heli-speed", ve.fState);
	}

	@Override
	public String getName() {
		return FInputManager.TOGGLE_SPEED_HELI;
	}

}
