package me.casiebarie.casiebounce.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import me.casiebarie.casiebounce.Main;

public class WorldGuardManager {
	private Main plugin;
	private String d = "DEFAULT";
	public WorldGuardManager(Main plugin) {this.plugin = plugin;}
	// 0 = Enabled | 1 = BounceForce | 2 = BounceSound | 3 = StopWhenCrouch | 4 = FallDamage
	// 5 = DeathMessage | 6 = RequirePermission | 7 = IsBlockBlackList | 8 = BounceBlocks
	public ArrayList<Object> getRegionSettings(Player player) {
		if (!plugin.wgEnabled) {return null;}
		org.bukkit.Location location = player.getLocation();
		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(loc);
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		ArrayList<Object> regionSettings = new ArrayList<>();
		Boolean enabled = null, stopWhenCrouch = null, requirePermission = null, isBlockBlacklist = null, fallDamage = null;
		Double bounceForce = null;
		Set<Sound> bounceSound = null;
		String deathMessage = null;
		Collection<Set<Material>> wgBounceBlocks = null;
		try {
			enabled = set.queryValue(localPlayer, Main.CB_ENABLED);
			stopWhenCrouch = set.queryValue(localPlayer, Main.CB_STOPWHENCROUCH);
			requirePermission = set.queryValue(localPlayer, Main.CB_REQUIREPERMISSION);
			isBlockBlacklist = set.queryValue(localPlayer, Main.CB_ISBLOCKBLACKLIST);
			bounceForce = set.queryValue(localPlayer, Main.CB_BOUNCEFORCE);
			bounceSound = set.queryValue(localPlayer, Main.CB_BOUNCESOUND);
			fallDamage = set.queryValue(localPlayer, Main.CB_FALLDAMAGE);
			deathMessage = set.queryValue(localPlayer, Main.CB_DEATHMESSAGE);
			wgBounceBlocks = set.queryAllValues(localPlayer, Main.CB_BOUNCEBLOCKS);
		} catch (NullPointerException e) {}
		regionSettings.add((enabled != null) ? enabled : false);
		regionSettings.add((bounceForce != null) ? bounceForce : d);
		regionSettings.add((bounceSound != null) ? bounceSound.toString().replaceAll("[\\[\\]]", "") : d);
		regionSettings.add((stopWhenCrouch != null) ? stopWhenCrouch : d);
		regionSettings.add((fallDamage != null) ? fallDamage : d);
		regionSettings.add((deathMessage != null) ? deathMessage : d);
		regionSettings.add((requirePermission != null) ? requirePermission : d);
		regionSettings.add((isBlockBlacklist != null) ? isBlockBlacklist : d);
		if(!wgBounceBlocks.isEmpty()) {
			String bounceBlocksString = wgBounceBlocks.toString();
			String[] bounceBlocksSplit = bounceBlocksString.replaceAll("\\[", "").replaceAll("\\]", "").split(", ");
			regionSettings.add(Arrays.asList(bounceBlocksSplit));
		} else {regionSettings.add(d);}
		return regionSettings;
	}
}