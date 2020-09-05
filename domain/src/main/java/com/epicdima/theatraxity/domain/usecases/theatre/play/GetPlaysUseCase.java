package com.epicdima.theatraxity.domain.usecases.theatre.play;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.Common;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.domain.dto.PlayDto;
import com.epicdima.theatraxity.domain.models.theatre.Genre;
import com.epicdima.theatraxity.domain.models.theatre.Play;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author EpicDima
 */
public final class GetPlaysUseCase {
    public PlayDao playDao;

    public GetPlaysUseCase(PlayDao playDao) {
        this.playDao = playDao;
    }

    public Result<List<PlayDto>> execute(Filter filter, Sort sort, boolean desc, User currentUser) {
        if (currentUser == null || currentUser.isNotAdminOrManager()) {
            filter = new Filter(null, null, false);
        }
        List<Play> plays = playDao.select();
        if (plays == null) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
        Stream<PlayDto> stream = reverse(sort(filter(plays.stream(), filter), sort), desc)
                .map(PlayDto::from);
        if (currentUser == null || currentUser.isNotAdminOrManager()) {
            stream = stream.peek(playDto -> {
                playDto.deleted = null;
                playDto.genre = null;
                playDto.author = null;
                playDto.description = null;
            });
        }
        return Result.success(stream.collect(Collectors.toList()));
    }

    private Stream<Play> filter(Stream<Play> stream, Filter filter) {
        return stream.filter(play -> {
            boolean marker;
            if (filter.deleted != null) {
                marker = play.isDeleted() == filter.deleted;
            } else {
                marker = !play.isDeleted();
            }
            if (filter.authorId != null) {
                marker = marker && (play.getAuthor().getId() == filter.authorId);
            }
            if (filter.genreId != null) {
                marker = marker && (play.getGenre().getId() == filter.genreId);
            }
            return marker;
        });
    }

    private Stream<Play> sort(Stream<Play> stream, Sort sort) {
        if (sort == null) {
            return stream;
        }
        switch (sort) {
            case ID:
                return stream.sorted(Comparator.comparingInt(Play::getId));
            case NAME:
                return stream.sorted(Comparator.comparing(Play::getName));
            case AUTHOR:
                return stream.sorted(Comparator.comparing(play -> play.getAuthor().getName()));
            case GENRE:
                return stream.sorted(Comparator.comparing(play -> play.getGenre().getName()));
        }
        return stream;
    }

    private Stream<Play> reverse(Stream<Play> stream, boolean desc) {
        if (desc) {
            return Common.reverse(stream);
        }
        return stream;
    }


    @Builder
    public static final class Filter {
        public final Integer authorId;
        public final Integer genreId;
        public final Boolean deleted;

        public Filter(Integer authorId, Integer genreId, Boolean deleted) {
            this.authorId = authorId;
            this.genreId = genreId;
            this.deleted = deleted;
        }
    }


    public enum Sort {
        ID("id"),
        NAME("name"),
        AUTHOR("author"),
        GENRE("genre");

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
