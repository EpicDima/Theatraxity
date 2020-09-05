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
public final class CreateAuthorUseCase {
    private final AuthorDao authorDao;

    public CreateAuthorUseCase(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public Result<AuthorDto> execute(AuthorDto author, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Author fromDao = authorDao.insert(AuthorDto.to(author));
        if (fromDao != null) {
            return Result.success(author);
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
