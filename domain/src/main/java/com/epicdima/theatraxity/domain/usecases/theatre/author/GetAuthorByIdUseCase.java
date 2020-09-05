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
public final class GetAuthorByIdUseCase {
    private final AuthorDao authorDao;

    public GetAuthorByIdUseCase(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public Result<AuthorDto> execute(int authorId, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Author fromDao = authorDao.select(authorId);
        if (fromDao != null) {
            return Result.success(AuthorDto.from(fromDao));
        } else {
            return Result.failure(HttpCodes.NOT_FOUND, Codes.AUTHOR_NOT_FOUND);
        }
    }
}
