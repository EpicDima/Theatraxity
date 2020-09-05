package com.epicdima.theatraxity.domain.dto;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.models.business.Ticket;

import java.math.BigDecimal;

/**
 * @author EpicDima
 */
@Builder
public final class LocationDto {
    public Ticket.Location location;
    public BigDecimal ticketCost;
    public Integer rows;
    public Integer seats;

    public LocationDto() {}

    public LocationDto(Ticket.Location location, BigDecimal ticketCost, Integer rows, Integer seats) {
        this.location = location;
        this.ticketCost = ticketCost;
        this.rows = rows;
        this.seats = seats;
    }

    public static LocationDto from(Ticket.Location location) {
        return new LocationDto(location, location.getTicketCost(),
                location.getRows(), location.getSeats());
    }

    public static Ticket.Location to(LocationDto location) {
        return location.location;
    }
}
