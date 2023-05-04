package com.javarush.quest.shubchynskyi.repository.liquibase;


import com.javarush.quest.shubchynskyi.repository.jdbc.CnnPool;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ValidatorDataBase {

    public static final String CLASSPATH_DB_CHANGELOG_XML = "classpath:/db/changelog.xml";

    @SneakyThrows
    public static void main(String[] args) {
        Map<String, Object> config = new HashMap<>();
        Scope.child(config, () -> {
            try (Connection connection = CnnPool.get()) {
                JdbcConnection jdbcConnection = new JdbcConnection(connection);
                Database database = DatabaseFactory
                        .getInstance()
                        .findCorrectDatabaseImplementation(jdbcConnection);

                Liquibase liquibase = new Liquibase(
                        CLASSPATH_DB_CHANGELOG_XML,
                        new ClassLoaderResourceAccessor(), database);
                liquibase.update(new Contexts(), new LabelExpression());
            }
            finally {
                CnnPool.destroy();
            }
        });
    }
}
