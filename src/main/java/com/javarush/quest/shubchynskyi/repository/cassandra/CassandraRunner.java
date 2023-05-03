package com.javarush.quest.shubchynskyi.repository.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import java.net.InetSocketAddress;

public class CassandraRunner {

    public static final String DATACENTER = "datacenter1";
    public static final String KEYSPACE_NAME = "quests";
    public static final String HOSTNAME = "127.0.0.1";
    public static final int PORT = 9042;

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(HOSTNAME, PORT);
        CqlSession cqlsession = CqlSession.builder()
                .addContactPoint(address)
                .withLocalDatacenter(DATACENTER)
                .withKeyspace(KEYSPACE_NAME)
                .build();
        try (cqlsession) {
//            ResultSet resultSet = cqlsession.execute("SELECT * FROM users");
            SimpleStatement statement = SimpleStatement.builder(
                            "SELECT * FROM users " +
                            "where country=:country ALLOW FILTERING")
                    .addNamedValue("country", "ua")
                    .build();
            ResultSet resultSet = cqlsession.execute(statement);
            for (Row row : resultSet) {
                String country = row.getString("country");
                Long id = row.getLong("id");
                String login = row.getString("login");
                String password = row.getString("password");
                String role = row.getString("role");
                System.out.printf("%5s%5d%15s%15s%10s%n", country, id, login, password, role);
                cqlsession.close();
            }
            System.out.println(resultSet.getExecutionInfo().getCoordinator());
            ColumnDefinitions cols = resultSet.getColumnDefinitions();
            cols.forEach(c -> System.out.println(c.getName() + " > " + c.getType()));
        }
    }
}
