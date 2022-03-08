package me.casiebarie.casiebounce.utils;

import me.casiebarie.casiebounce.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class ConfigManager {
	private static FileConfiguration config;
	final Main plugin; final Messages msg; static Utils utils;
	private static ArrayList<String> errors;
	private String defaultError(String setting, Object value, String type) {return "&cThe value '" + value + "' at setting '" + setting + "' cannot be recognized! &8(TYPE: " + type + ")";}
	public ConfigManager(Main plugin, Messages msg, Utils utils) {this.plugin = plugin; this.msg = msg; ConfigManager.utils = utils; reloadConfig(null);}
	public void checkConfig(CommandSender sender, Boolean isCommand) {
		errors = new ArrayList<>();
		for(configSettings setting : configSettings.values()) {
			Object value = config.get(setting.name());
			String type = setting.type.getSimpleName();
			if(value == null) {errors.add(defaultError(setting.name(), value, type)); continue;}
			if(!value.getClass().equals(setting.type)) {errors.add(defaultError(setting.name(), value, type)); continue;}
			setting.check(value);
		} if(errors.isEmpty()) {plugin.canBounce = true;
		} else {plugin.canBounce = false; msg.errorMessage(sender, errors, isCommand);}
	}

	public ArrayList<Object> getConfigSettings() {
		ArrayList<Object> configSettingsList = new ArrayList<>();
		for(configSettings setting : configSettings.values()) {
			if(setting.equals(configSettings.BounceSound)) {configSettingsList.add(config.get("." + setting.name()).toString().replace("CUSTOM:", ""));}
			else {configSettingsList.add(config.get("." + setting.name()));}}
		return configSettingsList;
	}

	public void reloadConfig(CommandSender sender) {
		File cFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
		config = YamlConfiguration.loadConfiguration(cFile);
		plugin.saveDefaultConfig();
		checkConfig(sender, false);
		msg.send(sender, "&6[&bCasieBounce&6] &aReloaded config.");
	}

	private enum configSettings {
		WorldGuardFlags(Boolean.class, 0),
		BounceForce(Double.class, 0),
		BounceSound(String.class, 1),
		BouncePrize(String.class, 2),
		StopWhenCrouch(Boolean.class, 0),
		FallDamage(Boolean.class, 0),
		DeathMessage(String.class, 0),
		RequirePermission(Boolean.class, 0),
		BounceBlocks(ArrayList.class, 3),
		IsBlockBlacklist(Boolean.class, 0);

		public final Class<?> type;
		public final Integer toCheck;
		configSettings(Class<?> type, Integer toCheck) {this.type = type; this.toCheck = toCheck;}
		public void check(Object input) {
			String start;
			switch (toCheck) {
			case 1:
				start = "&cThe value '" + input + "' at setting 'BounceSound' cannot be recognized! ";
				switch(utils.checkSound(input.toString())) {
				case 0: case 2: break;
				case 1: errors.add(start + "&cThe sound `" + input + "` in `BounceSound` is not a valid sound! &8Refer to https://helpch.at/docs/" + Main.bukkitVersion + "/index.html?org/bukkit/Sound.html for valid ids"); break;
				default: errors.add(start + "I dont know why! (please contact CasieBarie for help)");} break;
			case 2:
				start = "&cThe value '" + input + "' at setting 'BouncePrize' cannot be recognized! ";
				switch (utils.checkPrize(input.toString())) {
				case 0: break;
				case 1: errors.add(start + "Please use 'TYPE@VALUE'!"); break;
				case 2: errors.add(start + "Please use MONEY, ITEM, PERMISSION or COMMAND!"); break;
				case 3: errors.add(start + "Please use a Double as value!"); break;
				case 4: errors.add(start + "Material is not recognized! &8Please refer to https://helpch.at/docs/" + Main.bukkitVersion + "/org/bukkit/Material.html for valid ids"); break;
				case 5: errors.add(start + "Vault is not enabled!"); break;
				default: errors.add(start + "I dont know why! (please contact CasieBarie for help)");} break;
			case 3: for(String blockName : config.getStringList(".BounceBlocks")) {if(!utils.checkBlock(blockName)) {errors.add("&cThe block `" + blockName + "` in `BounceBlocks` is not a valid block! &8Refer to https://helpch.at/docs/" + Main.bukkitVersion + "/org/bukkit/Material.html for valid ids");}} break;}
		}
	}
}