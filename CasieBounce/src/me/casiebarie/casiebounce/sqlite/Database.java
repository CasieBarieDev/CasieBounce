package me.casiebarie.casiebounce.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.World;

import me.casiebarie.casiebounce.Main;

public abstract class Database {
	public Main plugin;
	public Connection connection;
	public String table = "bounce_stats";
	public Integer tokens = 0;
	public static String sqlConnectionExecute() {return "Couldn't execute MySQL statement: ";}
	public static String sqlConnectionClose() {return "Failed to close MySQL connection: ";}
	public static String noSQLConnection() {return "Unable to retreive MYSQL connection: ";}
	public static String noTableFound() {return "Database Error: No Table Found";}
	public static void execute(Main plugin, Exception ex) {plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);}
	public static void close(Main plugin, Exception ex) {plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);}
	public void close(PreparedStatement ps, ResultSet rs) {try {if(ps != null) {ps.close();} if(rs != null) {rs.close();}} catch (SQLException e) {close(plugin, e);}}
	public void doFinally(PreparedStatement ps, Connection conn) {try {if(ps != null) {ps.close();} if(conn != null) {conn.close();}} catch (SQLException e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionClose(), e);}}
	public Database(Main plugin) {this.plugin = plugin;}
	public abstract Connection getSQLConnection();
	public abstract void load();
	public void initialize() {
		connection = getSQLConnection();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE regionplayer = ?");
			ResultSet rs = ps.executeQuery();
			close(ps,rs);
		} catch (SQLException e) {plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", e);}
	}

	public Integer getBounces(String mode, String value, String world) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Integer> bounces = new ArrayList<>();
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + table);
			rs = ps.executeQuery();
			while (rs.next()) {
				switch (mode) {
				case "TOTAL": bounces.add(rs.getInt(4)); break;
				case "PLAYER": if(rs.getString(1).split(",")[1].equals(value)) {bounces.add(rs.getInt(4));} break;
				case "PLAYERREGION": if(rs.getString(1).equals(value) && rs.getString(3).equals(world)) {return rs.getInt(4);}
				case "TOTALREGION": if(rs.getString(1).split(",")[0].equals(value) && rs.getString(3).equals(world)) {bounces.add(rs.getInt(4));} break;
				default: break;}
			} return bounces.stream().mapToInt(Integer::intValue).sum();
		} catch (Exception e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionExecute(), e);
		} finally {doFinally(ps, conn);} return 0;
	}

	public Map<String, ArrayList<String>> getLeaderboard(String mode, String value, String world) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, ArrayList<String>> map = new HashMap<>();
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + table);
			rs = ps.executeQuery();
			while (rs.next()) {
				ArrayList<String> list = new ArrayList<>();
				if(mode.equals("TOTAL") || (rs.getString(1).split(",")[0].equals(value) && rs.getString(3).equals(world))) {
					String key = rs.getString(1).split(",")[1];
					list.add(rs.getString(2));
					if(map.containsKey(key)) {
						Integer bounces = rs.getInt(4) + Integer.valueOf(map.get(key).get(1));
						list.add(bounces.toString());
						map.put(key, list);
					} else {
						list.add(Integer.toString(rs.getInt(4)));
						map.put(key, list);
					}
				}
			} return map;
		} catch (Exception e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionExecute(), e);
		} finally {doFinally(ps, conn);} return null;
	}

	public void addBounces(String region, UUID uuid, String playerName, World world, Integer bounces) {
		String regionPlayer = region + "," + uuid.toString();
		Integer newBounces = bounces + getBounces("PLAYERREGION", regionPlayer, world.getName());
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("REPLACE INTO " + table + " (regionplayer,playername,world,bounces) VALUES(?,?,?,?)");
			ps.setString(1, regionPlayer);
			ps.setString(2, playerName);
			ps.setString(3, world.getName());
			ps.setInt(4, newBounces);
			ps.executeUpdate();
			return;
		} catch (SQLException e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionExecute(), e);
		} finally {doFinally(ps, conn);} return;
	}
}