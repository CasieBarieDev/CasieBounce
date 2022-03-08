package me.casiebarie.casiebounce.utils;

import me.casiebarie.casiebounce.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandException;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class PrizeManager {
	final Main plugin; private Economy econ; private Permission perm;
	public PrizeManager(Main plugin) {this.plugin = plugin; if(plugin.vaultPresent()) {this.econ = Main.econ; this.perm = Main.perm;}}
	@SuppressWarnings("deprecation")
	public void givePrize(Player player, String prize) {
		String[] prizeSplit = prize.split("@");
		switch (prizeSplit[0]) {
		case "MONEY": if(plugin.vaultPresent()) {econ.depositPlayer(player, Double.parseDouble(prizeSplit[1]));} break;
		case "PERMISSION": if(plugin.vaultPresent()) {perm.playerAdd(player, prizeSplit[1]);} break;
		case "ITEM":
			ItemStack itemStack;
			if(prizeSplit[1].contains(":")) {
				String[] itemSplit = prizeSplit[1].split(":");
				itemStack = new ItemStack(Material.matchMaterial(itemSplit[0]), 1, Byte.parseByte(itemSplit[1]));
			} else {itemStack = new ItemStack(Material.matchMaterial(prizeSplit[1]));}
			player.getInventory().addItem(itemStack); break;
		case "COMMAND":
			try {
				String command = prizeSplit[1]
					.replaceAll("%player%", player.getName())
					.replaceAll("%X%", "" + player.getLocation().getX())
					.replaceAll("%Y%", "" + player.getLocation().getY())
					.replaceAll("%Z%", "" + player.getLocation().getZ())
					.replaceAll("%PITCH%", "" + player.getLocation().getPitch())
					.replaceAll("%YAW%", "" + player.getLocation().getYaw());
				if(!Bukkit.getServer().dispatchCommand(new CasieBounceConsoleSender(), command)) {
					plugin.getLogger().log(Level.WARNING, "The command " + prizeSplit[1] + " is not recognized!");
				}
			} catch (CommandException e) {
				plugin.getLogger().log(Level.SEVERE, "The command " + prizeSplit[1] + " is not recognized!");
				e.printStackTrace();
			} break;
		default: break;}
	}

	public class CasieBounceConsoleSender implements ConsoleCommandSender {
		@Override public void sendMessage(String s) {}
		@Override public void sendMessage(String... strings) {}
		@Override public void sendMessage(UUID uuid, String s) {}
		@Override public void sendMessage(UUID uuid, String... strings) {}
		@Override public Server getServer() {return plugin.getServer();}
		@Override public String getName() {return "CasieBounceConsoleSender";}
		@Override public Spigot spigot() {return Bukkit.getConsoleSender().spigot();}
		@Override public boolean isConversing() {return false;}
		@Override public void acceptConversationInput(String s) {}
		@Override public boolean beginConversation(Conversation conversation) {return false;}
		@Override public void abandonConversation(Conversation conversation) {}
		@Override public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {}
		@Override public void sendRawMessage(String s) {}
		@Override public void sendRawMessage(UUID uuid, String s) {}
		@Override public boolean isPermissionSet(String s) {return false;}
		@Override public boolean isPermissionSet(org.bukkit.permissions.Permission permission) {return false;}
		@Override public boolean hasPermission(String s) {return true;}
		@Override public boolean hasPermission(org.bukkit.permissions.Permission permission) {return true;}
		@Override public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {return null;}
		@Override public PermissionAttachment addAttachment(Plugin plugin) {return null;}
		@Override public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {return null;}
		@Override public PermissionAttachment addAttachment(Plugin plugin, int i) {return null;}
		@Override public void removeAttachment(PermissionAttachment permissionAttachment) {}
		@Override public void recalculatePermissions() {}
		@Override public Set<PermissionAttachmentInfo> getEffectivePermissions() {return null;}
		@Override public boolean isOp() {return true;}
		@Override public void setOp(boolean b) {}
	}
}