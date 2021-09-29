package me.casiebarie.casiebounce.managers.flag;

import java.util.Collection;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

import me.casiebarie.casiebounce.managers.flag.ForcedStateFlag.ForcedState;

public class ForcedStateFlag extends Flag<ForcedState> {
	public static enum ForcedState {ENABLED, DISABLED, REDUCED}
	public ForcedStateFlag(String name) {super(name);}
	@Override
	public ForcedState getDefault() {return ForcedState.DISABLED;}
	@Override
	public boolean hasConflictStrategy() {return true;}
	@Override
	public ForcedState chooseValue(Collection<ForcedState> values) {
		ForcedState result = null;
		if(!values.isEmpty()) {
			for(ForcedState state : values) {
				if(state == ForcedState.ENABLED) {result = ForcedState.ENABLED;}
				else if(state == ForcedState.REDUCED) {result = ForcedState.REDUCED;}
				else if(state == ForcedState.DISABLED) {if(result == null) {result = ForcedState.DISABLED;}}
			}
		} return result;
	}
	@Override
	public ForcedState parseInput(FlagContext context) throws InvalidFlagFormat {
		String input = context.getUserInput();
		if(input.equalsIgnoreCase("ENABLED")) {return ForcedState.ENABLED;}
		else if(input.equalsIgnoreCase("DISABLED")) {return ForcedState.DISABLED;}
		else if(input.equalsIgnoreCase("REDUCED")) {return ForcedState.REDUCED;}
		else if (input.equalsIgnoreCase("none")) {return null;}
		else {throw new InvalidFlagFormat("Expected ENABLED/DISABLED/REDUCED but got '" + input + "'");}
	}
	@Override
	public ForcedState unmarshal(Object o) {
		String str = o.toString();
		switch(str) {
			case "ENABLED":
				return ForcedState.ENABLED;
			case "DISABLED":
				return ForcedState.DISABLED;
			case "REDUCED":
				return ForcedState.REDUCED;
			default:
				return null;
		}
	}
	@Override
	public Object marshal(ForcedState o) {
		if(o == null) {return null;}
		return o.toString();
	}
}