package com.epicdima.theatraxity.domain.usecases.business.order;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.OrderDao;
import com.epicdima.theatraxity.domain.models.business.Order;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class ConfirmOrderDeliveryUseCase {
    private final OrderDao orderDao;

    public ConfirmOrderDeliveryUseCase(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public Result<Void> execute(int orderId, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Order fromDao = orderDao.select(orderId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.ORDER_NOT_FOUND);
        }
        if (fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.ORDER_IS_DELETED);
        }
        if (Order.Status.PAID.equals(fromDao.getStatus())) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.ORDER_IS_PAID);
        }
        if (Order.Status.CANCELLED.equals(fromDao.getStatus())) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.ORDER_IS_CANCELLED);
        }
        Boolean updated = orderDao.update(fromDao.toBuilder()
                .status(Order.Status.PAID)
                .build());
        if (updated != null) {
            if (updated) {
                return Result.empty();
            } else {
                return Result.failure(HttpCodes.OK, Codes.ORDER_DELIVERY_NOT_CONFIRMED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
