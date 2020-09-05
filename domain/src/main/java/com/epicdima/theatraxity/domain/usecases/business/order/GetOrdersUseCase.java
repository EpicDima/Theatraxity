package com.epicdima.theatraxity.domain.usecases.business.order;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.Common;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.OrderDao;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.dto.OrderDto;
import com.epicdima.theatraxity.domain.models.business.Order;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author EpicDima
 */
public final class GetOrdersUseCase {
    private final UserDao userDao;
    private final OrderDao orderDao;

    public GetOrdersUseCase(UserDao userDao, OrderDao orderDao) {
        this.userDao = userDao;
        this.orderDao = orderDao;
    }

    public Result<List<OrderDto>> execute(Filter filter, Sort sort, boolean desc,
                                          int page, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        List<Order> orders;
        if (filter.userId != null || filter.userEmail != null) {
            User user;
            if (filter.userId != null) {
                user = userDao.select(filter.userId);
            } else {
                user = userDao.select(filter.userId);
            }
            if (user == null || user.isDeleted()) {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.USER_NOT_FOUND);
            }
            if (user.isAdminOrManager()) {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.USER_IS_ADMIN_OR_MANAGER);
            }
            if (User.Role.CLIENT.equals(user.getRole())) {
                orders = orderDao.selectByBuyerId(user.getId());
            } else {
                orders = orderDao.selectByCourierId(user.getId());
            }
        } else {
            orders = orderDao.select();
        }
        if (orders != null) {
            return Result.map(Result.success(reverse(sort(filter(orders.stream(), filter), sort), desc)
                    .map(OrderDto::from)
                    .collect(Collectors.toList())),
                    (list) -> Common.getPage(list, page));
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }

    private Stream<Order> filter(Stream<Order> stream, Filter filter) {
        return stream.filter(order -> {
            boolean marker;
            if (filter.deleted != null) {
                marker = order.isDeleted() == filter.deleted;
            } else {
                marker = !order.isDeleted();
            }
            if (filter.presentationId != null) {
                marker = marker && order.getPresentation().getId() == filter.presentationId;
            } else if (filter.presentationDate != null) {
                marker = marker && order.getPresentation().getDate().equals(filter.presentationDate);
            }
            if (filter.begin != null) {
                marker = marker && order.getDate().after(filter.begin);
            }
            if (filter.end != null) {
                marker = marker && order.getDate().before(filter.end);
            }
            if (filter.status != null) {
                marker = marker && filter.status.equals(order.getStatus());
            }
            return marker;
        });
    }

    private Stream<Order> sort(Stream<Order> stream, Sort sort) {
        if (sort == null) {
            return stream;
        }
        switch (sort) {
            case ID:
                return stream.sorted(Comparator.comparingInt(Order::getId));
            case DATE:
                return stream.sorted(Comparator.comparing(Order::getDate));
            case STATUS:
                return stream.sorted(Comparator.comparing(Order::getStatus));
        }

        return stream;
    }

    private Stream<Order> reverse(Stream<Order> stream, boolean desc) {
        if (desc) {
            return Common.reverse(stream);
        }
        return stream;
    }


    public static final class Filter {
        public final Integer userId;
        public final String userEmail;
        public final Integer presentationId;
        public final Date presentationDate;
        public final Date begin;
        public final Date end;
        public final Order.Status status;
        public final Boolean deleted;


        public Filter(Integer userId, String userEmail, Integer presentationId, Date presentationDate,
                      Date begin, Date end, Order.Status status, Boolean deleted) {
            this.userId = userId;
            this.userEmail = userEmail;
            this.presentationId = presentationId;
            this.presentationDate = presentationDate;
            this.begin = begin;
            this.end = end;
            this.status = status;
            this.deleted = deleted;
        }
    }


    public enum Sort {
        ID("id"),
        DATE("date"),
        STATUS("status");

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
