package me.casiebarie.casiebounce.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.casiebarie.casiebounce.Main;

public class ConfigManager {
	private static FileConfiguration config;
	private Main plugin;
	private Messages msg;
	public ConfigManager(Main plugin, Messages msg) {this.plugin = plugin; this.msg = msg; reloadConfig(null);}
	public void checkConfig(CommandSender sender, Boolean isCommand) {
		ArrayList<Object> errors = new ArrayList<>();
		if(!(config.get(".WorldGuardFlags") instanceof Boolean)) {errors.add("WorldGuardFlags"); errors.add(config.get(".WorldGuardFlags"));}
		if(!(config.get(".BounceForce") instanceof Double)) {errors.add("BounceForce"); errors.add(config.get(".BounceForce"));}
		Object bounceSound = config.get(".BounceSound");
		if(!(bounceSound instanceof String)) {errors.add("BounceSound"); errors.add(bounceSound);
		} else {if(!checkSound(bounceSound.toString())) {errors.add("BounceSound"); errors.add(bounceSound);}}
		if(!(config.get(".StopWhenCrouch") instanceof Boolean)) {errors.add("StopWhenCrouch"); errors.add(config.get(".StopWhenCrouch"));}
		if(!(config.get(".FallDamage") instanceof Boolean)) {errors.add("FallDamage"); errors.add(config.get(".FallDamage"));}
		if(!(config.get(".DeathMessage") instanceof String)) {errors.add("DeathMessage"); errors.add(config.get(".DeathMessage"));}
		if(!(config.get(".RequirePermission") instanceof Boolean)) {errors.add("RequirePermission"); errors.add(config.get(".RequirePermission"));}
		if(!(config.get(".IsBlockBlacklist") instanceof Boolean)) {errors.add("IsBlockBlacklist"); errors.add(config.get(".IsBlockBlacklist"));}
		Object bounceBlocks = config.get(".BounceBlocks");
		if(!(bounceBlocks instanceof List<?>)) {errors.add("BounceBlocks"); errors.add(bounceBlocks);
		} else {for(String blockName : config.getStringList(".BounceBlocks")) {if(!checkBlock(blockName)) {errors.add("BounceBlocks_"); errors.add(blockName);}}}
		if(!errors.isEmpty()) {plugin.canBounce = false; msg.errorMessage(sender, errors, isCommand);
		} else {plugin.canBounce = true;}
	}

	public boolean checkBlock(String block) {
		if(block.contains(":")) {
			String[] blockSplit = block.split(":");
			if(blockSplit.length > 2) {return false;}
			try {Byte.parseByte(blockSplit[1]);
			} catch (NumberFormatException e) {return false;}
			if(Material.getMaterial(blockSplit[0]) != null) {return true;}
		} else if(Material.getMaterial(block) != null) {return true;}
		return false;
	}

	private boolean checkSound(String sound) {
		if(sound.equalsIgnoreCase("NONE")) {return true;}
		try {Sound.valueOf(sound);
		} catch (IllegalArgumentException e) {return false;}
		return true;
	}

	// 0 = Enabled | 1 = BounceForce | 2 = BounceSound | 3 = StopWhenCrouch | 4 = FallDamage
	// 5 = DeathMessage | 6 = RequirePermission | 7 = BounceBlocks | 8 = IsBlockBlackList
	public ArrayList<Object> getConfigSettings() {
		ArrayList<Object> configSettings = new ArrayList<>();
		configSettings.add(config.getBoolean(".WorldGuardFlags"));
		configSettings.add(config.getDouble(".BounceForce"));
		configSettings.add(config.getString(".BounceSound"));
		configSettings.add(config.getBoolean(".StopWhenCrouch"));
		configSettings.add(config.getBoolean(".FallDamage"));
		configSettings.add(config.getString(".DeathMessage"));
		configSettings.add(config.getBoolean(".RequirePermission"));
		configSettings.add(config.getStringList(".BounceBlocks"));
		configSettings.add(config.getBoolean(".IsBlockBlacklist"));
		return configSettings;
	}

	public void reloadConfig(CommandSender sender) {
		File cFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
		config = YamlConfiguration.loadConfiguration(cFile);
		plugin.saveDefaultConfig();
		checkConfig(sender, false);
		msg.send(sender, "&6[&bCasieBounce&6] &aReloaded config.");
	}
}