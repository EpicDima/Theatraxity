package com.epicdima.theatraxity.dto;

/**
 * @author EpicDima
 */
public final class ChangeYourselfDto {
    public String email;
    public String password;
    public String newPassword;

    public ChangeYourselfDto() {}

    public ChangeYourselfDto(String email, String password, String newPassword) {
        this.email = email;
        this.password = password;
        this.newPassword = newPassword;
    }
}
