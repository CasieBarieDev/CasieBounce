package me.casiebarie.casiebounce.managers.flag;

import org.bukkit.Material;

import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class BlockFlag extends MaterialFlag {
	public BlockFlag(String name) {super(name);}
	@Override
	public Material parseInput(FlagContext context) throws InvalidFlagFormat {
		Material material = super.parseInput(context);
		if(!material.isBlock()) {throw new InvalidFlagFormat("This material isn't seen as 'placable block', use alternative id");}
		return material;
	}
}