package com.epicdima.theatraxity.domain.usecases.theatre.play;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.AuthorDao;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.domain.dto.GenreDto;
import com.epicdima.theatraxity.domain.dto.PlayDto;
import com.epicdima.theatraxity.domain.models.theatre.Author;
import com.epicdima.theatraxity.domain.models.theatre.Genre;
import com.epicdima.theatraxity.domain.models.theatre.Play;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class CreatePlayUseCase {
    private final PlayDao playDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public CreatePlayUseCase(PlayDao playDao, AuthorDao authorDao, GenreDao genreDao) {
        this.playDao = playDao;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    public Result<PlayDto> execute(PlayDto play, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Author author = authorDao.select(play.author.id);
        if (author == null || author.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.AUTHOR_NOT_FOUND);
        }
        Genre genre = genreDao.select(play.genre.id);
        if (genre == null || genre.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.GENRE_NOT_FOUND);
        }
        Result<PlayDto> validateResult = validate(play);
        if (validateResult != null) {
            return validateResult;
        }
        Play fromDao = playDao.insert(PlayDto.to(play));
        if (fromDao != null) {
            return Result.success(PlayDto.from(fromDao));
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }

    static Result<PlayDto> validate(PlayDto play) {
        if (play.name == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_PLAY_NAME);
        }
        if (play.description == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_PLAY_DESCRIPTION);
        }
        play.name = play.name.trim();
        play.description = play.description.trim();
        if (play.name.length() < 3 || play.name.length() > 100) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_PLAY_NAME);
        }
        if (play.description.length() < 5 || play.description.length() > 1024) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_PLAY_DESCRIPTION);
        }
        return null;
    }
}
