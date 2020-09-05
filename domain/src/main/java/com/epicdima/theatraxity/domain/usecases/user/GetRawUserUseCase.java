package com.epicdima.theatraxity.domain.usecases.user;

import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class GetRawUserUseCase {
    private final UserDao userDao;

    public GetRawUserUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public User execute(int userId) {
        User user = userDao.select(userId);
        if (user != null) {
            user = user.toBuilder().password(null).build();
        }
        return user;
    }
}
