package me.casiebarie.casiebounce.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.casiebarie.casiebounce.Main;

public class ConfigManager {
	private static FileConfiguration config;
	private Main plugin;
	private Messages msg;
	private Checker checker;
	private String defaultError(String setting, Object value, String type) {return "&cThe value '" + value + "' at setting '" + setting + "' cannot be recognized! &8(TYPE: " + type + ")";}
	public ConfigManager(Main plugin, Messages msg, Checker checker) {this.plugin = plugin; this.msg = msg; this.checker = checker; reloadConfig(null);}
	public void checkConfig(CommandSender sender, Boolean isCommand) {
		ArrayList<String> errors = new ArrayList<>();
		if(!(config.get(".WorldGuardFlags") instanceof Boolean)) {errors.add(defaultError("WorldGuardFlags", config.get(".WorldGuardFlags"), "Boolean"));}
		if(!(config.get(".BounceForce") instanceof Double)) {errors.add(defaultError("BounceForce", config.get(".BounceForce"), "Double"));}
		Object bounceSound = config.get(".BounceSound");
		if(!(bounceSound instanceof String)) {errors.add(defaultError("BounceSound", bounceSound, "String"));
		} else {if(!checker.checkSound(bounceSound.toString())) {errors.add("&cThe sound `" + bounceSound + "` in `BounceSound` is not a valid sound! &8Refer to https://helpch.at/docs/" + Main.bukkitVersion + "/index.html?org/bukkit/Sound.html for valid ids");}}
		Object bouncePrize = config.get(".BouncePrize");
		if(!(bouncePrize instanceof String)) {errors.add(defaultError("BouncePrize", bouncePrize, "String"));
		} else {
			String start = "&cThe value '" + bouncePrize + "' at setting 'BouncePrize' cannot be recognized! ";
			switch (checker.checkPrize(bouncePrize.toString())) {
			case 0: break;
			case 1: errors.add(start + "Please use 'TYPE@VALUE'!"); break;
			case 2: errors.add(start + "Please use MONEY, ITEM or PERMISSION!"); break;
			case 3: errors.add(start + "Please use a Double as value!"); break;
			case 4: errors.add(start + "Material is not recognized! &8Please refer to https://helpch.at/docs/" + Main.bukkitVersion + "/org/bukkit/Material.html for valid ids"); break;
			case 5: errors.add(start + "Vault is not enabled!"); break;
			default: errors.add(start + "I dont know why! (please contact CasieBarie for help)");}
		}
		if(!(config.get(".StopWhenCrouch") instanceof Boolean)) {errors.add(defaultError("StopWhenCrouch", config.get(".StopWhenCrouch"), "Boolean"));}
		if(!(config.get(".FallDamage") instanceof Boolean)) {errors.add(defaultError("FallDamage", config.get(".FallDamage"), "Boolean"));}
		if(!(config.get(".DeathMessage") instanceof String)) {errors.add(defaultError("DeathMessage", config.get(".DeathMessage"), "String"));}
		if(!(config.get(".RequirePermission") instanceof Boolean)) {errors.add(defaultError("RequirePermission", config.get(".RequirePermission"), "Boolean"));}
		Object bounceBlocks = config.get(".BounceBlocks");
		if(!(bounceBlocks instanceof List<?>)) {errors.add(defaultError("BounceBlocks", bounceBlocks, "List"));
		} else {for(String blockName : config.getStringList(".BounceBlocks")) {if(!checker.checkBlock(blockName)) {errors.add("&cThe block `" + blockName + "` in `BounceBlocks` is not a valid block! &8Refer to https://helpch.at/docs/" + Main.bukkitVersion + "/org/bukkit/Material.html for valid ids");}}}
		if(!(config.get(".IsBlockBlacklist") instanceof Boolean)) {errors.add(defaultError("IsBlockBlacklist", config.get(".IsBlockBlacklist"), "Boolean"));}
		if(errors.isEmpty()) {plugin.canBounce = true;
		} else {plugin.canBounce = false; msg.errorMessage(sender, errors, isCommand);}
	}

	// 0 = WorldGuardEnabled | 1 = BounceForce | 2 = BounceSound | 3 = BouncePrize | 4 = StopWhenCrouch
	// 5 = FallDamage | 6 = DeathMessage | 7 = RequirePermission | 8 = BounceBlocks | 9 = IsBlockBlackList
	public ArrayList<Object> getConfigSettings() {
		ArrayList<Object> configSettingsLiss = new ArrayList<>();
		configSettingsLiss.add(config.getBoolean(".WorldGuardFlags"));
		configSettingsLiss.add(config.getDouble(".BounceForce"));
		configSettingsLiss.add(config.getString(".BounceSound"));
		configSettingsLiss.add(config.getString(".BouncePrize"));
		configSettingsLiss.add(config.getBoolean(".StopWhenCrouch"));
		configSettingsLiss.add(config.getBoolean(".FallDamage"));
		configSettingsLiss.add(config.getString(".DeathMessage"));
		configSettingsLiss.add(config.getBoolean(".RequirePermission"));
		configSettingsLiss.add(config.getStringList(".BounceBlocks"));
		configSettingsLiss.add(config.getBoolean(".IsBlockBlacklist"));
		return configSettingsLiss;
	}

	public void reloadConfig(CommandSender sender) {
		File cFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
		config = YamlConfiguration.loadConfiguration(cFile);
		plugin.saveDefaultConfig();
		checkConfig(sender, false);
		msg.send(sender, "&6[&bCasieBounce&6] &aReloaded config.");
	}
}