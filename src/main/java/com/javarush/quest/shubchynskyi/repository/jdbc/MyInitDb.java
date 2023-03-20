package com.javarush.quest.shubchynskyi.repository.jdbc;


import com.javarush.quest.shubchynskyi.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class MyInitDb {

    public static void main(String[] args) {
        //передаем в сканер sql файл
        Scanner scanner = new Scanner(
                Objects.requireNonNull(
                        Cnn.class.getResourceAsStream("/postgres_init.sql")
                )
        ).useDelimiter(";"); // разделяем на отдельные запросы
        while (scanner.hasNext()) {
            String sql = scanner.next();
            process(sql); // пока есть строки - выполняем process
        }

    }

    private static void process(String sql) {
        String firstWord = sql.trim().split("\\s+", 2)[0]; // выбираем первое слово в запросе
        String result = switch (firstWord) {
            case "SELECT" -> executeSelect(sql);
            case "INSERT" -> executeInsert(sql);
            default -> executeSql(sql);
        };
        System.out.println(result);
    }

    private static String executeSelect(String sql) {
        StringBuilder out = new StringBuilder(sql.trim() + "\n"); // билдер для результата
        try (Connection connection = Cnn.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql); // получаю ResultSet по запросу из SELECT

            // в метадате есть данные о прочитанной таблице
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) { // getColumnCount - количество колонок таблицы
                out.append(String.format("%-10s ", metaData.getColumnName(i)));  // аппендим заголовки (%-10s - 10 - расстояние после вставки)
            }
            out.append("\n").append("-".repeat(metaData.getColumnCount()*11)).append("\n"); // аппендим "-----" разделитель
            while (resultSet.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {  // нумерация в SQL с "1" а не с нуля
                    out.append(String.format("%-10s ", resultSet.getString(metaData.getColumnName(i))));
                }
                out.append("\n");
            }
            return out.toString();
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    private static String executeInsert(String sql) {
        StringBuilder out = new StringBuilder(sql.trim()); // в билдер положили наш sql запрос, в который будем добавлять ключи
        try (Connection connection = Cnn.getConnection();
             Statement statement = connection.createStatement()) {
            int rowCount = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS); // количество записей которые поменялись
            // Statement.RETURN_GENERATED_KEYS - вернет resultSet с ключами

            // ResultSet - типа итератора по строкам, после каждого шага можно вытащить что угодно из строки
            ResultSet generatedKeys = statement.getGeneratedKeys();

            // добавляем к запросу ключи
            out.append("\nRows: ").append(rowCount).append(" Keys: "); // количество ключей, которое получили ранее (rowCount)
            while (generatedKeys.next()) {
                out.append(generatedKeys.getString(1)).append(" "); // аппендим ключи через пробел
            }
            out.append("\n");
            return out.toString(); // возвращаем результат
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    private static String executeSql(String sql) {
        try (Connection connection = Cnn.getConnection();
             Statement statement = connection.createStatement()) {
            // если не SELECT и не INSERT, то просто выполняем запрос и возвращаем его строковое представление
            // например DROP - результата нет
            statement.execute(sql);
            return sql.trim();
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }


}
