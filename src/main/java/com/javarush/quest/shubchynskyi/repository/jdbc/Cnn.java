package com.javarush.quest.shubchynskyi.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Cnn {
    public static final String DB_URL = "jdbc:postgresql://localhost:2345/quests?currentSchema=game";

    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "postgres";

    // ручная регистрация драйвера, без этого драйвер менеджер не будет искать драйвер
    // TODO добавить для MySQL
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        return connection;
    }
}
