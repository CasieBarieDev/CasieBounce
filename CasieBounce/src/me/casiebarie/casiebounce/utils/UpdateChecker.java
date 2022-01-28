package me.casiebarie.casiebounce.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.parser.JSONParser;

import me.casiebarie.casiebounce.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
@SuppressWarnings("deprecation")
public class UpdateChecker implements Listener {
	private Main plugin;
	private Integer recourceID;
	private String permission;
	JSONParser parser = new JSONParser();
	private static Boolean isNewVersion = false, firstCheck = true;
	private static String LastSpigotVersion, spigotVersion, currVersion;
	private void send(String msg) {Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));}
	public UpdateChecker(Main plugin, Integer recourceID, String permission) {
		this.plugin = plugin; this.recourceID = recourceID; this.permission = permission;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void checkForUpdate() {
		currVersion = plugin.getDescription().getVersion();
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			getLatestVersion(version -> {spigotVersion = version;});
			if(spigotVersion == null) {return;}
			int spigotVersionINT = Integer.parseInt(spigotVersion.replaceAll("\\.", ""));
			int currVersionINT = Integer.parseInt(currVersion.replaceAll("\\.", ""));
			if((currVersionINT == spigotVersionINT)) {if(firstCheck) {plugin.getLogger().info(String.format("You are using the most recent version. (v%s)", currVersion)); firstCheck = false;} return;}
			if(LastSpigotVersion == spigotVersion) {return;}
			LastSpigotVersion = spigotVersion;
			List<String> lines = new ArrayList<>();
			if(currVersionINT > spigotVersionINT) {
				lines.add(String.format("You are using the beta version of &b%s &e!", plugin.getName()));
				lines.add(" ");
				lines.add(String.format("Current version: &6v%s", currVersion));
				lines.add(String.format(" Public version: &av%s", spigotVersion));
				lines.add(" ");
				lines.add("Please note that this version is not officially released!");
			} else {
				isNewVersion = true;
				lines.add(String.format("There is a new version of &b%s &eavailable!", plugin.getName()));
				lines.add(" ");
				lines.add(String.format("Current version: &cv%s", currVersion));
				lines.add(String.format("    New version: &av%s", spigotVersion));
				lines.add(" ");
			}
			lines.add(String.format("Download: %shttps://www.spigotmc.org/resources/%s", ChatColor.YELLOW, recourceID));
			printToConsole(lines);
		}, 20l, 72000l);
	}

	private void getLatestVersion(final Consumer<String> consumer) {
		try(InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + recourceID).openStream(); Scanner scanner = new Scanner(inputStream)) {
			if(scanner.hasNext()) {consumer.accept(scanner.next());}
		} catch (Exception e) {plugin.getLogger().info("Failed to check for updates: " + e.getMessage());}
	}

	private void printToConsole(List<String> lines) {
		int longestLine = 0;
		for(String line : lines) {longestLine = Math.max(line.length(), longestLine);}
		longestLine += 4;
		if(longestLine > 120) {longestLine = 122;}
		StringBuilder stringBuilder = new StringBuilder(longestLine);
		Stream.generate(() -> "#").limit(longestLine).forEach(stringBuilder::append);
		send("&e" + stringBuilder.toString());
		for(String line : lines) {send("&e# " + line);}
		send("&e" + stringBuilder.toString());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			Player player = e.getPlayer();
			if(!(isNewVersion && (player.hasPermission(permission) || permission == null))) {return;}
			player.spigot().sendMessage(new ComponentBuilder("[").color(ChatColor.GOLD).append(plugin.getName()).color(ChatColor.AQUA).append("] ").color(ChatColor.GOLD)
				.append("There is a new update available. (").color(ChatColor.GRAY)
				.append("v" + spigotVersion).color(ChatColor.GREEN)
				.append(") Download the new version on the ").color(ChatColor.GRAY)
				.append("Spigot").color(ChatColor.YELLOW).underlined(true)
				.event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("https://www.spigotmc.org/resources/" + recourceID).color(ChatColor.GRAY).create()))
				.event(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/" + recourceID))
				.append(" page.").color(ChatColor.GRAY).underlined(false).event((HoverEvent) null).event((ClickEvent) null).create());
		}, 40l);
	}
}