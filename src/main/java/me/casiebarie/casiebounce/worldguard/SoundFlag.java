package me.casiebarie.casiebounce.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import me.casiebarie.casiebounce.Main;
import org.jetbrains.annotations.Nullable;

public class SoundFlag extends Flag<String>{
	protected SoundFlag(String name) {super(name);}
	@Override
	public String parseInput(FlagContext context) throws InvalidFlagFormat {
		String input = context.getUserInput();
		String f = "Unable to validate Flag! ";
		switch(Main.checker.checkSound(input)) {
		case 0: return input;
		case 1: throw new InvalidFlagFormat(f + "Invalid sound! Please refer to https://helpch.at/docs/" + Main.bukkitVersion + "/index.html?org/bukkit/Sound.html for valid ids");
		case 2: context.getSender().sendMessage(""); return input;
		default: throw new InvalidFlagFormat(f + "I dont know why! (please contact CasieBarie for help)");}
	}
	@Override public String unmarshal(@Nullable Object o) {return o.toString();}
	@Override public Object marshal(String s) {return s;}
}