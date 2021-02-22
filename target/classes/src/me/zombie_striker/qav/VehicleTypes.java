package me.zombie_striker.qav;

public enum VehicleTypes {
	CAR("Car"),PLANE("Plane"),BOAT("Boat"),HELI("Helicopter"),TRAIN("Train");
	private String name;
	private VehicleTypes(String name) {
		this.name= name;
	}
	public String getName() {
		return name;
	}
	public static VehicleTypes getTypeByName(String name) {
		for(VehicleTypes v : VehicleTypes.values()) {
			if(v.getName().equals(name))
				return v;
		}
		return null;
	}
}
