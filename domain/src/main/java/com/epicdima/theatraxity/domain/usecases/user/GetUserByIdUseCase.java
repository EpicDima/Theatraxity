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
public final class GetUserByIdUseCase {
    private final UserDao userDao;

    public GetUserByIdUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<UserDto> execute(int userId, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        User user = userDao.select(userId);
        if (user != null) {
            return Result.success(UserDto.from(user));
        } else {
            return Result.failure(HttpCodes.NOT_FOUND, Codes.USER_NOT_FOUND);
        }
    }
}
