package com.example.quest_project.config;

import com.example.quest_project.entity.User;
import com.example.quest_project.repository.*;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public enum Config {

    CONFIG;

    public final GameRepository gameRepository = new GameRepository();
    public final UserRepository userRepository = new UserRepository();
    public final QuestRepository questRepository = new QuestRepository();
    public final QuestionRepository questionRepository = new QuestionRepository();
    public final AnswerRepository answerRepository = new AnswerRepository();


    public final Path WEB_INF = Paths.get(URI.create(
                    Objects.requireNonNull(
                            Config.class.getResource("/")       // получили путь к папке WEB_INF/classes в папке Target
                    ).toString()))
            .getParent();   // поднялись на уровень выше, и теперь Path WEB_INF указывает на папку WEB_INF

}
