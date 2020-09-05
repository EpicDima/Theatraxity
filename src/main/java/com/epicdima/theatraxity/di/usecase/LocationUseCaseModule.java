package com.epicdima.theatraxity.di.usecase;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.Provides;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.domain.dao.TicketDao;
import com.epicdima.theatraxity.domain.usecases.business.location.ChangeLocationCostUseCase;
import com.epicdima.theatraxity.domain.usecases.business.location.GetLocationsUseCase;
import com.epicdima.theatraxity.domain.usecases.business.location.GetOccupiedSeatsUseCase;

/**
 * @author EpicDima
 */
@Module
public final class LocationUseCaseModule {
    @Provides
    public ChangeLocationCostUseCase provideChangeLocationCostUseCase() {
        return new ChangeLocationCostUseCase();
    }

    @Provides
    public GetLocationsUseCase provideGetLocationsUseCase() {
        return new GetLocationsUseCase();
    }

    @Provides
    public GetOccupiedSeatsUseCase provideGetOccupiedSeatsUseCase(PresentationDao presentationDao,
                                                                  TicketDao ticketDao) {
        return new GetOccupiedSeatsUseCase(presentationDao, ticketDao);
    }
}
