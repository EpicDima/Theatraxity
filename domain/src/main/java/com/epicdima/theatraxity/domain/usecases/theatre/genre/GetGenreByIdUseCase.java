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
public final class GetGenreByIdUseCase {
    private final GenreDao genreDao;

    public GetGenreByIdUseCase(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Result<GenreDto> execute(int genreId, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Genre fromDao = genreDao.select(genreId);
        if (fromDao != null) {
            return Result.success(GenreDto.from(fromDao));
        } else {
            return Result.failure(HttpCodes.NOT_FOUND, Codes.GENRE_NOT_FOUND);
        }
    }
}
