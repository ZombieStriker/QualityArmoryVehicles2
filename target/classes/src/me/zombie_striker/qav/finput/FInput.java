package me.zombie_striker.qav.finput;


import me.zombie_striker.qav.VehicleEntity;

import java.util.Map;

public interface FInput {

	public void onInputF(VehicleEntity ve);
	public void onInputLMB(VehicleEntity ve);
	public void onInputRMB(VehicleEntity ve);
	
	public void serialize(Map<String,Object> map, VehicleEntity ve);
	
	public String getName();
}
