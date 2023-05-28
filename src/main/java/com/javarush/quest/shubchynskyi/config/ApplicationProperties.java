package com.javarush.quest.shubchynskyi.config;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

@Component
public class ApplicationProperties extends Properties {

    public static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";
    public static final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";
    public static final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";
    public static final String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";
    public static final String HIBERNATE_CONNECTION_DEFAULT_SCHEMA = "hibernate.connection.default_schema";
    public static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

    @SneakyThrows
    public ApplicationProperties() {
        this.load(new FileReader(CLASSES_ROOT + "/application.properties"));
        try {
            String driver = this.getProperty(HIBERNATE_CONNECTION_DRIVER_CLASS);
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public final static Path CLASSES_ROOT = Paths.get(URI.create(
            Objects.requireNonNull(
                    ApplicationProperties.class.getResource("/")
            ).toString()));

    public final static Path WEB_INF = CLASSES_ROOT.getParent();
}
