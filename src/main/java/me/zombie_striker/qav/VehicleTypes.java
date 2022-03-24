package me.zombie_striker.qav;

import org.jetbrains.annotations.NotNull;

public enum VehicleTypes {
	CAR("Car"),
	PLANE("Plane"),
	BOAT("Boat"),
	HELI("Helicopter"),
	TRAIN("Train"),
	DRILL("Drill"),
	TRACTOR("Tractor"),
	INVALID("Invalid");

	private String name;
	VehicleTypes(String name) {
		this.name= name;
	}

	public String getName() {
		return name;
	}

	public static @NotNull VehicleTypes getTypeByName(String name) {
		for(VehicleTypes v : VehicleTypes.values()) {
			if(v.getName().equals(name))
				return v;
		}
		return INVALID;
	}
}
