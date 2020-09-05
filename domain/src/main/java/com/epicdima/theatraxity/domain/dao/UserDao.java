package com.epicdima.theatraxity.domain.dao;

import com.epicdima.theatraxity.domain.models.user.User;

import java.util.List;

/**
 * @author EpicDima
 */
public interface UserDao extends BaseDao<User> {
    User selectByEmail(String email);
    List<User> selectByRole(User.Role role);
}
