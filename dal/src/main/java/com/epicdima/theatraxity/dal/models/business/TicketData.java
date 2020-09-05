package com.epicdima.theatraxity.dal.models.business;

import com.epicdima.lib.dal.annotations.Column;
import com.epicdima.lib.dal.annotations.ForeignKey;
import com.epicdima.lib.dal.annotations.PrimaryKey;
import com.epicdima.lib.dal.annotations.Table;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.getters.GetById;
import com.epicdima.theatraxity.domain.models.business.Order;
import com.epicdima.theatraxity.domain.models.business.Ticket;

import java.math.BigDecimal;

/**
 * @author EpicDima
 */
@Table(name = "tickets")
public final class TicketData {

    @PrimaryKey
    @Column
    public int id;

    @ForeignKey(entity = OrderData.class, parentColumn = "id")
    @Column(name = "order_id")
    public int orderId;

    @Column
    public Ticket.Location location;

    @Column
    public int row;

    @Column
    public int seat;

    @Column
    public BigDecimal cost;

    @Column
    public boolean deleted;

    public TicketData() {}

    public TicketData(int id, int orderId, Ticket.Location location, int row,
                      int seat, BigDecimal cost, boolean deleted) {
        this.id = id;
        this.orderId = orderId;
        this.location = location;
        this.row = row;
        this.seat = seat;
        this.cost = cost;
        this.deleted = deleted;
    }

    @Named("dal_to_ticket")
    @Singleton
    public static final class ToTicket implements Mapper<TicketData, Ticket> {

        private final GetById<Order> orderGetter;

        @Inject
        public ToTicket(@Named("order") GetById<Order> orderGetter) {
            this.orderGetter = orderGetter;
        }

        @Override
        public Ticket map(TicketData item) {
            return new Ticket(item.id, orderGetter.getById(item.orderId), item.location,
                    item.row, item.seat, item.cost, item.deleted);
        }
    }

    @Named("dal_from_ticket")
    @Singleton
    public static final class FromTicket implements Mapper<Ticket, TicketData> {
        @Override
        public TicketData map(Ticket item) {
            return new TicketData(item.getId(), item.getOrder().getId(), item.getLocation(),
                    item.getRow(), item.getSeat(), item.getCost(), item.isDeleted());
        }
    }
}
