package com.epicdima.theatraxity.domain.dto;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
@Builder
public final class UserDto {
    public Integer id;
    public String email;
    public String password;
    public User.Role role;
    public Boolean deleted;

    public UserDto() {}

    public UserDto(Integer id, String email, String password, User.Role role, Boolean deleted) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.deleted = deleted;
    }

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getEmail(), null,
                user.getRole(), user.isDeleted());
    }
}
