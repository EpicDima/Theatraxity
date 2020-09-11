package com.epicdima.theatraxity.domain.models.business;

import com.epicdima.lib.builder.annotation.Builder;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author EpicDima
 */
@Builder
public final class Ticket {
    private final int id;
    private final Order order;
    private final Location location;
    private final int row;
    private final int seat;
    private final BigDecimal cost;
    private final boolean deleted;

    public Ticket(int id, Order order, Location location, int row,
                  int seat, BigDecimal cost, boolean deleted) {
        this.id = id;
        this.order = order;
        this.location = location;
        this.row = row;
        this.seat = seat;
        this.cost = cost;
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Location getLocation() {
        return location;
    }

    public int getRow() {
        return row;
    }

    public int getSeat() {
        return seat;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id == ticket.id &&
                row == ticket.row &&
                seat == ticket.seat &&
                ticket.cost.compareTo(cost) == 0 &&
                deleted == ticket.deleted &&
                Objects.equals(order, ticket.order) &&
                location == ticket.location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order, location, row, seat, cost, deleted);
    }


    public enum Location {
        BALCONY_LEFT,
        BALCONY_MIDDLE,
        BALCONY_RIGHT,
        MEZZANINE_LEFT,
        MEZZANINE_MIDDLE,
        MEZZANINE_RIGHT,
        AMPHITHEATRE,
        BENOIR_LEFT,
        BENOIR_RIGHT,
        PARTERRE;

        private BigDecimal ticketCost = new BigDecimal("0.00");
        private int rows = 0;
        private int seats = 0;

        public BigDecimal getTicketCost() {
            return ticketCost;
        }

        public void setTicketCost(BigDecimal ticketCost) {
            if (BigDecimal.ZERO.compareTo(ticketCost) >= 0) {
                throw new IllegalArgumentException("TicketCost must be positive, greater than zero");
            }
            this.ticketCost = ticketCost;
        }

        public int getRows() {
            return rows;
        }

        public void setRows(int rows) {
            if (rows <= 0) {
                throw new IllegalArgumentException("Rows must be positive");
            }
            this.rows = rows;
        }

        public int getSeats() {
            if (seats <= 0) {
                throw new IllegalArgumentException("Seats must be positive");
            }
            return seats;
        }

        public void setSeats(int seats) {
            this.seats = seats;
        }

        public int getNumberOfAllSeats() {
            return rows * seats;
        }
    }
}
