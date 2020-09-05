package com.epicdima.theatraxity.domain.dto;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.models.business.Order;

import java.util.Date;
import java.util.List;

/**
 * @author EpicDima
 */
@Builder
public final class OrderDto {
    public Integer id;
    public PresentationDto presentation;
    public UserDto buyer;
    public Date date;
    public UserDto courier;
    public Order.Status status;
    public Boolean deleted;
    public List<TicketDto> tickets;

    public OrderDto() {}

    public OrderDto(Integer id, PresentationDto presentation, UserDto buyer, Date date, UserDto courier,
                    Order.Status status, Boolean deleted, List<TicketDto> tickets) {
        this.id = id;
        this.presentation = presentation;
        this.buyer = buyer;
        this.date = date;
        this.courier = courier;
        this.status = status;
        this.deleted = deleted;
        this.tickets = tickets;
    }

    public static OrderDto from(Order order) {
        return new OrderDto(order.getId(), PresentationDto.from(order.getPresentation()),
                UserDto.from(order.getBuyer()), order.getDate(),
                UserDto.from(order.getCourier()), order.getStatus(),
                order.isDeleted(), null);
    }

    public static OrderDto from(Order order, List<TicketDto> tickets) {
        OrderDto orderDto = from(order);
        orderDto.tickets = tickets;
        return orderDto;
    }
}
