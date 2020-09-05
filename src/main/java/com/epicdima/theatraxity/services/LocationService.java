package com.epicdima.theatraxity.services;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.LocationDto;
import com.epicdima.theatraxity.domain.dto.TicketDto;
import com.epicdima.theatraxity.domain.usecases.business.location.ChangeLocationCostUseCase;
import com.epicdima.theatraxity.domain.usecases.business.location.GetLocationsUseCase;
import com.epicdima.theatraxity.domain.usecases.business.location.GetOccupiedSeatsUseCase;
import com.epicdima.theatraxity.model.SessionUser;

import java.util.List;

/**
 * @author EpicDima
 */
@Singleton
public final class LocationService {
    private final ChangeLocationCostUseCase changeLocationCostUseCase;
    private final GetLocationsUseCase getLocationsUseCase;
    private final GetOccupiedSeatsUseCase getOccupiedSeatsUseCase;

    @Inject
    public LocationService(ChangeLocationCostUseCase changeLocationCostUseCase,
                           GetLocationsUseCase getLocationsUseCase,
                           GetOccupiedSeatsUseCase getOccupiedSeatsUseCase) {
        this.changeLocationCostUseCase = changeLocationCostUseCase;
        this.getLocationsUseCase = getLocationsUseCase;
        this.getOccupiedSeatsUseCase = getOccupiedSeatsUseCase;
    }

    public Result<Void> changeLocationCost(LocationDto request, SessionUser currentUser) {
        return changeLocationCostUseCase.execute(request, currentUser.toUser());
    }

    public Result<List<LocationDto>> getLocations() {
        return getLocationsUseCase.execute();
    }

    public Result<List<TicketDto>> getOccupiedSeats(int presentationId) {
        return getOccupiedSeatsUseCase.execute(presentationId);
    }
}
