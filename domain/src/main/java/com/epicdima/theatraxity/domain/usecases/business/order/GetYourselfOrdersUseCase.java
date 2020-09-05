package com.epicdima.theatraxity.domain.usecases.business.order;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.Common;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.OrderDao;
import com.epicdima.theatraxity.domain.dto.OrderDto;
import com.epicdima.theatraxity.domain.models.business.Order;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author EpicDima
 */
public final class GetYourselfOrdersUseCase {
    private final OrderDao orderDao;

    public GetYourselfOrdersUseCase(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public Result<List<OrderDto>> execute(Filter filter, Sort sort, boolean desc,
                                          int page, User currentUser) {
        if (currentUser.isAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        List<Order> orders;
        if (User.Role.CLIENT.equals(currentUser.getRole())) {
            orders = orderDao.selectByBuyerId(currentUser.getId());
        } else {
            orders = orderDao.selectByCourierId(currentUser.getId());
        }
        if (orders != null) {
            return Result.map(Result.success(reverse(sort(filter(orders.stream(), filter), sort), desc)
                    .map(OrderDto::from)
                    .peek(orderDto -> {
                        orderDto.deleted = null;
                        orderDto.presentation.deleted = null;
                        orderDto.presentation.play.deleted = null;
                        orderDto.presentation.play.author.deleted = null;
                        orderDto.presentation.play.genre.deleted = null;
                    }).collect(Collectors.toList())),
                    (list) -> Common.getPage(list, page));
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }

    private Stream<Order> filter(Stream<Order> stream, Filter filter) {
        return stream.filter(order -> {
            boolean marker = !order.isDeleted();
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
        public final Date begin;
        public final Date end;
        public final Order.Status status;

        public Filter(Date begin, Date end, Order.Status status) {
            this.begin = begin;
            this.end = end;
            this.status = status;
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
