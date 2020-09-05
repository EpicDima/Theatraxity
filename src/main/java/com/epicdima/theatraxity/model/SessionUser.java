package com.epicdima.theatraxity.model;

import com.epicdima.theatraxity.domain.models.user.User;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author EpicDima
 */
public final class SessionUser implements HttpSessionBindingListener {

    private static final Map<SessionUser, HttpSession> USERS = new ConcurrentHashMap<>();

    public static void removeSession(int id) {
        for (Map.Entry<SessionUser, HttpSession> entry : USERS.entrySet()) {
            if (entry.getKey().id == id) {
                entry.getValue().invalidate();
                return;
            }
        }
    }

    private int id;
    private String email;
    private User.Role role;

    public SessionUser(int id, String email, User.Role role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public SessionUser(User user) {
        this(user.getId(), user.getEmail(), user.getRole());
    }

    public User toUser() {
        return User.builder().id(id).email(email).role(role).build();
    }

    public boolean equalToUser(User user) {
        if (user == null) {
            return false;
        }
        return user.getId() == id && user.getEmail().equals(email) && user.getRole().equals(role);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        HttpSession session = USERS.remove(this);
        if (session != null) {
            session.invalidate();
        }
        USERS.put(this, event.getSession());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        USERS.remove(this);
    }

    public void invalidateSession() {
        USERS.remove(this).invalidate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionUser that = (SessionUser) o;
        return id == that.id &&
                Objects.equals(email, that.email) &&
                role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
