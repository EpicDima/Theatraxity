package com.epicdima.theatraxity.domain.usecases.lifecycle;

import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.models.user.User;
import com.epicdima.theatraxity.domain.utils.SecurityUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author EpicDima
 */
public final class ConfigureDatabaseOnStartUseCase {
    private final UserDao userDao;

    public ConfigureDatabaseOnStartUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public void execute() {
        String adminEmail = "admin@admin.com";
        String courierEmail = "courier@courier.com";
        User admin = userDao.selectByEmail(adminEmail);
        User courier = userDao.selectByEmail(courierEmail);
        try {
            if (admin == null) {
                userDao.insert(User.builder()
                        .email(adminEmail)
                        .password(SecurityUtils.generatePasswordHash("adminadmin"))
                        .role(User.Role.ADMIN)
                        .build());
            }
           if (courier == null) {
               userDao.insert(User.builder()
                       .email(courierEmail)
                       .password(SecurityUtils.generatePasswordHash("couriercourier"))
                       .role(User.Role.COURIER)
                       .build());
           }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
