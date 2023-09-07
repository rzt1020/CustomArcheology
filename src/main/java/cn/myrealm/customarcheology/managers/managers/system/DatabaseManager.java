package cn.myrealm.customarcheology.managers.managers.system;

import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.enums.SQLs;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rzt10
 */
public class DatabaseManager extends AbstractManager {

    private static DatabaseManager instance;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    private void setupDatabase() {
        try {
            if (Config.USE_MYSQL.asBoolean()) {
                String host = Config.MYSQL_HOST.asString();
                int port = Config.MYSQL_PORT.asInt();
                String user = Config.MYSQL_USER.asString();
                String password = Config.MYSQL_PASSWORD.asString();
                String dbname = Config.MYSQL_DATABASE.asString();
                String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
                connection = DriverManager.getConnection(url, user, password);
            } else {
                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/database.db");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onInit() {
        setupDatabase();
        createTables();
    }

    private void createTables() {
        executeAsyncUpdate(SQLs.CREATE_BLOCK_TABLE.getSQL());
    }

    public void executeAsyncQuery(final String query, final Callback<Map<String, Object>> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    List<Map<String, Object>> result = new ArrayList<>();
                    while (resultSet.next()) {
                        Map<String, Object> resultData = new HashMap<>(5);
                        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                            resultData.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
                        }
                        result.add(resultData);
                    }
                    callback.onSuccess(result);
                } catch (SQLException e) {
                    callback.onFailure(e);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void executeAsyncUpdate(final String query) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    connection.createStatement().executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    protected void onDisable() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public interface Callback<T> {
        /**
         * call back the result of query
         * @param results result of query
         */
        void onSuccess(List<T> results);

        /**
         * call back the error of query
         * @param e error of query
         */
        void onFailure(Exception e);
    }
}