package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;


public class SessionCreator implements AutoCloseable{

    private final SessionFactory sessionFactory;

    public SessionCreator() {
        // если есть файл hibernate.cfg.xml, то нужно просто создать конфиг
        Configuration configuration = new Configuration(); //

        configuration.configure();  // получает настройки из "hibernate.cfg.xml" НУЖЕН ТОЛЬКО ДЛЯ xml
        configuration.addAnnotatedClass(User.class);
// **************** через Java код = configuration.setProperty
//        configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:2354/game");
//        configuration.setProperty("hibernate.connection.username", "postgres");
//        configuration.setProperty("hibernate.connection.password", "postgres");
//        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
//        configuration.setProperty("hibernate.show_sql", "true");
//        configuration.setProperty("hibernate.format_sql", "true");
//        configuration.setProperty("hibernate.hbm2ddl", "validate"); // по умолчанию = validate
//        configuration.addAnnotatedClass(User.class);    // класс, с которым будет работать гибер
        sessionFactory = configuration.buildSessionFactory();
    }

    // **************** через Java код + Properties = configuration.setProperties
//    Properties properties = new Properties();
//        properties.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
//        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
//        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/test");
//        properties.put(Environment.USER, "root");
//        properties.put(Environment.PASS, "root");
//
//    SessionFactory sessionFactory = new Configuration()
//            .setProperties(properties)
//            .addAnnotatedClass(Animal.class)
//            .buildSessionFactory();

    public Session getSession() {
        return sessionFactory.openSession();
    }


    @Override
    public void close() throws Exception {
        sessionFactory.close();
    }
}
