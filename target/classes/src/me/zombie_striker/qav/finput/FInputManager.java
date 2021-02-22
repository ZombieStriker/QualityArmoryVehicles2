package me.zombie_striker.qav.finput;

import java.util.HashMap;

public class FInputManager {


	
	public static String CAR_HONK = "HONK";
	public static String POLICE_SIREN = ("SIREN");
	public static String MININUKE_BOMBER = ("MININUKE_BOMBER");
	public static String TNTBOMBER = ("TNT_BOMBER");
	public static String LAUNCHER_40mm = ("40mm_LAUNCHER");
	public static String LAUNCHER_556 = ("BULLETS_556");
	public static String TOGGLE_SPEED_HELI = ("TOGGLE_HELI_SPEED");
	

	public static HashMap<String, FInput> handlers = new HashMap<>();

	public static void add(FInput c) {
		handlers.put(c.getName(), c);
	}

	public static FInput getHandler(String name) {
		return handlers.get(name);
	}
}
