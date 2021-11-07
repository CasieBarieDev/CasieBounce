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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import me.casiebarie.casiebounce.managers.ConfigManager;
import net.md_5.bungee.api.ChatColor;

public class BounceLegacy implements Listener {
	private Main plugin;
	private ConfigManager configManager;
	private ArrayList<Object> configSettings = new ArrayList<>();
	private ArrayList<UUID> isBouncing = new ArrayList<>();
	private ArrayList<UUID> canDie = new ArrayList<>();
	public BounceLegacy(Main plugin, ConfigManager configManager) {
		this.plugin = plugin;
		this.configManager = configManager;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@SuppressWarnings({"deprecation", "unchecked"})
	private boolean canBounce(Player player) {
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		List<Boolean> blockValid = new ArrayList<>();
		configSettings = configManager.getConfigSettings();
		if(!plugin.canBounce || (configSettings.get(6).equals(true) && player.hasPermission("CB.bounce"))) {return false;}
		if(player.isSneaking() && configSettings.get(3).equals(true)) {return false;}
		List<String> bounceBlocks = (List<String>) configSettings.get(8);
		for(String blockName : bounceBlocks) {
			if(blockName.contains(":")) {
				String[] blockSplit = blockName.split(":");
				if(blockSplit[0].equalsIgnoreCase(block.getType().name()) && blockSplit[1].equals(String.valueOf(block.getData()))) {blockValid.add(true);
				} else {blockValid.add(false);}
			} else if (blockName.equalsIgnoreCase(block.getType().name())) {blockValid.add(true);
			} else {blockValid.add(false);}
		}
		if(blockValid.contains(true)) {
			if(configSettings.get(7).equals(false)) {return true;
			} else {return false;}
		} else {
			if(configSettings.get(7).equals(true)) {return true;
			} else {return false;}
		}
	}

	private void goBounce(Player player) {
		isBouncing.remove(player.getUniqueId());
		player.setVelocity(new Vector(0, (double) configSettings.get(1), 0));
		player.setFallDistance(0);
		plugin.bounces += 1;
		try {player.playSound(player.getLocation(), Sound.valueOf((String) configSettings.get(2)), 1f, 1f);
		} catch (Exception e) {}
		isBouncing.add(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player) || (e.getCause() != DamageCause.FALL)) {return;}
		Player player = (Player) e.getEntity();
		canDie.remove(player.getUniqueId());
		if(!isBouncing.contains(player.getUniqueId())) {return;}
		if(configSettings.get(4).equals(false)) {e.setCancelled(true);}
		else {canDie.add(player.getUniqueId());}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		if(player.getLastDamageCause().getCause() == DamageCause.FALL && canDie.contains(player.getUniqueId()) && !configSettings.get(5).toString().equals("")) {
			String deathMessage = configSettings.get(5).toString().replaceAll("%player%", player.getDisplayName());
			e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathMessage));
		} canDie.remove(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerLand(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Entity entity = player;
		if(!isBouncing.contains(player.getUniqueId()) || !entity.isOnGround() || (player.getFallDistance() < 0)) {return;}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {@Override
		public void run() {
			if(canBounce(player)) {goBounce(player);
			} else {isBouncing.remove(player.getUniqueId());}
		}}, 1l);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(isBouncing.contains(player.getUniqueId())) {return;}
		if(canBounce(player)) {goBounce(player);}
	}
}