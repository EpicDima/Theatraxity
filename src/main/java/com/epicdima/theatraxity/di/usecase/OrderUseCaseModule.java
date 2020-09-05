package com.epicdima.theatraxity.di.usecase;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.Provides;
import com.epicdima.theatraxity.domain.dao.OrderDao;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.domain.dao.TicketDao;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.usecases.business.order.*;

/**
 * @author EpicDima
 */
@Module
public final class OrderUseCaseModule {
    @Provides
    public CancelOrderUseCase provideCancelOrderUseCase(OrderDao orderDao) {
        return new CancelOrderUseCase(orderDao);
    }

    @Provides
    public CancelYourselfOrderUseCase provideCancelYourselfOrderUseCase(OrderDao orderDao) {
        return new CancelYourselfOrderUseCase(orderDao);
    }

    @Provides
    public ConfirmOrderDeliveryUseCase provideConfirmOrderDeliveryUseCase(OrderDao orderDao) {
        return new ConfirmOrderDeliveryUseCase(orderDao);
    }

    @Provides
    public ConfirmOrderDeliveryYourselfUseCase provideConfirmOrderDeliveryYourselfUseCase(OrderDao orderDao) {
        return new ConfirmOrderDeliveryYourselfUseCase(orderDao);
    }

    @Provides
    public GetOrderByIdUseCase provideGetOrderByIdUseCase(OrderDao orderDao, TicketDao ticketDao) {
        return new GetOrderByIdUseCase(orderDao, ticketDao);
    }

    @Provides
    public GetOrdersUseCase provideGetOrdersUseCase(UserDao userDao, OrderDao orderDao) {
        return new GetOrdersUseCase(userDao, orderDao);
    }

    @Provides
    public GetYourselfOrdersUseCase provideGetYourselfOrdersUseCase(OrderDao orderDao) {
        return new GetYourselfOrdersUseCase(orderDao);
    }

    @Provides
    public MakeOrderUseCase provideMakeOrderUseCase(UserDao userDao, PresentationDao presentationDao,
                                                    OrderDao orderDao, TicketDao ticketDao) {
        return new MakeOrderUseCase(userDao, presentationDao, orderDao, ticketDao);
    }

    @Provides
    public MakeOrderYourselfUseCase provideMakeOrderYourselfUseCase(UserDao userDao,
                                                                    PresentationDao presentationDao,
                                                                    OrderDao orderDao, TicketDao ticketDao) {
        return new MakeOrderYourselfUseCase(userDao, presentationDao, orderDao, ticketDao);
    }
}
