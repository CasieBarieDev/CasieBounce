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
		sender.spigot().sendMessage(new ComponentBuilder("----------------- ").color(ChatColor.GOLD)
				.append("CasieBounce").color(ChatColor.AQUA).bold(true).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("By: ").color(ChatColor.BLUE).append("CasieBarie").color(ChatColor.YELLOW).create()))
				.append(" -----------------").color(ChatColor.GOLD).bold(false).event((HoverEvent) null)
				.append("\n\nBefore you start you've to make sure you set the 'WorldGuardFlags' in the correct state. If you don't use WorldGuard you can edit the settings in the config. Use ").color(ChatColor.DARK_AQUA).bold(false)
				.append("/cb ReloadConfig").color(ChatColor.YELLOW).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/cb ReloadConfig").color(ChatColor.GREEN).create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb ReloadConfig"))
				.append(" to reload the config. Any errors in the config will be announced. \nIf you use WorldGuard type ").color(ChatColor.DARK_AQUA).bold(false).event((HoverEvent) null).event((ClickEvent) null)
				.append("/cb Info WorldGuardFlags").color(ChatColor.YELLOW).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/cb Info WorldGuardFlags").color(ChatColor.GREEN).create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb Info WorldGuardFlags"))
				.append(" to see info about the WorldGuard flags that are used to define the settings").color(ChatColor.DARK_AQUA).bold(false).event((HoverEvent) null).event((ClickEvent) null)
				.append("\n\nHave fun with bouncing!").color(ChatColor.DARK_AQUA).bold(false)
				.append("\n------------------------------------------------").color(ChatColor.GOLD).bold(false)
				.create());
	}
	
	@SuppressWarnings("deprecation")
	public void WorldGuardInfo(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("----------------- ").color(ChatColor.GOLD).bold(false)
				.append("CasieBounce").color(ChatColor.AQUA).bold(true).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("By: ").color(ChatColor.BLUE).append("CasieBarie").color(ChatColor.YELLOW).create()))
				.append(" -----------------").color(ChatColor.GOLD).bold(false).event((HoverEvent) null)
				.append("\nTo enable CasieBounce in you're region you've to put the flag ").color(ChatColor.DARK_AQUA).bold(false)
				.append("CB-ENABLED").color(ChatColor.LIGHT_PURPLE).bold(false).append(" to ").color(ChatColor.DARK_AQUA).bold(false).append("true").color(ChatColor.YELLOW).bold(false)
				.append(" in that region. After that you can specify the settings using the following flags:").color(ChatColor.DARK_AQUA).bold(false)
				.append("\n\n* cb-stopwhencrouch\n* cb-requirepermission\n* cb-bounceforce\n* cb-bouncesound\n* cb-falldamage\n* cb-deathmessage\n* cb-bounceblocks\n* cb-isblockblacklist").color(ChatColor.LIGHT_PURPLE).bold(false)
				.append("\n\nIf a setting is not set in the region. The setting will default to the value that is set in the config.").color(ChatColor.DARK_AQUA).bold(false)
				.append("\n------------------------------------------------").color(ChatColor.GOLD).bold(false)
				.create());
	}
	
	@SuppressWarnings("deprecation")
	public void regionInfo(CommandSender sender) {
		if(!plugin.wgEnabled) {send(sender, "&6[&bCasieBounce&6] &cWorldGuard is not enabled!"); return;}
		ArrayList<Object> regionSettings = regionManager.getRegionSettings((Player) sender);
		sender.spigot().sendMessage(new ComponentBuilder("----------------- ").color(ChatColor.GOLD)
				.append("CasieBounce").color(ChatColor.AQUA).bold(true).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("By: ").color(ChatColor.BLUE).append("CasieBarie").color(ChatColor.YELLOW).create()))
				.append(" -----------------").color(ChatColor.GOLD).bold(false).event((HoverEvent) null)
				.append("\nRegion Settings:").color(ChatColor.DARK_AQUA).underlined(true)
				.append("\n\n* Enabled: ").color(ChatColor.DARK_PURPLE).underlined(false).append("" + regionSettings.get(0)).color(ChatColor.YELLOW).italic(true)
				.append("\n* StopWhenCrouch: ").color(ChatColor.DARK_PURPLE).italic(false).append("" + regionSettings.get(1)).color(ChatColor.YELLOW).italic(true)
				.append("\n* RequirePermission: ").color(ChatColor.DARK_PURPLE).italic(false).append("" + regionSettings.get(2)).color(ChatColor.YELLOW).italic(true)
				.append("\n* BounceForce: ").color(ChatColor.DARK_PURPLE).italic(false).append("" + regionSettings.get(3)).color(ChatColor.YELLOW).italic(true)
				.append("\n* BounceSound: ").color(ChatColor.DARK_PURPLE).italic(false).append("" + regionSettings.get(4)).color(ChatColor.YELLOW).italic(true)
				.append("\n* FallDamage: ").color(ChatColor.DARK_PURPLE).italic(false).append("" + regionSettings.get(5)).color(ChatColor.YELLOW).italic(true)
				.append("\n* DeathMessage: ").color(ChatColor.DARK_PURPLE).italic(false).append("" + regionSettings.get(6)).color(ChatColor.YELLOW).italic(true)
				.append("\n* BounceBlocks: ").color(ChatColor.DARK_PURPLE).italic(false).append("" + regionSettings.get(8)).color(ChatColor.YELLOW).italic(true)
				.append("\n* IsBlockBlacklist: ").color(ChatColor.DARK_PURPLE).italic(false).append("" + regionSettings.get(7)).color(ChatColor.YELLOW).italic(true)
				.append("\n------------------------------------------------").color(ChatColor.GOLD).italic(false)
				.create());
	}
	
	public void errorMessage(CommandSender sender, ArrayList<Object> errors, Boolean isCommand) {
		if(isCommand) {
			//PLAYER MSG
			send(sender, "&c-------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------");
			for(int i = 0; i < errors.size(); i += 2) {
				if(errors.get(i).toString().equals("BounceBlocks_")) {
					send(sender, "&c- The block '" + errors.get(i + 1) + "' is not recognized!");
				} else {send(sender, "&c- The config section '" + errors.get(i) + "' with the value '" + errors.get(i + 1) + "' is not recognized!");}
			}
			send(sender, "&c-----------------------------------------------------&r");
		} else {
			if(sender instanceof Player) {getErrorMSG(sender);}
			//CONSOLE MSG
			sender = Bukkit.getConsoleSender();
			send(sender, "&c-------------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------------");
			for(int i = 0; i < errors.size(); i += 2) {
				if(errors.get(i).toString().equals("BounceBlocks_")) {
					send(sender, "&c- The block '" + errors.get(i + 1) + "' is not recognized!");
				} else {send(sender, "&c- The config section '" + errors.get(i) + "' with the value '" + errors.get(i + 1) + "' is not recognized!");}
			}
			send(sender, "&c-------------------------------------------------------------------");
		}
	}

	@SuppressWarnings("deprecation")
	public void getErrorMSG(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("[").color(ChatColor.GOLD).append("CasieBounce").color(ChatColor.AQUA).append("] ").color(ChatColor.GOLD)
				.append("There are errors in the config, check the console or type ").color(ChatColor.RED)
				.append("/cb GetErrors").color(ChatColor.GRAY).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/cb GetErrors").color(ChatColor.GREEN).create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb GetErrors"))
				.append(" for more info!").color(ChatColor.RED).event((HoverEvent) null).event((ClickEvent) null)
				.create());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if(!plugin.canBounce && player.hasPermission("CB.admin")) {getErrorMSG(player);}
		if(plugin.wgError && player.hasPermission("CB.admin")) {
			send(player, "&6[&bCasieBounce&6] &cDue to a reload the WorldGuard flags are disabled. Please fully restart your server to fix it!");
		}
	}
}