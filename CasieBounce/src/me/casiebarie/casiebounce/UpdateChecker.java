package me.casiebarie.casiebounce;

import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.net.URL;

public class UpdateChecker {
	private Main plugin;
	private int recourceId;
	public UpdateChecker(Main plugin, int recourceId) {
		this.plugin = plugin;
		this.recourceId = recourceId;
	}
	public void getVersion(final Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try(InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.recourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
				if(scanner.hasNext()) {consumer.accept(scanner.next());}
			} catch (IOException exception) {plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());}
		});
	}
}
