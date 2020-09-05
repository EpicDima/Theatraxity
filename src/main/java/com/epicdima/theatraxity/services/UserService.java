package com.epicdima.theatraxity.services;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.UserDto;
import com.epicdima.theatraxity.domain.models.user.User;
import com.epicdima.theatraxity.domain.usecases.user.*;
import com.epicdima.theatraxity.dto.ChangeYourselfDto;
import com.epicdima.theatraxity.model.SessionUser;

import java.util.List;

import static com.epicdima.theatraxity.helpers.Utils.valueOfEnum;

/**
 * @author EpicDima
 */
@Singleton
public final class UserService {
    private final ChangeUserUseCase changeUserUseCase;
    private final ChangeYourselfUseCase changeYourselfUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final DeleteYourselfUseCase deleteYourselfUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetUsersUseCase getUsersUseCase;
    private final GetYourselfUseCase getYourselfUseCase;
    private final RestoreUserUseCase restoreUserUseCase;
    private final SignInUseCase signInUseCase;
    private final SignUpUseCase signUpUseCase;
    private final SignUpYourselfUseCase signUpYourselfUseCase;

    @Inject
    public UserService(ChangeUserUseCase changeUserUseCase, ChangeYourselfUseCase changeYourselfUseCase,
                       DeleteUserUseCase deleteUserUseCase, DeleteYourselfUseCase deleteYourselfUseCase,
                       GetUserByIdUseCase getUserByIdUseCase, GetUsersUseCase getUsersUseCase,
                       GetYourselfUseCase getYourselfUseCase, RestoreUserUseCase restoreUserUseCase,
                       SignInUseCase signInUseCase, SignUpUseCase signUpUseCase,
                       SignUpYourselfUseCase signUpYourselfUseCase) {
        this.changeUserUseCase = changeUserUseCase;
        this.changeYourselfUseCase = changeYourselfUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.deleteYourselfUseCase = deleteYourselfUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.getUsersUseCase = getUsersUseCase;
        this.getYourselfUseCase = getYourselfUseCase;
        this.restoreUserUseCase = restoreUserUseCase;
        this.signInUseCase = signInUseCase;
        this.signUpUseCase = signUpUseCase;
        this.signUpYourselfUseCase = signUpYourselfUseCase;
    }

    public Result<UserDto> changeUser(UserDto request, SessionUser currentUser) {
        return changeUserUseCase.execute(request, currentUser.toUser());
    }

    public Result<UserDto> changeYourself(ChangeYourselfDto request, SessionUser currentUser ) {
        return changeYourselfUseCase.execute(new ChangeYourselfUseCase.ParametersForChange(
                request.email, request.password, request.newPassword), currentUser.toUser());
    }

    public Result<Void> deleteUser(UserDto request, SessionUser currentUser) {
        return deleteUserUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<Void> deleteYourself(SessionUser currentUser) {
        return deleteYourselfUseCase.execute(currentUser.toUser());
    }

    public Result<UserDto> getUserById(int userId, SessionUser currentUser) {
        return getUserByIdUseCase.execute(userId, currentUser.toUser());
    }

    public Result<List<UserDto>> getUsers(String filterRole, Boolean filterDeleted,
                                          String sortString, boolean desc,
                                          int page, SessionUser currentUser) {
        return getUsersUseCase.execute(
                new GetUsersUseCase.Filter(valueOfEnum(User.Role.class, filterRole), filterDeleted),
                GetUsersUseCase.Sort.fromName(sortString),
                desc,
                page,
                currentUser.toUser());
    }

    public Result<UserDto> getYourself(SessionUser currentUser) {
        return getYourselfUseCase.execute(currentUser.toUser());
    }

    public Result<UserDto> restoreUser(UserDto request, SessionUser currentUser) {
        return restoreUserUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<UserDto> signIn(UserDto request) {
        return signInUseCase.execute(request);
    }

    public Result<UserDto> signUp(UserDto request, SessionUser currentUser) {
        return signUpUseCase.execute(request, currentUser.toUser());
    }

    public Result<UserDto> signUpYourself(UserDto request) {
        return signUpYourselfUseCase.execute(request);
    }
}
