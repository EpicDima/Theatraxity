package com.epicdima.theatraxity.services;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.GenreDto;
import com.epicdima.theatraxity.domain.usecases.theatre.genre.*;
import com.epicdima.theatraxity.model.SessionUser;

import java.util.List;

/**
 * @author EpicDima
 */
@Singleton
public final class GenreService {
    private final ChangeGenreUseCase changeGenreUseCase;
    private final CreateGenreUseCase createGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final GetGenresUseCase getGenresUseCase;
    private final RestoreGenreUseCase restoreGenreUseCase;

    @Inject
    public GenreService(ChangeGenreUseCase changeGenreUseCase, CreateGenreUseCase createGenreUseCase,
                        DeleteGenreUseCase deleteGenreUseCase, GetGenreByIdUseCase getGenreByIdUseCase,
                        GetGenresUseCase getGenresUseCase, RestoreGenreUseCase restoreGenreUseCase) {
        this.changeGenreUseCase = changeGenreUseCase;
        this.createGenreUseCase = createGenreUseCase;
        this.deleteGenreUseCase = deleteGenreUseCase;
        this.getGenreByIdUseCase = getGenreByIdUseCase;
        this.getGenresUseCase = getGenresUseCase;
        this.restoreGenreUseCase = restoreGenreUseCase;
    }
    
    public Result<GenreDto> changeGenre(GenreDto request, SessionUser currentUser) {
        return changeGenreUseCase.execute(request, currentUser.toUser());
    }

    public Result<GenreDto> createGenre(GenreDto request, SessionUser currentUser) {
        return createGenreUseCase.execute(request, currentUser.toUser());
    }

    public Result<Void> deleteGenre(GenreDto request, SessionUser currentUser) {
        return deleteGenreUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<GenreDto> getGenreById(int genreId, SessionUser currentUser) {
        return getGenreByIdUseCase.execute(genreId, currentUser.toUser());
    }

    public Result<List<GenreDto>> getGenres(Boolean filterDeleted, String sortString,
                                            boolean desc, SessionUser currentUser) {
        return getGenresUseCase.execute(
                new GetGenresUseCase.Filter(filterDeleted),
                GetGenresUseCase.Sort.fromName(sortString),
                desc,
                currentUser != null ? currentUser.toUser() : null);
    }

    public Result<GenreDto> restoreGenre(GenreDto request, SessionUser currentUser) {
        return restoreGenreUseCase.execute(request.id, currentUser.toUser());
    }
}
