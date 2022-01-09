package me.casiebarie.casiebounce;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.casiebarie.casiebounce.sqlite.Database;
import me.casiebarie.casiebounce.sqlite.SQLite;
import me.casiebarie.casiebounce.utils.Commands;
import me.casiebarie.casiebounce.utils.ConfigManager;
import me.casiebarie.casiebounce.utils.Messages;
import me.casiebarie.casiebounce.utils.Metrics;
import me.casiebarie.casiebounce.utils.PapiExpansion;
import me.casiebarie.casiebounce.utils.UpdateChecker;
import me.casiebarie.casiebounce.worldguard.WorldGuardManager;

public class Main extends JavaPlugin {
	private Database db;
	public Database getDatabase() {return db;}
	public static WorldGuardPlugin worldGuardPlugin;
	public static ConfigManager cM;
	public WorldGuardManager wgM;
	public boolean canBounce, isLegacy, wgEnabled, wgError;
	public int mBounces;
	private FileConfiguration config;
	@Override
	public void onEnable() {
		saveDefaultConfig();
		db = new SQLite(this);
		db.load();
		Messages msg = new Messages(this, wgM);
		cM = new ConfigManager(this, msg);
		new Commands(this, cM, msg);
		new Bounce(this, wgM, cM);
		if(papiPresent()) {new PapiExpansion(this, db).register();}
		new UpdateChecker(this, 90967, "CB.admin").checkForUpdate();
		Metrics metrics = new Metrics(this, 13216);
		createCharts(metrics);
	}

	@Override
	public void onLoad() {
		legacyCheck();
		worldGuardPlugin = getWorldGuard();
		File cFile = new File(getDataFolder() + File.separator + "config.yml");
		config = YamlConfiguration.loadConfiguration(cFile);
		wgEnabled = false;
		wgError = false;
		if(worldGuardPlugin == null) {getLogger().warning("WorldGuard not found, flags disabled!"); return;}
		getLogger().info("WorldGuard found!");
		if(!(config.get(".WorldGuardFlags") instanceof Boolean) || !config.getBoolean(".WorldGuardFlags")) {return;}
		wgM = new WorldGuardManager(this);
		wgM.registerFlags();
	}

	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
		if(plugin == null || !(plugin instanceof WorldGuardPlugin)) {return null;}
		return (WorldGuardPlugin) plugin;
	}

	public boolean papiPresent() {if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {return true;} else {return false;}}
	public void legacyCheck() {
		final String BukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.v","");
		final List<String> LegacyVersions = Arrays.asList("1_8_R1", "1_8_R2", "1_8_R3", "1_9_R1", "1_9_R2", "1_10_R1", "1_11_R1", "1_12_R1");
		final List<String> Versions = Arrays.asList("1_13_R1", "1_13_R2", "1_14_R1", "1_15_R1", "1_16_R1", "1_16_R2", "1_16_R3", "1_17_R1", "1_17_R2", "1_18_R1", "");
		if(LegacyVersions.contains(BukkitVersion)) {isLegacy = true;
		} else if(Versions.contains(BukkitVersion)) {isLegacy = false;
		} else {getLogger().severe("Incompatible version. Disabling plugin"); getServer().getPluginManager().disablePlugin(this);}
	}

	private void createCharts(Metrics metrics) {
		metrics.addCustomChart(new Metrics.SimplePie("using_worldguard", () -> {return (wgEnabled) ? "True" : "False";}));
		metrics.addCustomChart(new Metrics.SingleLineChart("bounces", new Callable<Integer>() {@Override public Integer call() throws Exception {Integer rBounce = mBounces; mBounces = 0; return rBounce;}}));
		metrics.addCustomChart(new Metrics.SimplePie("amount_of_regions", () -> {return (wgEnabled) ? wgM.getRegionAmmount() : "No WorldGuard";}));
		metrics.addCustomChart(new Metrics.AdvancedPie("bounce_blocks", new Callable<Map<String,Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				Map<String,Integer> valueMap = new HashMap<>();
				ArrayList<String> bounceBlocks = new ArrayList<>();
				if(wgEnabled) {bounceBlocks.addAll(wgM.getAllBounceBlocks());}
				@SuppressWarnings("unchecked")
				List<String> configBounceBlocks = (List<String>) cM.getConfigSettings().get(7);
				for(String blockName : configBounceBlocks) {
					if(!cM.checkBlock(blockName)) {continue;}
					if(!bounceBlocks.contains(blockName.toUpperCase())) {bounceBlocks.add(blockName.toUpperCase());}
				} for(String bounceBlock : bounceBlocks) {valueMap.put(bounceBlock, 1);} return valueMap;
			}
		}));
	}
}