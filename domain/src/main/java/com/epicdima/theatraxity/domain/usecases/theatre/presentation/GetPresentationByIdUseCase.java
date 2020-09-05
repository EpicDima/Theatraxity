package com.epicdima.theatraxity.domain.usecases.theatre.presentation;

import com.epicdima.theatraxity.domain.common.Codes;
import com.epicdima.theatraxity.domain.common.HttpCodes;
import com.epicdima.theatraxity.domain.common.Result;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.domain.dto.PresentationDto;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;
import com.epicdima.theatraxity.domain.models.user.User;

/**
 * @author EpicDima
 */
public final class GetPresentationByIdUseCase {
    private final PresentationDao presentationDao;

    public GetPresentationByIdUseCase(PresentationDao presentationDao) {
        this.presentationDao = presentationDao;
    }

    public Result<PresentationDto> execute(int presentationId, User currentUser) {
        Presentation presentation = presentationDao.select(presentationId);
        if (presentation != null) {
            if (presentation.isDeleted() && currentUser.isNotAdminOrManager()) {
                return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
            }
            return Result.success(PresentationDto.from(presentation));
        } else {
            return Result.failure(HttpCodes.NOT_FOUND, Codes.PRESENTATION_NOT_FOUND);
        }
    }
}
