package com.epicdima.theatraxity.domain.usecases.business.location;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dto.LocationDto;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class ChangeLocationCostUseCase {
    public Result<Void> execute(LocationDto location, User currentUser) {
        if (currentUser.isNotAdminOrManager()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        try {
            location.location.setTicketCost(location.ticketCost);
            return Result.empty();
        } catch (IllegalArgumentException e) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.LOCATION_COST_ERROR);
        }
    }
}
