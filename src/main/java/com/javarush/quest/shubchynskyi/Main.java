package com.javarush.quest.shubchynskyi;

import com.javarush.quest.shubchynskyi.config.JavaApplicationConfig;
import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.UserService;

public class Main {
    public static void main(String[] args) {
        UserService bean = JavaApplicationConfig.getBean(UserService.class);
        User build = User.builder().login("testMain").password("testPass").role(Role.ADMIN).build();
        bean.create(build);
    }
}
