package com.epicdima.theatraxity.domain.usecases.business.order;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.dao.OrderDao;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.domain.dao.TicketDao;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.dto.OrderDto;
import com.epicdima.theatraxity.domain.dto.TicketDto;
import com.epicdima.theatraxity.domain.models.business.Order;
import com.epicdima.theatraxity.domain.models.business.Ticket;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;
import com.epicdima.theatraxity.domain.models.user.User;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author EpicDima
 */
public final class MakeOrderYourselfUseCase {
    private final UserDao userDao;
    private final PresentationDao presentationDao;
    private final OrderDao orderDao;
    private final TicketDao ticketDao;

    public MakeOrderYourselfUseCase(UserDao userDao, PresentationDao presentationDao, OrderDao orderDao,
                                    TicketDao ticketDao) {
        this.userDao = userDao;
        this.presentationDao = presentationDao;
        this.orderDao = orderDao;
        this.ticketDao = ticketDao;
    }

    public Result<OrderDto> execute(OrderDto orderDto, User currentUser) {
        if (!User.Role.CLIENT.equals(currentUser.getRole())) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        if (orderDto.tickets.isEmpty()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.SEATS_LIST_IS_EMPTY);
        }
        Presentation presentation = presentationDao.select(orderDto.presentation.id);
        if (presentation == null || presentation.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PRESENTATION_NOT_FOUND);
        }
        List<User> couriers = userDao.selectByRole(User.Role.COURIER);
        if (couriers == null || couriers.isEmpty()) {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
        User courier = couriers.get(new Random().nextInt(couriers.size()));
        List<Ticket> tickets = ticketDao.selectNotCancelledByPresentationId(presentation.getId());
        List<Ticket> orderTickets = new ArrayList<>();
        for (TicketDto seat : orderDto.tickets) {
            orderTickets.add(Ticket.builder()
                    .location(seat.location.location)
                    .row(seat.row)
                    .seat(seat.seat)
                    .cost(seat.location.location.getTicketCost())
                    .build());
        }
        boolean anyExist = orderTickets.stream().anyMatch(orderTicket -> {
            for (Ticket ticket : tickets) {
                if (orderTicket.getLocation().equals(ticket.getLocation())
                        && orderTicket.getRow() == ticket.getRow()
                        && orderTicket.getSeat() == ticket.getSeat()) {
                    return true;
                }
            }
            return false;
        });
        if (anyExist) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.SEATS_ALREADY_OCCUPIED);
        }
        Order order = Order.builder()
                .presentation(presentation)
                .buyer(currentUser)
                .courier(courier)
                .date(new Date())
                .status(Order.Status.UNPAID)
                .build();
        order = orderDao.insert(order);
        if (order != null) {
            List<TicketDto> ticketsDto = new ArrayList<>();
            for (Ticket ticket : orderTickets) {
                Ticket fromDao = ticketDao.insert(ticket.toBuilder().order(order).build());
                if (fromDao != null) {
                    ticketsDto.add(TicketDto.from(fromDao));
                }
            }
            return Result.success(OrderDto.from(order, ticketsDto));
        }
        return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
    }
}
