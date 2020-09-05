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
public final class RestorePresentationUseCase {
    private final PresentationDao presentationDao;

    public RestorePresentationUseCase(PresentationDao presentationDao) {
        this.presentationDao = presentationDao;
    }

    public Result<PresentationDto> execute(int presentationId, User currentUser) {
        if (currentUser.isNotAdmin()) {
            return Result.failure(HttpCodes.FORBIDDEN, Codes.NOT_ALLOWED);
        }
        Presentation fromDao = presentationDao.select(presentationId);
        if (fromDao == null) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PRESENTATION_NOT_FOUND);
        }
        if (!fromDao.isDeleted()) {
            return Result.failure(HttpCodes.BAD_REQUEST, Codes.PRESENTATION_IS_NOT_DELETED);
        }
        Presentation forDao = fromDao.toBuilder().deleted(false).build();
        Boolean updated = presentationDao.update(forDao);
        if (updated != null) {
            if (updated) {
                return Result.success(PresentationDto.from(forDao));
            } else {
                return Result.failure(HttpCodes.OK, Codes.PRESENTATION_NOT_RESTORED);
            }
        } else {
            return Result.failure(HttpCodes.SERVER_ERROR, Codes.UNEXPECTED_ERROR);
        }
    }
}
