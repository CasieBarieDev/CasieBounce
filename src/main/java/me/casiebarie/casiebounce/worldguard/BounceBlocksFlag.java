package me.casiebarie.casiebounce.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import me.casiebarie.casiebounce.Main;
import org.bukkit.Material;

public class BounceBlocksFlag extends Flag<String> {
	public BounceBlocksFlag(String name) {super(name);}
	@Override public String unmarshal(Object o) {return o.toString();}
	@Override public Object marshal(String o) {return o;}
	@Override
	public String parseInput(FlagContext context) throws InvalidFlagFormat {
		String input = context.getUserInput();
		if(input.equals("NONE")) {return input;}
		if(!Main.checker.checkBlock(input)) {throw new InvalidFlagFormat("Unable to find the material! Please refer to https://helpch.at/docs/" + Main.bukkitVersion + "/org/bukkit/Material.html for valid ids");}
		Material material;
		if(input.contains(":")) {material = Material.matchMaterial(input.split(":")[0]);
		} else {material = Material.matchMaterial(input);}
		if(!material.isBlock()) {throw new InvalidFlagFormat("This material isn't seen as a 'placable block', use alternative id");}
		return input;
	}
}