package com.epicdima.theatraxity.domain.usecases.business.order;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.dao.OrderDao;
import com.epicdima.theatraxity.domain.models.business.Order;
import com.epicdima.theatraxity.domain.models.user.User;
import com.epicdima.theatraxity.domain.common.Result;

/**
 * @author EpicDima
 */
public final class CancelYourselfOrderUseCase {
    private final OrderDao orderDao;

    public CancelYourselfOrderUseCase(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public Result<Void> execute(int orderId, User currentUser) {
        if (!User.Role.CLIENT.equals(currentUser.getRole())) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Order fromDao = orderDao.select(orderId);
        if (fromDao == null || fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PRESENTATION_NOT_FOUND);
        }
        if (fromDao.getBuyer().getId() != currentUser.getId()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        if (Order.Status.PAID.equals(fromDao.getStatus())) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.ORDER_IS_PAID);
        }
        if (Order.Status.CANCELLED.equals(fromDao.getStatus())) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.ORDER_IS_CANCELLED);
        }
        Boolean updated = orderDao.update(fromDao.toBuilder()
                .status(Order.Status.CANCELLED)
                .build());
        if (updated != null) {
            if (updated) {
                return Result.empty();
            } else {
                return Result.failure(HttpCodes.OK, Codes.ORDER_NOT_CANCELLED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
