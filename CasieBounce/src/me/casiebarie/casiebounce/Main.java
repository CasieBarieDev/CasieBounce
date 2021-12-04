package me.casiebarie.casiebounce;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.casiebarie.casiebounce.managers.ConfigManager;
import me.casiebarie.casiebounce.managers.WorldGuardManager;
import me.casiebarie.casiebounce.managers.flag.BlockFlag;
import me.casiebarie.casiebounce.managers.flag.SoundFlag;
import me.casiebarie.casiebounce.other.Commands;
import me.casiebarie.casiebounce.other.Messages;
import me.casiebarie.casiebounce.other.Metrics;
import me.casiebarie.casiebounce.other.TabComplete;
import me.casiebarie.casiebounce.other.UpdateChecker;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	public static WorldGuardPlugin worldGuardPlugin;
	public static ConfigManager configManager;
	//WorldGuard Flags
	public static DoubleFlag CB_BOUNCEFORCE;
	public static StringFlag CB_DEATHMESSAGE;
	public static SetFlag<Sound> CB_BOUNCESOUND;
	public static SetFlag<Material> CB_BOUNCEBLOCKS;
	public static BooleanFlag CB_ENABLED, CB_STOPWHENCROUCH, CB_REQUIREPERMISSION, CB_ISBLOCKBLACKLIST, CB_FALLDAMAGE;
	public boolean canBounce, isLegacy, wgEnabled, wgError;
	public int bounces;
	private FileConfiguration config;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		//CALL CONSTRUCTORS
		WorldGuardManager wgM = new WorldGuardManager(this);
		Messages msg = new Messages(this, wgM);
		configManager = new ConfigManager(this, msg);
		new Commands(this, configManager, msg);
		new TabComplete(this);
		if(isLegacy) {new BounceLegacy(this, configManager);
		} else if(!isLegacy) {new Bounce(this, wgM, configManager);}
		new UpdateChecker(this, 90967, "CB.admin").checkForUpdate();

		//METRICS
		Metrics metrics = new Metrics(this, 13216);
		//Pie - Using WorldGuard
		metrics.addCustomChart(new Metrics.SimplePie("using_worldguard", () -> {
			if(configManager.getConfigSettings().get(0).equals(true) && wgEnabled) {return "True";}
			else {return "False";}
		}));
		//Line - Bounces
		metrics.addCustomChart(new Metrics.SingleLineChart("bounces", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {Integer rBounce = bounces; bounces = 0; return rBounce;}
		}));
		//Pie - Region Count
		metrics.addCustomChart(new Metrics.SimplePie("amount_of_regions", () -> {
			if(configManager.getConfigSettings().get(0).equals(true) && wgEnabled) {return getRegionAmount();
			} else {return "No WorldGuard";}
		}));
		//*Pie - BounceBlocks
		metrics.addCustomChart(new Metrics.AdvancedPie("bounce_blocks", new
				Callable<Map<String,Integer>>() {
			@Override public Map<String, Integer> call() throws Exception {
				Map<String,Integer> valueMap = new HashMap<>();
				ArrayList<String> bounceBlocks = new ArrayList<>();
				bounceBlocks = getAllBounceBlocks();
				for(String bounceBlock : bounceBlocks) {valueMap.put(bounceBlock, 1);}
				return valueMap;
			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoad() {
		legacyCheck();
		worldGuardPlugin = getWorldGuard();
		File cFile = new File(getDataFolder() + File.separator + "config.yml");
		config = YamlConfiguration.loadConfiguration(cFile);
		wgEnabled = false;
		wgError = false;
		//WorldGuard Check
		if(worldGuardPlugin == null) {getLogger().warning("WorldGuard not found, flags disabled!"); return;}
		getLogger().info("WorldGuard found!");
		if(!(config.get(".WorldGuardFlags") instanceof Boolean)) {return;}
		if(config.getBoolean(".WorldGuardFlags")) {
			if(isLegacy) {getLogger().warning("This is a legacy server, flags disabled!"); return;}
			//Create Flags
			wgEnabled = true;
			getLogger().info("Creating WorldGuard flags...");
			FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
			try {
				//Enabled
				try {
					BooleanFlag flag = new BooleanFlag("cb-enabled");
					registry.register(flag);
					CB_ENABLED = flag;
				} catch (FlagConflictException e) {
					Flag<?> existing = registry.get("cb-enabled");
					if(existing instanceof BooleanFlag) {CB_ENABLED = (BooleanFlag) existing;}
				}
				//BounceForce
				try {
					DoubleFlag flag = new DoubleFlag("cb-bounceforce");
					registry.register(flag);
					CB_BOUNCEFORCE = flag;
				} catch (FlagConflictException e) {
					Flag<?> existing = registry.get("cb-bounceforce");
					if(existing instanceof DoubleFlag) {CB_BOUNCEFORCE = (DoubleFlag) existing;}
				}
				//BounceSound
				try {
					SetFlag<Sound> flag = new SetFlag<>("cb-bouncesound", new SoundFlag(null));
					registry.register(flag);
					CB_BOUNCESOUND = flag;
				} catch (FlagConflictException e) {
					Flag<?> existing = registry.get("cb-bouncesound");
					if(existing instanceof SetFlag<?>) {CB_BOUNCESOUND = (SetFlag<Sound>) existing;}
				}
				//FallDamage
				try {
					BooleanFlag flag = new BooleanFlag("cb-falldamage");
					registry.register(flag);
					CB_FALLDAMAGE = flag;
				} catch (FlagConflictException e) {
					Flag<?> existing = registry.get("cb-falldamage");
					if(existing instanceof BooleanFlag) {CB_FALLDAMAGE = (BooleanFlag) existing;}
				}
				//DeathMessage
				try {
					StringFlag flag = new StringFlag("cb-deathmessage");
					registry.register(flag);
					CB_DEATHMESSAGE = flag;
				} catch (FlagConflictException e) {
					Flag<?> existing = registry.get("cb-deathmessage");
					if(existing instanceof StringFlag) {CB_DEATHMESSAGE = (StringFlag) existing;}
				}

				//BounceBlocks
				try {
					SetFlag<Material> flag = new SetFlag<>("cb-bounceblocks", new BlockFlag(null));
					registry.register(flag);
					CB_BOUNCEBLOCKS = flag;
				} catch (FlagConflictException e) {
					Flag<?> existing = registry.get("cb-bounceblocks");
					if(existing instanceof SetFlag<?>) {CB_BOUNCEBLOCKS = (SetFlag<Material>) existing;}
				}

				//StopWhenCrouch
				try {
					BooleanFlag flag = new BooleanFlag("cb-stopwhencrouch");
					registry.register(flag);
					CB_STOPWHENCROUCH = flag;
				} catch (FlagConflictException e) {
					Flag<?> existing = registry.get("cb-stopwhencrouch");
					if(existing instanceof BooleanFlag) {CB_STOPWHENCROUCH = (BooleanFlag) existing;}
				}
				//RequirePermission
				try {
					BooleanFlag flag = new BooleanFlag("cb-requirepermission");
					registry.register(flag);
					CB_REQUIREPERMISSION = flag;
				} catch (FlagConflictException e) {
					Flag<?> existing = registry.get("cb-requirepermission");
					if(existing instanceof BooleanFlag) {CB_REQUIREPERMISSION = (BooleanFlag) existing;}
				}
				//IsBlockBlacklist
				try {
					BooleanFlag flag = new BooleanFlag("cb-isblockblacklist");
					registry.register(flag);
					CB_ISBLOCKBLACKLIST = flag;
				} catch (FlagConflictException e) {
					Flag<?> existing = registry.get("cb-isblockblacklist");
					if(existing instanceof BooleanFlag) {CB_ISBLOCKBLACKLIST = (BooleanFlag) existing;}
				}
			} catch (Exception e) {
				getLogger().log(Level.SEVERE, "Failed to (re)create WorldGuard flags!");
				getLogger().log(Level.SEVERE, "Due to a reload the WorldGuard flags are disabled. Please fully restart your server to fix it!");
				wgEnabled = false;
				wgError = true;
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(player.hasPermission("CB.admin")) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&bCasieBounce&6] &cDue to a reload the WorldGuard flags are disabled. Please fully restart your server to fix it!"));
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> getAllBounceBlocks() {
		ArrayList<String> bounceBlocks = new ArrayList<>();
		if(configManager.getConfigSettings().get(0).equals(true) && wgEnabled) {
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			for(World world : getServer().getWorlds()) {
				RegionManager regions = container.get(BukkitAdapter.adapt(world));
				Map<String, ProtectedRegion> regionMap = regions.getRegions();
				for(Entry<String, ProtectedRegion> regionEntry : regionMap.entrySet()) {
					for(Material block : regionEntry.getValue().getFlag(CB_BOUNCEBLOCKS)) {
						if(!bounceBlocks.contains(block.name().toUpperCase())) {bounceBlocks.add(block.name().toUpperCase());}
					}
				}
			}
		}
		List<String> configBounceBlocks = (List<String>) configManager.getConfigSettings().get(8);
		for(String blockName : configBounceBlocks) {if(!bounceBlocks.contains(blockName.toUpperCase())) {bounceBlocks.add(blockName.toUpperCase());}}
		return bounceBlocks;
	}

	private String getRegionAmount() {
		int regionAmount = 0;
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		for(World world : getServer().getWorlds()) {
			RegionManager regions = container.get(BukkitAdapter.adapt(world));
			Map<String, ProtectedRegion> regionMap = regions.getRegions();
			for(Entry<String, ProtectedRegion> regionEntry : regionMap.entrySet()) {
				if(regionEntry.getValue().getFlag(CB_ENABLED).booleanValue()) {regionAmount += 1;}
			}
		} return "" + regionAmount;
	}

	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
		if(plugin == null || !(plugin instanceof WorldGuardPlugin)) {return null;}
		return (WorldGuardPlugin) plugin;
	}

	public void legacyCheck() {
		final String BukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.v","");
		final List<String> LegacyVersions = Arrays.asList("1_8_R1", "1_8_R2", "1_8_R3", "1_9_R1", "1_9_R2", "1_10_R1", "1_11_R1", "1_12_R1");
		final List<String> Versions = Arrays.asList("1_13_R1", "1_13_R2", "1_14_R1", "1_15_R1", "1_16_R1", "1_16_R2", "1_16_R3", "1_17_R1", "1_17_R2", "1_18_R1", "");
		if(LegacyVersions.contains(BukkitVersion)) {isLegacy = true;
		} else if(Versions.contains(BukkitVersion)) {isLegacy = false;
		} else {
			//Disable Plugin
			getLogger().severe("Incompatible version. Disabling plugin");
			getServer().getPluginManager().disablePlugin(this);
		}
	}
}