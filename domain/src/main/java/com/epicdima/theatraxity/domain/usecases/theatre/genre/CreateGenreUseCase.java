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
public final class CreateGenreUseCase {
    private final GenreDao genreDao;

    public CreateGenreUseCase(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Result<GenreDto> execute(GenreDto genre, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Result<GenreDto> validateResult = validate(genre);
        if (validateResult != null) {
            return validateResult;
        }
        Genre fromDao = genreDao.insert(GenreDto.to(genre));
        if (fromDao != null) {
            return Result.success(GenreDto.from(fromDao));
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }

    static Result<GenreDto> validate(GenreDto genre) {
        if (genre.name == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_GENRE_NAME);
        }
        genre.name = genre.name.trim();
        if (genre.name.length() < 3 || genre.name.length() > 100) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_GENRE_NAME);
        }
        return null;
    }
}
