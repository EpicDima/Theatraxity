package com.epicdima.theatraxity.dal.mysql.dao.user;

import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.models.user.UserData;
import com.epicdima.theatraxity.dal.mysql.dao.MySqlBaseDao;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.List;

/**
 * @author EpicDima
 */
@Singleton
public final class MySqlUserDao extends MySqlBaseDao<UserData, User> implements UserDao {

    @Inject
    public MySqlUserDao(ConnectionPool connectionPool,
                        @Named("dal_to_user") Mapper<UserData, User> to,
                        @Named("dal_from_user") Mapper<User, UserData> from) {
        super(connectionPool, to, from);
    }

    @Override
    public User selectByEmail(String email) {
        return selectSingleWhere("email", "deleted", email, false);
    }

    @Override
    public List<User> selectByRole(User.Role role) {
        return selectWhere("role", "deleted", role.name(), false);
    }
}
