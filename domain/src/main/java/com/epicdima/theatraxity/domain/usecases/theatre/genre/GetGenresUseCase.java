package com.epicdima.theatraxity.domain.usecases.theatre.genre;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.Common;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.domain.dto.GenreDto;
import com.epicdima.theatraxity.domain.models.theatre.Author;
import com.epicdima.theatraxity.domain.models.theatre.Genre;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author EpicDima
 */
public final class GetGenresUseCase {
    private final GenreDao genreDao;

    public GetGenresUseCase(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Result<List<GenreDto>> execute(Filter filter, Sort sort, boolean desc, User currentUser) {
        if (currentUser == null || currentUser.isNotAdminOrManager()) {
            filter = new Filter(false);
        }
        List<Genre> genres = genreDao.select();
        if (genres == null) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
        Stream<GenreDto> stream = reverse(sort(filter(genres.stream(), filter), sort), desc)
                .map(GenreDto::from);
        if (currentUser == null || currentUser.isNotAdminOrManager()) {
            stream = stream.peek(genreDto -> genreDto.deleted = null);
        }
        return Result.success(stream.collect(Collectors.toList()));
    }

    private Stream<Genre> filter(Stream<Genre> stream, Filter filter) {
        return stream.filter(genre -> {
            boolean marker;
            if (filter.deleted != null) {
                marker = genre.isDeleted() == filter.deleted;
            } else {
                marker = !genre.isDeleted();
            }
            return marker;
        });
    }

    private Stream<Genre> sort(Stream<Genre> stream, Sort sort) {
        if (sort == null) {
            return stream;
        }
        switch (sort) {
            case ID:
                return stream.sorted(Comparator.comparingInt(Genre::getId));
            case NAME:
                return stream.sorted(Comparator.comparing(Genre::getName));
        }
        return stream;
    }

    private Stream<Genre> reverse(Stream<Genre> stream, boolean desc) {
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
