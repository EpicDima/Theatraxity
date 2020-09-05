package com.epicdima.theatraxity.domain.usecases.theatre.author;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.AuthorDao;
import com.epicdima.theatraxity.domain.models.theatre.Author;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class DeleteAuthorUseCase {
    private final AuthorDao authorDao;

    public DeleteAuthorUseCase(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public Result<Void> execute(int authorId, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Author fromDao = authorDao.select(authorId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.AUTHOR_NOT_FOUND);
        }
        if (fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.AUTHOR_IS_DELETED);
        }
        Boolean updated = authorDao.update(fromDao.toBuilder().deleted(true).build());
        if (updated != null) {
            if (updated) {
                return Result.empty();
            } else {
                return Result.failure(HttpCodes.OK, Codes.AUTHOR_NOT_DELETED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.NOT_ALLOWED);
        }
    }
}
