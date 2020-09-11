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
        Result<AuthorDto> validateResult = validate(author);
        if (validateResult != null) {
            return validateResult;
        }
        Author fromDao = authorDao.insert(AuthorDto.to(author));
        if (fromDao != null) {
            return Result.success(AuthorDto.from(fromDao));
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }

    static Result<AuthorDto> validate(AuthorDto author) {
        if (author.name == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_AUTHOR_NAME);
        }
        author.name = author.name.trim();
        if (author.name.length() < 3 || author.name.length() > 100) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.NOT_VALID_AUTHOR_NAME);
        }
        return null;
    }
}
