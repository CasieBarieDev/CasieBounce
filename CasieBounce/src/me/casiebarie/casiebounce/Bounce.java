package me.casiebarie.casiebounce;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import me.casiebarie.casiebounce.sqlite.Database;
import me.casiebarie.casiebounce.utils.ConfigManager;
import me.casiebarie.casiebounce.worldguard.WorldGuardManager;
import net.md_5.bungee.api.ChatColor;

public class Bounce implements Listener {
	private Main plugin;
	private WorldGuardManager wgM;
	private ConfigManager cM;
	private Database db;
	private ArrayList<UUID> isBouncing = new ArrayList<>(), canDie = new ArrayList<>();
	public Bounce(Main plugin, WorldGuardManager wgM, ConfigManager cM) {
		this.plugin = plugin;
		this.wgM = wgM;
		this.cM = cM;
		this.db = plugin.getDatabase();
		Bukkit.getPluginManager().registerEvents(this, plugin);
		repeat();
	}

	private void repeat() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for(Player player : Bukkit.getOnlinePlayers()) {
				Entity entity = player;
				if(!entity.isOnGround() && player.getFallDistance() > 0) {isBouncing.remove(player.getUniqueId()); continue;}
				if(canDie.contains(player.getUniqueId())) {continue;}
				if(isBouncing.contains(player.getUniqueId())) {continue;}
				if(canBounce(player)) {goBounce(player, (plugin.wgEnabled) ? wgM.getRegionName(player) : "Global");}
			}
		}, 0, 1l);
	}

	private void goBounce(Player player, String region) {
		isBouncing.remove(player.getUniqueId());
		ArrayList<Object> finalSettings = new ArrayList<>();
		finalSettings = getFinalSettings(player, cM.getConfigSettings());
		if(finalSettings == null) {return;}
		player.setVelocity(new Vector(0, (double) finalSettings.get(1), 0));
		player.setFallDistance(0);
		try {player.playSound(player.getLocation(), Sound.valueOf((String) finalSettings.get(2)), 1f, 1f);} catch (Exception e) {}
		plugin.mBounces += 1;
		db.addBounces(region, player.getUniqueId(), player.getName(), player.getWorld(), 1);
		isBouncing.add(player.getUniqueId());
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private Boolean canBounce(Player player) {
		if(!plugin.canBounce) {return false;}
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		List<Boolean> blockValid = new ArrayList<>();
		ArrayList<Object> finalSettings = new ArrayList<>();
		finalSettings = getFinalSettings(player, cM.getConfigSettings());
		if((finalSettings == null)) {return false;}
		if(finalSettings.get(6).equals(true)) {if(!player.hasPermission("CB.bounce") && !player.hasPermission("CB.bounce." + wgM.getRegionName(player))) {return false;}}
		if(player.isSneaking() && finalSettings.get(3).equals(true)) {return false;}
		List<String> bounceBlocks = (List<String>) finalSettings.get(7);
		for(String blockName : bounceBlocks) {
			if(blockName.contains(":")) {
				String[] blockSplit = blockName.split(":");
				if(blockSplit[0].equalsIgnoreCase(block.getType().name()) && blockSplit[1].equals(String.valueOf(block.getData()))) {blockValid.add(true);
				} else {blockValid.add(false);}
			} else if (blockName.equalsIgnoreCase(block.getType().name())) {blockValid.add(true);
			} else {blockValid.add(false);}
		}
		if(blockValid.contains(true)) {if(finalSettings.get(8).equals(false)) {return true; 
		} else if(finalSettings.get(8).equals(true)) {return true;}}
		return false;
	}

	private ArrayList<Object> getFinalSettings(Player player, ArrayList<Object> configSettings) {
		ArrayList<Object> regionSettings = new ArrayList<>();
		ArrayList<Object> finalSettings = new ArrayList<>();
		if(configSettings.get(0).equals(true) && plugin.wgEnabled) {
			regionSettings = wgM.getRegionSettings(player);
			if(regionSettings.isEmpty() || regionSettings.get(0).equals(false)) {return null;}
			for(int i = 0; i <= 8; i++) {finalSettings.add(i, (regionSettings.get(i).equals("DEFAULT") ? configSettings.get(i) : regionSettings.get(i)));}
		} else {finalSettings = configSettings;}
		return finalSettings;
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player) || (e.getCause() != DamageCause.FALL)) {return;}
		Player player = (Player) e.getEntity();
		canDie.remove(player.getUniqueId());
		if(!isBouncing.contains(player.getUniqueId())) {return;}
		ArrayList<Object> finalSettings = new ArrayList<>();
		finalSettings = getFinalSettings(player, cM.getConfigSettings());
		if(finalSettings == null) {return;}
		if(finalSettings.get(4).equals(false)) {e.setCancelled(true);}
		else {canDie.add(player.getUniqueId());}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		ArrayList<Object> finalSettings = new ArrayList<>();
		finalSettings = getFinalSettings(player, cM.getConfigSettings());
		if(finalSettings == null) {return;}
		if(player.getLastDamageCause().getCause() == DamageCause.FALL && canDie.contains(player.getUniqueId()) && !finalSettings.get(5).toString().equals("")) {
			String deathMessage = finalSettings.get(5).toString().replaceAll("%player%", player.getDisplayName());
			e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathMessage));
		} canDie.remove(player.getUniqueId());
	}
}