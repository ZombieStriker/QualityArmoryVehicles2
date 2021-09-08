package me.zombie_striker.qav.finput;


import me.zombie_striker.qav.VehicleEntity;

import java.util.Map;

public interface FInput {

	void onInputF(VehicleEntity ve);
	void onInputLMB(VehicleEntity ve);
	void onInputRMB(VehicleEntity ve);
	
	void serialize(Map<String, Object> map, VehicleEntity ve);
	
	String getName();
}
