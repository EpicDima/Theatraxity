package com.epicdima.theatraxity.domain.usecases.user;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.models.user.User;
import com.epicdima.theatraxity.domain.common.Result;

/**
 * @author EpicDima
 */
public final class DeleteYourselfUseCase {
    private final UserDao userDao;

    public DeleteYourselfUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<Void> execute(User currentUser) {
        User fromDao = userDao.select(currentUser.getId());
        if (fromDao == null) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
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
