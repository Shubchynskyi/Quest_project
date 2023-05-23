package com.javarush.quest.shubchynskyi.repository.jdbc;



import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.DaoException;
import com.javarush.quest.shubchynskyi.repository.Repository;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class UserDao implements Repository<User> {

    public static final String SQL_GET_ALL = """
            SELECT "id", "login", "password", "role"
            FROM "users"
            """;

    public static final String SQL_GET_BY_ID = """
            SELECT "id", "login", "password", "role"
            FROM "users"
            WHERE id=?
            """;

    public static final String SQL_FIND = """
            SELECT "id", "login", "password", "role"
            FROM "users"
            WHERE
            (? OR id=?) AND
            (? OR login=?) AND
            (? OR password=?) AND
            (? OR role=?);
            """;
    public static final String SQL_CREATE = """
             INSERT INTO "users"(login, password, role)
             VALUES (?,?,?)
            """;
    public static final String SQL_UPDATE = """
            UPDATE "users"
               SET login=?,
                   password=?,
                   role=?
             WHERE id = ?;
            """;
    public static final String SQL_DELETE = """
            DELETE
            FROM "users"
            WHERE id=?
            """;

    @Override
    public List<User> getAll() {
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_GET_ALL)) {
            return buildUserStream(preparedStatement).toList();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Stream<User> find(User user) {
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_FIND)) {
            //Need reflection? yes!
            Long id = user.getId();
            preparedStatement.setBoolean(1, Objects.isNull(id));
            preparedStatement.setLong(2, Objects.nonNull(id) ? id : 0L);

            String login = user.getLogin();
            preparedStatement.setBoolean(3, Objects.isNull(login));
            preparedStatement.setString(4, Objects.nonNull(login) ? login : "");

            String password = user.getPassword();
            preparedStatement.setBoolean(5, Objects.isNull(password));
            preparedStatement.setString(6, Objects.nonNull(password) ? password : "");

            Role role = user.getRole();
            preparedStatement.setBoolean(7, Objects.isNull(role));
            preparedStatement.setString(8, Objects.nonNull(role) ? role.toString() : "");
            return buildUserStream(preparedStatement);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Stream<User> buildUserStream(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        Stream.Builder<User> users = Stream.builder();
        while (resultSet.next()) {
            users.add(User.builder()
                    .id(resultSet.getLong("id"))
                    .login(resultSet.getString("login"))
                    .password(resultSet.getString("password"))
                    .role(Role.valueOf(resultSet.getString("role")))
                    .build());
        }
        return users.build();
    }

    @Override
    public User get(long id) {
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_GET_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return User.builder()
                    .id(resultSet.getLong("id"))
                    .login(resultSet.getString("login"))
                    .password(resultSet.getString("password"))
                    .role(Role.valueOf(resultSet.getString("role")))
                    .build();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void create(User user) {
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole().toString());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            user.setId(generatedKeys.getLong(1));
        } catch (SQLException e) {
            throw new DaoException(e);
        }
//        return user;
    }

    @Override
    public void update(User user) {
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole().toString());
            preparedStatement.setLong(4, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void delete(User user) {
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_DELETE)) {
            preparedStatement.setLong(1, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
