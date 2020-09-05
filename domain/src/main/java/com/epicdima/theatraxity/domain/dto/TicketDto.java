package com.epicdima.theatraxity.domain.dto;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.models.business.Ticket;

import java.math.BigDecimal;

/**
 * @author EpicDima
 */
@Builder
public final class TicketDto {
    public Integer id;
    public LocationDto location;
    public Integer row;
    public Integer seat;
    public BigDecimal cost;
    public Boolean deleted;

    public TicketDto() {}

    public TicketDto(Integer id, LocationDto location, Integer row, Integer seat,
                     BigDecimal cost, Boolean deleted) {
        this.id = id;
        this.location = location;
        this.row = row;
        this.seat = seat;
        this.cost = cost;
        this.deleted = deleted;
    }

    public static TicketDto from(Ticket ticket) {
        return new TicketDto(ticket.getId(), LocationDto.from(ticket.getLocation()),
                ticket.getRow(), ticket.getSeat(), ticket.getCost(), ticket.isDeleted());
    }

    public static Ticket to(TicketDto ticketDto) {
        Ticket.Builder builder = Ticket.builder();
        if (ticketDto == null) {
            return builder.build();
        }
        if (ticketDto.id != null) {
            builder.id(ticketDto.id);
        }
        if (ticketDto.location != null) {
            builder.location(LocationDto.to(ticketDto.location));
        }
        if (ticketDto.row != null) {
            builder.row(ticketDto.row);
        }
        if (ticketDto.seat != null) {
            builder.seat(ticketDto.seat);
        }
        if (ticketDto.cost != null) {
            builder.cost(ticketDto.cost);
        }
        if (ticketDto.deleted != null) {
            builder.deleted(ticketDto.deleted);
        }
        return builder.build();
    }
}
