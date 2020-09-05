package com.epicdima.theatraxity.domain.usecases.user;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.dto.UserDto;
import com.epicdima.theatraxity.domain.models.user.User;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.models.utils.SecurityUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author EpicDima
 */
public final class ChangeYourselfUseCase {
    private final UserDao userDao;

    public ChangeYourselfUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<UserDto> execute(ParametersForChange forChange, User currentUser) {
        if (forChange.password == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NEED_PASSWORD);
        }
        User fromDao = userDao.select(currentUser.getId());
        if (fromDao == null) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
        try {
            if (!SecurityUtils.checkPassword(forChange.password, fromDao.getPassword())) {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.INCORRECT_PASSWORD);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }

        User.Builder builder = fromDao.toBuilder();
        if (forChange.email != null && forChange.email.equals(fromDao.getEmail())) {
            if (SignUpYourselfUseCase.isNotValidEmail(forChange.email)) {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_EMAIL);
            }
            if (userDao.selectByEmail(forChange.email) == null) {
                builder.email(forChange.email);
            } else {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_UNIQUE_EMAIL);
            }
        }
        if (forChange.newPassword != null) {
            if (SignUpYourselfUseCase.isNotValidPassword(forChange.newPassword)) {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_PASSWORD);
            }
            try {
                builder.password(SecurityUtils.generatePasswordHash(forChange.newPassword));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
            }
        }
        User forUpdate = builder.build();
        Boolean updated = userDao.update(forUpdate);
        if (updated != null) {
            if (updated) {
                return Result.success(UserDto.builder()
                        .id(forUpdate.getId())
                        .email(forUpdate.getEmail())
                        .role(forUpdate.getRole())
                        .build());
            } else {
                return Result.failure(HttpCodes.OK, Codes.NO_USER_CHANGE);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }


    public static final class ParametersForChange {
        public final String email;
        public final String password;
        public final String newPassword;

        public ParametersForChange(String email, String password, String newPassword) {
            this.email = email;
            this.password = password;
            this.newPassword = newPassword;
        }
    }
}
