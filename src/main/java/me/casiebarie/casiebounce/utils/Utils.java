package me.casiebarie.casiebounce.utils;

import me.casiebarie.casiebounce.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class Utils {
	final Main plugin;
	public Utils(Main plugin) {this.plugin = plugin;}

	public String hex(String msg) {
		Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
		Matcher matcher = pattern.matcher(msg);
		while(matcher.find()) {
			String color = msg.substring(matcher.start(), matcher.end());
			msg = msg.replace(color, ChatColor.of(color) + "");
			matcher = pattern.matcher(msg);
		} return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public boolean checkBlock(String block) {
		ItemStack itemStack;
		if(block.contains(":")) {
			String[] itemSplit = block.split(":");
			try {itemStack = new ItemStack(Material.matchMaterial(itemSplit[0]), 1, Byte.parseByte(itemSplit[1]));
			} catch (Exception e) {return false;}
		} else {itemStack = new ItemStack(Material.matchMaterial(block));}
		return !itemStack.getType().equals(Material.AIR);
	}

	//0 = Good || 1 = Invalid minecraft sound || 2 = Custom sound
	public Integer checkSound(String sound) {
		if(sound.startsWith("CUSTOM:")) {return 2;}
		if(sound.equalsIgnoreCase("NONE")) {return 0;}
		try {Sound.valueOf(sound);
		} catch (IllegalArgumentException e) {return 1;
		} return 0;
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
		case "PERMISSION": return (plugin.vaultPresent()) ? 0 : 5;
		case "COMMAND": return 0;
		default: return 2;}
	}
}