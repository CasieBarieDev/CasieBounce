package me.casiebarie.casiebounce.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

import me.casiebarie.casiebounce.Main;

public class MaterialFlag extends Flag<String> {
	public MaterialFlag(String name) {super(name);}
	@Override
	public Object marshal(String o) {return o;}
	@Override
	public String parseInput(FlagContext context) throws InvalidFlagFormat {
		String input = context.getUserInput();
		if(input.equals("NONE")) {return input;}
		if(Main.checker.checkBlock(input)) {return input;
		} else {throw new InvalidFlagFormat("Unable to find the material! Please refer to https://helpch.at/docs/" + Main.bukkitVersion + "/org/bukkit/Material.html for valid ids");}
	}
	@Override
	public String unmarshal(Object o) {return o.toString();}
}