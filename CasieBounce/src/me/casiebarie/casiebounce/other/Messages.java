package me.casiebarie.casiebounce.other;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.casiebarie.casiebounce.Main;
import me.casiebarie.casiebounce.managers.WorldGuardManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class Messages implements Listener {
	private Main plugin;
	private WorldGuardManager regionManager;
	public void send(CommandSender sender, String message) {if(sender != null) {sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));}}
	public Messages(Main plugin, WorldGuardManager regionManager) {
		this.plugin = plugin;
		this.regionManager = regionManager;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@SuppressWarnings("deprecation")
	public void info(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("§6----------------- ").append("§b§lCasieBounce").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9By: §eCasieBarie").create())).append("§6 -----------------").event((HoverEvent) null)
				.append("\n\n§3Before you start you've to make sure you set the '§7WorldGuardFlags§3' in the config to the correct state. If you don't use WorldGuard you can edit the settings in the config. Use '")
				.append("§e/cb ReloadConfig").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a/cb ReloadConfig").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb ReloadConfig"))
				.append("§3' to reload the config. Any errors in the config will be announced. \n\nIf you use WorldGuard type '").event((HoverEvent) null).event((ClickEvent) null)
				.append("§e/cb Info WorldGuardFlags").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a/cb Info WorldGuardFlags").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb Info WorldGuardFlags"))
				.append("§3' to see info about the WorldGuard flags that are used to define the settings.").event((HoverEvent) null).event((ClickEvent) null)
				.append("\n\n§3More support at: ").event((ClickEvent) null).event((HoverEvent) null)
				.append("\n§e§nSPIGOT").event(new ClickEvent(Action.OPEN_URL, "https://www.spigotmc.org/resources/.90967")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.spigotmc.org/resources/.90967").create()))
				.append(" §3| ").event((ClickEvent) null).event((HoverEvent) null)
				.append("§b§nDISCORD").event(new ClickEvent(Action.OPEN_URL, "https://discord.gg/ZptCBHeHyg")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://discord.gg/ZptCBHeHyg").create()))
				.append(" §3| ").event((ClickEvent) null).event((HoverEvent) null)
				.append("§4§nWEBSITE").event(new ClickEvent(Action.OPEN_URL, "https://www.casiebariedev.ga/")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.casiebariedev.ga/").create()))
				.append(" §3| ").event((ClickEvent) null).event((HoverEvent) null)
				.append("§f§nGITHUB").event(new ClickEvent(Action.OPEN_URL, "https://github.com/CasieBarieDev/CasieBounce")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://github.com/CasieBarieDev/CasieBounce").create()))
				.append("\n§6------------------------------------------------")
				.create());
	}
	
	@SuppressWarnings("deprecation")
	public void WorldGuardInfo(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("§6----------------- ").append("§b§lCasieBounce").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9By: §eCasieBarie").create())).append("§6 -----------------").event((HoverEvent) null)
				.append("\n§3To enable CasieBounce in you're region you've to put the flag ")
				.append("§dCB-ENABLED §3to §etrue §3in that region. After that you can specify the settings using the following flags:")
				.append("\n\n§d* cb-stopwhencrouch * cb-requirepermission * cb-bounceforce \n* cb-bouncesound * cb-falldamage * cb-deathmessage\n* cb-bounceblocks * cb-isblockblacklist")
				.append("\n\n§3If a setting is not set in the region. The setting will default to the value that is set in the config. Be aware that the plugin gets the FallDamage and DeathMessage settings from the region the player is landing in. So if you start bouncing in a region where FallDamage is disabled and you land in a region where it is enabled, you will still get falldamage.")
				.append("\n§6------------------------------------------------")
				.create());
	}
	
	@SuppressWarnings("deprecation")
	public void regionInfo(CommandSender sender) {
		if(!plugin.wgEnabled) {send(sender, "&6[&bCasieBounce&6] &cWorldGuard is not enabled!"); return;}
		ArrayList<Object> regionSettings = regionManager.getRegionSettings((Player) sender);
		sender.spigot().sendMessage(new ComponentBuilder("§6----------------- ").append("§b§lCasieBounce").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9By: §eCasieBarie").create())).append("§6 -----------------").event((HoverEvent) null)
				.append("\n§3§nRegion Settings:")
				.append("\n\n* §5Enabled: ").append("§e§o" + regionSettings.get(0))
				.append("\n* §5StopWhenCrouch: ").append("§e§o" + regionSettings.get(3))
				.append("\n* §5RequirePermission: ").append("§e§o" + regionSettings.get(6))
				.append("\n* §5BounceForce: ").append("§e§o" + regionSettings.get(1))
				.append("\n* §5BounceSound: ").append("§e§o" + regionSettings.get(2))
				.append("\n* §5FallDamage: ").append("§e§o" + regionSettings.get(4))
				.append("\n* §5DeathMessage: ").append("§e§o" + regionSettings.get(5))
				.append("\n* §5BounceBlocks: ").append("§e§o" + regionSettings.get(8))
				.append("\n* §5IsBlockBlacklist: ").append("§e§o" + regionSettings.get(7))
				.append("\n§6------------------------------------------------")
				.create());
	}
	
	public void errorMessage(CommandSender sender, ArrayList<Object> errors, Boolean isCommand) {
		if(isCommand) {
			//PLAYER MSG
			send(sender, "&c-------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------");
			for(int i = 0; i < errors.size(); i += 2) {
				if(errors.get(i).toString().equals("BounceBlocks_")) {send(sender, "&c- The block '" + errors.get(i + 1) + "' is not recognized!");
				} else {send(sender, "&c- The config section '" + errors.get(i) + "' with the value '" + errors.get(i + 1) + "' is not recognized!");}
			} send(sender, "&c-----------------------------------------------------&r");
		} else {
			if(sender instanceof Player) {getErrorMSG(sender);}
			//CONSOLE MSG
			sender = Bukkit.getConsoleSender();
			send(sender, "&c-------------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------------");
			for(int i = 0; i < errors.size(); i += 2) {
				if(errors.get(i).toString().equals("BounceBlocks_")) {send(sender, "&c- The block '" + errors.get(i + 1) + "' is not recognized!");
				} else {send(sender, "&c- The config section '" + errors.get(i) + "' with the value '" + errors.get(i + 1) + "' is not recognized!");}
			} send(sender, "&c-------------------------------------------------------------------");
		}
	}

	@SuppressWarnings("deprecation")
	public void getErrorMSG(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("§6[§bCasieBounce§6]")
				.append("§cThere are errors in the config, check the console or type ")
				.append("§7/cb GetErrors").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a/cb GetErrors").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb GetErrors"))
				.append("§c for more info!").event((HoverEvent) null).event((ClickEvent) null)
				.create());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if(!plugin.canBounce && player.hasPermission("CB.admin")) {getErrorMSG(player);}
		if(plugin.wgError && player.hasPermission("CB.admin")) {send(player, "&6[&bCasieBounce&6] &cDue to a reload the WorldGuard flags are disabled. Please fully restart your server to fix it!");}
	}
}