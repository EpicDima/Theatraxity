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
public final class SignInUseCase {
    private final UserDao userDao;

    public SignInUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<UserDto> execute(UserDto user) {
        if (SignUpYourselfUseCase.isNotValidEmail(user.email)) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.INCORRECT_EMAIL_OR_PASSWORD);
        }
        if (SignUpYourselfUseCase.isNotValidPassword(user.password)) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.INCORRECT_EMAIL_OR_PASSWORD);
        }
        User fromDao = userDao.selectByEmail(user.email);
        if (fromDao == null || fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.INCORRECT_EMAIL_OR_PASSWORD);
        }
        try {
            if (SecurityUtils.checkPassword(user.password, fromDao.getPassword())) {
                return Result.success(UserDto.builder()
                        .id(fromDao.getId())
                        .email(fromDao.getEmail())
                        .role(fromDao.getRole())
                        .build());
            }
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.INCORRECT_EMAIL_OR_PASSWORD);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
