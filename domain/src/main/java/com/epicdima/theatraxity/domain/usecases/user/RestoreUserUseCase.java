package com.epicdima.theatraxity.domain.usecases.user;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.dto.UserDto;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class RestoreUserUseCase {
    private final UserDao userDao;

    public RestoreUserUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<UserDto> execute(int userId, User currentUser) {
        if (currentUser.isNotAdmin()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        User fromDao = userDao.select(userId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.USER_NOT_FOUND);
        }
        if (!fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.USER_IS_NOT_DELETED);
        }
        User forDao = fromDao.toBuilder().deleted(false).build();
        Boolean updated = userDao.update(forDao);
        if (updated != null) {
            if (updated) {
                return Result.success(UserDto.from(forDao));
            } else {
                return Result.failure(HttpCodes.OK, Codes.USER_NOT_RESTORED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
