package me.casiebarie.casiebounce.database;

import me.casiebarie.casiebounce.Main;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLite extends Database {
	final String dbName = "bounce_stats";
	public SQLite(Main plugin) {super(plugin);}
	public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS bounce_stats (`regionplayer` varchar(32) NOT NULL,`playername` varchar(32) NOT NULL,`world` varchar(32) NOT NULL,`bounces` int(11) NOT NULL,PRIMARY KEY (`regionplayer`));";
	public Connection getSQLConnection() {
		File dataFile = new File(plugin.getDataFolder(), dbName + ".db");
		if(!dataFile.exists()) {
			try {dataFile.createNewFile();
			} catch (Exception e) {plugin.getLogger().log(Level.SEVERE, "Failed to create: " + dbName + ".db");}
		}
		try {
			if(connection != null && !connection.isClosed()) {return connection;}
			Class.forName("org.sqlite.JDBC");
			return DriverManager.getConnection("jdbc:sqlite:" + dataFile);
		} catch (SQLException e) {plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", e);
		} catch (ClassNotFoundException e) {plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");}
		return null;
	}

	public void load() {
		connection = getSQLConnection();
		try {
			Statement s = connection.createStatement();
			s.executeUpdate(SQLiteCreateTokensTable);
			s.close();
		} catch (SQLException e) {e.printStackTrace();}
	}
}