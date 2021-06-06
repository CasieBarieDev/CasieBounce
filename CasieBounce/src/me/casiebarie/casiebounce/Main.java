package me.casiebarie.casiebounce;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Main extends JavaPlugin {
	public WorldGuardPlugin worldGuardPlugin;
	@Override
	public void onEnable() {
		saveDefaultConfig();
		worldGuardPlugin = getWorldGuard();
		//Version Checker
		final String BukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.v","");
		final List<String> LegacyVersions = Arrays.asList("1_8_R1", "1_8_R2", "1_8_R3", "1_9_R1", "1_9_R2", "1_10_R1", "1_11_R1", "1_12_R1");
		final List<String> Versions = Arrays.asList("1_13_R1", "1_13_R2", "1_14_R1", "1_15_R1", "1_16_R1", "1_16_R2", "1_16_R3");
		if(LegacyVersions.contains(BukkitVersion)) {new BounceLegacy(this);
		} else if (Versions.contains(BukkitVersion)) {new Bounce(this);
		} else {
			Bukkit.getLogger().warning("Incompatible version. Disabling plugin");
			getServer().getPluginManager().disablePlugin(this);
		}
		new CBCommand(this);
		getCommand("CB").setTabCompleter(new CBTabCompleter());
		//Worldguard Checker
		if(worldGuardPlugin == null) {
			this.getLogger().info("WorldGuard not found, regions disabled!");
		} else {this.getLogger().info("WorldGuard found!");}
		//Update Checker
		new UpdateChecker(this, 90967).getVersion(version -> {
			if(this.getDescription().getVersion().equalsIgnoreCase(version)) {
				this.getLogger().info("You are using the most recent version. (v" + this.getDescription().getVersion() + ")");
			} else {this.getLogger().info("There is a new update available (v" + version + ")!   You are using: v" + this.getDescription().getVersion());}
		});
	}
	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
		if(plugin == null || !(plugin instanceof WorldGuardPlugin)) {return null;}
		return (WorldGuardPlugin) plugin;
	}
}