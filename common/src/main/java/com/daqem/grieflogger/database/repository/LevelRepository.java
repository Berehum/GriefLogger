package com.daqem.grieflogger.database.repository;

import com.daqem.grieflogger.GriefLogger;
import com.daqem.grieflogger.database.Database;
import com.daqem.grieflogger.database.dialect.MySQLDialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LevelRepository extends Repository {

    private final Database database;

    public LevelRepository(Database database) {
        this.database = database;
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS levels (
                	id integer PRIMARY KEY,
                	name text NOT NULL UNIQUE
                );
                """;
        if (database.getDialect() instanceof MySQLDialect) {
            sql = """
                    CREATE TABLE IF NOT EXISTS levels (
                    	id int PRIMARY KEY AUTO_INCREMENT,
                    	name varchar(256) NOT NULL UNIQUE
                    )
                    ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4;
                    """;
        }
        database.createTable(sql);
    }

    public void insert(String name) {
        String query = database.getDialect().getInsertIgnore() + " INTO levels(name) VALUES(?);";

        database.queue.add(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
            }
        });
    }
}
