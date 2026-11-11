package com.daqem.grieflogger.database.repository;

import com.daqem.grieflogger.GriefLogger;
import com.daqem.grieflogger.database.Database;
import com.daqem.grieflogger.database.dialect.MySQLDialect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserRepository extends Repository {

    private final Database database;

    public UserRepository(Database database) {
        this.database = database;
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                	id integer PRIMARY KEY,
                	name text NOT NULL,
                	uuid text DEFAULT NULL UNIQUE
                );
                """;
        if (database.getDialect() instanceof MySQLDialect) {
            sql = """
                    CREATE TABLE IF NOT EXISTS users (
                    	id int PRIMARY KEY AUTO_INCREMENT,
                    	name varchar(16) NOT NULL,
                    	uuid varchar(36) DEFAULT NULL UNIQUE
                    )
                    ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4;
                    """;
        }
        database.createTable(sql);
    }


    public void insertOrUpdateName(String name, String uuid) {
        String query = "INSERT INTO users(name, uuid) VALUES(?, ?) " +
                database.getDialect().getOnConflictUpdate("uuid", "name = ?");

        database.queue.add(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, uuid);
                preparedStatement.setString(3, name);
                preparedStatement.executeUpdate();
            }
        });
    }

    public void insertNonPlayer(String name) {
        String query = "INSERT INTO users(name) VALUES(?) " +
                database.getDialect().getOnConflictDoNothing("name");

        database.queue.add(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
            }
        });
    }

    public Map<Integer, String> getAllUsernames() {
        Map<Integer, String> usernames = new HashMap<>();
        String query = "SELECT id, name FROM users";

        try (PreparedStatement preparedStatement = database.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                usernames.put(
                        resultSet.getInt(1),
                        resultSet.getString(2)
                );
            }
        } catch (SQLException exception) {
            GriefLogger.LOGGER.error("Failed to get all usernames from database", exception);
        }
        return usernames;
    }
}
