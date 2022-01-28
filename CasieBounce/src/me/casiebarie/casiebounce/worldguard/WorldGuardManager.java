package me.casiebarie.casiebounce.worldguard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.casiebarie.casiebounce.Main;
import net.md_5.bungee.api.ChatColor;

public class WorldGuardManager {
	private Main plugin;
	private String d = "DEFAULT";
	private Boolean l;
	private static DoubleFlag CB_BOUNCEFORCE;
	private static StringFlag CB_DEATHMESSAGE;
	private static SetFlag<Sound> CB_BOUNCESOUND;
	private static SetFlag<String> CB_BOUNCEBLOCKS, CB_BOUNCEPRIZE;
	private static BooleanFlag CB_ENABLED, CB_STOPWHENCROUCH, CB_REQUIREPERMISSION, CB_ISBLOCKBLACKLIST, CB_FALLDAMAGE;
	public WorldGuardManager(Main plugin) {this.plugin = plugin; l = plugin.isLegacy;}
	@SuppressWarnings("unchecked")
	public void registerFlags() {
		plugin.wgEnabled = true;
		plugin.getLogger().info("Registering WorldGuard flags...");
		FlagRegistry registry;
		if(l) {registry = plugin.getWorldGuard().getFlagRegistry();
		} else {registry = com.sk89q.worldguard.WorldGuard.getInstance().getFlagRegistry();}
		try {
			try {
				BooleanFlag flag = new BooleanFlag("cb-enabled");
				registry.register(flag);
				CB_ENABLED = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-enabled");
				if(existing instanceof BooleanFlag) {CB_ENABLED = (BooleanFlag) existing;}
			}
			try {
				DoubleFlag flag = new DoubleFlag("cb-bounceforce");
				registry.register(flag);
				CB_BOUNCEFORCE = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-bounceforce");
				if(existing instanceof DoubleFlag) {CB_BOUNCEFORCE = (DoubleFlag) existing;}
			}
			try {
				SetFlag<Sound> flag = new SetFlag<>("cb-bouncesound", new SoundFlag(null));
				registry.register(flag);
				CB_BOUNCESOUND = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-bouncesound");
				if(existing instanceof SetFlag<?>) {CB_BOUNCESOUND = (SetFlag<Sound>) existing;}
			}
			try {
				SetFlag<String> flag = new SetFlag<>("cb-bounceprize", new PrizeFlag(null));
				registry.register(flag);
				CB_BOUNCEPRIZE = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-bounceprize");
				if(existing instanceof SetFlag<?>) {CB_BOUNCEPRIZE = (SetFlag<String>) existing;}
			}
			try {
				BooleanFlag flag = new BooleanFlag("cb-falldamage");
				registry.register(flag);
				CB_FALLDAMAGE = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-falldamage");
				if(existing instanceof BooleanFlag) {CB_FALLDAMAGE = (BooleanFlag) existing;}
			}
			try {
				StringFlag flag = new StringFlag("cb-deathmessage");
				registry.register(flag);
				CB_DEATHMESSAGE = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-deathmessage");
				if(existing instanceof StringFlag) {CB_DEATHMESSAGE = (StringFlag) existing;}
			}
			try {
				SetFlag<String> flag = new SetFlag<>("cb-bounceblocks", new BlockFlag(null));
				registry.register(flag);
				CB_BOUNCEBLOCKS = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-bounceblocks");
				if(existing instanceof SetFlag<?>) {CB_BOUNCEBLOCKS = (SetFlag<String>) existing;}
			}
			try {
				BooleanFlag flag = new BooleanFlag("cb-stopwhencrouch");
				registry.register(flag);
				CB_STOPWHENCROUCH = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-stopwhencrouch");
				if(existing instanceof BooleanFlag) {CB_STOPWHENCROUCH = (BooleanFlag) existing;}
			}
			try {
				BooleanFlag flag = new BooleanFlag("cb-requirepermission");
				registry.register(flag);
				CB_REQUIREPERMISSION = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-requirepermission");
				if(existing instanceof BooleanFlag) {CB_REQUIREPERMISSION = (BooleanFlag) existing;}
			}
			try {
				BooleanFlag flag = new BooleanFlag("cb-isblockblacklist");
				registry.register(flag);
				CB_ISBLOCKBLACKLIST = flag;
			} catch (FlagConflictException e) {
				Flag<?> existing = registry.get("cb-isblockblacklist");
				if(existing instanceof BooleanFlag) {CB_ISBLOCKBLACKLIST = (BooleanFlag) existing;}
			} plugin.getLogger().info("WorldGuard flags succesfully registered");
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to (re)create WorldGuard flags!");
			plugin.getLogger().log(Level.SEVERE, "Due to a reload the WorldGuard flags are disabled. Please fully restart your server to fix it!");
			plugin.wgEnabled = false; plugin.wgError = true;
			for(Player player : Bukkit.getOnlinePlayers()) {if(player.hasPermission("CB.admin")) {player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&bCasieBounce&6] &cDue to a reload the WorldGuard flags are disabled. Please fully restart your server to fix it!"));}}
		}
	}

	public ArrayList<Object> getRegionSettings(Player player) {
		if(!plugin.wgEnabled) {return null;}
		ApplicableRegionSet set = (l) ? getRegionSetLegacy(player) : getRegionSet(player);
		LocalPlayer localPlayer;
		if(plugin.isLegacy) {localPlayer = plugin.getWorldGuard().wrapPlayer(player);
		} else {localPlayer = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(player);}
		ArrayList<Object> regionSettings = new ArrayList<>();
		Boolean enabled = null, stopWhenCrouch = null, requirePermission = null, isBlockBlacklist = null, fallDamage = null;
		Double bounceForce = null;
		Set<Sound> bounceSound = null;
		Set<String> prize = null;
		String deathMessage = null;
		Collection<Set<String>> wgBounceBlocks = null;
		try {
			enabled = set.queryValue(localPlayer, CB_ENABLED);
			stopWhenCrouch = set.queryValue(localPlayer, CB_STOPWHENCROUCH);
			requirePermission = set.queryValue(localPlayer, CB_REQUIREPERMISSION);
			isBlockBlacklist = set.queryValue(localPlayer, CB_ISBLOCKBLACKLIST);
			bounceForce = set.queryValue(localPlayer, CB_BOUNCEFORCE);
			bounceSound = set.queryValue(localPlayer, CB_BOUNCESOUND);
			fallDamage = set.queryValue(localPlayer, CB_FALLDAMAGE);
			deathMessage = set.queryValue(localPlayer, CB_DEATHMESSAGE);
			prize = set.queryValue(localPlayer, CB_BOUNCEPRIZE);
			wgBounceBlocks = set.queryAllValues(localPlayer, CB_BOUNCEBLOCKS);
		} catch (NullPointerException e) {}
		regionSettings.add((enabled != null) ? enabled : false);
		regionSettings.add((bounceForce != null) ? bounceForce : d);
		regionSettings.add((bounceSound != null) ? bounceSound.toString().replaceAll("[\\[\\]]", "") : d);
		regionSettings.add((prize != null) ? prize : d);
		regionSettings.add((stopWhenCrouch != null) ? stopWhenCrouch : d);
		regionSettings.add((fallDamage != null) ? fallDamage : d);
		regionSettings.add((deathMessage != null) ? deathMessage : d);
		regionSettings.add((requirePermission != null) ? requirePermission : d);
		if(!wgBounceBlocks.isEmpty()) {
			String bounceBlocksString = wgBounceBlocks.toString();
			String[] bounceBlocksSplit = bounceBlocksString.replaceAll("\\[", "").replaceAll("\\]", "").split(", ");
			regionSettings.add(Arrays.asList(bounceBlocksSplit));
		} else {regionSettings.add(d);}
		regionSettings.add((isBlockBlacklist != null) ? isBlockBlacklist : d);
		return regionSettings;
	}

	public String getRegionAmmount() {
		int regionAmount = 0;
		for(World world : plugin.getServer().getWorlds()) {
			RegionManager regions = getRegionManager(world);
			Map<String, ProtectedRegion> regionMap = regions.getRegions();
			for(Entry<String, ProtectedRegion> regionEntry : regionMap.entrySet()) {
				if(regionEntry.getValue().getFlag(CB_ENABLED).booleanValue()) {regionAmount++;}
			}
		} return "" + regionAmount;
	}

	public ArrayList<String> getAllBounceBlocks() {
		ArrayList<String> bounceBlocks = new ArrayList<>();
		for(World world : plugin.getServer().getWorlds()) {
			RegionManager regions = getRegionManager(world);
			Map<String, ProtectedRegion> regionMap = regions.getRegions();
			for(Entry<String, ProtectedRegion> regionEntry : regionMap.entrySet()) {
				for(String block : regionEntry.getValue().getFlag(CB_BOUNCEBLOCKS)) {
					if(!bounceBlocks.contains(block.toUpperCase())) {bounceBlocks.add(block.toUpperCase());}
				}
			}
		} return bounceBlocks;
	}

	private RegionManager getRegionManager(World world) {
		if(l) {
			com.sk89q.worldguard.bukkit.RegionContainer container = plugin.getWorldGuard().getRegionContainer();
			RegionManager regions = container.get(world);
			return regions;
		} else {
			com.sk89q.worldguard.protection.regions.RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(world));
			return regions;
		}
	}

	private ApplicableRegionSet getRegionSetLegacy(Player player) {
		Location location = player.getLocation();
		com.sk89q.worldguard.bukkit.RegionContainer regionContainer = plugin.getWorldGuard().getRegionContainer();
		com.sk89q.worldguard.bukkit.RegionQuery query = regionContainer.createQuery();
		return query.getApplicableRegions(location);
	}

	private ApplicableRegionSet getRegionSet(Player player) {
		Location location = player.getLocation();
		com.sk89q.worldedit.util.Location loc = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(location);
		com.sk89q.worldguard.protection.regions.RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
		com.sk89q.worldguard.protection.regions.RegionQuery query = container.createQuery();
		return query.getApplicableRegions(loc);
	}

	public String getRegionName(Player player) {
		ApplicableRegionSet set = (l) ? getRegionSetLegacy(player) : getRegionSet(player);
		for(ProtectedRegion region : set.getRegions()) {
			if(region.getFlag(CB_ENABLED) != null) {return region.getId();}
		} return "Global";
	}
}