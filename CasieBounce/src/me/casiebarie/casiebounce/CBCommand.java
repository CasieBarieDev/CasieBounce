package me.casiebarie.casiebounce;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class CBCommand implements CommandExecutor{
	public Main plugin;
	private FileConfiguration config;
	public static boolean stopWhenCrouch, fallDamage, requirePermission, IsRegionBlacklist, IsBlockBlacklist;
	public static String deathMessage, bounceSound;
	public static double bounceForge;
	public static List<String> regionNames;
	public static List<String> bounceBlockTypes = new ArrayList<String>();
	public static List<Byte> bounceBlockData = new ArrayList<Byte>();
	
	public CBCommand(Main plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		plugin.getCommand("CB").setExecutor(this);
		getconfig(null);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("CB")) {
			if(sender.hasPermission("CB.admin")) {
				if(args.length == 1) {
					if(args[0].toUpperCase().equals("RELOADCONFIG")) {
						File cFile = new File(plugin.getDataFolder() + "/config.yml");
						config = YamlConfiguration.loadConfiguration(cFile);
						plugin.saveDefaultConfig();
						send(sender, "&6[&bCasieBounce&6] &aReloaded config.");
						getconfig(sender);
					} else if(args[0].toUpperCase().equals("INFO")) {
						if(sender instanceof Player) {info(sender);
						} else {send(sender, "&cThis command can only be executed by a player!");}
					} else {send(sender, "&4USAGE: &c/cb <reloadconfig/info>");}
				} else {send(sender, "&4USAGE: &c/cb <reloadconfig/info>");}
			} else {send(sender, "&cYou don't have permission to use this Command!");}
		} return false;
	}
	public void send(CommandSender sender, String message) {sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));}
	public void getconfig(CommandSender sender) {
		regionNames = config.getStringList(".Regions");
		bounceForge = config.getDouble(".Bounceforce");
		stopWhenCrouch = config.getBoolean(".StopWhenCrouch");
		fallDamage = config.getBoolean(".FallDamage");
		deathMessage = config.getString(".DeathMessage");
		bounceSound = config.getString(".BounceSound");
		requirePermission = config.getBoolean(".RequirePermission");
		IsRegionBlacklist = config.getBoolean(".IsRegionBlacklist");
		IsBlockBlacklist = config.getBoolean("IsBlockBlacklist");
		bounceBlockData = new ArrayList<Byte>();
		bounceBlockTypes = new ArrayList<String>();
		for(String bounceBlockConfig : config.getStringList(".BounceBlocks")) {
			if(bounceBlockConfig.contains(":")) {
				String[] bounceBlockSplit = bounceBlockConfig.split(":");
				bounceBlockTypes.add(bounceBlockSplit[0]);
				bounceBlockData.add(Byte.parseByte(bounceBlockSplit[1]));
			} else {
				bounceBlockTypes.add(bounceBlockConfig);
				bounceBlockData.add(Byte.parseByte("-1"));
			}
		}
	}
	@SuppressWarnings("deprecation")
	private void info(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("----------------- ").color(ChatColor.GOLD)
			.append("CasieBounce").color(ChatColor.AQUA).bold(true).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("By: ").color(ChatColor.BLUE).append("CasieBarie").color(ChatColor.YELLOW).create()))
			.append(" -----------------").color(ChatColor.GOLD).bold(false).event((HoverEvent) null)
			.append("\n\nIn the config you can find most of the settings.\nUse ").color(ChatColor.DARK_AQUA).bold(false)
			.append("/cb reloadconfig").color(ChatColor.YELLOW).bold(false).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/cb reloadconfig").color(ChatColor.GREEN).create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb reloadconfig"))
			.append(" to reload the config.").color(ChatColor.DARK_AQUA).bold(false).event((HoverEvent) null).event((ClickEvent) null)
			.append("\n\nHave fun with bouncing!").color(ChatColor.DARK_AQUA).bold(false)
			.append("\n------------------------------------------------").color(ChatColor.GOLD).bold(false)
			.create());
	}
}