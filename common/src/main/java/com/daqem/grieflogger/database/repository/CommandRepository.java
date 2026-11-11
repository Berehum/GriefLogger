package com.daqem.grieflogger.database.repository;

import com.daqem.grieflogger.GriefLogger;
import com.daqem.grieflogger.database.Database;
import com.daqem.grieflogger.database.dialect.MySQLDialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandRepository extends Repository {

    private final Database database;

    public CommandRepository(Database database) {
        this.database = database;
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS commands (
                    time integer NOT NULL,
                    user integer NOT NULL,
                    level integer NOT NULL,
                    x integer NOT NULL,
                    y integer NOT NULL,
                    z integer NOT NULL,
                    command text NOT NULL,
                    FOREIGN KEY(user) REFERENCES users(id),
                    FOREIGN KEY(level) REFERENCES levels(id)
                );
                """;
        if (database.getDialect() instanceof MySQLDialect) {
            sql = """
                    CREATE TABLE IF NOT EXISTS commands (
                        time bigint NOT NULL,
                        user int NOT NULL,
                        level int NOT NULL,
                        x int NOT NULL,
                        y int NOT NULL,
                        z int NOT NULL,
                        command varchar(256) NOT NULL,
                        FOREIGN KEY(user) REFERENCES users(id),
                        FOREIGN KEY(level) REFERENCES levels(id)
                    )
                    ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4;
                    """;
        }
        database.createTable(sql);
    }

    public void createIndexes() {
        String sql;
        if (database.getDialect() instanceof MySQLDialect) {
            sql = "ALTER TABLE commands ADD INDEX coordinates (x, y, z);";
        } else {
            sql = "CREATE INDEX IF NOT EXISTS coordinates ON commands (x, y, z);";
        }
        database.execute(sql, false);
    }

    public void insert(long time, String userUuid, String levelName, int x, int y, int z, String command) {
        String query = database.getDialect().getInsertIgnore() + " INTO commands(time, user, level, x, y, z, command) " +
                "VALUES(?, (" +
                "SELECT id FROM users WHERE uuid = ?" +
                "), (" +
                "SELECT id FROM levels WHERE name = ?" +
                "), ?, ?, ?, ?);";

        database.queue.add(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setLong(1, time);
                preparedStatement.setString(2, userUuid);
                preparedStatement.setString(3, levelName);
                preparedStatement.setInt(4, x);
                preparedStatement.setInt(5, y);
                preparedStatement.setInt(6, z);
                preparedStatement.setString(7, command);
                preparedStatement.executeUpdate();
            }
        });
    }
}