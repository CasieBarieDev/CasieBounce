package me.casiebarie.casiebounce.database;

import me.casiebarie.casiebounce.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResetData implements Listener{
	final Main plugin;
	final Database db;
	private static final Map<String, String> map = new HashMap<>();
	private void finalMSG(CommandSender sender, String type, String data) {sender.sendMessage("§6[§bCasieBounce§6] §aDeleting " + type.toLowerCase() + "data of §4" + data + "§a!");}
	private void noDataMSG(CommandSender sender, String type, String error) {sender.sendMessage("§6[§bCasieBounce§6] §cThere is no data of this " + type.toLowerCase() + " in the database! §8" + error);}
	public ResetData(Main plugin) {this.plugin = plugin; this.db = plugin.getDatabase(); Bukkit.getPluginManager().registerEvents(this, plugin);}

	public void createMessage(CommandSender sender, String type, String data) {
		Boolean c = !(sender instanceof Player);
		if(!checkExisting(sender, c, type, data)) {return;}
		if(type.equals("ALL")) {sender.sendMessage("§6[§bCasieBounce§6] §cYou are about to delete §4ALL §cof the data! To confirm please type §a/cb CONFIRM §cin the chat within 30 seconds!");
		} else {sender.sendMessage("§6[§bCasieBounce§6] §cYou are about to delete the " + type + " data of §4" + data + "§c! To confirm please type §a/cb CONFIRM §cin the chat within 30 seconds!");}
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if(map.containsKey((c) ? "CONSOLE" : ((Player)sender).getUniqueId().toString())) {
				sender.sendMessage("§6[§bCasieBounce§6] §cDeletion cancelled. §8OUT_OF_TIME");
				map.remove((c) ? "CONSOLE" : ((Player)sender).getUniqueId().toString());
			}
		}, 600L);
	}

	private Boolean checkExisting(CommandSender sender, Boolean c, String type, String data) {
		if(!data.contains("(") && !data.contains(":")) {noDataMSG(sender, type, ""); return false;}
		String[] dataSplit;
		String uuid, error = "";
		List<String> available = new ArrayList<>();
		switch (type) {
			case "REGIONPLAYER":
				dataSplit = data.split("@");
				uuid = dataSplit[1].split("\\(")[1].replaceAll("\\)", "");
				available.addAll(db.getCompletions(type, uuid)); break;
			case "REGION":
				if(data.contains(":")) {available.addAll(db.getCompletions(type, null));
				} else {error = "PLEASE SPECIFY WORLD";} break;
			case "PLAYER":
				uuid = data.split("\\(")[1].replaceAll("\\)", "");
				available.addAll(db.getCompletions(type, uuid)); break;
			case "ALL": map.put((c) ? "CONSOLE" : ((Player)sender).getUniqueId().toString(), "ALL-ALL"); return true;
			default: break;}
		if(available.contains(data)) {map.put((c) ? "CONSOLE" : ((Player)sender).getUniqueId().toString(), type + "_" + data); return true;
		} else {noDataMSG(sender, type, error); return false;}
	}

	public void confirm(CommandSender sender, Boolean c) {
		if(!map.containsKey((c) ? "CONSOLE" : ((Player)sender).getUniqueId().toString())) {return;}
		String[] typeData;
		if(c) {typeData = map.get("CONSOLE").split("_");
		} else {typeData = map.get(((Player)sender).getUniqueId().toString()).split("_");}
		switch (typeData[0]) {
			case "REGIONPLAYER": db.resetData(typeData[0], typeData[1].split(":")[0], typeData[1].split("\\(")[1].replaceAll("\\)", ""), typeData[1].split(":")[1]); finalMSG(sender, typeData[0], typeData[1]); break;
			case "REGION": db.resetData(typeData[0], typeData[1].split(":")[0], null, typeData[1].split(":")[1]); finalMSG(sender, typeData[0], typeData[1]); break;
			case "PLAYER": db.resetData(typeData[0], null, typeData[1].split("\\(")[1].replaceAll("\\)", ""), null); finalMSG(sender, typeData[0], typeData[1]); break;
			case "ALL": db.resetData(typeData[0], null, null, null); sender.sendMessage("§6[§bCasieBounce§6] §aDeleting §4ALL §athe data!"); break;
			default: break;} map.remove((c) ? "CONSOLE" : ((Player)sender).getUniqueId().toString());
	}
}