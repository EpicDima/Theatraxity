package com.epicdima.theatraxity.domain.usecases.business.order;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.OrderDao;
import com.epicdima.theatraxity.domain.dao.TicketDao;
import com.epicdima.theatraxity.domain.dto.OrderDto;
import com.epicdima.theatraxity.domain.dto.TicketDto;
import com.epicdima.theatraxity.domain.models.business.Order;
import com.epicdima.theatraxity.domain.models.business.Ticket;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author EpicDima
 */
public final class GetOrderByIdUseCase {
    private final OrderDao orderDao;
    private final TicketDao ticketDao;

    public GetOrderByIdUseCase(OrderDao orderDao, TicketDao ticketDao) {
        this.orderDao = orderDao;
        this.ticketDao = ticketDao;
    }

    public Result<OrderDto> execute(int orderId, User currentUser) {
        Order order = orderDao.select(orderId);
        if (currentUser.isNotAdminOrManager()) {
            if (order == null || order.isDeleted()) {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.ORDER_IS_DELETED);
            }
            if (User.Role.CLIENT.equals(currentUser.getRole())) {
                if (order.getBuyer().getId() != currentUser.getId()) {
                    return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
                }
            } else if (User.Role.COURIER.equals(currentUser.getRole())) {
                if (order.getCourier().getId() != currentUser.getId()) {
                    return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
                }
            }
        } else {
            if (order == null) {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.ORDER_NOT_FOUND);
            }
            if (order.isDeleted()) {
                return Result.failure(HttpCodes.BAD_REQUEST, Codes.ORDER_IS_DELETED);
            }
        }
        List<Ticket> tickets = ticketDao.selectByOrderId(order.getId());
        if (tickets == null || tickets.isEmpty()) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
        OrderDto orderDto = OrderDto.from(order);
        Stream<TicketDto> stream = tickets.stream().map(TicketDto::from);
        if (currentUser.isNotAdminOrManager()) {
            orderDto.deleted = null;
            orderDto.presentation.deleted = null;
            orderDto.presentation.play.deleted = null;
            orderDto.presentation.play.author.deleted = null;
            orderDto.presentation.play.genre.deleted = null;
            stream = stream.filter(ticketDto -> !ticketDto.deleted)
                    .peek(ticketDto -> ticketDto.deleted = null);
        }
        orderDto.tickets = stream.collect(Collectors.toList());
        return Result.success(orderDto);
    }
}
