package com.epicdima.theatraxity.domain.models.user;

import com.epicdima.lib.builder.annotation.Builder;

import java.util.Objects;

/**
 * @author EpicDima
 */
@Builder
public final class User {
    public enum Role {
        CLIENT, COURIER, MANAGER, ADMIN
    }

    private final int id;
    private final String email;
    private final String password;
    private final Role role;
    private final boolean deleted;

    public User(int id, String email, String password, Role role, boolean deleted) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(role);
    }

    public boolean isNotAdmin() {
        return !isAdmin();
    }

    public boolean isManager() {
        return Role.MANAGER.equals(role);
    }

    public boolean isNotManager() {
        return !isManager();
    }

    public boolean isAdminOrManager() {
        return isAdmin() || isManager();
    }

    public boolean isNotAdminOrManager() {
        return !isAdminOrManager();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                deleted == user.deleted &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, role, deleted);
    }
}
