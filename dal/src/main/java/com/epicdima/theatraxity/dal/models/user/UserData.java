package com.epicdima.theatraxity.dal.models.user;

import com.epicdima.lib.dal.annotations.Column;
import com.epicdima.lib.dal.annotations.PrimaryKey;
import com.epicdima.lib.dal.annotations.Table;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
@Table(name = "users")
public final class UserData {

    @PrimaryKey
    @Column
    public int id;

    @Column(length = 64)
    public String email;

    @Column
    public String password;

    @Column
    public User.Role role;

    @Column
    public boolean deleted;

    public UserData() {}

    public UserData(int id, String email, String password, User.Role role, boolean deleted) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.deleted = deleted;
    }

    @Named("dal_to_user")
    @Singleton
    public static final class ToUser implements Mapper<UserData, User> {
        @Override
        public User map(UserData item) {
            return new User(item.id, item.email, item.password, item.role, item.deleted);
        }
    }

    @Named("dal_from_user")
    @Singleton
    public static final class FromUser implements Mapper<User, UserData> {
        @Override
        public UserData map(User item) {
            return new UserData(item.getId(), item.getEmail(), item.getPassword(), item.getRole(), item.isDeleted());
        }
    }
}
