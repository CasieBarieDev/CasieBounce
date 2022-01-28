package me.casiebarie.casiebounce.worldguard;

import org.bukkit.Material;

import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class BlockFlag extends MaterialFlag {
	public BlockFlag(String name) {super(name);}
	@Override
	public String parseInput(FlagContext context) throws InvalidFlagFormat {
		String input = super.parseInput(context);
		Material material;
		if(input.contains(":")) {material = Material.matchMaterial(input.split(":")[0]);
		} else {material = Material.matchMaterial(input);}
		if(!material.isBlock()) {throw new InvalidFlagFormat("This material isn't seen as a 'placable block', use alternative id");}
		return input;
	}
}