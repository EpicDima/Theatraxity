package com.epicdima.theatraxity.domain.usecases.user;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.dto.UserDto;
import com.epicdima.theatraxity.domain.models.user.User;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.models.utils.SecurityUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author EpicDima
 */
public final class SignUpYourselfUseCase {
    private final UserDao userDao;

    public SignUpYourselfUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<UserDto> execute(UserDto user) {
        return execute(user, User.Role.CLIENT);
    }

    Result<UserDto> execute(UserDto user, User.Role role) {
        if (isNotValidEmail(user.email)) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_EMAIL);
        }
        if (isNotValidPassword(user.password)) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_PASSWORD);
        }
        User fromDao = userDao.selectByEmail(user.email);
        if (fromDao != null) {
            return Result.failure(HttpCodes.NOT_FOUND, Codes.NOT_UNIQUE_EMAIL);
        }
        try {
            User forDao = User.builder()
                    .email(user.email)
                    .password(SecurityUtils.generatePasswordHash(user.password))
                    .role(role)
                    .build();
            User inserted = userDao.insert(forDao);
            if (inserted != null) {
                return Result.success(UserDto.builder()
                        .id(inserted.getId())
                        .email(inserted.getEmail())
                        .role(inserted.getRole())
                        .build());
            }
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }

    static boolean isNotValidEmail(String email) {
        if (email != null) {
            email = email.trim();
            if (email.length() > 6 && email.length() < 64) {
                int dogIndex = email.indexOf("@");
                if (dogIndex != -1 && dogIndex != 0 && dogIndex == email.lastIndexOf("@")) {
                    int dotIndex = email.lastIndexOf(".");
                    return dotIndex == -1 || dotIndex <= dogIndex;
                }
            }
        }
        return true;
    }

    static boolean isNotValidPassword(String password) {
        if (password != null) {
            password = password.trim();
            return password.length() < 8 || password.length() >= 64;
        }
        return true;
    }
}
