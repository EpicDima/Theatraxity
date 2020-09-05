package com.epicdima.theatraxity.services;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.AuthorDto;
import com.epicdima.theatraxity.domain.usecases.theatre.author.*;
import com.epicdima.theatraxity.model.SessionUser;

import java.util.List;

/**
 * @author EpicDima
 */
@Singleton
public final class AuthorService {
    private final ChangeAuthorUseCase changeAuthorUseCase;
    private final CreateAuthorUseCase createAuthorUseCase;
    private final DeleteAuthorUseCase deleteAuthorUseCase;
    private final GetAuthorByIdUseCase getAuthorByIdUseCase;
    private final GetAuthorsUseCase getAuthorsUseCase;
    private final RestoreAuthorUseCase restoreAuthorUseCase;

    @Inject
    public AuthorService(ChangeAuthorUseCase changeAuthorUseCase, CreateAuthorUseCase createAuthorUseCase,
                         DeleteAuthorUseCase deleteAuthorUseCase, GetAuthorByIdUseCase getAuthorByIdUseCase,
                         GetAuthorsUseCase getAuthorsUseCase, RestoreAuthorUseCase restoreAuthorUseCase) {
        this.changeAuthorUseCase = changeAuthorUseCase;
        this.createAuthorUseCase = createAuthorUseCase;
        this.deleteAuthorUseCase = deleteAuthorUseCase;
        this.getAuthorByIdUseCase = getAuthorByIdUseCase;
        this.getAuthorsUseCase = getAuthorsUseCase;
        this.restoreAuthorUseCase = restoreAuthorUseCase;
    }

    public Result<AuthorDto> changeAuthor(AuthorDto request, SessionUser currentUser) {
        return changeAuthorUseCase.execute(request, currentUser.toUser());
    }

    public Result<AuthorDto> createAuthor(AuthorDto request, SessionUser currentUser) {
        return createAuthorUseCase.execute(request, currentUser.toUser());
    }

    public Result<Void> deleteAuthor(AuthorDto request, SessionUser currentUser) {
        return deleteAuthorUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<AuthorDto> getAuthorById(int authorId, SessionUser currentUser) {
        return getAuthorByIdUseCase.execute(authorId, currentUser.toUser());
    }

    public Result<List<AuthorDto>> getAuthors(Boolean filterDeleted, String sortString,
                                              boolean desc, SessionUser currentUser) {
        return getAuthorsUseCase.execute(
                new GetAuthorsUseCase.Filter(filterDeleted),
                GetAuthorsUseCase.Sort.fromName(sortString),
                desc,
                currentUser != null ? currentUser.toUser() : null);
    }

    public Result<AuthorDto> restoreAuthor(AuthorDto request, SessionUser currentUser) {
        return restoreAuthorUseCase.execute(request.id, currentUser.toUser());
    }
}
