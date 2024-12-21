package com.javarush.quest.shubchynskyi.config;

import com.javarush.quest.shubchynskyi.entity.Role;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class RoleConfig {

    public static final List<Role> ALLOWED_ROLES_FOR_QUEST_CREATE =
            List.of(Role.USER,
                    Role.MODERATOR,
                    Role.ADMIN
            );
    public static final List<Role> ALLOWED_ROLES_FOR_QUEST_DELETE =
            List.of(Role.MODERATOR,
                    Role.ADMIN
            );
    public static final List<Role> ALLOWED_ROLES_FOR_QUEST_EDIT =
            List.of(Role.MODERATOR,
                    Role.ADMIN
            );
    public static final List<Role> ALLOWED_ROLES_FOR_USER_EDIT =
            List.of(Role.USER,
                    Role.MODERATOR,
                    Role.ADMIN
            );
    public static final List<Role> ALLOWED_ROLES_FOR_USERS_LIST =
            List.of(Role.MODERATOR,
                    Role.ADMIN
            );

}