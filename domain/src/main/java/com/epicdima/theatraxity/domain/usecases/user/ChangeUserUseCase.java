package com.epicdima.theatraxity.domain.usecases.user;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.dto.UserDto;
import com.epicdima.theatraxity.domain.models.user.User;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.utils.SecurityUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author EpicDima
 */
public final class ChangeUserUseCase {
    private final UserDao userDao;

    public ChangeUserUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<UserDto> execute(UserDto forChange, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        if (forChange.id == currentUser.getId()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        User fromDao = userDao.select(forChange.id);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.USER_NOT_FOUND);
        }
        if (fromDao.isDeleted() && currentUser.isManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        if (fromDao.isAdmin() && currentUser.isManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
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
        if (forChange.password != null) {
            if (SignUpYourselfUseCase.isNotValidPassword(forChange.password)) {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_PASSWORD);
            }
            try {
                builder.password(SecurityUtils.generatePasswordHash(forChange.password));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
            }
        }
        User forUpdate = builder.build();
        Boolean updated = userDao.update(forUpdate);
        if (updated != null) {
            if (updated) {
                return Result.success(UserDto.from(forUpdate));
            } else {
                return Result.failure(HttpCodes.OK, Codes.NO_USER_CHANGE);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
