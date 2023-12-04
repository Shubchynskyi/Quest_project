package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserService userService;
    private final ImageService imageService;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO registerNewUser(UserDTO userDTOFromModel,
                                   MultipartFile imageFile,
                                   String tempImageId,
                                   boolean imageIsValid,
                                   boolean isTempImagePresent)
    {
        User user = userMapper.userDTOToUser(userDTOFromModel);
        User createdUser = userService.create(user).orElseThrow();

        if (imageIsValid) {
            imageService.uploadFromMultipartFile(imageFile, createdUser.getImage(), false);
        } else if (isTempImagePresent) {
            imageService.uploadFromExistingFile(tempImageId, createdUser.getImage());
        }

        return userMapper.userToUserDTOWithoutPassword(createdUser);
    }

}
