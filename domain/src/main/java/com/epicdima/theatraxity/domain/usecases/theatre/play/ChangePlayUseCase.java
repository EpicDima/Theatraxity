package com.epicdima.theatraxity.domain.usecases.theatre.play;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.AuthorDao;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.domain.dto.PlayDto;
import com.epicdima.theatraxity.domain.models.theatre.Author;
import com.epicdima.theatraxity.domain.models.theatre.Genre;
import com.epicdima.theatraxity.domain.models.theatre.Play;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class ChangePlayUseCase {
    private final PlayDao playDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public ChangePlayUseCase(PlayDao playDao, AuthorDao authorDao, GenreDao genreDao) {
        this.playDao = playDao;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    public Result<PlayDto> execute(PlayDto play, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Play fromDao = playDao.select(play.id);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PLAY_NOT_FOUND);
        }
        if (fromDao.isDeleted() && currentUser.isManager()) {
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
        Result<PlayDto> validateResult = CreatePlayUseCase.validate(play);
        if (validateResult != null) {
            return validateResult;
        }
        Play forDao = fromDao.toBuilder()
                .name(play.name)
                .description(play.description)
                .author(fromDao.getAuthor().toBuilder().id(play.author.id).build())
                .genre(fromDao.getGenre().toBuilder().id(play.genre.id).build())
                .build();
        Boolean updated = playDao.update(forDao);
        if (updated != null) {
            if (updated) {
                return Result.success(PlayDto.from(forDao));
            } else {
                return Result.failure(HttpCodes.OK, Codes.NO_PLAY_CHANGE);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
