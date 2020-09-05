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
import com.epicdima.theatraxity.dal.models.theatre.PresentationData;
import com.epicdima.theatraxity.dal.models.user.UserData;
import com.epicdima.theatraxity.domain.models.business.Order;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;
import com.epicdima.theatraxity.domain.models.user.User;

import java.util.Date;

/**
 * @author EpicDima
 */
@Table(name = "orders")
public final class OrderData {

    @PrimaryKey
    @Column
    public int id;

    @ForeignKey(entity = PresentationData.class, parentColumn = "id")
    @Column(name = "presentation_id")
    public int presentationId;

    @ForeignKey(entity = UserData.class, parentColumn = "id")
    @Column(name = "buyer_id")
    public int buyerId;

    @Column
    public Date date;

    @ForeignKey(entity = UserData.class, parentColumn = "id")
    @Column(name = "courier_id")
    public int courierId;

    @Column
    public Order.Status status;

    @Column
    public boolean deleted;

    public OrderData() {}

    public OrderData(int id, int presentationId, int buyerId, Date date,
                     int courierId, Order.Status status, boolean deleted) {
        this.id = id;
        this.presentationId = presentationId;
        this.buyerId = buyerId;
        this.date = date;
        this.courierId = courierId;
        this.status = status;
        this.deleted = deleted;
    }

    @Named("dal_to_order")
    @Singleton
    public static final class ToOrder implements Mapper<OrderData, Order> {

        private final GetById<Presentation> presentationGetter;
        private final GetById<User> userGetter;

        @Inject
        public ToOrder(@Named("presentation") GetById<Presentation> presentationGetter,
                       @Named("user") GetById<User> userGetter) {
            this.presentationGetter = presentationGetter;
            this.userGetter = userGetter;
        }

        @Override
        public Order map(OrderData item) {
            return new Order(item.id, presentationGetter.getById(item.presentationId),
                    userGetter.getById(item.buyerId), item.date, userGetter.getById(item.courierId),
                    item.status, item.deleted);
        }
    }

    @Named("dal_from_order")
    @Singleton
    public static final class FromOrder implements Mapper<Order, OrderData> {
        @Override
        public OrderData map(Order item) {
            return new OrderData(item.getId(), item.getPresentation().getId(), item.getBuyer().getId(),
                    item.getDate(), item.getCourier().getId(), item.getStatus(), item.isDeleted());
        }
    }
}
