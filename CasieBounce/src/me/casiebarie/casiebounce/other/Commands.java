package me.casiebarie.casiebounce.other;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.casiebarie.casiebounce.Main;
import me.casiebarie.casiebounce.managers.ConfigManager;

public class Commands implements CommandExecutor, Listener {
	private ConfigManager configManager;
	private Messages msg;
	public Commands(Main plugin, ConfigManager configManager, Messages msg) {
		this.configManager = configManager;
		this.msg = msg;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		plugin.getCommand("CB").setExecutor(this);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("CB")) {
			if(sender.hasPermission("CB.admin")) {
				if(args.length == 1 || args.length == 2) {
					if(args[0].toUpperCase().equals("RELOADCONFIG")) {
						configManager.reloadConfig(sender);
					} else if(args[0].toUpperCase().equals("GETREGIONSETTINGS")) {
						if(sender instanceof Player) {msg.regionInfo(sender);
						} else {msg.send(sender, "&cThis command can only be executed by a player!");}
					} else if(args[0].toUpperCase().equals("INFO")) {
						if(sender instanceof Player) {
							if(args.length == 2) {
								if(args[1].toUpperCase().equals("WORLDGUARD")) {msg.WorldGuardInfo(sender);
								} else {msg.info(sender);}
							} else {msg.info(sender);}
						} else {msg.send(sender, "&cThis command can only be executed by a player!");}
					} else if (args[0].toUpperCase().equals("GETERRORS")) {
						configManager.checkConfig(sender, true);
					} else {msg.send(sender, "&4USAGE: &c/cb <ReloadConfig/Info/GetErrors/GetRegionSettings> [WorldGuardFlags]");}
				} else {msg.send(sender, "&4USAGE: &c/cb <ReloadConfig/Info/GetErrors/GetRegionSettings> [WorldGuardFlags]");}
			} else {msg.send(sender, "&cYou don't have permission to use this Command!");}
		} return false;
	}
}