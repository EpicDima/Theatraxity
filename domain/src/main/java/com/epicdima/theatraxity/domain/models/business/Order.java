package com.epicdima.theatraxity.domain.models.business;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.Date;
import java.util.Objects;

/**
 * @author EpicDima
 */
@Builder
public final class Order {
    public enum Status {
        UNPAID, PAID, CANCELLED
    }

    private final int id;
    private final Presentation presentation;
    private final User buyer;
    private final Date date;
    private final User courier;
    private final Status status;
    private final boolean deleted;

    public Order(int id, Presentation presentation, User buyer, Date date, User courier,
                 Status status, boolean deleted) {
        this.id = id;
        this.presentation = presentation;
        this.buyer = buyer;
        this.date = date;
        this.courier = courier;
        this.status = status;
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public User getBuyer() {
        return buyer;
    }

    public Date getDate() {
        return date;
    }

    public User getCourier() {
        return courier;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id &&
                deleted == order.deleted &&
                Objects.equals(presentation, order.presentation) &&
                Objects.equals(buyer, order.buyer) &&
                Objects.equals(date, order.date) &&
                Objects.equals(courier, order.courier) &&
                status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, presentation, buyer, date, courier, status, deleted);
    }
}
