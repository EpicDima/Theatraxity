package com.epicdima.theatraxity.di.usecase;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.Provides;
import com.epicdima.theatraxity.domain.dao.AuthorDao;
import com.epicdima.theatraxity.domain.usecases.theatre.author.*;

/**
 * @author EpicDima
 */
@Module
public final class AuthorUseCaseModule {
    @Provides
    public ChangeAuthorUseCase provideChangeAuthorUseCase(AuthorDao authorDao) {
        return new ChangeAuthorUseCase(authorDao);
    }

    @Provides
    public CreateAuthorUseCase provideCreateAuthorUseCase(AuthorDao authorDao) {
        return new CreateAuthorUseCase(authorDao);
    }

    @Provides
    public DeleteAuthorUseCase provideDeleteAuthorUseCase(AuthorDao authorDao) {
        return new DeleteAuthorUseCase(authorDao);
    }

    @Provides
    public GetAuthorByIdUseCase provideGetAuthorByIdUseCase(AuthorDao authorDao) {
        return new GetAuthorByIdUseCase(authorDao);
    }

    @Provides
    public GetAuthorsUseCase provideGetAuthorsUseCase(AuthorDao authorDao) {
        return new GetAuthorsUseCase(authorDao);
    }

    @Provides
    public RestoreAuthorUseCase provideRestoreAuthorUseCase(AuthorDao authorDao) {
        return new RestoreAuthorUseCase(authorDao);
    }
}
