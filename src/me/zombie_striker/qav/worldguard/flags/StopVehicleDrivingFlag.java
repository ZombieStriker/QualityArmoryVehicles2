package me.zombie_striker.qav.worldguard.flags;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

public class StopVehicleDrivingFlag extends Flag<State>{

	public StopVehicleDrivingFlag(String name) {
		super(name);
	}

	@Override
	public Object marshal(State arg0) {
		return arg0.toString();
	}


	@Override
	public State parseInput(FlagContext f) throws InvalidFlagFormat {
		String s = f.getUserInput().trim();
		return State.valueOf(s);
	}

	@Override
	public State unmarshal(Object arg0) {
		return State.valueOf(arg0.toString());
	}

}
