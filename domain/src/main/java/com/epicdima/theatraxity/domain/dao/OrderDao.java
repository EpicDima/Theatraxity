package com.epicdima.theatraxity.domain.dao;

import com.epicdima.theatraxity.domain.models.business.Order;

import java.util.List;

/**
 * @author EpicDima
 */
public interface OrderDao extends BaseDao<Order> {
    List<Order> selectByBuyerId(int buyerId);

    List<Order> selectByCourierId(int courierId);
}
