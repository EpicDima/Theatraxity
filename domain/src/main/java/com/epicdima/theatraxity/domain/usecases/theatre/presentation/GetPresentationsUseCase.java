package com.epicdima.theatraxity.domain.usecases.theatre.presentation;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.Common;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.domain.dto.PresentationDto;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author EpicDima
 */
public final class GetPresentationsUseCase {
    private final PresentationDao presentationDao;

    public GetPresentationsUseCase(PresentationDao presentationDao) {
        this.presentationDao = presentationDao;
    }

    public Result<List<PresentationDto>> execute(Filter filter, Sort sort, boolean desc,
                                                 int page, User currentUser) {
        if (currentUser == null || currentUser.isNotAdminOrManager()) {
            filter = changeFilter(filter);
        }
        List<Presentation> presentations = presentationDao.select();
        if (presentations == null) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
        Stream<PresentationDto> stream = reverse(sort(filter(presentations.stream(), filter), sort), desc)
                .map(PresentationDto::from);
        if (currentUser == null || currentUser.isNotAdminOrManager()) {
            stream = stream.peek(presentationDto -> {
                presentationDto.deleted = null;
                presentationDto.play.deleted = null;
                presentationDto.play.author.deleted = null;
                presentationDto.play.genre.deleted = null;
            });
        }
        return Result.map(Result.success(stream.collect(Collectors.toList())),
                (list) -> Common.getPage(list, page));
    }

    static Date toMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    static Date getCurrentDay() {
        return toMidnight(new Date());
    }

    private Filter changeFilter(Filter filter) {
        Filter.Builder builder = filter.toBuilder().deleted(false);
        Date date = getCurrentDay();
        if (filter.begin == null || filter.begin.before(date)) {
            builder = builder.begin(date);
        }
        return builder.build();
    }

    private Stream<Presentation> filter(Stream<Presentation> stream, Filter filter) {
        return stream.filter(presentation -> {
            boolean marker;
            if (filter.deleted != null) {
                marker = presentation.isDeleted() == filter.deleted;
            } else {
                marker = !presentation.isDeleted();
            }
            if (filter.playId != null) {
                marker = marker && (presentation.getPlay().getId() == filter.playId);
            } else {
                if (filter.authorId != null) {
                    marker = marker && (presentation.getPlay().getAuthor().getId() == filter.authorId);
                }
                if (filter.genreId != null) {
                    marker = marker && (presentation.getPlay().getGenre().getId() == filter.genreId);
                }
            }
            if (filter.begin != null) {
                marker = marker && presentation.getDate().after(filter.begin);
            }
            if (filter.end != null) {
                marker = marker && presentation.getDate().before(filter.end);
            }
            return marker;
        });
    }

    private Stream<Presentation> sort(Stream<Presentation> stream, Sort sort) {
        if (sort == null) {
            return stream;
        }
        switch (sort) {
            case ID:
                return stream.sorted(Comparator.comparingInt(Presentation::getId));
            case PLAY:
                return stream.sorted(Comparator.comparing(presentation -> presentation.getPlay().getName()));
            case AUTHOR:
                return stream.sorted(Comparator.comparing(presentation -> presentation.getPlay().getAuthor().getName()));
            case GENRE:
                return stream.sorted(Comparator.comparing(presentation -> presentation.getPlay().getGenre().getName()));
            case DATE:
                return stream.sorted(Comparator.comparing(Presentation::getDate));
        }
        return stream;
    }

    private Stream<Presentation> reverse(Stream<Presentation> stream, boolean desc) {
        if (desc) {
            return Common.reverse(stream);
        }
        return stream;
    }


    @Builder
    public static final class Filter {
        public final Date begin;
        public final Date end;
        public final Integer playId;
        public final Integer authorId;
        public final Integer genreId;
        public final Boolean deleted;

        public Filter(Date begin, Date end, Integer playId, Integer authorId,
                      Integer genreId, Boolean deleted) {
            this.begin = begin;
            this.end = end;
            this.playId = playId;
            this.authorId = authorId;
            this.genreId = genreId;
            this.deleted = deleted;
        }
    }


    public enum Sort {
        ID("id"),
        PLAY("play"),
        AUTHOR("author"),
        GENRE("genre"),
        DATE("date");

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
