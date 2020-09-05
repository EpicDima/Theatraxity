package com.epicdima.theatraxity.dal.getters;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.dao.OrderDao;
import com.epicdima.theatraxity.domain.models.business.Order;

/**
 * @author EpicDima
 */
@Named("order")
@Singleton
public final class GetOrderById implements GetById<Order> {

    private final OrderDao orderDao;

    @Inject
    public GetOrderById(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public synchronized Order getById(int id) {
        return orderDao.select(id);
    }
}
