package com.epicdima.theatraxity.di.usecase;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.Provides;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.usecases.user.*;

/**
 * @author EpicDima
 */
@Module
public final class UserUseCaseModule {
    @Provides
    public ChangeUserUseCase provideChangeUserUseCase(UserDao userDao) {
        return new ChangeUserUseCase(userDao);
    }

    @Provides
    public ChangeYourselfUseCase provideChangeYourselfUseCase(UserDao userDao) {
        return new ChangeYourselfUseCase(userDao);
    }

    @Provides
    public DeleteUserUseCase provideDeleteUserUseCase(UserDao userDao) {
        return new DeleteUserUseCase(userDao);
    }

    @Provides
    public DeleteYourselfUseCase provideDeleteYourselfUseCase(UserDao userDao) {
        return new DeleteYourselfUseCase(userDao);
    }

    @Provides
    public GetRawUserUseCase provideGetRawUserUseCase(UserDao userDao) {
        return new GetRawUserUseCase(userDao);
    }

    @Provides
    public GetUserByIdUseCase provideGetUserByIdUseCase(UserDao userDao) {
        return new GetUserByIdUseCase(userDao);
    }

    @Provides
    public GetUsersUseCase provideGetUsersUseCase(UserDao userDao) {
        return new GetUsersUseCase(userDao);
    }

    @Provides
    public GetYourselfUseCase provideGetYourselfUseCase(UserDao userDao) {
        return new GetYourselfUseCase(userDao);
    }

    @Provides
    public RestoreUserUseCase provideRestoreUserUseCase(UserDao userDao) {
        return new RestoreUserUseCase(userDao);
    }

    @Provides
    public SignInUseCase provideSignInUseCase(UserDao userDao) {
        return new SignInUseCase(userDao);
    }

    @Provides
    public SignUpUseCase provideSignUpUseCase(SignUpYourselfUseCase signUpYourselfUseCase) {
        return new SignUpUseCase(signUpYourselfUseCase);
    }

    @Provides
    public SignUpYourselfUseCase provideSignUpYourselfUseCase(UserDao userDao) {
        return new SignUpYourselfUseCase(userDao);
    }
}
