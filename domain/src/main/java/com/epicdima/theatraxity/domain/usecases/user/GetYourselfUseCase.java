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
public final class GetYourselfUseCase {
    private final UserDao userDao;

    public GetYourselfUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<UserDto> execute(User currentUser) {
        User user = userDao.select(currentUser.getId());
        if (user != null) {
            return Result.success(UserDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build());
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
