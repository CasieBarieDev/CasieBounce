package me.casiebarie.casiebounce;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import me.casiebarie.casiebounce.managers.ConfigManager;
import me.casiebarie.casiebounce.managers.WorldGuardManager;
import me.casiebarie.casiebounce.managers.flag.BlockFlag;
import me.casiebarie.casiebounce.managers.flag.SoundFlag;
import me.casiebarie.casiebounce.other.Commands;
import me.casiebarie.casiebounce.other.Messages;
import me.casiebarie.casiebounce.other.TabComplete;
import me.casiebarie.casiebounce.other.UpdateChecker;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	public WorldGuardPlugin worldGuardPlugin;
	public boolean canBounce;
	public boolean isLegacy;
	public boolean wgEnabled;
	public boolean wgError;
	private FileConfiguration config;
	//WorldGuard Flags
	public static DoubleFlag CB_BOUNCEFORCE;
	public static StringFlag CB_DEATHMESSAGE;
	public static SetFlag<Sound> CB_BOUNCESOUND;
	public static SetFlag<Material> CB_BOUNCEBLOCKS;
	public static BooleanFlag CB_ENABLED, CB_STOPWHENCROUCH, CB_REQUIREPERMISSION, CB_ISBLOCKBLACKLIST, CB_FALLDAMAGE;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		//Call Constructors
		WorldGuardManager wgM = new WorldGuardManager(this);
		Messages msg = new Messages(this, wgM);
		ConfigManager configManager = new ConfigManager(this, msg);
		new Commands(this, configManager, msg);
		new TabComplete(this);
		if(isLegacy) {new BounceLegacy(this, configManager);
		} else if(!isLegacy) {new Bounce(this, wgM, configManager);}
		new UpdateChecker(this, 90967, "CB.admin").checkForUpdate();
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
					SetFlag<Material> flag = new SetFlag<Material>("cb-bounceblocks", new BlockFlag(null));
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
	
	public void legacyCheck() {
		final String BukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.v","");
		final List<String> LegacyVersions = Arrays.asList("1_8_R1", "1_8_R2", "1_8_R3", "1_9_R1", "1_9_R2", "1_10_R1", "1_11_R1", "1_12_R1");
		final List<String> Versions = Arrays.asList("1_13_R1", "1_13_R2", "1_14_R1", "1_15_R1", "1_16_R1", "1_16_R2", "1_16_R3", "1_17_R1", "1_17_R2");
		if(LegacyVersions.contains(BukkitVersion)) {isLegacy = true;
		} else if(Versions.contains(BukkitVersion)) {isLegacy = false;
		} else {
			//Disable Plugin
			getLogger().severe("Incompatible version. Disabling plugin");
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
		if(plugin == null || !(plugin instanceof WorldGuardPlugin)) {return null;}
		return (WorldGuardPlugin) plugin;
	}
}