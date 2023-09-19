package de.banarnia.fancyhomes.api.sql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class MySQL extends Database {

    private final String host;
    private final int port;

    private final String user;
    private final String password;

    public MySQL(String databaseName, Logger logger, String host, int port, String user, String password) {
        super(databaseName, logger);
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Override
    public boolean openConnection() {
        try {
            if (connection != null && !connection.isClosed())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            logger.warning("Error while connecting to database '" + databaseName + "'.");
            return false;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.warning("Cannot open connection to mysql server - jdbc driver missing.");
            return false;
        }
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"
                            + this.host + ":" + this.port + "/" + this.databaseName
                            + "?useJDBCCompliantTimezoneShift=true&serverTimezone=Europe/Berlin&useUnicode=true&autoReconnect=true",
                    this.user, this.password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            logger.warning("Error while connecting to database '" + databaseName + "'.");
            return false;
        }

        return true;
    }
}
