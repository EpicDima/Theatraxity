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
public final class ChangeAuthorUseCase {
    private final AuthorDao authorDao;

    public ChangeAuthorUseCase(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public Result<AuthorDto> execute(AuthorDto author, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Author fromDao = authorDao.select(author.id);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.AUTHOR_NOT_FOUND);
        }
        if (fromDao.isDeleted() && currentUser.isManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Result<AuthorDto> validateResult = CreateAuthorUseCase.validate(author);
        if (validateResult != null) {
            return validateResult;
        }
        Author forDao = fromDao.toBuilder().name(author.name).build();
        Boolean updated = authorDao.update(forDao);
        if (updated != null) {
            if (updated) {
                return Result.success(AuthorDto.from(forDao));
            } else {
                return Result.failure(HttpCodes.OK, Codes.NO_AUTHOR_CHANGE);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
