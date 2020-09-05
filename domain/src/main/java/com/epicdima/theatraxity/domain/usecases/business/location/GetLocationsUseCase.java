package com.epicdima.theatraxity.domain.usecases.business.location;

import com.epicdima.theatraxity.domain.dto.LocationDto;
import com.epicdima.theatraxity.domain.models.business.Ticket;
import com.epicdima.theatraxity.domain.common.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * @author EpicDima
 */
public final class GetLocationsUseCase {

    public Result<List<LocationDto>> execute() {
        List<LocationDto> locations = new ArrayList<>();
        for (Ticket.Location location : Ticket.Location.values()) {
            if (location.getNumberOfAllSeats() > 0) {
                locations.add(LocationDto.from(location));
            }
        }
        return Result.success(locations);
    }
}
