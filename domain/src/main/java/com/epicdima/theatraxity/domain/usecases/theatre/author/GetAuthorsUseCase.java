package com.epicdima.theatraxity.domain.usecases.theatre.author;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.Common;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.AuthorDao;
import com.epicdima.theatraxity.domain.dto.AuthorDto;
import com.epicdima.theatraxity.domain.models.theatre.Author;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author EpicDima
 */
public final class GetAuthorsUseCase {
    private final AuthorDao authorDao;

    public GetAuthorsUseCase(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public Result<List<AuthorDto>> execute(Filter filter, Sort sort, boolean desc, User currentUser) {
        if (currentUser == null || currentUser.isNotAdminOrManager()) {
            filter = new Filter(false);
        }
        List<Author> authors = authorDao.select();
        if (authors == null) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
        Stream<AuthorDto> stream = reverse(sort(filter(authors.stream(), filter), sort), desc)
                .map(AuthorDto::from);
        if (currentUser == null || currentUser.isNotAdminOrManager()) {
            stream = stream.peek(authorDto -> {
                authorDto.deleted = null;
            });
        }
        return Result.success(stream.collect(Collectors.toList()));
    }

    private Stream<Author> filter(Stream<Author> stream, Filter filter) {
        return stream.filter(author -> {
            boolean marker;
            if (filter.deleted != null) {
                marker = author.isDeleted() == filter.deleted;
            } else {
                marker = !author.isDeleted();
            }
            return marker;
        });
    }

    private Stream<Author> sort(Stream<Author> stream, Sort sort) {
        if (sort == null) {
            return stream;
        }
        switch (sort) {
            case ID:
                return stream.sorted(Comparator.comparingInt(Author::getId));
            case NAME:
                return stream.sorted(Comparator.comparing(Author::getName));
        }
        return stream;
    }

    private Stream<Author> reverse(Stream<Author> stream, boolean desc) {
        if (desc) {
            return Common.reverse(stream);
        }
        return stream;
    }


    public static final class Filter {
        public final Boolean deleted;

        public Filter(Boolean deleted) {
            this.deleted = deleted;
        }
    }


    public enum Sort {
        ID("id"),
        NAME("name");

        public final String name;

        Sort(String name) {
            this.name = name;
        }

        public static Sort fromName(String name) {
            if (name == null) {
                return null;
            }
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
