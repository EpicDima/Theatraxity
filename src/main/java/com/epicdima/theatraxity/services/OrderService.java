package com.epicdima.theatraxity.services;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.OrderDto;
import com.epicdima.theatraxity.domain.models.business.Order;
import com.epicdima.theatraxity.domain.usecases.business.order.*;
import com.epicdima.theatraxity.helpers.DateFormat;
import com.epicdima.theatraxity.model.SessionUser;

import java.util.List;

import static com.epicdima.theatraxity.helpers.Utils.valueOfEnum;

/**
 * @author EpicDima
 */
@Singleton
public final class OrderService {

    private final CancelOrderUseCase cancelOrderUseCase;
    private final CancelYourselfOrderUseCase cancelYourselfOrderUseCase;
    private final ConfirmOrderDeliveryUseCase confirmOrderDeliveryUseCase;
    private final ConfirmOrderDeliveryYourselfUseCase confirmOrderDeliveryYourselfUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final GetOrdersUseCase getOrdersUseCase;
    private final GetYourselfOrdersUseCase getYourselfOrdersUseCase;
    private final MakeOrderUseCase makeOrderUseCase;
    private final MakeOrderYourselfUseCase makeOrderYourselfUseCase;
    private final DateFormat dateFormat;

    @Inject
    public OrderService(CancelOrderUseCase cancelOrderUseCase,
                        CancelYourselfOrderUseCase cancelYourselfOrderUseCase,
                        ConfirmOrderDeliveryUseCase confirmOrderDeliveryUseCase,
                        ConfirmOrderDeliveryYourselfUseCase confirmOrderDeliveryYourselfUseCase,
                        GetOrderByIdUseCase getOrderByIdUseCase, GetOrdersUseCase getOrdersUseCase,
                        GetYourselfOrdersUseCase getYourselfOrdersUseCase, MakeOrderUseCase makeOrderUseCase,
                        MakeOrderYourselfUseCase makeOrderYourselfUseCase, DateFormat dateFormat) {
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.cancelYourselfOrderUseCase = cancelYourselfOrderUseCase;
        this.confirmOrderDeliveryUseCase = confirmOrderDeliveryUseCase;
        this.confirmOrderDeliveryYourselfUseCase = confirmOrderDeliveryYourselfUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.getOrdersUseCase = getOrdersUseCase;
        this.getYourselfOrdersUseCase = getYourselfOrdersUseCase;
        this.makeOrderUseCase = makeOrderUseCase;
        this.makeOrderYourselfUseCase = makeOrderYourselfUseCase;
        this.dateFormat = dateFormat;
    }

    public Result<Void> cancelOrder(OrderDto request, SessionUser currentUser) {
        return cancelOrderUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<Void> cancelOrderYourself(OrderDto request, SessionUser currentUser) {
        return cancelYourselfOrderUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<Void> confirmOrderDelivery(OrderDto request, SessionUser currentUser) {
        return confirmOrderDeliveryUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<Void> confirmOrderDeliveryYourself(OrderDto request, SessionUser currentUser) {
        return confirmOrderDeliveryYourselfUseCase.execute(request.id, currentUser.toUser());
    }

    public Result<OrderDto> getOrderById(int orderId, SessionUser currentUser) {
        return getOrderByIdUseCase.execute(orderId, currentUser.toUser());
    }

    public Result<List<OrderDto>> getOrders(Integer filterUserId, String filterUserEmail,
                                            Integer filterPresentationId, String filterPresentationDate,
                                            String filterBegin, String filterEnd, String filterStatus,
                                            Boolean filterDeleted, String sortString,
                                            boolean desc, int page, SessionUser currentUser) {
        return getOrdersUseCase.execute(
                new GetOrdersUseCase.Filter(filterUserId, filterUserEmail, filterPresentationId,
                        dateFormat.format(filterPresentationDate),
                        dateFormat.format(filterBegin), dateFormat.format(filterEnd),
                        valueOfEnum(Order.Status.class, filterStatus), filterDeleted),
                GetOrdersUseCase.Sort.fromName(sortString),
                desc,
                page,
                currentUser.toUser()
        );
    }

    public Result<List<OrderDto>> getYourselfOrders(String filterBegin, String filterEnd,
                                                    String filterStatus, String sortString,
                                                    boolean desc, int page, SessionUser currentUser) {
        return getYourselfOrdersUseCase.execute(
                new GetYourselfOrdersUseCase.Filter(dateFormat.format(filterBegin),
                        dateFormat.format(filterEnd), valueOfEnum(Order.Status.class, filterStatus)),
                GetYourselfOrdersUseCase.Sort.fromName(sortString),
                desc,
                page,
                currentUser.toUser()
        );
    }

    public Result<OrderDto> makeOrder(OrderDto request, SessionUser currentUser) {
        return makeOrderUseCase.execute(request, currentUser.toUser());
    }

    public Result<OrderDto> makeOrderYourself(OrderDto request, SessionUser currentUser) {
        return makeOrderYourselfUseCase.execute(request, currentUser.toUser());
    }
}
