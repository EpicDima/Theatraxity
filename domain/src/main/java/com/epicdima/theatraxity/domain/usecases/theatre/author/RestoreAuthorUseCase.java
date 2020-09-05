package com.epicdima.theatraxity.domain.usecases.theatre.author;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.AuthorDao;
import com.epicdima.theatraxity.domain.dto.AuthorDto;
import com.epicdima.theatraxity.domain.models.theatre.Author;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class RestoreAuthorUseCase {
    private final AuthorDao authorDao;

    public RestoreAuthorUseCase(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public Result<AuthorDto> execute(int authorId, User currentUser) {
        if (currentUser.isNotAdmin()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Author fromDao = authorDao.select(authorId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.AUTHOR_NOT_FOUND);
        }
        if (!fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.AUTHOR_IS_NOT_DELETED);
        }
        Author forDao = fromDao.toBuilder().deleted(false).build();
        Boolean updated = authorDao.update(forDao);
        if (updated != null) {
            if (updated) {
                return Result.success(AuthorDto.from(forDao));
            } else {
                return Result.failure(HttpCodes.OK, Codes.AUTHOR_NOT_RESTORED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.NOT_ALLOWED);
        }
    }
}
