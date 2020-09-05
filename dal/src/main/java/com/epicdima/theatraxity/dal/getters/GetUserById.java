package com.epicdima.theatraxity.dal.getters;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
@Named("user")
@Singleton
public final class GetUserById implements GetById<User> {

    private final UserDao userDao;

    @Inject
    public GetUserById(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public synchronized User getById(int id) {
        return userDao.select(id);
    }
}
