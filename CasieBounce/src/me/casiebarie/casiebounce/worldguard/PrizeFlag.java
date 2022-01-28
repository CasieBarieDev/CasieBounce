package me.casiebarie.casiebounce.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

import me.casiebarie.casiebounce.Main;

public class PrizeFlag extends Flag<String> {
	public PrizeFlag(String name) {super(name);}
	@Override
	public Object marshal(String o) {return o;}
	@Override
	public String parseInput(FlagContext context) throws InvalidFlagFormat {
		String input = context.getUserInput();
		switch (Main.checker.checkPrize(input)) {
		case 0: return input;
		case 1: throw new InvalidFlagFormat("Unable to validate Flag! Please use 'TYPE@VALUE'!");
		case 2: throw new InvalidFlagFormat("Unable to validate Flag! Please use MONEY, ITEM or PERMISSION!");
		case 3: throw new InvalidFlagFormat("Unable to validate Flag! Please use a Double as value!");
		case 4: throw new InvalidFlagFormat("Unable to validate Flag! Material is not recognized! Please refer to https://helpch.at/docs/" + Main.bukkitVersion + "/org/bukkit/Material.html for valid ids");
		case 5: throw new InvalidFlagFormat("Unable to validate Flag! Vault is not enabled!");
		default: throw new InvalidFlagFormat("Unable to validate Flag! I dont know why! (please contact CasieBarie for help)");}
	}
	@Override
	public String unmarshal(Object o) {return o.toString();}
}