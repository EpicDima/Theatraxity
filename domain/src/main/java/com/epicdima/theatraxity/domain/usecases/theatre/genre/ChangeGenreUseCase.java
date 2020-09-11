package com.epicdima.theatraxity.domain.usecases.theatre.genre;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.domain.dto.AuthorDto;
import com.epicdima.theatraxity.domain.dto.GenreDto;
import com.epicdima.theatraxity.domain.models.theatre.Genre;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class ChangeGenreUseCase {
    private final GenreDao genreDao;

    public ChangeGenreUseCase(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Result<GenreDto> execute(GenreDto genre, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Genre fromDao = genreDao.select(genre.id);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.GENRE_NOT_FOUND);
        }
        if (fromDao.isDeleted() && currentUser.isManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Result<GenreDto> validateResult = CreateGenreUseCase.validate(genre);
        if (validateResult != null) {
            return validateResult;
        }
        Genre forDao = fromDao.toBuilder().name(genre.name).build();
        Boolean updated = genreDao.update(forDao);
        if (updated != null) {
            if (updated) {
                return Result.success(GenreDto.from(forDao));
            } else {
                return Result.failure(HttpCodes.OK, Codes.NO_GENRE_CHANGE);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
