package com.example.quest_project.config;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Config {
    public static final Path WEB_INF = Paths.get(URI.create(
                    Objects.requireNonNull(
                            Config.class.getResource("/")       // получили путь к папке WEB_INF/classes в папке Target
                    ).toString()))
            .getParent();   // поднялись на уровень выше, и теперь Path WEB_INF указывает на папку WEB_INF
}
