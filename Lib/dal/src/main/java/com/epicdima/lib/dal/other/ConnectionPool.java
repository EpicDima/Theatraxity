package com.epicdima.lib.dal.other;

import com.epicdima.lib.dal.exceptions.ConnectionPoolException;
import com.epicdima.lib.dal.utils.ConfigurationManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author EpicDima
 */
public abstract class ConnectionPool implements AutoCloseable {

    private static final String URL_KEY = "CONNECTION_STRING";
    private static final String DATABASE_NAME_KEY = "DATABASE_NAME";
    private static final String PARAMETERS_KEY = "PARAMETERS";
    private static final String USERNAME_KEY = "USERNAME";
    private static final String PASSWORD_KEY = "PASSWORD";
    private static final String NUMBER_OF_CONNECTIONS_KEY = "CONNECTIONS";

    private static final int DEFAULT_NUMBER_OF_CONNECTIONS = 5;

    private final ConfigurationManager configurationManager;

    private final List<Connection> connections = new ArrayList<>();

    private String url;
    private String databaseName;
    private String parameters;
    private String username;
    private String password;

    public ConnectionPool(ConfigurationManager configurationManager, DatabaseInitializer databaseInitializer) {
        this.configurationManager = configurationManager;
        getConfiguration();
        databaseInitializer.createDatabase(createConnection(), databaseName);
        url += databaseName;
        databaseInitializer.createTables(createConnection());
        openConnections();
    }

    private void getConfiguration() {
        url = configurationManager.getProperty(URL_KEY);
        databaseName = configurationManager.getProperty(DATABASE_NAME_KEY);
        parameters = configurationManager.getProperty(PARAMETERS_KEY);
        username = configurationManager.getProperty(USERNAME_KEY);
        password = configurationManager.getProperty(PASSWORD_KEY);
        checkConfiguration();
    }

    private void checkConfiguration() {
        if (url == null) {
            throw new ConnectionPoolException("URL not specified in property file");
        }
        if (databaseName == null) {
            throw new ConnectionPoolException("Database name not specified in property file");
        }
        if (parameters == null) {
            throw new ConnectionPoolException("Parameters name not specified in property file");
        }
        if (username == null) {
            throw new ConnectionPoolException("Username name not specified in property file");
        }
        if (password == null) {
            throw new ConnectionPoolException("Password name not specified in property file");
        }
    }

    private void openConnections() {
        String numberOfConnectionsString = configurationManager.getProperty(NUMBER_OF_CONNECTIONS_KEY);
        int numberOfConnections;
        if (numberOfConnectionsString != null) {
            try {
                numberOfConnections = Integer.parseInt(numberOfConnectionsString);
                if (numberOfConnections <= 0) {
                    throw new ConnectionPoolException(String.format("Number of connections must be positive. Actual: %d", numberOfConnections));
                }
            } catch (NumberFormatException e) {
                throw new ConnectionPoolException("Number of connections must be a number");
            }
        } else {
            numberOfConnections = DEFAULT_NUMBER_OF_CONNECTIONS;
        }
        for (int i = 0; i < numberOfConnections; i++) {
            addConnection();
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(url + parameters, username, password);
        } catch (SQLException e) {
            throw new ConnectionPoolException("Create connection exception", e);
        }
    }

    private void addConnection() {
        connections.add(createConnection());
    }

    public synchronized Connection getConnection() {
        if (connections.isEmpty()) {
            addConnection();
        }
        return connections.remove(0);
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection == null) {
            throw new ConnectionPoolException("Release connection method does not accept null");
        }
        connections.add(connection);
    }

    @Override
    public void close() {
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new ConnectionPoolException(String.format("Close connection '%s' exception", connection), e);
            }
        }
    }
}
