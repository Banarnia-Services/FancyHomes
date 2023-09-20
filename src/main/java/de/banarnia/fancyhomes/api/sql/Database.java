package de.banarnia.fancyhomes.api.sql;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public abstract class Database {

    protected String databaseName;
    protected Connection connection;
    protected Logger logger;

    public Database(String databaseName, Logger logger) {
        this.databaseName = databaseName;
        this.logger = logger;
    }

    /**
     * Check if connection in up.
     * @return True if database is connected, else false.
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getLogger().warning("Error while connecting to database '" + databaseName + "'.");
            return false;
        }
    }

    /**
     * Close the connection to the database.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
            logger.info("Connection to database '" + databaseName + "' closed.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            logger.warning("Error while closing the connection to the database '" + databaseName + "'.");
        }
    }

    /**
     * Establish a connection to the database.
     * @return True if connected successfully, else false.
     */
    public abstract boolean openConnection();

    /**
     * Execute a sql update.
     * @param sql Sql statement.
     * @return True if it was successful, else false.
     */
    public boolean executeUpdate(String sql) {
        return executeUpdate(sql, null);
    }

    /**
     * Execute a mysql update.
     * @param sql Sql statement.
     * @param objects Objects to insert.
     * @return True if it was successful, else false.
     */
    public boolean executeUpdate(String sql, Object... objects) {
        if (connection == null) return false;
        openConnection();
        if (sql == null || sql.length() == 0) return false;

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (objects != null)
                for (int i = 0; i < objects.length; i++)
                    preparedStatement.setObject(i + 1, objects[i]);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.warning("Error while sending an mysql update:");
            logger.warning("Statement: " + sql);
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception ex) {


            }
        }

        return true;
    }

    /**
     * Execute an update on another thread.
     * @param sql Sql statement.
     */
    public void executeUpdateAsync(String sql) {
        CompletableFuture.runAsync(() -> {
            openConnection();
            executeUpdate(sql);
        });
    }

    /**
     * Execute an update on another statement.
     * @param sql Sql statement.
     * @param objects Objects to insert.
     */
    public void executeUpdateAsync(String sql, Object... objects) {
        CompletableFuture.runAsync(() -> {
            openConnection();
            executeUpdate(sql, objects);
        });
    }

    /**
     * Execute a sql query.
     * @param sql Sql statement.
     * @return Result.
     */
    public ResultSet executeQuery(String sql) {
        return executeQuery(sql, null);
    }

    /**
     * Execute a sql query.
     * @param sql Sql statement.
     * @param objects Objects to insert.
     * @return Result.
     */
    public ResultSet executeQuery(String sql, Object... objects) {
        openConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            if (objects != null)
                for (int i = 0; i < objects.length; i++)
                    preparedStatement.setObject(i + 1, objects[i]);

            resultSet = preparedStatement.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.warning("Error while sending a mysql query:");
            logger.warning("Query: " + sql);
        }

        return resultSet;
    }

    /**
     * Execute a query async.
     * @param sql Sql statement.
     * @return Result.
     */
    public CompletableFuture<ResultSet> executeQueryAsync(String sql) {
        return CompletableFuture.supplyAsync(() -> {
            openConnection();
            return executeQuery(sql);
        });
    }

    /**
     * Execute a query async.
     * @param sql Sql statement.
     * @param objects Objects to insert.
     * @return Result.
     */
    public CompletableFuture<ResultSet> executeQueryAsync(String sql, Object... objects) {
        return CompletableFuture.supplyAsync(() -> {
            openConnection();
            return executeQuery(sql, objects);
        });
    }

    /**
     * Deletes a mysql table async.
     * @param name Table.
     */
    public void deleteTable(String name) {
        executeUpdateAsync("DROP TABLE '" + name + "';");
    }

    /**
     * Reset a mysql table.
     * @param name Table.
     */
    public void clearTable(String name) {
        executeUpdateAsync("TRUNCATE TABLE '" + name + "';");
    }


}
