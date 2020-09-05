package com.epicdima.theatraxity.domain.usecases.user;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.UserDto;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class SignUpUseCase {
    private final SignUpYourselfUseCase signUpYourselfUseCase;

    public SignUpUseCase(SignUpYourselfUseCase signUpYourselfUseCase) {
        this.signUpYourselfUseCase = signUpYourselfUseCase;
    }

    public Result<UserDto> execute(UserDto user, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        if (currentUser.getId() == user.id) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        if (user.role == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.USER_ROLE_NOT_SPECIFIED);
        }
        if (User.Role.ADMIN.equals(user.role) && currentUser.isManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Result<UserDto> result = signUpYourselfUseCase.execute(user, user.role);
        if (result.isSuccess()) {
            result.getValue().deleted = false;
        }
        return result;
    }
}
