package com.epicdima.theatraxity.domain.usecases.theatre.genre;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.domain.models.theatre.Genre;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class DeleteGenreUseCase {
    private final GenreDao genreDao;

    public DeleteGenreUseCase(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Result<Void> execute(int genreId, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Genre fromDao = genreDao.select(genreId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.GENRE_NOT_FOUND);
        }
        if (fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.GENRE_IS_DELETED);
        }
        Boolean updated = genreDao.update(fromDao.toBuilder().deleted(true).build());
        if (updated != null) {
            if (updated) {
                return Result.empty();
            } else {
                return Result.failure(HttpCodes.OK, Codes.GENRE_NOT_DELETED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
