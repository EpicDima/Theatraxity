package com.epicdima.theatraxity.domain.usecases.user;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class DeleteUserUseCase {
    private final UserDao userDao;

    public DeleteUserUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<Void> execute(int userId, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        if (userId == currentUser.getId()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        User fromDao = userDao.select(userId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.USER_NOT_FOUND);
        }
        if ((fromDao.isAdmin() || fromDao.isManager()) && currentUser.isManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        if (fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.USER_IS_DELETED);
        }
        Boolean updated = userDao.update(fromDao.toBuilder().deleted(true).build());
        if (updated != null) {
            if (updated) {
                return Result.empty();
            } else {
                return Result.failure(HttpCodes.OK, Codes.USER_NOT_DELETED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
