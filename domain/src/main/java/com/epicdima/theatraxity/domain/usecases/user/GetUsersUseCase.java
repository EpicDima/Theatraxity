package com.epicdima.theatraxity.domain.usecases.user;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.Common;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.dto.UserDto;
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
public final class GetUsersUseCase {
    private final UserDao userDao;

    public GetUsersUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result<List<UserDto>> execute(Filter filter, Sort sort, boolean desc,
                                         int page, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        List<User> users = userDao.select();
        if (users == null) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
        return Result.map(Result.success(reverse(sort(filter(users.stream(), filter), sort), desc)
                .map(UserDto::from)
                .collect(Collectors.toList())),
                (list) -> Common.getPage(list, page));
    }

    private Stream<User> filter(Stream<User> stream, Filter filter) {
        return stream.filter(user -> {
            boolean marker;
            if (filter.deleted != null) {
                marker = user.isDeleted() == filter.deleted;
            } else {
                marker = !user.isDeleted();
            }
            if (filter.role != null) {
                marker = marker && user.getRole().equals(filter.role);
            }
            return marker;
        });
    }

    private Stream<User> sort(Stream<User> stream, Sort sort) {
        if (sort == null) {
            return stream;
        }
        switch (sort) {
            case ID:
                return stream.sorted(Comparator.comparingInt(User::getId));
            case EMAIL:
                return stream.sorted(Comparator.comparing(User::getEmail));
        }
        return stream;
    }

    private Stream<User> reverse(Stream<User> stream, boolean desc) {
        if (desc) {
            return Common.reverse(stream);
        }
        return stream;
    }


    public static final class Filter {
        public final User.Role role;
        public final Boolean deleted;

        public Filter(User.Role role, Boolean deleted) {
            this.role = role;
            this.deleted = deleted;
        }
    }


    public enum Sort {
        ID("id"),
        EMAIL("email");

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
