package me.casiebarie.casiebounce.database;

import me.casiebarie.casiebounce.Main;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public abstract class Database {
	public Main plugin;
	public Connection connection;
	public String table = "bounce_stats";
	public Integer tokens = 0;
	public static String sqlConnectionExecute = "Couldn't execute MySQL statement: ", sqlConnectionClose = "Failed to close MySQL connection: ", noSQLConnection = "Unable to retreive MYSQL connection: ", noTableFound = "Database Error: No Table Found";
	public static void execute(Main plugin, Exception ex) {plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);}
	public static void close(Main plugin, Exception ex) {plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);}
	public void close(PreparedStatement ps, ResultSet rs) {try {if(ps != null) {ps.close();} if(rs != null) {rs.close();}} catch (SQLException e) {close(plugin, e);}}
	public void doFinally(PreparedStatement ps, Connection conn) {try {if(ps != null) {ps.close();} if(conn != null) {conn.close();}} catch (SQLException e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionClose, e);}}
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
		ResultSet rs;
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
		} catch (Exception e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionExecute, e);
		} finally {doFinally(ps, conn);} return 0;
	}

	public Map<String, ArrayList<String>> getLeaderboard(String mode, String value, String world) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs;
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
						int bounces = rs.getInt(4) + Integer.parseInt(map.get(key).get(1));
						list.add(Integer.toString(bounces));
					} else {list.add(Integer.toString(rs.getInt(4)));}
					map.put(key, list);
				}
			} return map;
		} catch (Exception e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionExecute, e);
		} finally {doFinally(ps, conn);} return null;
	}

	public List<String> getCompletions(String mode, String data) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs;
		List<String> completions = new ArrayList<>();
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + table);
			rs = ps.executeQuery();
			while(rs.next()) {
				String[] regionPlayer = rs.getString(1).split(",");
				String world = rs.getString(3);
				switch (mode) {
					case "PLAYER": if(!completions.contains(rs.getString(2) + "(" + regionPlayer[1] + ")")) {completions.add(rs.getString(2) + "(" + regionPlayer[1] + ")");} break;
					case "REGION": if(!completions.contains(regionPlayer[0] + ":" + world)) {completions.add(regionPlayer[0] + ":" + world);} break;
					case "REGIONPLAYER": if(regionPlayer[0].equals(data.split(":")[0]) && rs.getString(3).equals(data.split(":")[1])) {if(!completions.contains(rs.getString(2) + "(" + regionPlayer[1] + ")")) {completions.add(rs.getString(2) + "(" + regionPlayer[1] + ")");}} break;
					default: break;}
			} return completions;
		} catch (Exception e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionExecute, e);
		} finally {doFinally(ps, conn);} return null;
	}

	public void addBounces(String region, UUID uuid, String playerName, World world, Integer bounces) {
		String regionPlayer = region + "," + uuid.toString();
		int newBounces = bounces + getBounces("PLAYERREGION", regionPlayer, world.getName());
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
		} catch (SQLException e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionExecute, e);
		} finally {doFinally(ps, conn);}
	}

	public void resetData(String type, String region, String player, String world) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			switch (type) {
				case "REGIONPLAYER": ps = conn.prepareStatement("DELETE FROM " + table + " WHERE regionplayer=`" + region + "," + player + "` AND world=`" + world + "';"); break;
				case "REGION": ps = conn.prepareStatement("DELETE FROM " + table + " WHERE regionplayer LIKE '" + region + ",%' AND world=`" + world + "`;"); break;
				case "PLAYER": ps = conn.prepareStatement("DELETE FROM " + table + " WHERE regionplayer LIKE '%," + player + "';"); break;
				case "ALL": ps = conn.prepareStatement("DELETE FROM " + table);
				default: break;}
			ps.executeUpdate();
		} catch (SQLException e) {plugin.getLogger().log(Level.SEVERE, sqlConnectionExecute, e);
		} finally {doFinally(ps, conn);}
	}
}