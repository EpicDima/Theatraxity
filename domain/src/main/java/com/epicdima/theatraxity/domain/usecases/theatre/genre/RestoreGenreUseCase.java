package com.epicdima.theatraxity.domain.usecases.theatre.genre;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.domain.dto.GenreDto;
import com.epicdima.theatraxity.domain.models.theatre.Genre;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class RestoreGenreUseCase {
    private final GenreDao genreDao;

    public RestoreGenreUseCase(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Result<GenreDto> execute(int genreId, User currentUser) {
        if (currentUser.isNotAdmin()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Genre fromDao = genreDao.select(genreId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.GENRE_NOT_FOUND);
        }
        if (!fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.GENRE_IS_NOT_DELETED);
        }
        Genre forDao = fromDao.toBuilder().deleted(false).build();
        Boolean updated = genreDao.update(forDao);
        if (updated != null) {
            if (updated) {
                return Result.success(GenreDto.from(forDao));
            } else {
                return Result.failure(HttpCodes.OK, Codes.GENRE_NOT_RESTORED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
