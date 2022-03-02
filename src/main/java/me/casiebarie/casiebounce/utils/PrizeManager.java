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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class PrizeManager {
	final Main plugin;
	private Economy econ;
	private Permission perm;
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
		@Override public void sendMessage(@NotNull String s) {}
		@Override public void sendMessage(@NotNull String... strings) {}
		@Override public void sendMessage(@Nullable UUID uuid, @NotNull String s) {}
		@Override public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {}
		@NotNull @Override public Server getServer() {return plugin.getServer();}
		@NotNull @Override public String getName() {return "CasieBounceConsoleSender";}
		@NotNull @Override public Spigot spigot() {return Bukkit.getConsoleSender().spigot();}
		@Override public boolean isConversing() {return false;}
		@Override public void acceptConversationInput(@NotNull String s) {}
		@Override public boolean beginConversation(@NotNull Conversation conversation) {return false;}
		@Override public void abandonConversation(@NotNull Conversation conversation) {}
		@Override public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent conversationAbandonedEvent) {}
		@Override public void sendRawMessage(@NotNull String s) {}
		@Override public void sendRawMessage(@Nullable UUID uuid, @NotNull String s) {}
		@Override public boolean isPermissionSet(@NotNull String s) {return false;}
		@Override public boolean isPermissionSet(@NotNull org.bukkit.permissions.Permission permission) {return false;}
		@Override public boolean hasPermission(@NotNull String s) {return true;}
		@Override public boolean hasPermission(@NotNull org.bukkit.permissions.Permission permission) {return true;}
		@NotNull @Override public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {return null;}
		@NotNull @Override public PermissionAttachment addAttachment(@NotNull Plugin plugin) {return null;}
		@Nullable @Override public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {return null;}
		@Nullable @Override public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {return null;}
		@Override public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {}
		@Override public void recalculatePermissions() {}
		@NotNull @Override public Set<PermissionAttachmentInfo> getEffectivePermissions() {return null;}
		@Override public boolean isOp() {return true;}
		@Override public void setOp(boolean b) {}
	}
}