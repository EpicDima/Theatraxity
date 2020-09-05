package com.epicdima.theatraxity.dal.mysql.dao.business;

import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.models.business.OrderData;
import com.epicdima.theatraxity.dal.mysql.dao.MySqlBaseDao;
import com.epicdima.theatraxity.domain.dao.OrderDao;
import com.epicdima.theatraxity.domain.models.business.Order;

import java.util.List;

/**
 * @author EpicDima
 */
@Singleton
public final class MySqlOrderDao extends MySqlBaseDao<OrderData, Order> implements OrderDao {
    @Inject
    public MySqlOrderDao(ConnectionPool connectionPool,
                         @Named("dal_to_order") Mapper<OrderData, Order> to,
                         @Named("dal_from_order") Mapper<Order, OrderData> from) {
        super(connectionPool, to, from);
    }

    @Override
    public List<Order> selectByBuyerId(int buyerId) {
        return selectWhere("buyer_id", buyerId);
    }

    @Override
    public List<Order> selectByCourierId(int courierId) {
        return selectWhere("courier_id", courierId);
    }
}
