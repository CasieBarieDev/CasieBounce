package me.casiebarie.casiebounce;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class CBCommand implements CommandExecutor, Listener{
	public Main plugin;
	private FileConfiguration config;
	public static boolean stopWhenCrouch, requirePermission, IsRegionBlacklist, IsBlockBlacklist;
	public static String deathMessage, bounceSound, fallDamage;
	public static double bounceForce;
	public static List<String> regionNames;
	public static List<String> bounceBlockTypes = new ArrayList<String>();
	public static List<Byte> bounceBlockData = new ArrayList<Byte>();
	
	public CBCommand(Main plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		Bukkit.getPluginManager().registerEvents(this, plugin);
		plugin.getCommand("CB").setExecutor(this);
		File cFile = new File(plugin.getDataFolder() + "/config.yml");
		config = YamlConfiguration.loadConfiguration(cFile);
		plugin.saveDefaultConfig();
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
						if(getconfig(sender)) {send(sender, "&6[&bCasieBounce&6] &aReloaded config.");
						} else {send(sender, "&6[&bCasieBounce&6] &cThere are errors in the config, check the console or type &7/cb geterror &cfor more info!");}
					} else if(args[0].toUpperCase().equals("INFO")) {
						if(sender instanceof Player) {info(sender);
						} else {send(sender, "&cThis command can only be executed by a player!");}
					} else if(args[0].toUpperCase().equals("GETERROR")) {
						checkConfig(sender, true);
					} else {send(sender, "&4USAGE: &c/cb <reloadconfig/info>");}
				} else {send(sender, "&4USAGE: &c/cb <reloadconfig/info>");}
			} else {send(sender, "&cYou don't have permission to use this Command!");}
		} return false;
	}
	public void send(CommandSender sender, String message) {sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));}
	public boolean getconfig(CommandSender sender) {
		if(checkConfig(sender, false)) {
			regionNames = config.getStringList(".Regions");
			bounceForce = config.getDouble(".Bounceforce");
			stopWhenCrouch = config.getBoolean(".StopWhenCrouch");
			fallDamage = config.getString(".FallDamage");
			deathMessage = config.getString(".DeathMessage");
			bounceSound = config.getString(".BounceSound");
			requirePermission = config.getBoolean(".RequirePermission");
			IsRegionBlacklist = config.getBoolean(".IsRegionBlacklist");
			IsBlockBlacklist = config.getBoolean(".IsBlockBlacklist");
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
			Main.canBounce = true;
			return true;
		} else {
			Main.canBounce = false;
			return false;
		}
	}
	private boolean checkConfig(CommandSender sender, Boolean isCommand) {
		ArrayList<Boolean> check = new ArrayList<>();
		check.add((config.get(".Regions") instanceof List<?>) ? false : true);
		check.add((config.get(".Bounceforce") instanceof Double) ? false : true);
		check.add((config.get(".StopWhenCrouch") instanceof Boolean) ? false : true);
		check.add((config.get(".DeathMessage") instanceof String) ? false : true);
		check.add((config.get(".RequirePermission") instanceof Boolean) ? false : true);
		check.add((config.get(".IsRegionBlacklist") instanceof Boolean) ? false : true);
		check.add((config.get(".IsBlockBlacklist") instanceof Boolean) ? false : true);
		if(config.get(".FallDamage") instanceof String) {
			String falldamage = config.getString(".FallDamage");
			if(falldamage.equals("DISABLED") || falldamage.equals("ENABLED") || falldamage.equals("REDUCED")) {check.add(false);
			} else {check.add(true);}
		} else {check.add(true);}
		if(!config.get(".BounceSound").toString().toUpperCase().equals("NONE")) {
			if(config.get(".BounceSound") instanceof String) {check.add(checkSound(config.getString(".BounceSound")));}
		} else {check.add(false);}
		ArrayList<String> invalidBlocks = new ArrayList<>();
		if(config.get(".BounceBlocks") instanceof List<?>) {
			for(String bounceBlockConfig : config.getStringList(".BounceBlocks")) {
				if(checkBlock(bounceBlockConfig)) {invalidBlocks.add(bounceBlockConfig);}
			}
			check.add((invalidBlocks.isEmpty()) ? false : true);
		} else {check.add(true);}
		if(check.contains(true)) {
			if(!isCommand || !(sender instanceof Player)) {
				sender = Bukkit.getConsoleSender();
				sender.sendMessage(color("&c-------------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------------"));
			} else {sender.sendMessage(color("&c-------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------"));}
			if(check.get(0).equals(true)) {sender.sendMessage(color("&cThe config section 'Regions' can not be validated! &8(Type: List)"));}
			if(check.get(1).equals(true)) {sender.sendMessage(color("&cThe config section 'BounceForce' can not be validated! &8(Type: Double)"));}
			if(check.get(2).equals(true)) {sender.sendMessage(color("&cThe config section 'StopWhenCrouch' can not be validated! &8(Type: Boolean)"));}
			if(check.get(3).equals(true)) {sender.sendMessage(color("&cThe config section 'DeathMessage' can not be validated! &8(Type: String)"));}
			if(check.get(4).equals(true)) {sender.sendMessage(color("&cThe config section 'RequirePermission' can not be validated! &8(Type: Boolean)"));}
			if(check.get(5).equals(true)) {sender.sendMessage(color("&cThe config section 'IsRegionBlacklist' can not be validated! &8(Type: Boolean)"));}
			if(check.get(6).equals(true)) {sender.sendMessage(color("&cThe config section 'IsBlockBlacklist' can not be validated! &8(Type: Boolean)"));}
			if(check.get(7).equals(true)) {sender.sendMessage(color("&cThe config section 'FallDamage' can not be validated! &8(Use: 'ENABLED', 'DISABLED' or 'REDUCED')"));}
			if(check.get(8).equals(true)) {sender.sendMessage(color("&cThe 'BounceSound' " + config.get(".BounceSound") + " doesn't exist!"));}
			if(check.get(9).equals(true)) {sender.sendMessage(color("&cThe 'BounceBlocks' " + invalidBlocks + " are not recognized!"));}
			if(isCommand && sender instanceof Player) {sender.sendMessage(color("&c-----------------------------------------------------&r"));}
			else {sender.sendMessage(color("&c-------------------------------------------------------------------"));}
			return false;
		} else {
			if(isCommand) {send(sender, "&6[&bCasieBounce&6] &aThere are no errors in the config!");}
			return true;
		}
	}
	private boolean checkSound(String sound) {
		try {Sound.valueOf(sound);
		} catch (IllegalArgumentException e) {return true;}
		return false;
	}
	private boolean checkBlock(String bounceBlock) {
		if(bounceBlock.contains(":")) {
			String[] bounceBlockSplit = bounceBlock.split(":");
			if(bounceBlockSplit.length > 2) {return true;}
			try {Byte.parseByte(bounceBlockSplit[1]);
			} catch (NumberFormatException e) {return true;}
			if(Material.getMaterial(bounceBlockSplit[0]) == null) {return true;
			} else {return false;}
		} else {
			if(Material.getMaterial(bounceBlock) == null) {return true;
			} else {return false;}
		}
	}
	private String color(String msg) {return org.bukkit.ChatColor.translateAlternateColorCodes('&', msg);}
	@SuppressWarnings("deprecation")
	private void info(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("----------------- ").color(ChatColor.GOLD)
			.append("CasieBounce").color(ChatColor.AQUA).bold(true).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("By: ").color(ChatColor.BLUE).append("CasieBarie").color(ChatColor.YELLOW).create()))
			.append(" -----------------").color(ChatColor.GOLD).bold(false).event((HoverEvent) null)
			.append("\n\nIn the config you can find most of the settings.\nUse ").color(ChatColor.DARK_AQUA).bold(false)
			.append("/cb reloadconfig").color(ChatColor.YELLOW).bold(false).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/cb reloadconfig").color(ChatColor.GREEN).create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb reloadconfig"))
			.append(" to reload the config. \nAny errors in the config will be announced.").color(ChatColor.DARK_AQUA).bold(false).event((HoverEvent) null).event((ClickEvent) null)
			.append("\n\nHave fun with bouncing!").color(ChatColor.DARK_AQUA).bold(false)
			.append("\n------------------------------------------------").color(ChatColor.GOLD).bold(false)
			.create());
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if(!Main.canBounce && player.hasPermission("CB.admin")) {
			player.spigot().sendMessage(new ComponentBuilder("[").color(ChatColor.GOLD).append("CasieBounce").color(ChatColor.AQUA).append("] ").color(ChatColor.GOLD)
					.append("There are errors in the config, check the console or type ").color(ChatColor.RED)
					.append("/cb geterror").color(ChatColor.GRAY).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/cb geterror").color(ChatColor.GREEN).create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb geterror"))
					.append(" for more info!").color(ChatColor.RED).event((HoverEvent) null).event((ClickEvent) null)
					.create());
		}
	}
}