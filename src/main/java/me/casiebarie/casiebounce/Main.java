package me.casiebarie.casiebounce;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.casiebarie.casiebounce.database.*;
import me.casiebarie.casiebounce.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class Main extends JavaPlugin {
	private Database db;
	public Database getDatabase() {return db;}
	static WorldGuardPlugin worldGuardPlugin;
	public static Economy econ;
	public static Permission perm;
	public static Utils utils;
	private ConfigManager cM;
	public static WorldGuardManager wgM;
	public boolean canBounce, isLegacy, wgEnabled, wgError;
	public static String bukkitVersion;
	public int mBounces;
	FileConfiguration config;
	public boolean papiPresent() {return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;}
	@Override
	public void onEnable() {
		saveDefaultConfig();
		db = new SQLite(this);
		db.load();
		utils = new Utils(this);
		wgM = new WorldGuardManager(this);
		ResetData rD = new ResetData(this);
		Messages msg = new Messages(this, wgM);
		cM = new ConfigManager(this, msg, utils);
		PrizeManager pM = new PrizeManager(this);
		new Commands(this, db, cM, msg, rD);
		new Bounce(this, utils, wgM, cM, pM);
		if(papiPresent()) {new PapiExpansion(this, db).register();}
		new UpdateChecker(this, 90967, "CB.admin", ChatColor.YELLOW).checkForUpdate();
		Metrics metrics = new Metrics(this, 13216);
		createCharts(metrics);
	}

	@Override
	public void onLoad() {
		legacyCheck();
		worldGuardPlugin = getWorldGuard();
		File cFile = new File(getDataFolder() + File.separator + "config.yml");
		config = YamlConfiguration.loadConfiguration(cFile);
		wgEnabled = false; wgError = false;
		if(worldGuardPlugin == null) {getLogger().warning("WorldGuard not found, flags disabled!"); return;}
		getLogger().info("WorldGuard found!");
		if(!(config.get(".WorldGuardFlags") instanceof Boolean) || !config.getBoolean(".WorldGuardFlags")) {return;}
		new WorldGuardManager(this).registerFlags();
	}

	public void legacyCheck() {
		final String bukkitVersion = getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.v","");
		Main.bukkitVersion = bukkitVersion.replaceFirst("_", ".").split("_")[0];
		final List<String> legacyVersions = Arrays.asList("1_8_R1", "1_8_R2", "1_8_R3", "1_9_R1", "1_9_R2", "1_10_R1", "1_11_R1", "1_12_R1");
		final List<String> versions = Arrays.asList("1_13_R1", "1_13_R2", "1_14_R1", "1_15_R1", "1_16_R1", "1_16_R2", "1_16_R3", "1_17_R1", "1_17_R2", "1_18_R1", "1_18_R2");
		if(legacyVersions.contains(bukkitVersion)) {isLegacy = true;
		} else if(versions.contains(bukkitVersion)) {isLegacy = false;
		} else {getLogger().severe("Incompatible version. Disabling plugin"); getServer().getPluginManager().disablePlugin(this);}
	}

	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if(!(plugin instanceof WorldGuardPlugin)) {return null;}
		return (WorldGuardPlugin) plugin;
	}

	public boolean vaultPresent() {
		if(getServer().getPluginManager().getPlugin("Vault") == null) {return false;}
		RegisteredServiceProvider<Economy> rspE = getServer().getServicesManager().getRegistration(Economy.class);
		RegisteredServiceProvider<Permission> rspP = getServer().getServicesManager().getRegistration(Permission.class);
		if(rspE == null || rspP == null) {return false;}
		Main.econ = rspE.getProvider(); Main.perm = rspP.getProvider();
		return true;
	}

	@SuppressWarnings("unchecked")
	private void createCharts(Metrics metrics) {
		metrics.addCustomChart(new SimplePie("using_worldguard", () -> (wgEnabled) ? "True" : "False"));
		metrics.addCustomChart(new SingleLineChart("bounces", () -> {int rBounce = mBounces; mBounces = 0; return rBounce;}));
		metrics.addCustomChart(new SimplePie("amount_of_regions", () -> (wgEnabled) ? wgM.getRegionAmmount() : "No WorldGuard"));
		metrics.addCustomChart(new AdvancedPie("bounce_blocks", () -> {
			Map<String,Integer> valueMap = new HashMap<>();
			ArrayList<String> bounceBlocks = new ArrayList<>();
			if(wgEnabled) {bounceBlocks.addAll(wgM.getAllBounceBlocks());}
			List<String> configBounceBlocks = (List<String>) cM.getConfigSettings().get(8);
			for(String blockName : configBounceBlocks) {
				if(!utils.checkBlock(blockName)) {continue;}
				if(!bounceBlocks.contains(blockName.toUpperCase())) {bounceBlocks.add(blockName.toUpperCase());}
			} for(String bounceBlock : bounceBlocks) {valueMap.put(bounceBlock, 1);} return valueMap;
		}));
	}
}