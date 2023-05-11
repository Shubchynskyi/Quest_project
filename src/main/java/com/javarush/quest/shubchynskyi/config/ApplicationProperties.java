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

    @SneakyThrows
    public ApplicationProperties() {
        this.load(new FileReader(CLASSES_ROOT + "/application.properties"));
    }

    //any runtime
    public final static Path CLASSES_ROOT = Paths.get(URI.create(
            Objects.requireNonNull(
                    ApplicationProperties.class.getResource("/")
            ).toString()));

    //only in Tomcat (not use in tests)
    public final static Path WEB_INF = CLASSES_ROOT.getParent();
}
