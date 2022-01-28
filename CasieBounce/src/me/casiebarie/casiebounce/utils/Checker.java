package me.casiebarie.casiebounce.utils;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import me.casiebarie.casiebounce.Main;
@SuppressWarnings("deprecation")
public class Checker {
	private Main plugin;
	public Checker(Main plugin) {this.plugin = plugin;}

	public boolean checkBlock(String block) {
		ItemStack itemStack;
		if(block.contains(":")) {
			String[] itemSplit = block.split(":");
			try {itemStack = new ItemStack(Material.matchMaterial(itemSplit[0]), 1, Byte.parseByte(itemSplit[1]));
			} catch (Exception e) {return false;}
		} else {itemStack = new ItemStack(Material.matchMaterial(block));}
		if(!itemStack.getType().equals(Material.AIR)) {return true;}
		return false;
	}

	public boolean checkSound(String sound) {
		if(sound.equalsIgnoreCase("NONE")) {return true;}
		try {Sound.valueOf(sound.toString());
		} catch (IllegalArgumentException e) {return false;}
		return true;
	}

	//0 = Good || 1 = Doenst contain @ || 2 = Wrong type || 3 = Not a number || 4 = Not a Item || 5 = Vault not present
	public Integer checkPrize(String prize) {
		if(prize.equals("NONE")) {return 0;}
		if(!prize.contains("@")) {return 1;}
		String[] prizeSplit = prize.split("@");
		switch (prizeSplit[0].toUpperCase()) {
		case "MONEY":
			if(!plugin.vaultPresent()) {return 5;}
			try {Double.parseDouble(prizeSplit[1]); return 0;
			} catch (NumberFormatException e) {return 3;}
		case "ITEM": return (checkBlock(prizeSplit[1])) ? 0 : 4;
		case "PERMISSION": if(!plugin.vaultPresent()) {return 5;} return 0;
		default: return 2;}
	}
}